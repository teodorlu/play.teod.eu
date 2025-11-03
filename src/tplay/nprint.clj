(ns tplay.nprint
  "Print data neatly

  Faster and less elegant than pprint. Strips commas, and avoids map namespace
  syntax. Runs on Babashka."
  (:require [clojure.string :as str]
            [clojure.walk :refer [postwalk]]))

(defn nprint-string
  "A slightly faster pr-str for strings (22 % when I measured)"
  {:benchmark-code (quote ;; (quote) not ' so you can run benchmark with eval-last-sexp.
                    (let [s "hei\t, du\n! \"lol\"."]
                      (println "pr-str:")
                      (time (dotimes [_ 1000000] (pr-str s)))
                      (println "nprint-string:")
                      (time (dotimes [_ 1000000] (nprint-string s)))))}
  [s]
  (str \"
       (str/escape s
                   {\newline "\\n"
                    \tab  "\\t"
                    \return "\\r"
                    \" "\\\""
                    \\  "\\\\"
                    \formfeed "\\f"
                    \backspace "\\b"})
       \"))

(defn nprint-default-fallback [data]
  (str "[:nprint/unsupported " (pr-str (type data)) "]"))

(defn nprint-prim
  [data opts]
  (let [fallback-fn (get opts :fallback-fn nprint-default-fallback)]
    (cond (number? data) (str data)
          (string? data) (nprint-string data)
          (keyword? data) (str data)
          (symbol? data) (str data)
          :else (fallback-fn data))))

(declare nprint)

(defn nprint-coll [coll opts]
  (let [fallback-fn (get opts :fallback-fn nprint-default-fallback)]
    (cond (vector? coll) (str "[" (str/join " " coll) "]")
          (list? coll) (str "(" (str/join " " coll) ")")
          (set? coll) (str "#{" (str/join " " coll) "}")
          (map? coll) (str "{" (str/join " "
                                         (map (fn [[kstr vstr]]
                                                (str kstr " " vstr))
                                              coll))
                           "}")
          :else (fallback-fn coll))))

(def maxlen 40)

(defn nprint-pre [data opts]
  (cond
    ;; What a can of footguns!
    (map-entry? data) data
    (coll? data) (let [printed (nprint-coll data opts)]
                   (if (< maxlen (count printed))
                     data
                     printed))
    :else (nprint-prim data opts)))

(defn nprint-indent
  [data indent]
  (if (string? data) data
      (str "["
           (str/join (str "\n" (apply str (repeat (+ indent 1) " ")))
                     data)
           "]")))

(defn nprint
  "Print data neatly

  Faster and less elegant than pprint. Strips commas, and avoids map namespace
  syntax. Runs on Babashka.

  ---

  Strategy:
  - Start from the inside
  - Try print everything on one line
  - When that line exceeds a width of 40, switch strategy to everything on its
    own line.

  Implementation: Postwalk prepare then indented print.

  Postwalk prepare:
  - Print inner nodes on one line until printed lengths start exceeding 40 columns
  - At that point, leave the node.

  Indented print:
  - After the postwalk prepare, we're guaranteed that:
    - All primitive nodes have been printed
    - ... and all non-wrapping collection nodes have been printed.
  - Therefore, we can \"indent all the time\"."
  ([data] (nprint data {}))
  ([data opts]
   (-> (postwalk #(nprint-pre % opts) data)
       (nprint-indent 0))))
