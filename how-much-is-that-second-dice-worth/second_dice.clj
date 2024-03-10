;; # How much is that second dice worth?

(ns second-dice
  {:nextjournal.clerk/toc true}
  (:require
   [nextjournal.clerk :as clerk]))

;; ## Rationale

;; what is the expected value of the best of two dice throws?
;;
;; let's find out with monte-carlo!

;; in which you'll learn:
;;
;; - How to use Monte-Carlo simulation to optimize your Arcane Survivors strategy.
;; - How to implement Monte-Carlo simulation in Clojure.

;; ## To sample uniform data

213

(defprotocol Sample
  (sample [this n]))

(defrecord Uniform [lower upper]
  Sample
  (sample [_this n]
    (repeatedly n #(+ lower (rand (- upper lower))))))

(sample (Uniform. 1 300) 10)

(defrecord Dice [sides]
  Sample
  (sample [_this n]
    (repeatedly n #(inc (rand-int sides)))))

(comment (clerk/clear-cache!))

(defn mean [xs] (float (/ (reduce + xs) (count xs))))

(def d6 (Dice. 6))

(let [d6-sample (sample d6 1000)]
  {:mean (mean d6-sample)
   :min (apply min d6-sample)
   :max (apply max d6-sample)})

(sample d6 10)

(def d20 (Dice. 20))

(sample d20 10)

;; this doesn't look _directly_ wrong.

;; but we want to view histograms, not numbers with lots of decimal places!

;; let's use Vega-Lite.

;; ## Histogram viewer, take 1

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

;; ## but ... buckets???

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
