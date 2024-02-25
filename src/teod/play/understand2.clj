(ns teod.play.understand2
  (:require
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [teod.play.cli :as cli]))

(set! *print-namespace-maps* false)

;; # Building a reflective understanding—part 2

(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

(defn relations []
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
   :teod.play/authors {:db/cardinality :db.cardinality/many
                       :db/valueType   :db.type/ref}})

(require '[datascript.core :as d])

(defn relations->datascript-db [rels]
  (let [conn (d/create-conn teod-play-schema)]
    (d/transact! conn rels)
    @conn))

(defonce db (relations->datascript-db (relations)))

(comment
  ;; the sole purpose of (do ,, nil) is to avoid slowdown from printing the
  ;; whole db to the repl.
  (do (alter-var-root #'db (constantly (relations->datascript-db (relations)))) :done)
  )

(type db)

;; I've got a real datomic db.
;; Nice!

(into {} (:teod.play/authors (d/entity db [:slug "simple-made-easy"])))

(into {} (d/entity db [:slug "simple-made-easy"]))

^{:nextjournal.clerk/auto-expand-results? true}
(d/pull db '[*] [:slug "simple-made-easy"])

^{:nextjournal.clerk/auto-expand-results? true}
(d/pull db
        '[:slug {:teod.play/authors [*]}]
        [:slug "simple-made-easy"])

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
