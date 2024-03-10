(ns second-dice
  (:require
   [nextjournal.clerk :as clerk]))

;; what is the expected value of the best of two dice throws?
;;
;; let's find out with monte-carlo!

;; in which you'll learn:
;;
;; - How Monte-Carlo simulation can help you win in Arcane Survivors
;; - How to implement Monte-Carlo simulation in Clojure.

213

(defprotocol Sample
  (sample [this n]))

(defrecord Uniform [lower upper]
  Sample
  (sample [_this n]
    (repeatedly n #(+ lower (rand (- upper lower))))))

(sample (Uniform. 1 300) 10)

;; this doesn't look _directly_ wrong.

;; but we want to view histograms, not numbers with lots of decimal places!

;; let's use Vega-Lite.

(clerk/caption "Looks like a histogram?"
 (clerk/vl
  {:width 500
   :height 300
   :data {:values [{"a" "A", "b" 28}, {"a" "B", "b" 55}, {"a" "C", "b" 43},
                   {"a" "D", "b" 91}, {"a" "E", "b" 81}, {"a" "F", "b" 53},
                   {"a" "G", "b" 19}, {"a" "H", "b" 87}, {"a" "I", "b" 52}]},
   :mark :bar,
   :encoding {:x {:field "a", :type "nominal", "axis" {:labelAngle 0}},
              :y {:field "b", :type "quantitative"}}}))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
