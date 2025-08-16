(ns pandoc
  "Copied from https://github.com/iterate/mikrobloggeriet/blob/85b135ddf716a22854ab84b5ab5fe44a8a8332c6/src/mikrobloggeriet/pandoc.clj"
  (:require
   [babashka.fs :as fs]
   [babashka.process]
   [cheshire.core :as json]
   [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LOW LEVEL PANDOC WRAPPER

(defn- from-json-str [s]
  (json/parse-string s keyword))

(defn- to-json-str [x]
  (json/generate-string x))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PANDOC IR HELPERS

(defn pandoc?
  "Whether `x` looks like a pandoc document represented as Clojure data"
  [x]
  (and
   (map? x)
   (contains? x :pandoc-api-version)
   (contains? x :blocks)
   (contains? x :meta)))

(declare el->plaintext)

(defn- els->plaintext
  "Convert a sequence of pandoc expressions to plaintext without shelling out to pandoc

  els->plaintext is an implementation detail. Please use `el->plaintext` instead."
  [els]
  (str/join
   (->> els
        (map el->plaintext)
        (filter some?))))

(defn el->plaintext
  "Convert a pandoc expression to plaintext without shelling out to pandoc"
  [expr]
  (cond (= "Str" (:t expr))
        (:c expr)

        (= "MetaInlines" (:t expr))
        (els->plaintext (:c expr))

        (= "Space" (:t expr))
        " "

        (= "SoftBreak" (:t expr))
        " "

        (= "Para" (:t expr))
        (els->plaintext (:c expr))

        (= "Emph" (:t expr))
        (els->plaintext (:c expr))

        (= "Code" (:t expr))
        (second (:c expr))

        :else
        nil))

(defn set-title [pandoc title]
  (assert (pandoc? pandoc))
  (assoc-in pandoc [:meta :title] {:t "MetaInlines" :c [{:t "Str" :c title}]}))

(defn title [pandoc]
  (when-let [title-el (-> pandoc :meta :title)]
    (el->plaintext title-el)))

(defn header? [el]
  (= (:t el) "Header"))

(defn h1? [el]
  (let [header-level (-> el :c first)]
    (and (header? el)
         (= header-level 1))))

(defn para? [el]
  (= (:t el) "Para"))

(defn header->plaintext [el]
  (when (header? el)
    (els->plaintext (get-in el [:c 2]))))

(defn infer-title [pandoc]
  (or (title pandoc)
      (when-let [first-h1 (->> (:blocks pandoc)
                               (filter h1?)
                               first)]
        (header->plaintext first-h1))))

(defn infer-description [pandoc]
  (when-let [first-para (->> (:blocks pandoc)
                             (filter para?)
                             first)]
    (els->plaintext (:c first-para))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PANDOC BINARY

(defn get-pandoc-path
  "Look for a pandoc binary.

    1. First look on the user's PATH
    2. Then look inside GARDEN_STORAGE

    The GARDEN_STORAGE case is needed for Application.garden, where a Pandoc binary has been uploaded."
  []
  (or (some-> (fs/which "pandoc") str)
      (when-let [pandoc-storage-path (some-> (System/getenv "GARDEN_STORAGE") (fs/file "pandoc") str)]
        (when (fs/exists? pandoc-storage-path)
          pandoc-storage-path))))

(defn- run-pandoc [stdin pandoc-args]
  (let [process-handle (deref (apply babashka.process/process
                                     {:in stdin :out :string}
                                     (get-pandoc-path)
                                     pandoc-args))]
    (when (= 0 (:exit process-handle))
      (:out process-handle))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; FROM TEXT FORMAT TO IR

(defn read-str [raw-str from-format]
  (when (string? raw-str)
    (from-json-str (run-pandoc raw-str ["--from" from-format "--to" "json"]))))

(defn from-html [html-str] (read-str html-str "html"))
(defn from-markdown [markdown-str] (read-str markdown-str "markdown+smart"))
(defn from-org [org-str] (read-str org-str "org+smart"))
(defn from-rst [rst-str] (read-str rst-str "rst+smart"))
(defn from-typst [typst-str] (read-str typst-str "typst+smart"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; FROM IR TO TEXT FORMAT

(defn write-str [pandoc to-format & extra-pandoc-cli-args]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc)
                (concat extra-pandoc-cli-args
                        ["--from" "json" "--to" to-format]))))

(defn to-html [pandoc] (write-str pandoc "html"))
(defn to-html-standalone [pandoc] (write-str pandoc "html" "--standalone"))
(defn to-markdown [pandoc] (write-str pandoc "markdown"))
(defn to-markdown-standalone [pandoc] (write-str pandoc "markdown" "--standalone"))
(defn to-org [pandoc] (write-str pandoc "org"))
(defn to-org-standalone [pandoc] (write-str pandoc "org" "--standalone"))
(defn to-plain [pandoc] (write-str pandoc "plain"))
(defn to-plain-standalone [pandoc] (write-str pandoc "plain" "--standalone"))
(defn to-typst [pandoc] (write-str pandoc "typst"))
