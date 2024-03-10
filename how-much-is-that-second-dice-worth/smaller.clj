(ns smaller)

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
