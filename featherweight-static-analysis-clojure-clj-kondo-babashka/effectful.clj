#!/usr/bin/env bb

(ns effectful
  "Figure out which namespaces of your project are effectful through static analysis."
  (:require [babashka.process :refer [shell]]
            [babashka.fs :as fs]
            [clojure.edn :as edn]))

(defn kondo-analyze [root src-folders]
  (let [args (concat ["clj-kondo"]
                     ["--lang" "clj"]
                     ["--lint"] src-folders
                     ["--config" (pr-str {:analysis true :output {:format :edn}})])]
    (->
     (apply shell
            {:out :string :dir root :continue true}
            args)
     :out
     edn/read-string
     :analysis)))

(def ana (kondo-analyze (fs/file (fs/home) "dev/teodorlu/pandocir")
                        ["src" "test"]))

(defn kondo-analysis-references [kondo-analysis]
  (->> kondo-analysis
       :var-usages
       (map (fn [{:keys [from to name]}]
              {:from-ns from :to (symbol (clojure.core/name to)
                                         (clojure.core/name name))}))))

;; We don't care about cljs, so we'll skip references to cljs.core.

(defn cljs-ref? [reference]
  (#{"cljs.core"} (some-> reference :to namespace)))

(->> (kondo-analysis-references ana)
     (into #{})         ; remove duplicates
     (remove cljs-ref?) ; we don't care about clojurescript for now
     count)
;; => 189

(->> (kondo-analysis-references ana)
     (into #{})
     (remove cljs-ref?))

(count
 (kondo-analysis-references ana))
;; => 958

(count (remove cljs-ref? (kondo-analysis-references ana)))

(defn -main [& args]
  ;; MAIN TO DO STUFF
  )

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
