(ns teod.play.dev.cl-madness
  (:require
    [clojure.string :as str]))

(defmacro gen-c*r [c*r]
  (let [c*r-str (str c*r)]
    (assert #{"c"} (first c*r-str))
    (assert #{"r"} (last c*r-str))
    (assert (<= 3 (count c*r-str)))
    (let [ad-str (subs (str c*r) 1 (dec (count c*r-str)))]
      (assert (every? #{\a \d} ad-str))
      `(comp ~@(map {\a first \d rest} ad-str)))))

((gen-c*r cadadddr)
 '(1 2 3 (4 5)))
;; => 5

(every? #{\a \d} (seq "daddd"))

(def lflast (comp last first last))
(lflast '(junk ((ignore 5) garbage)))
;; => 5

(defn gen-frl
  "Bring the joy of _cadadddr_ to Clojure, but Clojure-flavoured"
  [frl]
  (assert (symbol? frl))
  (let [frl-str (str frl)
        full {"first" first "last" last "rest" rest "butlast" butlast}
        short {\f first \l last \r rest \b butlast}
        ending (cond (str/ends-with? frl-str "butlast") "butlast"
                     (str/ends-with? frl-str "first") "first"
                     (str/ends-with? frl-str "last") "last"
                     (str/ends-with? frl-str "rest") "rest")]
    (assert (contains? full ending))
    (let [frl-rest (subs frl-str 0 (- (count frl-str) (count ending)))]
      (println frl-rest)
      (assert (every? #(contains? short %) frl-rest))
      (reduce comp (full ending) (map short frl-rest)))))

((gen-frl 'bbbutlast)
 '(1 2 3 (4 ((5)))))
;; => (1)

((gen-frl 'llast)
 '(1 2 3 (4 5)))
;; => 5

((gen-frl 'llllast)
 '(1 2 3 (4 ((5)))))
;; => 5


(let [l '(1 2 3 (4 5))]
  (assert (= ((eval 'last) l)
             ((gen-frl 'last) l))))

(comp ~@(map {\a first \d rest} "ad"))
