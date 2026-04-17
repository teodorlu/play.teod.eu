(ns tplay.writer
  "Deploy ~/code/writer to play.teod.eu"
  (:require [babashka.fs :as fs]
            [tplay.templater :as templater]))

(comment

  (def source-folder (fs/expand-home "~/code/writer"))

  (def bundle
    (merge
     (templater/file->bundle (fs/file source-folder "index.html"))
     (templater/folder->bundle (fs/file source-folder "js") source-folder)
     (templater/folder->bundle (fs/file source-folder "css") source-folder)))

  (templater/write bundle (fs/file "writer"))

  )
