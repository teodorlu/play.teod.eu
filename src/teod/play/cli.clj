(ns teod.play.cli
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [cheshire.core :as json]
   [clojure.edn :as edn]
   [clojure.java.shell]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [teod.play.api :as play]
   [teod.play.pandoc-toolbox :as pandoc]
   [tplay.index]))

;; relations example
;;
;; page slug -> page metadata
{"emacs" {:slug "emacs"
          :title "(Doom) Emacs learning journal"
          :form :rambling
          :readiness :in-progress}
 "feedback-design-impl" {:title "Feedback loops, API design and how stuff works"}}

(defn bash [cmd]
  (str/trim (:out (clojure.java.shell/sh "bash" "-c" cmd))))

(defn words [& args] (str/join " " (map str args)))
(defn lines [& args] (str/join "\n" (map str args)))

(defn pages []
  (->> (bash "ls **/play.edn | sed 's|/play.edn||g'")
       (str/split-lines)
       (map (fn [slug] {:slug slug}))))

(defn conform-relation [relation]
  (cond-> relation
    (:slug relation) (assoc :page/slug (:slug relation))
    (:uuid relation) (assoc :page/uuid (:uuid relation))))

(defn files->relations
  [{:keys []}]
  (->> (pages)
       (pmap (fn [page]
               (-> page
                   (merge page (edn/read-string (slurp (str (:slug page) "/play.edn"))))
                   conform-relation)))
       (map (juxt :slug identity))
       (into {})))

(defn relations->lines
  "Produce one line per page

  Example:

    :slug emacs, :title \"(Doom) Emacs learning journal\", :form :rambling, :readiness :in-progress"
  [rels]
  (doseq [l (vals rels)]
    (prn l)))

(defn relations->lines2
  "Produce one line per relation

  Output format:

    [:slug \"emacs\"] :title \"(Doom) Emacs learning journal\"
    [:slug \"emacs\"] :form :rambling
    [:slug \"emacs\"] :readiness :in-progress

  Example: how many WTF-pages do we have?

    ./play.clj relations :from files :to lines2 | grep -i \":readiness :wtf\" | wc -l
    30

  Example: how many non-WTF-pages do we have?

    ./play.clj relations :from files :to lines2 | grep :readiness | grep -v \":readiness :wtf\" | wc -l
    9
  "
  [rels]
  (doseq [[slug page-meta] rels]
    (doseq [[attribute value] (dissoc page-meta :slug)]
      (prn [:slug slug] attribute value))))

(defn relations->lines+recent
  "Produce one line per relation, recent first

  Example: list recently created pages, with page ID and created date:

    ./play.clj relations :from files :to lines+recent | ./play.clj relations :from lines :to lines2 | grep :created
  "
  [rels]
  (let [recent-lines (->> (vals rels)
                          (filter :created)
                          (sort-by :created)
                          reverse)]
    (doseq [l recent-lines]
      (prn l))))

(def relations->pretty pprint)

(defn lines->relations
  "Relations from lines on stdin"
  [{}]
  (->> (str/split-lines (slurp *in*))
       (map edn/read-string)
       (map (fn [{:keys [slug] :as page}] {slug page}))
       (into {})))

(defn relations->files [rels]
  (doseq [[id page] rels]
    (spit (str id "/play.edn")
          (with-out-str
            (pprint (dissoc page :slug))))))

(defn page-index-org [{:keys [title trailing-blank-lines uuid body]}]
  (let [body (or body "DRAFT")]
    (str/join "\n"
              (concat
               ;; Link for org-roam
               (when uuid
                 [":PROPERTIES:"
                  (str ":ID: " uuid)
                  ":END:"])

               ;; Header, title, link up
               [(str "#+TITLE: " title)
                ""
                "[[file:..][..]]"
                ""
                (str/trim body)
                ""]

               ;; Trailing whitespace.
               (when trailing-blank-lines
                 (concat ["#+begin_verse"]
                         (repeat trailing-blank-lines "")
                         ["#+end_verse"
                          ""]))))))

#_ (page-index-org {:title "The Lollercoasters"
                    :body "The Best Band Ever"})

(def relations-config
  {:sources {:files files->relations
             :lines lines->relations}
   :targets {:lines relations->lines
             :lines2 relations->lines2
             :lines+recent relations->lines+recent
             :pretty relations->pretty
             :files relations->files}})

(defn relations-helptext []
  (let [helptext (lines
                  "usage: ./play.clj relations :from SOURCE :to TARGET"
                  ""
                  "valid sources:"
                  (str "  " (apply words (keys (:sources relations-config))))
                  ""
                  "valid targets:"
                  (str "  " (apply words (keys (:targets relations-config)))))]
    helptext))

(defn cmd-relations [{:keys [opts]}]
  (let [sources {:files files->relations
                 :lines lines->relations}
        targets {:lines relations->lines
                 :lines2 relations->lines2
                 :lines+recent relations->lines+recent
                 :pretty relations->pretty
                 :files relations->files}
        {:keys [from to]} opts
        input-is-valid (and (contains? sources from)
                            (contains? targets to))]
    (if-not input-is-valid
      (println (relations-helptext))
      (let [rels ((sources from) opts)]
        ((targets to) rels)))))

(defn cmd-random-page [{:keys [opts]}]
  (if (:dry-run opts)
    (pprint (->> (files->relations {})
                 vals
                 (remove #(= :remote-reference
                             (:form %)))
                 (take 5)))
    (let [n (or (:n opts) 1)]
      (println
       (str/join "\n"
                 (->> (files->relations {})
                      vals
                      (remove #(= :remote-reference
                                  (:form %)))
                      shuffle
                      (take n)
                      (map :slug)))))))

(defn cmd-create-page [{:keys [opts]}]
  (let [slug (:slug opts)
        title (or (:title opts) slug)
        uuid (or (:uuid opts) (str (random-uuid)))
        lang (or (:lang opts) :en)
        valid-opts? (and slug title uuid lang)
        fake-spit (fn [path content]
                    (println (str  "Would write to " path ":"))
                    (println)
                    (doseq [l (str/split-lines content)]
                      (println "  " l))
                    (println))
        fake-bash (fn [& args]
                    (println "Would run:")
                    (println)
                    (println (str "  " (pr-str (apply list 'bash args))))
                    (println)
                    )
        helptext (str/trim "
Usage:

  ./play.clj create-page [:slug] SLUG [OPT...]

Allowed options:

  :title TITLE
  :uuid UUID
  :lang LANG
  :form FORM
")]
    (when (or (:h opts) (:help opts))
      (println helptext)
      (System/exit 0))
    (when (not valid-opts?)
      (println helptext)
      (System/exit 1))
    (assert slug "Page slug is required.")
    (let [org-file (str slug "/index.org")
          play-file (str slug "/play.edn")]
      (fs/create-dirs slug)

      ;; Org file
      (when-not (fs/exists? org-file)
        (let [org-file-contents (page-index-org {:title title
                                                 :uuid uuid})]
          (if (:dry-run opts)
            (fake-spit org-file org-file-contents)
            (spit org-file org-file-contents))))

      ;; Play file
      (when-not (fs/exists? play-file)
        (let [page-meta (into (sorted-map)
                              {:title title
                               :readiness :wtf-is-this
                               :uuid uuid
                               :author-url "https://teod.eu"
                               :created (play/today-str)
                               :lang lang})
              page-meta (if (:form opts) (assoc page-meta :form (:form opts))
                            page-meta)
              play-file-content (with-out-str (pprint page-meta))]
          (if (:dry-run opts)
            (fake-spit play-file play-file-content)
            (spit play-file play-file-content)))

        ;; Regenerate the makefile since we've added a new target
        (if (:dry-run opts)
          (fake-bash "./play.clj makefile")
          (bash "./play.clj makefile")))

      nil)))

(defn builder [rel]
  (:builder rel :pandoc-page))

(defmulti makefile-entry builder)

(defmethod makefile-entry :no-build
  [rel]
  (str "# No build for rel: " (pr-str rel)))

(defmethod makefile-entry :pandoc-page
  [slug]
  (let [org-file-name (str slug "/index.org")
        html-file-name (str slug "/index.html")]
    (str html-file-name ": " org-file-name
         "\n\t"
         (words "pandoc -s --shift-heading-level-by=1 --from=org+smart -i" org-file-name "-t json"
                "|"
                "./play.clj filter resolve-links"
                "|"
                "pandoc -f json -o" html-file-name "--standalone --toc -H header-default-include.html"))))

(defn cmd-makefile [{:keys [opts]}]
  (let [{:keys [dry-run]} opts
        targets (->> (files->relations {})
                     vals
                     ;; For now, /only/ make builders for normal pandoc pages (same as default)
                     (filter (fn [rel]
                               (let [b (builder rel)]
                                 (or (= b :pandoc-page)
                                     (= b :smart-pandoc-page)))))
                     (map :slug)
                     sort)
        html (fn [target] (str target "/index.html"))
        play-edn (fn [target] (str target "/play.edn"))
        indent (fn [s] (str "\t" s))
        makefile (lines
                  "# DO NOT EDIT directly -- THIS MAKEFILE IS AUTO-GENERATED"
                  "# SEE `make makefile` TARGET"
                  ""
                  ""

                  "# Generate target for root index"
                  ;; TODO root index also depends on all the play.edn files found
                  (str/join " " (concat ["index.html:"]
                                        (map html targets)
                                        (map play-edn targets)
                                        (list "404.html" "header-default-include.html")))
                  "\t./play.clj index"
                  ""
                  ""

                  "# Generate target for 404"
                  "404.html: 404.org"
                  (indent
                   "pandoc --from=org+smart -i 404.org -t json | cat | pandoc -f json -o 404.html --standalone -H header-default-include.html -H scittle/scittle-with-extras.html")


                  ""
                  ""
                  "# Generate target for each page"
                  (str/join "\n\n"
                            (for [t targets]
                              ;; Here's the pandoc command I'd like for some pages:
                              (makefile-entry t)))
                  ""
                  ""


                  "# Note: generating the makefile with the makefile is sometimes problematic."
                  ".PHONY: makefile"
                  "makefile:"
                  "\t./play.clj makefile"
                  ""
                  ""

                  "# Rengenerate the index"
                  "# Note: we don't remove the makefile, as that gets us ... stuck."
                  "#       we also regenerate the metadata index."
                  ".PHONY: clean"
                  "clean:"
                  "\trm -f index.html"
                  "\t./play.clj reindex"
                  ""
                  ""

                  "# Regenerate everything"
                  ".PHONY: ultraclean"
                  "ultraclean: clean"
                  (str "\t"
                       "rm -f "
                       ;; Here's a pandoc command I'd like for some pages
                       (str/join " " (concat ["index.html"] (map html targets))))
                  )]
    (if dry-run
      (println makefile)
      (spit "Makefile" (str (str/trim makefile) "\n")))))

(defn generate-js-index [index]
  (lines "export const index ="
         (json/generate-string index {:pretty true})
         ";"))

(defn cmd-reindex
  "Create an index from page uuid to slug and title.

  Not to be confused with `cmd-index`, which produces an index.html file."
  [{:keys [opts]}]
  (let [{:keys [dry-run]} opts
        uuid-index (->> (files->relations {})
                        vals
                        (filter :uuid)
                        #_
                        (map #(select-keys % [:uuid :slug :title :id]))
                        (sort-by :uuid))
        big-index (atom [])]
    (if dry-run
      ;; just print pages
      (doseq [page uuid-index]
        (prn page))
      ;; otherwise, write to index/by-uuid/{UUID}.edn
      (do
        ;; ensure "index/by-uuid/" exists
        (fs/create-dirs "index/by-uuid/")
        ;; write an EDN file per UUID
        (doseq [page uuid-index]
          (spit (str "index/by-uuid/" (:uuid page) ".edn")
                (with-out-str (pprint page)))
          (swap! big-index conj page))
        ;; write one big EDN file
        (spit "index/big.edn" (with-out-str (pprint @big-index)))
        ;; write one big JSON index
        (spit "index/big.json" (json/generate-string @big-index {:pretty true}))
        ;; write one big JSON index -- as a js module!
        (spit "index/big.js" (generate-js-index @big-index))
        (spit "index/big.mjs" (generate-js-index @big-index))
        ))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn dbg
  "Debug a value only when run with verbose

  I typically include calls to dbg only when I need them. Usage requires setting
  an environment variable to opts into verbose mode."
  [& args]
  (when (not (nil? (System/getenv "EU_TEOD_PLAY_VERBOSE")))
    (binding [*out* *err*]
      (cond
        (zero? (count args))
        nil ;; no-op
        (= 1 (count args))
        (pprint (first args)) ;; raw pprint on arg
        :else
        (pprint args) ;; pprint the seq
        ))))

(defn cmd-filter
  "Apply play.teod.eu filters.

  For now, the only supported filter is resolve-links.

  Example usage:

    ./play.clj filter resolve-links < ../pandoc-toolbox/pandoc-examples/link.json"
  [{:as cmd-opts}]
  (when (:h (:opts cmd-opts))
    (println (str/trim "
Usage:

  ./play.clj filter resolve-links < ../pandoc-toolbox/pandoc-examples/link.json
      "))
    (System/exit 0))
  (when (contains? (set (:args cmd-opts))
                   "resolve-links")
    (let [pandoc-json (json/parse-string (slurp *in*))
          by-uuid (fn [uuid]
                    (let [path (str "index/by-uuid/" uuid ".edn")]
                      (when (fs/regular-file? path)
                        (edn/read-string (slurp path)))))
          uuid->slug (fn [uuid] (:slug (by-uuid uuid)))
          replace-link (fn [node]
                         (if (pandoc/link? node)
                           (let [uuid (last (str/split (pandoc/link-target node) #":"))
                                 slug (uuid->slug uuid)]
                             (if slug
                               (assoc-in node pandoc/link-target-path (str "../" slug "/"))
                               node))
                           node))
          resolved (pandoc/filter-body-postwalk pandoc-json replace-link)]
      (println (json/generate-string resolved)))))

(defn cmd-index
  "Produces the index.html file for play.teod.eu"
  [_opts+args]
  (if (= (System/getenv "ALT")
         "1")
    (tplay.index/alt)
    (tplay.index/main)))

(def dispatch-table
  [{:cmds ["create-page"] :fn cmd-create-page :cmds-opts [:slug]}
   {:cmds ["filter"] :fn cmd-filter :cmds-opts [:resolve-links]}
   {:cmds ["makefile"] :fn cmd-makefile}
   {:cmds ["random-page"] :fn cmd-random-page}
   {:cmds ["reindex"] :fn cmd-reindex}
   {:cmds ["relations"] :fn cmd-relations}
   {:cmds ["index"] :fn cmd-index}])

(defn print-subcommands [{}]
  (println "usage: ./play.clj COMMAND")
  (println "")
  (println "Available commands:")
  (doseq [{:keys [cmds]} dispatch-table]
    (println (str "  "
                  (str/join " " cmds)))))

(defn main [& args]
  (cli/dispatch (concat dispatch-table
                        [{:cmds ["help"] :fn print-subcommands}
                         {:cmds [] :fn print-subcommands}])
                args
                {:coerce {;; relations
                          :from :keyword    ; page relation format
                          :to :keyword      ; page relation format
                          :dry-run :boolean
                          ;; page
                          :title :string    ; Page title
                          :n :long          ; Count - eg random page count
                          :uuid :string     ; UUID for me and Org-roam
                          :lang :keyword    ; Article language, :en or :no
                          ;; filter
                          :resolve-links :boolean
                          }}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

 ;; How to run:
 ;;
 ;;   ./play.clj relations :from :files :to :lines

(comment
  ;; chat med oddmund
  ;;
  ;; kan sikkert slettes.

  (defrecord Person [name age])

  (into {} (Person. "john" 27))
  ;; => {:name "john", :age 27}

  (:name (Person. "john" 27))
  ;; => "john"

  (.name (Person. "john" 27))
  ;; => "john"

  (update-in (Person. "john" 27)
             [:age]
             inc)
  ;; => {:name "john", :age 28}

  (update (Person. "john" 27) :age inc)
  ;; => {:name "john", :age 28}

  assoc-in get-in update-in

  ((juxt identity inc) 27)
  ;; => [27 28]


  (get-in {:age 27}
          [#(:age %)])
  ;; => nil

  (get-in {:age 27}
          [:age])
  ;; => 27

  (:age {:age 27})
  ;; => 27

  (map :age [{:age 27} {:age 28}])
  ;; => (27 28)


  ;; DET ER MANGE TING SOM ER FUNKSJONER!

  (ifn? #(+ %))
  ;; => true

  ;; Siden `ifn?` er en funksjon, kan vi sjekke med `map`.

  (map ifn? [:name ; nøkkelord er funksjoner
             {:name "Oddmund"} ; maps er funksjoner
             #{:name :age} ; sets er funksjoner
             ["Oddmund" "Teodor"] ; vektorer er funksjoner
             ; ... og funksjoner er funksjoner.
             #(+ % 1)
             inc
             #(map inc %)
             (fn [x] (* 10 x))
             map])
  ;; => (true true true true true true true true true)




  :rcf)
