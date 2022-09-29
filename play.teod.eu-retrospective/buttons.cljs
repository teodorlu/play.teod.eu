(ns buttons)

(require '[reagent.core :as r]
         '[reagent.dom :as rdom])

(defn random-page-button [text]
  [:button text])

(doseq [button (.querySelectorAll js/document "random-page-button")]
  (rdom/render [random-page-button "this is button?"] button))
