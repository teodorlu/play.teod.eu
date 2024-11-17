(ns tplay.db
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]
   [clojure.string :as str]
   [clojure.edn :as edn]
   [clojure.set :as set]
   [datascript.core :as d]))

;; A fresh take on loading all the pages into a datascript database for metadata

(set! *print-namespace-maps* false)

(def schema
  {:page/slug {:db/unique :db.unique/identity}
   :page/uuid {:db/unique :db.unique/identity}
   :page/authors {:db/cardinality :db.cardinality/many
                  :db/valueType   :db.type/ref}})

(def reference-attributes
  (->> schema
       (filter (fn [[_attr attr-map]]
                 (= :db.type/ref (:db/valueType attr-map))))
       (map first)
       (into #{})))

(def identity-attributes
  (->> schema
       (filter (fn [[_attr attr-map]]
                 (= :db.unique/identity (:db/unique attr-map))))
       (map first)
       (into #{})))

(def metadata-file-name "play.edn")

(defn canonicalize-metadata [metadata]
  (-> metadata
      (set/rename-keys {:title :page/title
                        :readiness :page/readiness
                        :created :page/created-date
                        :lang :page/lang
                        :uuid :page/uuid
                        :form :page/form})
      (select-keys [:page/title
                    :page/readiness
                    :page/created-date
                    :page/lang
                    :page/uuid
                    :page/form
                    :page/authors])))


(defn load-page [page-dir]
  (let [page-dir (fs/file page-dir)
        metadata-file (fs/file page-dir metadata-file-name)]
    (when (fs/exists? metadata-file)
      (into (sorted-map)
            (assoc (canonicalize-metadata (edn/read-string (slurp metadata-file)))
                   :page/slug (fs/file-name page-dir))))))

(defn load-pages [root]
  (->> (fs/glob (fs/canonicalize root) "**/play.edn")
       (map fs/parent)
       (map load-page)
       (filter some?)))

(defn page->stage1
  "A stage 1 page is the part of the page that can load without assuming other
  pages are loaded."
  [page]
  (dissoc page :page/authors))

(defn page->stage2
  "A stage 2 page is the part of the page that links to other pages."
  [page]
  (when (contains? page :page/authors)
    (select-keys page [:page/authors :page/uuid :page/slug])))

(defn db [pages]
  (let [conn (d/create-conn schema)]
    (d/transact! conn (map page->stage1 pages))
    (d/transact! conn (some->> pages (map page->stage2)))
    @conn))

(comment
  (def ROOT (-> (p/shell {:out :string} "git rev-parse --show-toplevel")
                :out str/trim))
  (def DB (db (load-pages ROOT)))

  (load-pages "..")

  (into {} (d/entity DB [:page/slug "rich-hickey"]))

  (d/q '[:find ?e
         :where [?e :page/slug "rich-hickey"]]
       DB)

  (d/q '[:find ?text-name ?author-name
         :where
         [?text :page/title ?text-name]
         [?text :page/authors ?author]
         [?author :page/title ?author-name]]
       DB)

  )
