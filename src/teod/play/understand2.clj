(ns teod.play.understand2
  (:require
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [teod.play.cli :as cli]))

;; # Building a reflective understanding—part 2

(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

(defn relations []
  (->> (cli/files->relations {})
       vals
       (sort-by :slug)))

#_
^{::clerk/width :full}
(big-table
 (->> (cli/files->relations {})
      vals
      (sort-by :slug)))

^{::clerk/width :full}
(big-table
 (->> (relations)
      (take 4)))

(clerk/table
 (->> (relations)
      (filter (fn [rel]
                (re-matches #".*Simple.*" (:title rel))))))

;; I currently do not have any metadata for:
;;
;; - The fact that Simple Made Easy is by Rich Hickey
;; - The fact that Simple Made Easy is a talk with a youtube link

;; How about `:author/uuid` ... ?

;; This is when I probably want a datascript database of my stuff.
;; ... and I (think) I want to keep my current file storage system, just add an index.

(defn grep-title [re xs]
  (filter #(re-matches re (:title %)) xs))

^{:nextjournal.clerk/auto-expand-results? true}
(->> (relations)
     (grep-title #".*[Cc]lerk.*"))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
