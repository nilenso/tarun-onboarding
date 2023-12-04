(ns swift-ticketing.specs
  (:require [clojure.spec.alpha :as s]))

(defn date? [date]
  (let [date-regex #"\d{4}-\d{2}-\d{2}"]
    (boolean 
      (and (string? date) (re-matches date-regex date)))))

(defn uuid? [x]
  (try
    (java.util.UUID/fromString x)
    (catch Exception e false)))

(defn nilable [pred]
  (fn [x] (or (nil? x) (pred x))))

(s/def ::venue (nilable string?))
(s/def ::to (nilable date?))
(s/def ::from (nilable date?))

(s/def ::name string?)
(s/def ::description string?)
(s/def ::date date?)

(s/def ::event-id uuid?)
(s/def ::booking-id uuid?)
(s/def ::ticket-type-id uuid?)

(s/def ::ticket_type string?)
(s/def ::seat_type string?)
(s/def ::ticket_type_id uuid?)
(s/def ::quantity int?)
(s/def ::price int?)
(s/def ::reservation_limit_in_seconds int?)
(s/def ::seats vector?)
(s/def ::ticket_ids vector?)

(s/def ::get-event-params
  (s/keys :opt-un [::venue ::to ::from]))

(s/def ::create-event-params
  (s/keys :req-un [::name ::description ::date ::venue]))

(s/def ::create-tickets-params
  (s/keys :req-un [(or (and ::ticket_type ::seat_type ::description ::quantity ::price)
                         (and ::ticket_type ::seat_type ::description ::seats ::reservation_limit_in_seconds ::price))]))

(s/def ::reserve-tickets-params
  (s/keys :req-un [(or (and ::quantity ::ticket_type_id) ::ticket_ids)]))

; (s/valid? ::create-event-params {:name "a"})
