(ns teod.play.understand2
  (:require
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [teod.play.cli :as cli]
   [datascript.core :as d]))

(set! *print-namespace-maps* false)

;; # Building a reflective understanding—part 2

(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

(defn conform-relation [page]
  (-> page
      (dissoc :uuid) ; we need only :page/uuid
      (dissoc :slug) ; we need only :page/slug
      ))

(defn relations []
  (->> (cli/files->relations {})
       vals
       (map conform-relation)
       (sort-by :page/slug)))


^{::clerk/width :full}
(big-table
 (->> (relations)
      (take 4)))

^{::clerk/width :full}
(big-table
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

(def schema
  {:slug {:db/unique :db.unique/identity}
   :uuid {:db/unique :db.unique/identity}
   :teod.play/authors {:db/cardinality :db.cardinality/many
                       :db/valueType   :db.type/ref}

   :page/slug {:db/unique :db.unique/identity}
   :page/uuid {:db/unique :db.unique/identity}
   :page/authors {:db/cardinality :db.cardinality/many
                  :db/valueType   :db.type/ref}
   })

(defn relations->datascript-db [rels]
  (let [conn (d/create-conn schema)]
    (d/transact! conn rels)
    @conn))

(defonce db (relations->datascript-db (relations)))

(comment
  (do (alter-var-root #'db (constantly (relations->datascript-db (relations)))) :done)
  )

(type db)

;; I've got a real datascript db.
;; Nice!

(into {} (:teod.play/authors (d/entity db [:page/slug "simple-made-easy"])))

(into {} (d/entity db [:page/slug "simple-made-easy"]))

^{:nextjournal.clerk/auto-expand-results? true}
(into {}
      (-> (d/entity db [:page/slug "simple-made-easy"])
          :page/authors))

^{:nextjournal.clerk/auto-expand-results? true}
(d/pull db '[*] [:page/slug "simple-made-easy"])

^{:nextjournal.clerk/auto-expand-results? true}
(d/pull db
        '[:db/id :page/slug {:teod.play/authors [*]}]
        [:page/slug "simple-made-easy"])
(comment
  ;; gir:
  {:db/id 284,
   :page/slug "simple-made-easy",
   :teod.play/authors
   [{:slug "rich-hickey",
     :page/uuid "a172782b-bceb-4b44-afdf-7a2348d02970",
     :created "2023-03-10",
     :readiness :wtf-is-this,
     :title "Rich Hickey",
     :lang :en,
     :page/slug "rich-hickey",
     :db/id 269,
     :form :remote-reference,
     :uuid "a172782b-bceb-4b44-afdf-7a2348d02970"}]})

;; GØY!

;; hvor mange dokumenter i databasen?

(d/q '[:find (count ?e) .
       :where [?e :page/slug ?slug]]
     db)

;; per 2024-02-25, 368.
;; Nice!

(into {} (d/entity db [:page/slug "journal"]))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
