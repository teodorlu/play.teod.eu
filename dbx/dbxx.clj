#!/usr/bin/env bb

(ns dbxx
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [babashka.process :as process]
   [clojure.edn :as edn]
   [clojure.java.browse]
   [clojure.string :as str]))

(defn provider
  "List available providers"
  [{}]
  (println "providers TODO"))

(def dispatch-table
  [{:cmds ["provider"] :fn provider}])

(defn main [& args]
  (cli/dispatch dispatch-table args {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
