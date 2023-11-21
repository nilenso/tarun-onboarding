(ns swift-ticketing.handlers
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            ; [ring.util.response :as response]
            [swift-ticketing.db.event :as event]
            [swift-ticketing.db.ticket :as ticket]
            [swift-ticketing.db.booking :as booking]
            [swift-ticketing.worker :as worker]))

(defn get-events [db-spec venue from to]
  (let [events (jdbc/execute! db-spec (event/get-events venue from to) {:builder-fn rs/as-unqualified-maps})]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body events}))

(defn get-event [db-spec event-id]
  (let [event (jdbc/execute! db-spec (event/get-event event-id) {:builder-fn rs/as-unqualified-maps})]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body event}))

(defn create-event [db-spec uid event-req]
  (let [event-id (java.util.UUID/randomUUID)]
    (jdbc/execute! db-spec (event/insert-event uid event-id event-req))
    {:status 201
     :headers {"Content-Type" "application/json"}
     :body {"event_id" event-id}}))

(defn create-tickets [db-spec uid event-id ticket-req]
  (let [seat-type (:seat_type ticket-req)
        tickets-map (if (= seat-type ticket/NAMED)
                      (:seats ticket-req)
                      (map (fn [_] {:name ""}) (range (:quantity ticket-req))))
        price (:price ticket-req)
        ticket-type-id (java.util.UUID/randomUUID)
        tickets
        (map (fn [m] (assoc m :ticket-id (java.util.UUID/randomUUID))) tickets-map)]
    (jdbc/execute! db-spec (ticket/insert-ticket-type event-id ticket-type-id ticket-req))
    (jdbc/execute! db-spec (ticket/insert-tickets ticket-type-id tickets price))
    {:status 201
     :headers {"Content-Type" "application/json"}
     :body {"tickets" tickets}}))

(defn book-ticket [db-spec uid event-id booking-req]
  (let [booking-id (java.util.UUID/randomUUID)
        ticket-id (:ticket-id booking-req)]
    (jdbc/execute! db-spec (booking/insert-booking uid booking-id))
    (worker/add-reserve-ticket-request-to-queue {:booking-id booking-id
                                                 :ticket-type-id (:ticket_type_id booking-req)
                                                 :ticket-ids (map #(java.util.UUID/fromString %)
                                                                  (:ticket_ids booking-req))
                                                 :quantity (:quantity booking-req)})
    ; (worker/add-ticket-request-to-queue event-id {:booking-id booking-id
    ;                                               :ticket-type (:ticket_name booking-req)
    ;                                               :quantity quantity})
    {:status 201
     :headers {"Content-Type" "application/json"}
     :body {"booking_id" booking-id}}))

(defn make-payment [db-spec booking-id]
  ((worker/add-book-ticket-request-to-queue {:booking-id booking-id})
   {:status 201
    :headers {"Content-Type" "application/json"}
    :body {"booking_id" booking-id}}))

(defn get-booking-status [db-spec uid booking-id]
  (let [result (:booking/booking_status (jdbc/execute-one! db-spec (booking/get-booking-status uid booking-id)))]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {"booking_status" result}}))

(defn get-tickets [db-spec ticket-type-id]
  (let [tickets (jdbc/execute! db-spec (ticket/get-unbooked-tickets ticket-type-id) {:builder-fn rs/as-unqualified-maps})]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body tickets}))
