(ns swift-ticketing.components.db
  (:require [com.stuartsierra.component :as component]
            [next.jdbc.connection :as connection]
            [taoensso.timbre :as log])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn create-connection-pool ^HikariDataSource [database-config]
  (connection/->pool com.zaxxer.hikari.HikariDataSource database-config))

(defrecord Database [db-config]
  component/Lifecycle

  (start [component]
    (log/info "Init db connection pool")
    (let [^HikariDataSource ds (create-connection-pool (into {} db-config))]
      (assoc component :connection ds)))

  (stop [component]
    (log/info "Stopping db connection pool")
    (.close (:connection component))
    (assoc component :connection nil)))

(defn new-database [db-config]
  (map->Database {:db-config db-config}))
