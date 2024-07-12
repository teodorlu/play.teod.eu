;; # Understanding remote references and authorship

(ns tplay.dev.understand4
  (:require
   [datascript.core :as d]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [tplay.cli :as cli]))

;; ## Prior work: list pages & put into db

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

(defn conform-relation [page]
  (cond-> page
    (:slug page) (assoc :page/slug (:slug page))
    (:uuid page) (assoc :page/uuid (:uuid page))))

(defn relations []
  (->> (cli/files->relations {})
       vals
       (map conform-relation)
       (sort-by :page/slug)))

(->> (relations)
     (take 5))

(def schema
  {:page/slug {:db/unique :db.unique/identity}
   :page/uuid {:db/unique :db.unique/identity}
   :page/authors {:db/cardinality :db.cardinality/many
                  :db/valueType   :db.type/ref}})

(defn relations->datascript-db [rels]
  (let [conn (d/create-conn schema)]
    (d/transact! conn rels)
    @conn))

(do (defonce db (relations->datascript-db (relations))) nil)

(comment
  (do (alter-var-root #'db (constantly (relations->datascript-db (relations)))) :done)
  :rcf)

;; ## Problem statement: find pages missing metadata
;;
;; Principle: all pages which are themselves remote references should have :page/author set!

(d/q '[:find (count ?e) .
       :where [?e :page/slug ?slug]]
     db)

^{:nextjournal.clerk/auto-expand-results? true}
(into {} (d/entity db [:page/slug "simple-made-easy"]))

(d/q '[:find (count ?slug) .
       :where
       [?e :page/slug ?slug]
       [?e :form :remote-reference]]
     db)

;; problem: persons are themselves remote references.
;; how do we model that an entity is a person?
;; we could include a `:person/name`.
;;
;; bah, let's just list some.

(take 3
      (-> (d/q '[:find ?slug ?title
                 :where
                 [?e :page/slug ?slug]
                 [?e :title ?title]
                 [?e :form :remote-reference]]
               db)))

#_
^{::clerk/width :full
  ::clerk/no-cache true}
(clerk/caption "xx"
 (big-table
  (->>
   (d/q '[:find [(pull ?e [:slug :title]) ...]
          :where
          [?e :form :remote-reference]]
        db)
   shuffle
   )))

^{::clerk/no-cache true
  :nextjournal.clerk/auto-expand-results? true}
(clerk/caption "Page titles"
 (big-table
  (->>
   (d/q '[:find [(pull ?e [:title]) ...]
          :where
          [?e :form :remote-reference]]
        db)
   shuffle
   )))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
