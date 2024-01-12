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
