(ns swift-ticketing.db.booking
  (:require [honey.sql :as sql]
            [swift-ticketing.db.query :refer [run-query! run-query-one!]]))

(defonce in-process "InProcess")
(defonce confirmed "Confirmed")
(defonce payment-pending "PaymentPending")
(defonce rejected "Rejected")
(defonce canceled "Canceled")

(defn insert-booking [db-spec uid booking-id booking-status]
  (run-query!
   db-spec
   (sql/format {:insert-into :booking
                :columns [:booking_id :user_id :booking_status]
                :values [[booking-id
                          [:cast uid :uuid]
                          [:cast booking-status :booking_status]]]})))

(defn get-booking [db-spec booking-id]
  (run-query-one!
   db-spec
   (sql/format {:select [:*] :from :booking
                :where [[:= :booking_id [:cast booking-id :uuid]]]})))

(defn get-booking-status [db-spec booking-id]
  (:booking-status (get-booking db-spec booking-id)))

(defn update-booking-status [db-spec booking-id booking-status]
  (run-query!
   db-spec
   (sql/format {:update :booking
                :set {:booking_status [:cast booking-status :booking_status]}
                :where [:= :booking_id [:cast booking-id :uuid]]})))
