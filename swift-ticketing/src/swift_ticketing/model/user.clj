(ns swift-ticketing.model.user
  (:require [swift-ticketing.db.user :as db-user]))

(defn create-user [db-spec user-id {:keys [name]}]
  (db-user/insert-user db-spec user-id name))

(defn get-user-stats [db-spec]
  (db-user/get-user-tickets db-spec))
