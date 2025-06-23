(ns other
  (:require [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer]))

^{::clerk/visibility {:code :hide :result :hide}}
(def clojure-data
  {:hello "world 👋"
   :tacos (map #(repeat % '🌮) (range 1 30))
   :zeta "The\npurpose\nof\nvisualization\nis\ninsight,\nnot\npictures."})

;; I'm aware that I can use ::clerk/auto-expand-results to auto-expand top-level forms.
^{::clerk/auto-expand-results? true}
clojure-data

;; However, I can't figure out how to use ::clerk/auto-expand-results when I'm not toplevel.

[:vector :of :keywords :and :clojure :data
 ^{::clerk/auto-expand-results? true}
 clojure-data]

;; Any tips? Is this possible?
