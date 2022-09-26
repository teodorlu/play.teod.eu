#!/usr/bin/env bb

(ns dbxx
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [babashka.process :as process]
   [clojure.edn :as edn]
   [clojure.java.browse]
   [clojure.string :as str]))

(defn providers []
  (let [config-path (fs/expand-home "~/.config/dbx/dbx.edn")]
    (when (fs/exists? config-path)
      (let [edn (edn/read-string (slurp (str  config-path)))]
        (keys (:providers edn))))))

(defn provider
  "List available providers"
  [{}]
  (doseq [p (providers)]
    (println p)))

(def dispatch-table
  [{:cmds ["provider"] :fn provider}])

(defn main [& args]
  (cli/dispatch dispatch-table args {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
