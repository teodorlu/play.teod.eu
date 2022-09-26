#!/usr/bin/env bb

(ns dbxx
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [babashka.process :as process]
   [clojure.edn :as edn]
   [clojure.java.browse]
   [clojure.string :as str]
   [clojure.pprint :refer [pprint]]))

(def ^:dynamic *config-path* "~/.config/dbx/dbx.edn")

(defn config-path []
  (fs/expand-home *config-path*))

(defn provider-names []
  (when (fs/exists? (config-path))
    (let [edn (edn/read-string (slurp (str (config-path))))]
      (keys (:providers edn)))))

(defn cmd-provider
  "List available providers"
  [{}]
  (doseq [p (provider-names)]
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

(defn read-provider [provider-name]
  (when (fs/exists? (config-path))
    (let [edn (edn/read-string (slurp (str (config-path))))]
      (get (:providers edn) provider-name))))

(defn provider-links [provider]
  (cond
    ;; provider has a Clojure function to get links
    (contains? provider :fn)
    (let [provider-fn (eval (:fn provider))]
      (provider-fn))
    :else nil))

(defn embark [provider url]
  (cond
    (contains? provider :embark-fn)
    (let [embark-fn (eval (:embark-fn provider))]
      (embark-fn url))))

(defn nav
  "Choose provider, then choose link

  Problem: I've hard-coded that links must have :title, :description and :href.
  I'd rather make links that have :label and :target.
  Then push the label and target logic to config."
  [{:keys [opts]}]
  (let [provider-name (or (:provider opts) (symbol (fzf (provider-names))))
        provider (read-provider provider-name)
        links (provider-links provider)
        by-title-description (into {}
                                   (for [l links]
                                     [(str (:title l) " | " (:description l))
                                      l]))
        choice (fzf (for [l links]
                      (str (:title l) " | " (:description l))))
        ]

    (embark provider (:href (get by-title-description choice)))
    )
  )

(def dispatch-table
  [{:cmds ["provider"] :fn cmd-provider}
   {:cmds ["nav"] :fn nav :args->opts [:provider] :coerce {:provider :symbol}}
   {:cmds [] :fn nav :args->opts [:provider] :coerce {:provider :symbol}}])

(defn main [& args]
  (cli/dispatch dispatch-table args {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
