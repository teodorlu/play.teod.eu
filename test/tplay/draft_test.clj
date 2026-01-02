(ns tplay.draft-test
  (:require [babashka.fs :as fs]
            [clojure.test :refer [deftest is]]
            [tplay.draft :as draft]))

(deftest find-org-title
  (is (= "Consider in-lining"
         (draft/find-org-title "#+TITLE: Consider in-lining"))))

(deftest infer-slug
  (is (= "consider-inlining"
         (draft/find-slug "drafts/consider-inlining.org")
         (draft/find-slug (fs/file "drafts/consider-inlining.org")))))
