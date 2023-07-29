(ns teod.play.api-test
  (:require
   [clojure.test :refer [deftest is]]
   [teod.play.api :as play]))

(deftest today-str-test
  (is (= (count "YYYY-MM-DD")
         (count (play/today-str)))
      "today-str returns an iso timestamp"))

(deftest git-infer-created-date-test
  (is (some? (play/git-infer-created-date "deps.edn")))
  (is (not (some? (play/git-infer-created-date "file that does not exist")))))

(deftest pages-test
  (is (contains? (into #{} (play/pages))
                 {:slug "rich-hickey"})))
