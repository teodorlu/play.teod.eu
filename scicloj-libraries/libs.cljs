(ns libs
  (:require
    [clojure.edn :as edn]))

(require '[reagent.core :as r]
         '[reagent.dom :as rdom]
         '[ajax.core :refer [GET]]
         '[clojure.edn :as edn]
         )

;; (def data (slurp "https://scicloj.github.io/docs/resources/model.edn"))

(def state (r/atom {
                    :clicks 0
                    :raw-response nil
                    }))

(defn set-raw-response! [response]
  (swap! state assoc :raw-response response))


(defn table []
  [:div
   [:p "Clacks: " (:clicks @state)]
   [:p [:button {:on-click #(swap! state update :clicks inc)}
        "Click me!"]]
   (when-let [response (:raw-response @state)]
     (let [model (edn/read-string response)]
       [:div
        [:pre (str "hello. Response length: " (count response))]
        [:pre (pr-str (keys model))]
        [:pre (pr-str (:teodorlu/questions model))]]))])

(GET "https://scicloj.github.io/docs/resources/model.edn" {:handler set-raw-response!})
