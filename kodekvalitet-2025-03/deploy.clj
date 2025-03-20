(ns deploy
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]))

(p/shell {:out :string} "pwd")

(fs/copy-tree "." (fs/expand-home "~/dev/teodorlu/play.teod.eu/kodekvalitet-2025-03"))

{:replace-existing true}
