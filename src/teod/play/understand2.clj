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

(comment
  (->> (relations)
       shuffle
       first)
  ;; =>
  {:slug "the-commons",
   :title "The Commons",
   :readiness :wtf-is-this,
   :uuid "3eab9578-dec5-4c21-b5b6-7c18d6258d62",
   :author-url "https://teod.eu",
   :created "2022-09-03",
   :lang :en})
;; observations:
;;
;; - `:slug` is unique
;; - `:uuid` is unique
;;
;; (for a given point in time)
;;
;; could have opted for ns qualified keywords like
;;
;;     :teod.play/slug
;;     :teod.play/uuid
;;
;; ... but ... I didn't!

(def teod-play-schema
  "A schema for Teodor's play.

  Who plays with schemas? Better not wonder too deeply about that question."
  {;; we already have a bunch of unqualified :slug and :uuid
   ;; keep them for now!
   ;; we can fixup later if we want to.
   :slug {:db/unique :db.unique/identity}
   :uuid {:db/unique :db.unique/identity}
   ;; But use namespace qualified name for new properties.
   :teod.play/author {:db/cardinality :db.cardinality/many}})

(require '[datascript.core :as d])

(defn relations->datascript-db [rels]
  (let [conn (d/create-conn teod-play-schema)]
    (d/transact! conn rels)
    @conn))

(def db (relations->datascript-db (relations)))

(type db)

^{:nextjournal.clerk/auto-expand-results? true}
(into {}
      (d/entity db [:slug "journal"]))

;; it works!

^{:nextjournal.clerk/auto-expand-results? true}
(d/pull db '[*] [:slug "journal"])

(type (d/entity db [:slug "journal"]))
(type (into {} (d/entity db [:slug "journal"])))
(type (d/pull db '[*] [:slug "journal"]))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
