;; # Performance tax for shelling out to Pandoc

(ns pandoc-shellout-tax
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [pandoc]
   [tplay.api :as tplay]))

;; All relations might be powered by org files.

(def relations
  (->> (p/shell {:dir ".." :out :string}
                "./play.clj relations :from :files :to :lines")
       :out
       (str/split-lines)
       (map read-string)))

;; A nil builder is the default Org-mode builder.

(defn org-build? [rel]
  (nil? (:builder rel)))

(def slugs
  (->> relations
       (filter org-build?)
       (map :slug)
       (sort)))

(def org-files
  (->> slugs
       (map #(fs/file ".." % "index.org"))
       (map str)))

(->> org-files
     (map (fn [file]
            {:file file
             :length (count (slurp file))}))
     (take 3))

(defn org->html [org-str]
  (-> org-str pandoc/from-org pandoc/to-html-standalone))

(defn measure [path]
  (let [s (slurp path)
        before-measure-ms (System/currentTimeMillis)
        html (org->html s)
        after-measure-ms (System/currentTimeMillis)]
    {:path path
     :ms (- after-measure-ms before-measure-ms)
     :org-length (count s)
     :html-length (count html)}))

(comment
  ;; re-measure
  (future (spit "measurements.edn" (mapv measure org-files)))
  )

^{::clerk/budget nil ::clerk/auto-expand-results? true
  ::clerk/page-size nil}
(->> "measurements.edn"
     slurp read-string
     (sort-by (juxt :org-length :ms))
     clerk/table)

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
 ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
 ((requiring-resolve 'clojure.repl.deps/sync-deps))
 (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
 (clerk/clear-cache!))
