(ns buttons
  (:require [clojure.edn :as edn]))

(comment

  (require '[reagent.core :as r]
           '[reagent.dom :as rdom])

  (defn random-page-button [text]
    [:button text])

  (doseq [button (.querySelectorAll js/document "random-page-button")]
    (rdom/render [random-page-button "this is button?"] button)))

(defn goto-random-page []

  (prn "random page!!!"))

(set! (.-goto_random_page js/window) goto-random-page)
