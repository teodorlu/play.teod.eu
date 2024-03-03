(ns teod.play.understand3
  (:require
   [clojure.set :as set]
   [datascript.core :as d]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [teod.play.cli :as cli]))

(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

;; Goal: tag & collect talks on its own page.

;; metadata aspect:

:page/youtube-recording-url

:page/authors

;; query by author.

(defn conform-relation [page]
  (cond-> page
    (:slug page) (assoc :page/slug (:slug page))
    (:uuid page) (assoc :page/uuid (:uuid page))
    (:teod.play/authors page) (assoc :page/authors (:teod.play/authors page))))

(defn relations []
  (->> (cli/files->relations {})
       vals
       (map conform-relation)
       (sort-by :page/slug)))

^{::clerk/width :full}
(big-table
 (->> (relations)
      (filter (fn [page]
                (= "simple-made-easy" (:page/slug page))))
      (take 3)
      ))

(def schema
  {:page/slug {:db/unique :db.unique/identity}
   :page/uuid {:db/unique :db.unique/identity}
   :uuid {:db/unique :db.unique/identity}
   :page/authors {:db/cardinality :db.cardinality/many
                  :db/valueType   :db.type/ref}})

(defn relations->datascript-db [rels]
  (let [conn (d/create-conn schema)]
    (d/transact! conn rels)
    @conn))

;; breaks because "simple-made-easy" links to Rich by :uuid, which is removed from the db.

(defonce db (relations->datascript-db (relations)))

(comment
  (do (alter-var-root #'db (constantly (relations->datascript-db (relations)))) :done)
  :rcf)
