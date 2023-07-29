(ns teod.play.api
  (:require
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]
   [clojure.edn :as edn]
   [clojure.string :as str]))

(defn today-str []
  (.format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")
           (java.time.LocalDateTime/now)))

(defn lol []
  (let [n (Math/floor (inc  (rand 10)))]
    (apply str "l"
           (repeat n "ol"))))

(defn git-infer-created-date [f]
  (let [proc-handle (shell {:out :string}
                           "git log --pretty=\"format:%cs\""
                           (str (fs/absolutize f)))]
    (when (= 0 (:exit proc-handle))
      (-> proc-handle :out str/split-lines sort first))))

(defn pages []
  (map (fn [play-edn]
         {:slug (str (fs/parent play-edn))})
       (fs/glob "." "*/play.edn")))

(defn files->relations
  "Read relations from play.edn files on disk

  :uuid-from-org - when true, try to crudely find UUIDs from index.org files.
  "
  [{:keys []}]
  (let [enrich (fn [page] page)]
    (->> (pages)
         (map (fn [{:keys [slug] :as p}]
                (merge p (edn/read-string (slurp (str slug "/play.edn"))))))
         (map enrich)
         (map (juxt :slug identity))
         (into {}))))

(get (files->relations {}) "rich-hickey")
