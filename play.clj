#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]]
         '[babashka.fs :as fs])

(defn print-help []
  (println (str/trim "
Usage: ./playground <subcommand> <options>

Subcommands:

page PAGE_NAME

    Create a new Org-mode page, with a tiny Pandoc build system.
")))

(defn parse-opts [opts]
  (let [[cmds opts] (split-with #(not (str/starts-with? % ":")) opts)]
    (into {:cmds cmds}
          (for [[arg-name arg-val] (partition 2 opts)]
            [(keyword (subs arg-name 1)) arg-val]))))

(defn org-doc [{:keys [title trailing-blank-lines]}]
  (str/join "\n"
            (concat
             ;; Header, title, link up
             [(str "#+title: " title)
              ""
              "DRAFT

[[./..][..]]"
              ""]

             ;; Trailing whitespace.
             (when trailing-blank-lines
               (concat ["#+begin_verse"]
                       (repeat trailing-blank-lines "")
                       ["#+end_verse"
                        ""])))))

(defn page [args]
  (let [opts (parse-opts args)
        page (or (:name opts)
                 (first (:cmds opts)))
        org-file (str page "/index.org")
        watch-file (str page "/watchbuild.sh")
        play-page (str page "/play.edn")]
    (fs/create-dirs page)

    ;; Org file
    (when-not (fs/exists? org-file)
      (spit org-file (org-doc {:title page
                               :trailing-blank-lines 20})))

    (when-not (fs/exists? play-page)
      (spit play-page (pr-str {:title page
                               :readiness :wtf-is-this
                               :author-url "https://teod.eu"})))

    nil
    ))

(defn -main []
  (let [[subcommand & args] *command-line-args*]
    (case subcommand
      "page" (page args)
      (print-help))))

(when (= *file* (System/getProperty "babashka.file"))
  (-main))
