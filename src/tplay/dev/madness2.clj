(ns tplay.dev.madness2
  (:require
   [clojure.string :as str]))

(defn penultimate [coll]
  (when (<= 2 (count coll))
    (nth coll (- (count coll) 2))))

(butlast [4 5 6 7])

(defn gen-frl
  "Bring the joy of _cadadddr_ to Clojure, but Clojure-flavoured"
  [frl]
  (assert (symbol? frl))
  (let [frl-str (str frl)
        full {"butlast" butlast "first" first "last" last "penultimate" penultimate "rest" rest "second" second}
        short {\b butlast \f first \l last \p penultimate \r rest \s second}
        ending (first (filter (partial str/ends-with? frl-str) (sort-by (comp - count) (keys full))))]
    (assert (contains? full ending))
    (let [frl-rest (subs frl-str 0 (- (count frl-str) (count ending)))]
      (assert (every? #(contains? short %) frl-rest))
      (reduce comp (full ending) (map short frl-rest)))))

((gen-frl 'rrrrest)
 [1 2 3 4 5])
