(ns teod.play.api
  (:require
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]
   [clojure.edn :as edn]
   [clojure.string :as str]))

(defn today-str []
  (.format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")
           (java.time.LocalDateTime/now)))

(defn git-infer-created-date [f]
  (let [proc-handle (shell {:out :string :continue true}
                           "git log --pretty=\"format:%cs\""
                           (str (fs/absolutize f)))]
    (when (= 0 (:exit proc-handle))
      (-> proc-handle :out str/split-lines sort first))))


(defn page-slugs []
  (map (fn [play-edn] (str (fs/parent play-edn))) (fs/glob "." "*/play.edn")))

(defn slug->meta-file-path [slug]
  (str slug "/play.edn"))

(defn files->relations
  "Read relations from play.edn files on disk

  returns a map from slug to relation.
  "
  []
  (->> (page-slugs)
       (map (fn [slug] (merge {:slug slug}
                              (edn/read-string (slurp (slug->meta-file-path slug))))))
       (map (juxt :slug identity))
       (into {})))

(defn files->relations2
  "Read relations from play.edn files on disk

  returns a map from slug to relation.
  "
  []
  (->> (page-slugs)
       sort
       (map (fn [slug] (merge {:slug slug}
                              (edn/read-string (slurp (slug->meta-file-path slug))))))))
