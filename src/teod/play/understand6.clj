(ns teod.play.understand6
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [teod.iki :as iki]))

(def db (iki/datascript-db))

(def uuids (set
            (d/q '[:find [?uuid ...]
                   :where [?e :page/uuid ?uuid]]
                 db)))

(defn casing [s]
  (if (= s (str/lower-case s))
    :lower
    :upper))

(frequencies (map casing uuids))
;; => {:lower 361, :upper 36}
