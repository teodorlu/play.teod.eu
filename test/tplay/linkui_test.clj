(ns tplay.linkui-test
  (:require [clojure.test :refer [deftest is]]
            [tplay.linkui :as linkui]))

(deftest ->link
  (is (= (linkui/->link {:title "\"Strong opinions loosely held\" is an excuse for sloppy thinking"
                         :uuid "bd1be8c0-9227-4f87-9e9e-86b0f5903d5d"})
         "[[id:bd1be8c0-9227-4f87-9e9e-86b0f5903d5d][\"Strong opinions loosely held\" is an excuse for sloppy thinking]]")))
