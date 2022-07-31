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

(defn view-table
  [{:keys [rows header viewers derived-rows id]}]
  (let [prep-row (fn [row]
                   (reduce (fn [row [row-id create-derived]]
                             (assoc row row-id (create-derived row)))
                           row
                           derived-rows))]
    [:table {:id id}
     [:thead
      (for [h header]
        [:th h])]
     [:tbody
      (for [r rows]
        (let [r (prep-row r)]
          [:tr
           (for [h header]
             (let [view (or (h viewers) identity)]
               [:td (view (h r))]))]))]]))

(defn scicloj-libs-model->table [model]
  {:header [:library :star :description #_#_ :lib/name :lib/url #_:lib/category :tags]
   :rows (apply concat (:libs model))
   :viewers {:star (fn [item]
                     (when (= item :star)
                       "⭐"))
             :lib/url (fn [url]
                        (when url
                          (let [shortname (str/replace url #"https://github.com/" "")]
                            [:a {:href url} shortname])))}
   :derived-rows {:library (fn [row]
                           [:a {:href (:lib/url row)} (:lib/name row)])}})

(defn table []
  [:div
   #_#_
   [:p "Clacks: " (:clicks @state)]
   [:p [:button {:on-click #(swap! state update :clicks inc)}
        "Click me!"]]
   (when-let [response (:raw-response @state)]
     (let [model (edn/read-string response)]
       [:div
        [:h2 "What data do we have?"]
        [view (keys (ffirst (:libs model)))]
        [:p "Slight problem: description contains markdown."
         #_" So we should really try to render that properly."]

        [:h2 "Plain HTML table"]
        [view-table (merge {:id "table1"}
                           (scicloj-libs-model->table model))]

        [:h2 "Fancy table"]
        [:p "try that data thing."]]))])

(GET "https://scicloj.github.io/docs/resources/model.edn" {:handler set-raw-response!})
