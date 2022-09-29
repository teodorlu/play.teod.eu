(ns buttons
  (:require [clojure.edn :as edn]))

(defn goto-random-page []

  (prn "random page!!!"))

(set! (.-goto_random_page js/window) goto-random-page)
