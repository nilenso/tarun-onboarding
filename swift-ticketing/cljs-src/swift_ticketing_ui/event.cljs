(ns swift-ticketing-ui.event
  (:require
   [ajax.core :as ajax]
   [reagent.core :as r]
   [secretary.core :as secretary]
   [accountant.core :as accountant]
   [camel-snake-kebab.extras :as cske]
   [camel-snake-kebab.core :as csk]))

(defn post-event [url handler event-info]
  (let [{:keys [name description date venue]} event-info]
    (ajax/ajax-request
     {:uri url
      :method :post
      :params {:name name
               :description description
               :date date
               :venue venue}
      :handler handler
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})})))

(defn event-form []
  (let [event-state (r/atom {:name ""
                             :description ""
                             :date ""
                             :venue ""})
        button-class (str "rounded-md border border-transparent bg-indigo-600 "
                          "px-4 py-2 text-base font-medium text-white shadow-sm "
                          "hover:bg-indigo-700 focus:outline-none focus:ring-2 "
                          "focus:ring-indigo-500 focus:ring-offset-2")
        handler (fn [[ok response]]
                  (if ok
                    (do
                      (accountant/navigate! "/event"))
                    (js/alert "Couldn't create the Event")))]
    (fn []
      [:div
       [:h2 "Enter Event Details"]
       [:form
        {:on-submit
         (fn [e]
           (post-event "http://127.0.0.1:9090/event" handler @event-state)
           (.preventDefault e))}
        [:label {:for "event-name"} "Event Name"]
        [:input
         {:type "text"
          :id "event-name"
          :value (:name @event-state)
          :on-change #(swap! event-state assoc :name (-> % .-target .-value))}]

        [:label {:for "event-description"} "Description"]
        [:input
         {:type "text"
          :id "event-description"
          :value (:description @event-state)
          :on-change #(swap! event-state assoc :description (-> % .-target .-value))}]

        [:label {:for "event-date"} "Event Date"]
        [:input
         {:type "date"
          :id "event-date"
          :value (:date @event-state)
          :on-change #(swap! event-state assoc :date (-> % .-target .-value))}]

        [:label {:for "event-location"} "Event Location"]
        [:input
         {:type "text"
          :id "event-location"
          :value (:venue @event-state)
          :on-change #(swap! event-state assoc :venue (-> % .-target .-value))}]

        [:button {:type "submit" :class button-class} "Submit"]]])))

(defn get-events [url handler]
  (ajax/ajax-request
   {:uri url
    :method :get
    :handler handler
    :format (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}))

(defn event-card [event]
  [:a {:href "#" :class "group" :on-click (fn [] (accountant/navigate! (str "/event/" (:event-id event))))}
   [:div {:class "aspect-h-1 aspect-w-1 w-full overflow-hidden rounded-lg sm:aspect-h-3 sm:aspect-w-2"}
    [:img {:src "https://images.pexels.com/photos/976866/pexels-photo-976866.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2" :class "h-full w-full object-cover object-center group-hover:opacity-75" :alt "Product"}]]
   [:div {:class "mt-4 flex items-center justify-between text-base font-medium text-gray-900"}
    [:h3 (:event-name event)]
    [:p (:event-date event)]]
   [:p {:class "mt-1 text-sm italic text-gray-500"} (:event-description event)]])

(defn events-list [events]
  [:div#events-page
   [:div {:class "py-24 text-center"}
    [:h1 {:class "text-4xl font-bold tracking-tight text-gray-900"} "Events"]
    [:p {:class "mx-auto mt-4 max-w-3xl text-base text-gray-500"} "Book Events"]
    [:button {:class "float-right rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-base font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
              :on-click #(accountant/navigate! (str "/event/create"))} "Create Event"]]
   [:section.mt-8
    [:h2#products-heading.sr-only "Events"]
    [:div {:class "grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-3 xl:gap-x-8"}
     (for [event events]
       ^{:key (:event-id event)} [event-card event])]]])

(defn events-page []
  (let [loading (r/atom true)
        events (r/atom [])]
    (r/create-class
     {:display-name "events-page"
      :component-did-mount
      (fn []
        (let [handler (fn [[ok response]]
                        (if ok
                          (do
                            (reset! loading false)
                            (reset! events
                                    (cske/transform-keys csk/->kebab-case-keyword response)))
                          (do (js/console.log "Response else: ", response)
                              (reset! loading false))))]
          (get-events "http://127.0.0.1:9090/event" handler)))
      :reagent-render (fn []
                        (if @loading
                          [:div "Loading..."]
                          [events-list @events]))})))

(defn get-event [url handler]
  (ajax/ajax-request
   {:uri url
    :method :get
    :handler handler
    :format (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}))

(defn post-booking [url handler quantity ticket-type-id]
  (ajax/ajax-request
   {:uri url
    :method :post
    :params {:quantity quantity
             :ticket_type_id ticket-type-id}
    :handler handler
    :format (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}))

(defn ticket-card [ticket booking-id]
  (let [selected-quantity (r/atom (min (:ticket-count ticket) 1))
        booking-handler (fn [[ok response]]
                          (if ok
                            (do
                              (accountant/navigate! (str "/booking/payment/" (:booking-id (cske/transform-keys csk/->kebab-case-keyword response)))))
                            (println "Booking Failed")))
        generalTicket? (= "General" (:seat-type ticket))
        button-class (str "rounded-md border border-transparent bg-indigo-600 "
                          "px-4 py-2 text-base font-medium text-white shadow-sm "
                          "hover:bg-indigo-700 focus:outline-none focus:ring-2 "
                          "focus:ring-indigo-500 focus:ring-offset-2")
        quantity-selector-class (str "max-w-full rounded-md border border-gray-300 "
                                     "py-1.5 text-left text-base font-medium leading-5 "
                                     "text-gray-700 shadow-sm focus:border-indigo-500 "
                                     "focus:outline-none focus:ring-1 focus:ring-indigo-500 sm:text-sm")
        general-ticket-booking-button [:button {:class button-class
                                                :on-click #(post-booking
                                                            (str "http://127.0.0.1:9090/event/" (:event-id ticket) "/booking")
                                                            booking-handler
                                                            @selected-quantity
                                                            (:ticket-type-id ticket))}
                                       "Buy Tickets"]
        select-seats-button [:button {:class button-class
                                      :on-click (fn [] (accountant/navigate! (str "/event/" (:event-id ticket) "/ticket/" (:ticket-type-id ticket))))}
                             "Select Seats"]
        quantity-selector [:select {:class quantity-selector-class
                                    :on-change #(reset! selected-quantity (-> % .-target .-value js/parseInt))}
                           (for [i (range (:ticket-count ticket))]
                             ^{:key (:value i)} [:option {:value (inc i)} (inc i)])]]
    ^{:key (:ticket-type ticket)}
    (if (zero? (:ticket-count ticket))
      [:p "Sold Out!!"]
      [:li {:class "flex py-6 sm:py-10"}
       [:div {:class "flex-shrink-0"}
        [:img {:class "h-20 w-20 rounded-md object-cover object-center sm:h-40 sm:w-40"
               :src "https://images.pexels.com/photos/5863541/pexels-photo-5863541.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"}]]
       [:div {:class "ml-4 flex flex-1 flex-col justify-between sm:ml-6"}
        [:div {:class "relative pr-9 sm:grid sm:grid-cols-2 sm:gap-x-6 sm:pr-0"}
         [:div
          [:div {:class "font-medium text-gray-700 hover:text-gray-800"} (:ticket-type ticket)]
          [:div {:class "text-gray-500"} (:ticket-description ticket)]]
         [:div {:class "mt-4 sm:mt-0 sm:pr-9 text-right"}
          [:p {:class "mt-1 mb-4 font-medium text-gray-900"} (str "₹" (:ticket-price ticket))]
          (when generalTicket?
            quantity-selector)]]
        [:div {:class "flex items-center justify-end sm:pr-9"}
         (if generalTicket?
           general-ticket-booking-button
           select-seats-button)]]])))

(defn ticket-info [tickets booking-id]
  [:div.grid-cols-2
   [:div {:class "mx-auto max-w-2xl px-4 pb-24 pt-16 sm:px-6 lg:max-w-7xl lg:px-8"}
    [:button {:class "float-right rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-base font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
              :on-click #(accountant/navigate! (str "/event/" (:event-id (first tickets)) "/ticket/create"))} "Create Tickets"]
    [:h1 {:class "text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl"} (:event-name (first tickets))]
    [:p {:class "py-4 text-gray-500"} (:event-description (first tickets))]
    [:section {:class "lg:col-span-7"}
     [:ul {:role "list" :class "divide-y divide-gray-200 border-b border-t border-gray-200"}
      (for [ticket tickets]
        ^{:key (:ticket-type-id ticket)} (ticket-card ticket booking-id))]]]])

(defn event-page [event-id]
  (let [loading (r/atom true)
        event (r/atom nil)
        booking-id (r/atom nil)]
    (r/create-class
     {:display-name "event-pagew"
      :component-did-mount
      (fn []
        (let [handler (fn [[ok response]]
                        (if ok
                          (do
                            (reset! loading false)
                            (reset! event
                                    (cske/transform-keys csk/->kebab-case-keyword response)))
                          (reset! loading false)))]
          (get-event (str "http://127.0.0.1:9090/event/" event-id) handler)))
      :reagent-render (fn []
                        (if @loading
                          [:div "Loading..."]
                          [ticket-info @event booking-id]))})))

