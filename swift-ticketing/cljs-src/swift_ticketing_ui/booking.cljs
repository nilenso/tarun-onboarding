(ns swift-ticketing-ui.booking
  (:require
   [ajax.core :as ajax]
   [reagent.core :as r]
   [clojure.math :as math]
   [accountant.core :as accountant]
   [swift-ticketing-ui.config :refer [API_URL]]
   [camel-snake-kebab.extras :as cske]
   [camel-snake-kebab.core :as csk]))

(defn get-url [url handler]
  (ajax/ajax-request
   {:uri url
    :method :get
    :handler handler
    :format (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}))

(defn get-booking-status [url handler]
  (get-url url handler))

(defn get-booking-tickets [url handler]
  (get-url url handler))

(defn booking-page [booking-id]
  (let [loading (r/atom true)
        booking-status (r/atom nil)
        handler (fn [[ok response]]
                  (if ok
                    (do
                      (reset! loading false)
                      (reset! booking-status
                              (:booking-status (cske/transform-keys csk/->kebab-case-keyword response))))
                    (do (js/console.log "Response else: ", response)
                        (reset! loading false))))]
    (fn []
      @booking-status
      (do
        (when (or (= "InProcess" @booking-status)
                  (= "PaymentPending" @booking-status)
                  (nil? @booking-status))
          (js/setTimeout
           (fn []
             (get-booking-status
              (str API_URL "/booking/" booking-id "/status")
              handler))
           3000))

(defn display-time [time]
  (let [seconds (math/floor (/ time 1000))
        minutes (math/floor (/ seconds 60))]
    (str (mod minutes 60) ":"
         (mod seconds 60))))

(defn payment-page [booking-id]
  (let [tickets (r/atom nil)
        remaining-time (r/atom nil)]
    (r/create-class
     {:display-name "payment-page"
      :component-did-mount
      (fn []
        (let [handler (fn [[ok response]]
                        (if (and ok (pos? (count response)))
                          (do
                            (reset! tickets
                                    (cske/transform-keys csk/->kebab-case-keyword
                                                         response))
                            (js/setInterval
                             #(reset! remaining-time
                                      (- (js/Date. (:reservation-expiration-time
                                                    (first @tickets)))
                                         (js/Date.)))
                             1000))
                          (accountant/navigate! (str "/booking/" booking-id))))]
          (get-booking-tickets (str API_URL "/booking/" booking-id "/ticket") handler)))
      :reagent-render
      (fn []
        @remaining-time
        (let [handler (fn [[ok _]]
                        (if ok
                          (accountant/navigate! (str "/booking/" booking-id))
                          (js/alert "Payment Failed")))]
          (cond
            (nil? @tickets) [:span "Loading..."]
            :else [:div {:class "flex min-h-full flex-1 flex-col justify-center py-12 sm:px-6 lg:px-8"}
                   [:div {:class "mt-10 sm:mx-auto sm:w-full sm:max-w-[480px]"}
                    (cond
                      (neg? @remaining-time)
                      [:div {:class "bg-white px-6 py-12 shadow sm:rounded-lg sm:px-12"}
                       [:strong "Reservation Expired. Please try booking again"]]
                      :else
                      [:div {:class "bg-white px-6 py-12 shadow sm:rounded-lg sm:px-12"}
                       [:div.py-4.grid.grid-cols-2
                        [:div.text-gray-400 "Time remaining: "]
                        [:div.text-right (if (nil? @remaining-time)
                                           ""
                                           (display-time @remaining-time))]]
                       [:div {:class "space-y-6 text-sm text-gray-400"}
                        (str "Booking #" booking-id)]
                       [:div.py-4.grid.grid-cols-2
                        [:div "Tickets: "]
                        [:div.text-right (count @tickets)]
                        [:div "Price: "]
                        [:div.text-right
                         (reduce + (map :ticket-price @tickets))]]
                       [:div {:class "mt-10 flex flex-row-reverse gap-2"}
                        [:button {:class theme/primary-button-class
                                  :on-click #(client/http-post
                                              (str "/booking/" booking-id "/payment")
                                              handler
                                              {})} "Make Payment"]
                        [:button {:class theme/danger-button-class
                                  :on-click #(client/http-post
                                              (str "/booking/" booking-id "/cancel")
                                              handler
                                              {})} "Cancel Booking"]]])]])))})))

