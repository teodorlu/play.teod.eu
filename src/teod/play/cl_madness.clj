(ns teod.play.cl-madness)

(comment
  (def car first)
  (def cdr rest)

  (def caar (comp car car))
  (def cadr (comp car cdr))
  (def cdar (comp cdr car))
  (def cddr (comp cdr cdr))

  (def caaar (comp car car car))
  (def cadar (comp car cdr car))
  (def caddr (comp car cdr cdr))

  (def cdaar (comp cdr car car))
  (def cdadr (comp cdr car cdr)) ; CDADR!
  (def cddar (comp cdr cdr car)) ; nesten CHEDDAR

  (doseq [[sym var] (ns-publics *ns*)]
    (intern 'clojure.core sym var))

  (clojure.core/cddar '((1 2 3)))
  ;; => (3)
  )

(defmacro genc*r [c*r]
  (let [c*r-str (str c*r)]
    (assert #{"c"} (first c*r-str))
    (assert #{"r"} (last c*r-str))
    (assert (<= 3 (count c*r-str)))
    (let [ad-str (subs (str c*r) 1 (dec (count c*r-str)))]
      (assert (every? #{\a \d} ad-str))
      `(comp ~@(map {\a first \d rest} ad-str)))))

((genc*r cadadddr)
 '(1 2 3 (4 5)))
;; => 5

(count 'x)

(let [s "car"]
  (subs s 1 (dec (count s))))

((genc*r 'cdr) '(1 2 3))

;; => (1 2 3)

(macroexpand-1 '(genc*r 'cddr))


((genc*r 'cddar) '((1 2 3)))

(let [as-and-ds 'a]
  (every? {\a \d} (str as-and-ds)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(every? #{\a \d} (seq "daddd"))

(def lflast (comp last first last))
(lflast '(junk ((ignore 5) garbage)))
;; => 5
