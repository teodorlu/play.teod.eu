#!/usr/bin/env bb

(require 'tplay.index)

(if (= (System/getenv "ALT")
       "1")
  (tplay.index/alt)
  (tplay.index/main))
