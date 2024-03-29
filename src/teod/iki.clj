(ns teod.iki
  (:require
   [datascript.core :as d]
   [teod.play.cli :as cli]))

(def datascript-schema
  {:page/slug {:db/unique :db.unique/identity}
   :page/uuid {:db/unique :db.unique/identity}
   :page/authors {:db/cardinality :db.cardinality/many
                  :db/valueType   :db.type/ref}})

(defn datascript-db []
  (let [rels (->> (cli/files->relations {})
                  vals
                  (sort-by :page/slug))
        conn (d/create-conn datascript-schema)]
    (d/transact! conn rels)
    @conn))
