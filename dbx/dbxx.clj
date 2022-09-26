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

(defn firefox
  "Open URL in Firefox"
  [url]
  (babashka.process/process ['firefox '-new-window url]))

(defn fzf [options]
  (-> @(process/process ["fzf"] {:in (str/join "\n" options)
                                 :out :string
                                 :err :inherit})
      :out
      str/trim))

(defn nav
  "Choose provider, then choose link"
  [{}]
  (let [p (fzf (providers))]
    (println p))

  )

(def dispatch-table
  [{:cmds ["provider"] :fn provider}
   {:cmds ["nav"] :fn nav}])

(defn main [& args]
  (cli/dispatch dispatch-table args {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
