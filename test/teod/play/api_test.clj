(ns teod.play.api-test
  (:require
   [clojure.test :refer [deftest is]]
   [tplay.api :as play]))

(deftest today-str-test
  (is (= (count "YYYY-MM-DD")
         (count (play/today-str)))
      "today-str returns an iso timestamp"))

(deftest git-infer-created-date-test
  (is (some? (play/git-infer-created-date "deps.edn")))
  (is (not (some? (play/git-infer-created-date "file that does not exist")))))

(deftest files->relations-test
  (let [rels (play/files->relations)]
    (is (= :remote-reference (get-in rels ["rich-hickey" :form])))))

(deftest page-slugs-test
  (is (contains? (into #{} (play/page-slugs))
                 "rich-hickey")))

(deftest files->relations2-test
  (let [titles (into #{} (map :title (play/files->relations2)))]
    (is (contains? titles "Rich Hickey"))))
