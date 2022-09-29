(ns buttons)

(require '[reagent.core :as r]
         '[reagent.dom :as rdom])

#_
(defn random-page-button [text]
  [:button text])

#_
(doseq [button (.querySelectorAll js/document "random-page-button")]
  (rdom/render [random-page-button "this is button?"] button))

(defn goto-random-page []
  (prn "random page!!!"))

(set! (.-goto_random_page js/window) goto-random-page)
