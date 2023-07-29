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

  returns a map from slug to relation.
  "
  []
  (->> (pages)
       (map (fn [{:keys [slug] :as p}]
              (merge p (edn/read-string (slurp (str slug "/play.edn"))))))
       (map (juxt :slug identity))
       (into {})))

(comment
  ;; give me relations where there's no :created

  (filter (fn [[_slug rel]]
            (not (:created rel)))
          (files->relations))

  (->>
   (files->relations)
   (filter (fn [[_slug rel]]
             (not (:created rel))))
   vals
   (map :slug)
   sort
   ))
