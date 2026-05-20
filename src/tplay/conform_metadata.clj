(ns tplay.conform-metadata
  (:refer-clojure :exclude [load])
  (:require [babashka.fs :as fs]
            [clojure.set :as set]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Problem statement
;;
;; I don't have total control of attributes in use. Each page has an EDN file
;; with some stuff in it. But are the keys qualified? Unqualified? What is the
;; semantics for each key?

;; which attributes are in use?
;; where is my Datomic?

;; I'm happy to give up Babashka to gain Datomic.

(defn load [f]
  (read-string (slurp (fs/file f))))

(->> (fs/glob "." "**/play.edn")
     (mapcat (comp keys load))
     (into (sorted-set)))
#{:author-url :builder :builds :created :file :form :lang :norandompls :published :readiness :seed :slug :status :title :uuid :page/authors :page/uuid}

(defn newish [meta]
  (or (:page/authors meta)
      (:page/uuid meta)))

(def the-newish
  (->> (fs/glob "." "**/play.edn")
       (mapv str)
       (filter (comp newish read-string slurp))
       (into (sorted-set))))

(->> the-newish
     (mapv load)
     )

(->> (fs/glob "." "**/play.edn")
     (mapv (comp set keys load))
     (reduce set/intersection))
#{:title :lang :uuid}
;; lol.

;; ... less useful than I imagined ...
