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

(->> uuids
     (remove #(= % (str/lower-case %)))
     count)
;; => 36

;; BAH, I've generated both upper case UUIDs and lower-case UUIDS.
;; BAD!
;; LOL.
;;
;; And references to those uuids can be anywhere.

(take 3 (shuffle uuids))
