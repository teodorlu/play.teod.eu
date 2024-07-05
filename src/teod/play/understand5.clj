(ns teod.play.understand5
  (:require
   [datascript.core :as d]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [teod.play.cli :as cli]))

(defn relations []
  (->> (cli/files->relations {})
       vals
       (sort-by :page/slug)))

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
#_ (do (alter-var-root #'db (constantly (relations->datascript-db (relations)))) :done)

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


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

^{::clerk/no-cache true
  :nextjournal.clerk/auto-expand-results? true
  ::clerk/width :full}
(clerk/caption "Page titles"
 (big-table
  (->>
   (d/q '[:find [(pull ?e [:title :slug]) ...]
          :where
          [?e :form :remote-reference]]
        db)
   shuffle
   )))

^{:nextjournal.clerk/auto-expand-results? true
  :nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
