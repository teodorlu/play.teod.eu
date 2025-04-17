;; # Estimating π with dtype-next and Clojure vectors

;; In Clojure, we often reach for sequences of maps to store data.
;; This approach works well for small datasets - we say within Clojure happy-land, and can always inspect our data.
;; Just print it, it's data.
;;
;; At some point, computing with sequences of maps feels worse: we start having to wait.
;; In those cases, we might consider storing our data column oriented, and practicing [array programming].
;; The Clojure data science toolbox gives us some great tools here.
;; `dtype-next` provides high-performance containers, and `scicloj/tablecloth` provides a nice high-level interface.
;;
;; [array programming]: http://www.appliedscience.studio/articles/array-programming-for-clojurists.html

;; We will explore these two approaches by estimating Pi with monte carlo simulation.
;; Why?
;; Not because it's the fastest way to compute π digits!
;;
;; > These Monte Carlo methods for approximating π are very slow compared to
;; > other methods, and do not provide any information on the exact number of
;; > digits that are obtained. Thus they are never used to approximate π when
;; > speed or accuracy is desired.[142]
;; > https://en.wikipedia.org/wiki/Pi#Monte_Carlo_methods
;;
;; So, why do we want to compute π with Monte Carlo simulation?
;;
;; 1. It's a well defined problem: we're looking for an answer close to π.
;; 2. We can get closer to π by throwing more computational power on the problem.
;;
;; ... which gives us a great starting point to compare dtype-next and Clojure vectors.

(ns estimating-pi
  {:nextjournal.clerk/toc true}
  (:require
   [clojure.math]
   [nextjournal.clerk :as clerk]))

;; ## Estimating π with Clojure vectors

;; We will estimate π by estimating the area of a circle with radius of 1, the unit circle.

;; We can tell if a point is inside the unit circle by calculating the point's distance from the origin, and compare that distance to 1.

(defn inside-unit-circle? [[x y]]
  (< (clojure.math/sqrt (+ (* x x) (* y y))) 1))

(inside-unit-circle? [0.5 0.5])
(inside-unit-circle? [0.9 0.9])

;; Now, we can estimate by by estimating the area of the unit circle.

;; And we estimate that area by estimating the difference between the unit circle area and the area of the square just outside the unit circle.
;; The area of the square just outside the unit circle is 4.

(defn rand-between [min max]
  (+ min (* (rand) (- max min))))

(defn estimate-unit-circle-area [n-samples]
  (let [samples (repeatedly n-samples
                            (fn []
                              [(rand-between -1 1)
                               (rand-between -1 1)]))]
    (double
     (* 4 (/ (count (filter inside-unit-circle? samples))
             n-samples)))))

;; So, how do we get our pi-estimate?

(clerk/tex "A_c = \\pi r^2")

;; The unit circle has radius 1.
;; This gives our formula for pi:

(clerk/tex "\\pi = A_c")

;; Let's see what we get for 10 samples.

(estimate-unit-circle-area 10)

;; Not exactly Pi.
;; 😬
;; We're looking for this number:

clojure.math/PI

;; Let's try gradually increasing our sample size

(->> [10 100 1000 10000 100000 1000000]
     (map (fn [n]
            {(clerk/tex "n") n
             (clerk/tex "\\pi") (estimate-unit-circle-area n)}))
     clerk/table
     (clerk/caption "Estimating Pi with increasing number of samples"))

;; ## Estimating Pi with dtype-next
