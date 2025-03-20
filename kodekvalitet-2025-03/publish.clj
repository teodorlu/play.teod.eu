(ns publish
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]))

(def publish-dir (fs/expand-home "~/dev/teodorlu/play.teod.eu/kodekvalitet-2025-03"))

(defn publish! []
  (fs/copy-tree "." publish-dir {:replace-existing true})
  (p/shell {:dir publish-dir} "git add .")
  (p/shell {:dir publish-dir} "git commit -m \"kodekvalitet-2025-03 deploy\"")
  (p/shell {:dir publish-dir} "git push"))
