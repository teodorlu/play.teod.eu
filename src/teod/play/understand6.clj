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

(let [only-lower #(= % (str/lower-case %))]
  {:upper (count (remove only-lower uuids))
   :lower (count (filter only-lower uuids))})
;; => {:upper 36, :lower 360}


(def only-lower #(= % (str/lower-case %)))

(rand-nth (filter only-lower uuids))
;; => "0dec1845-f5c3-4061-8843-5089ff3b8657"

(rand-nth (remove only-lower uuids))
;; => "EBDC5A9A-6B70-4FD4-8BFC-635C08521879"


(->> uuids
     (remove #(= % (str/lower-case %)))
     count)
