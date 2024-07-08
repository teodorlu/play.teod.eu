(ns teod.play.cl-madness)

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

(do
  (defn gen-frl
    "Bring the joy of _cadadddr_ to Clojure, but Clojure-flavoured"
    [frl]
    (assert (symbol? frl))
    (let [frl-str (str frl)]
      (cond (= "first" frl-str) first
            (= "last" frl-str) last)))

  (let [l '(1 2 3 (4 5))]
    (assert (= ((eval 'last) l)
               ((gen-frl 'last) l)))))
