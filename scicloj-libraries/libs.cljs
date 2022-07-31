(ns libs)

(require '[clojure.edn :as edn]
         '[reagent.core :as r]
         '[ajax.core :refer [GET]])

;; (def data (slurp "https://scicloj.github.io/docs/resources/model.edn"))

(def state (r/atom {
                    :clicks 0
                    :raw-response nil
                    }))

(defn set-raw-response! [response]
  (swap! state assoc :raw-response response))

(defn view [data]
  [:pre (pr-str data)])

(defn table2 [{}]
  [:table [:thead [:th "x"] [:th "y"]]
   [:tbody [:tr [:td 1] [:td 2]]]])

(defn table3 [{:keys [rows header viewers]}]
  [:table [:thead
           (for [h header]
             [:th h])]
   [:tbody [:tr [:td 1] [:td 2]]
    (for [r rows]
      [:tr
       (for [h header]
         (let [view (or (h viewers) identity)]
           [:td (view (h r))]))])]])

(defn table []
  [:div
   [:p "Clacks: " (:clicks @state)]
   [:p [:button {:on-click #(swap! state update :clicks inc)}
        "Click me!"]]
   (when-let [response (:raw-response @state)]
     (let [model (edn/read-string response)]
       [:div
        [view (keys model)]
        [view (first (:tags model))]
        [view (ffirst (:libs model))]
        [table2]
        [table3 {:header [:lib/name :lib/url :lib/category :tags :star]
                 :rows (first (:libs model))
                 :viewers {:star (fn [item]
                                   (when (= item :star)
                                     "⭐"))}
                 }]
        ]))])

(GET "https://scicloj.github.io/docs/resources/model.edn" {:handler set-raw-response!})
