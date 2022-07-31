(ns libs)

(require '[clojure.edn :as edn]
         '[reagent.core :as r]
         '[ajax.core :refer [GET]]
         '[clojure.string :as str])

;; (def data (slurp "https://scicloj.github.io/docs/resources/model.edn"))

(def state (r/atom {
                    :clicks 0
                    :raw-response nil
                    }))

(defn set-raw-response! [response]
  (swap! state assoc :raw-response response))

(defn view [data]
  [:pre (pr-str data)])

(defn view-table [{:keys [rows header viewers]}]
  [:table [:thead
           (for [h header]
             [:th h])]
   [:tbody
    (for [r rows]
      [:tr
       (for [h header]
         (let [view (or (h viewers) identity)]
           [:td (view (h r))]))])]])

(defn scicloj-libs-model->table [model]
  {:header [:lib/name :lib/url #_:lib/category :tags :star]
   :rows (apply concat (:libs model))
   :viewers {:star (fn [item]
                     (when (= item :star)
                       "⭐"))
             :lib/url (fn [url]
                        (when url
                          (let [shortname (str/replace url #"https://github.com/" "")]
                            [:a {:href url} shortname])))}})

(defn table []
  [:div
   #_#_
   [:p "Clacks: " (:clicks @state)]
   [:p [:button {:on-click #(swap! state update :clicks inc)}
        "Click me!"]]
   [:h2 "Plain HTML table"]
   (when-let [response (:raw-response @state)]
     (let [model (edn/read-string response)]
       [:div
        [view-table (scicloj-libs-model->table model)]
        ]))

   [:h2 "Fancy table"]
   [:p "try that data thing."]

   ])

(GET "https://scicloj.github.io/docs/resources/model.edn" {:handler set-raw-response!})
