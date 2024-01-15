(ns swift-ticketing.db.user
  (:require [honey.sql :as sql]
            [swift-ticketing.db.query :refer [run-query!]]))

(defn insert-user [db-spec user-id user-name]
  (run-query!
    db-spec
    (sql/format {:insert-into :user-account
                 :columns [:user-id :name]
                 :values[[user-id
                          user-name]]})))

(defn get-user-tickets [db-spec]
  (run-query!
   db-spec
   (sql/format {:select [:u.name [[:array_agg :ticket_name] :ticket_name]]
                :from [[:booking :b]]
                :inner-join [[:user_account :u] [:= :b.user_id :u.user_id]]
                :left-join [[:ticket :t] [:= :b.booking_id :t.booking_id]]
                :where [:= :b.booking_status [:cast "Confirmed" :booking_status]]
                :group-by :u.user_id})))
