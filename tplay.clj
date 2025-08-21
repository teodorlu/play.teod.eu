#!/usr/bin/env bb

(require '[tplay.cli])

(when (= *file* (System/getProperty "babashka.file"))
  (apply tplay.cli/main *command-line-args*))
