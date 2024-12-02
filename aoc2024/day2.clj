(ns day2
  (:require [nextjournal.clerk :as clerk]))

;; # Advent of Code: Day 2

(ns day2
  {:nextjournal.clerk/toc true}
  (:require [clojure.string :as str]
            [nextjournal.clerk :as clerk]))

;; ## Part 1

(def example-input
  "
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
")


(defn parse [s]
  (->> (str/trim s)
       (str/split-lines)
       (map #(str/split % #"\s+"))
       (map (partial map parse-long))))

(parse example-input)


;; > The levels are either all increasing or all decreasing.
;; > Any two adjacent levels differ by at least one and at most three.
;; >
;; > In the example above, the reports can be found safe or unsafe by checking those rules:
;; >
;; > - `7 6 4 2 1`: Safe because the levels are all decreasing by 1 or 2.
;; > - `1 2 7 8 9`: Unsafe because 2 7 is an increase of 5.
;; > - `9 7 6 2 1`: Unsafe because 6 2 is a decrease of 4.
;; > - `1 3 2 4 5`: Unsafe because 1 3 is increasing but 3 2 is decreasing.
;; > - `8 6 4 4 1`: Unsafe because 4 4 is neither an increase or a decrease.
;; > - `1 3 6 7 9`: Safe because the levels are all increasing by 1, 2, or 3.

(defn all-increasing? [numbers]
  (= numbers (sort numbers)))

(assert (all-increasing? (sort [1 2 7 8 9])))
(assert (not (all-increasing? [1 2 5 2])))

(defn all-decreasing? [numbers]
  (all-increasing? (reverse numbers)))

(assert (all-decreasing? [8 6 4 4 1]))

(defn items-adjacent-by-123? [numbers]
  (->> (partition 2 1 numbers)
       (every? (fn [[a b]] (contains? #{1 2 3} (abs (- a b)))))))

(defn safe? [numbers]
  (and (or (all-increasing? numbers)
           (all-decreasing? numbers))
       (items-adjacent-by-123? numbers)))

(do
  (assert (safe? [7 6 4 2 1]))
  (assert (not (safe? [1 2 7 8 9])))
  (assert (not (safe? [9 7 6 2 1])))
  (assert (not (safe? [1 3 2 4 5])))
  (assert (not (safe? [1 3 2 4 5])))
  (assert (safe? [1 3 6 7 9])))

(defn solve-part1 [rows]
  (count (filter safe? rows)))

(assert (= 2 (solve-part1 (parse example-input))))

(solve-part1 (parse (slurp "input/day2.txt")))

^{::clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "40vh"}}])
