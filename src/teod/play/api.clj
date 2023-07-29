(ns teod.play.api
  (:require
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]
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
