(ns tplay.iki
  (:require
   [datascript.core :as d]
   [tplay.cli :as cli]))

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

(comment
  (def initial [4000 3000])
  (def factor (/ 576 (first initial)))

  (map (partial * factor) initial)
  ;; => (576N 432N)

  (map (partial * 2 factor) initial)
  ;; => (1152N 864N)

  :rcf)
