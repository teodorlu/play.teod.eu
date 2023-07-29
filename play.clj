#!/usr/bin/env bb

(require '[teod.play.cli :as play.cli])

(when (= *file* (System/getProperty "babashka.file"))
  (apply play.cli/main *command-line-args*))
