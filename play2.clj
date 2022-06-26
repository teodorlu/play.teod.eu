#!/usr/bin/env bb

(require '[babashka.deps :as deps])

(deps/add-deps '{:deps {#_#_ borkdude/rewrite-edn {:mvn/version "0.2.0"}
                        org.babashka/cli {:mvn/version "0.3.28"}}})

(require '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]]
         '[babashka.fs :as fs])

;; idea
;;
;; consolidate the babashka scripts I've created in this repo in a CLI
;;
;; provide --dry-run options to use instead of the (System/getenv "ALT") env var I used for that previously
;;
;; expose the knowledge model of this repo through entrypoints into that script
;;
;;     tags : play.edn files to flat file
;;            flat file to play.edn files
;;                apply partial with tags
;;            flat file to SQLite
;;            SQLite from flat file
;;
;; generate makefile from deps graph

;; more ideas
;;
;; load the tag data into a big map

{"emacs" {:id "emacs"
          :title "(Doom) Emacs learning journal"
          :form :rambling
          :readiness :in-progress}
 "feedback-design-impl" {:title "Feedback loops, API design and how stuff works"}}

;; relations from fs to EDN
;;
;;   ./play relations :from :files :to :edn-str
;;   ./play relations :from :edn-str :to :files
;;
;;   :from :sqlite :to :files
;;
;;   ./play relations :in file.edn :out relations.sqlite
;;
;; Action - edit tags
;;
;;   ./play relations :from :files :to :lines > lines.txt
;;   # edit lines.txt - do stuff in batch ...
;;   ./play relations :from :lines :to :files < lines.txt
