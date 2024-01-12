(ns swift-ticketing-ui.user
  (:require
   [reagent.core :as r]
   [reagent.cookies :as c]
   [clojure.string :as s]
   [camel-snake-kebab.extras :as cske]
   [camel-snake-kebab.core :as csk]
   [accountant.core :as accountant]
   [swift-ticketing-ui.client :as client]
   [swift-ticketing-ui.theme :as theme]))

(defn login-page []
  (let [user-name (r/atom "")]
    (fn []
      @user-name
      (let [handler (fn [[ok response]]
                      (if ok
                        (do
                          (c/set! "uid"
                                  (:user_id response)
                                  {:path "/"
                                   :raw? true})
                          (accountant/navigate! "/event"))
                        (js/alert "Login Failed")))]
        [:div {:class "flex min-h-full flex-1 flex-col justify-center py-12 sm:px-6 lg:px-8"}
         [:div {:class "mt-10 sm:mx-auto sm:w-full sm:max-w-[400px]"}
          [:div {:class "bg-white px-6 py-12 shadow sm:rounded-lg sm:px-12"}
           [:h3
            {:class "mb-4 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900"}
            "Enter your name"]

           [:input
            {:type "text"
             :class theme/input-class
             :id "user-name"
             :value @user-name
             :on-change #(reset! user-name (-> % .-target .-value))}]

           [:div {:class "mt-10 flex flex-row-reverse gap-2"}
            [:button {:type "button"
                      :class theme/primary-button-class
                      :disabled (if (s/blank? @user-name) true false)
                      :on-click #(client/http-post
                                  "/user"
                                  handler
                                  {:name @user-name})} "Login"]]]]]))))

(defn display-user-stats [stats]
  (let [cell (fn [content]
               [:td {:class "whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-0"}
                content])
        header (fn [content]
                 [:th {:scope "col"
                       :class "py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-0"}
                  content])
        render-row
        (fn [stat]
          (js/console.log stat)
          [:tr
           (cell (:name stat))
           (cell (clojure.string/join ", " (:ticket-name stat)))])]
    [:div
     [:table {:class "min-w-full divide-y divide-gray-300"}
      [:thead
       [:tr
        (header "Name")]
       (header "Tickets")]
      [:tbody {:class "divide-y divide-gray-200"}
       (for [stat stats]
         ^{:key (get stat "name")} (render-row stat))]]]))

(defn stats-page []
  (let [loading (r/atom true)
        stats (r/atom nil)
        booking-id (r/atom nil)]
    (r/create-class
     {:display-name "stats-page"
      :component-did-mount
      (fn []
        (let [handler (fn [[ok response]]
                        (if ok
                          (do
                            (reset! loading false)
                            (reset! stats
                                    (cske/transform-keys csk/->kebab-case-keyword
                                                         response)))
                          (js/alert "Something went wrong while fetching stats!")))]
          (client/http-get "/user/stats" handler)))
      :reagent-render (fn []
                        (if @loading
                          [:div "Loading..."]
                          [display-user-stats @stats]))})))
