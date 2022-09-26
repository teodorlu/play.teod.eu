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

(defn providers []
  (when (fs/exists? (config-path))
    (let [edn (edn/read-string (slurp (str (config-path))))]
      (keys (:providers edn)))))

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

(defn provider-links [p]
  (when (fs/exists? (config-path))
    (let [edn (edn/read-string (slurp (str (config-path))))]
      (when-let [provider-method (get (:providers edn) p)]
        #_
        (prn provider-method)
        (cond
          ;; functions are interpreted "raw"
          (contains? provider-method :fn)
          (let [provider-fn (eval (:fn provider-method))]
            (provider-fn))
          :else nil

          )


        )
      ))

  )

(defn nav
  "Choose provider, then choose link"
  [{:keys [opts]}]
  #_
  (prn opts)
  (let [p (or (:provider opts) (fzf (providers)))
        links (provider-links p)
        by-title-description (into {}
                                   (for [l links]
                                     [(str (:title l) " | " (:description l))
                                      l]))
        choice (fzf (for [l links]
                      (str (:title l) " | " (:description l))))
        ]

    (pprint choice)
    (pprint (get by-title-description choice))


    )

  )

(def dispatch-table
  [{:cmds ["provider"] :fn provider}
   {:cmds ["nav"] :fn nav :args->opts [:provider] :coerce {:provider :symbol}}])

(defn main [& args]
  (cli/dispatch dispatch-table args {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
