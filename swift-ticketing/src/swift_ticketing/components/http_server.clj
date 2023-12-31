(ns swift-ticketing.components.http-server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]
            [swift-ticketing.app :as app]
            [taoensso.timbre :as log]))

(defrecord HTTPServer [port join? database worker]
  component/Lifecycle

  (start [component]
    (log/info "Starting API Server")
    (let [connection (:connection database)
          message-queue (:message-queue worker)
          server (run-jetty
                  (app/swift-ticketing-app connection message-queue)
                  {:port port
                   :join? join?})]
      (assoc component :http-server server)))

  (stop [component]
    (log/info "Stopping API Server")
    (.stop (:http-server component))
    (assoc component :http-server nil)))

(defn new-http-server [port join?]
  (map->HTTPServer {:port port
                    :join? join?}))
