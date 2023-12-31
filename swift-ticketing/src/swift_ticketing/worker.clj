(ns swift-ticketing.worker
  (:require [clojure.core.async :as async]
            [swift-ticketing.db.ticket :as ticket]
            [swift-ticketing.db.booking :as booking]
            [next.jdbc :as jdbc]
            [clojure.core :as c]
            [swift-ticketing.redis :as redis]
            [taoensso.timbre :as log])
  (:import [java.time Instant Duration]))

(def reserve-event "RESERVE")
(def book-event "BOOK")
(def cancel-event "CANCEL")

(defn add-ticket-request-to-queue [message-queue request]
  (async/go (async/>! message-queue request)))

(defn add-reserve-ticket-request-to-queue
  [message-queue
   {:keys [booking-id ticket-ids ticket-type-id quantity]}]
  (let [event-type reserve-event]
    (add-ticket-request-to-queue
     message-queue
     {:event event-type
      :data {:booking-id booking-id
             :ticket-ids ticket-ids
             :ticket-type-id ticket-type-id
             :quantity quantity}})))

(defn add-book-ticket-request-to-queue [message-queue request]
  (let [event-type book-event
        booking-id (:booking-id request)]
    (add-ticket-request-to-queue
     message-queue
     {:event event-type
      :data {:booking-id booking-id}})))

(defn add-cancel-ticket-request-to-queue [message-queue request]
  (let [event-type cancel-event
        booking-id (:booking-id request)]
    (add-ticket-request-to-queue
     message-queue
     {:event event-type
      :data {:booking-id booking-id}})))

(defn- select-unbooked-tickets-with-db-lock [tx ticket-ids ticket-type-id ticket-quantity]
  (let [selected-rows (ticket/lock-unbooked-tickets tx ticket-ids ticket-type-id ticket-quantity)
        selected-ticket-ids (map #(:ticket/ticket_id %) selected-rows)
        reservation-timelimit-seconds (:ticket_type/reservation_timelimit_seconds
                                       (first selected-rows))]
    {:locked-ticket-ids selected-ticket-ids
     :reservation-timelimit-seconds reservation-timelimit-seconds}))

(defn- select-unbooked-tickets-with-redis-lock [redis-opts tx ticket-ids ticket-type-id ticket-quantity]
  (let [selected-rows (ticket/select-unbooked-tickets tx false ticket-ids ticket-type-id ticket-quantity)
        selected-ticket-ids (map #(:ticket/ticket_id %) selected-rows)
        reservation-timelimit-seconds (:ticket_type/reservation_timelimit_seconds (first selected-rows))
        reduction-fn (fn [ticket-id-set ticket-id]
                       (if (nil? (redis/acquire-lock redis-opts ticket-id reservation-timelimit-seconds))
                         ticket-id-set
                         (conj ticket-id-set ticket-id)))
        locked-ticket-ids (reduce reduction-fn #{} selected-ticket-ids)]
    {:locked-ticket-ids locked-ticket-ids
     :reservation-timelimit-seconds reservation-timelimit-seconds}))

(defn- make-lock-unbooked-tickets-fn [redis-opts tx]
  (cond
    (nil? redis-opts) (partial select-unbooked-tickets-with-db-lock tx)
    :else (partial select-unbooked-tickets-with-redis-lock redis-opts tx)))

(defn- make-unlock-unbooked-tickets-fn [redis-opts]
  (cond
    (nil? redis-opts) (constantly nil)
    :else (partial redis/release-lock redis-opts)))

(defn- handle-reserve-event
  [db-spec
   redis-opts
   {{:keys [booking-id ticket-ids ticket-type-id quantity]} :data}]
  (let [requested-quantity quantity
        nil-to-zero (fn [x] (if (nil? x) 0 x))
        ticket-quantity (if (nil? ticket-ids)
                          (nil-to-zero requested-quantity)
                          (count ticket-ids))]
    (if (zero? ticket-quantity)
      (booking/update-booking-status db-spec booking-id booking/REJECTED)
      (jdbc/with-transaction [tx db-spec]
        (let [lock-unbooked-tickets (make-lock-unbooked-tickets-fn redis-opts tx)
              release-lock (make-unlock-unbooked-tickets-fn redis-opts)
              {:keys [locked-ticket-ids
                      reservation-timelimit-seconds]}
              (lock-unbooked-tickets ticket-ids ticket-type-id ticket-quantity)
              quantity-available? (and (pos? (count locked-ticket-ids))
                                       (= (count locked-ticket-ids) ticket-quantity))
              booking-status (if quantity-available? booking/PAYMENTPENDING booking/REJECTED)
              current-time (Instant/now)
              reservation-expiration-time (if (nil? reservation-timelimit-seconds)
                                            nil
                                            (.plus current-time
                                                   (Duration/ofSeconds reservation-timelimit-seconds)))]
          (when quantity-available?
            (ticket/reserve-tickets tx locked-ticket-ids booking-id reservation-expiration-time))
          (booking/update-booking-status tx booking-id booking-status)
          (map #(release-lock %) locked-ticket-ids))))))

(defn- handle-book-event [db-spec request]
  (jdbc/with-transaction [tx db-spec]
    (let [booking-id (get-in request [:data :booking-id])
          selected-ticket-ids (->> (ticket/lock-reserved-tickets tx booking-id)
                                   (map #(:ticket/ticket_id %)))]
      (ticket/confirm-tickets tx selected-ticket-ids)
      (booking/update-booking-status tx booking-id booking/CONFIRMED))))

(defn- handle-cancel-event [db-spec request]
  (jdbc/with-transaction [tx db-spec]
    (let [booking-id (get-in request [:data :booking-id])
          selected-ticket-ids (->> (ticket/lock-reserved-tickets tx booking-id)
                                   (map #(:ticket/ticket_id %)))]
      (ticket/cancel-tickets tx selected-ticket-ids)
      (booking/update-booking-status tx booking-id booking/CANCELED))))

(defn- process-ticket-request* [worker-id db-spec redis-opts request]
  (try
    (let [event-type (:event request)]
      (log/debug "Got Message:" request)
      (cond
        (= event-type reserve-event)
        (handle-reserve-event db-spec redis-opts request)
        (= event-type book-event)
        (handle-book-event db-spec request)
        (= event-type cancel-event)
        (handle-cancel-event db-spec request)
        :else (log/error "Worker: Unknown event"))
      :continue)
    (catch Exception e
      (log/error "Exception in Worker: " worker-id " :" e))))

(defn process-ticket-requests [worker-id message-queue db-spec redis-opts exit-ch]
  (async/go-loop []
    (cond
      (= :continue
         (async/alt!
           message-queue (partial process-ticket-request* worker-id db-spec redis-opts)
           exit-ch :exit)) (recur)
      :else nil)))
