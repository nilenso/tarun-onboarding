(ns swift-ticketing.db.booking
  (:require [honey.sql :as sql]))

(defn insert-booking [uid booking-id]
  (sql/format {:insert-into :booking
                 :columns [:booking_id :user_id :booking_status]
                 :values [[booking-id
                           [:cast uid :uuid]
                           [:cast "InProcess" :booking_status]]]}))

(defn get-booking-status [uid booking-id]
  (sql/format {:select [:booking_status] :from :booking
               :where [[:= :booking_id [:cast booking-id :uuid]] 
                       ]}))

(defn update-booking-status [booking-id]
  (sql/format {:update :swift_ticketing.booking
               :set {:booking_status [:cast "Confirmed" :swift_ticketing.booking_status]}
               :where [:= :booking_id [:cast booking-id :uuid]]}))
