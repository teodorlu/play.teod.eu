(ns teod.play.api-test
  (:require
   [clojure.test :refer [deftest is]]
   [teod.play.api :as play]))

(deftest today-str-test
  (is (= (count "YYYY-MM-DD")
         (count (play/today-str)))))
