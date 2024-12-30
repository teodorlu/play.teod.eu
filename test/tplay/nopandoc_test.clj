(ns tplay.nopandoc-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [tplay.nopandoc :as nopandoc]))

(deftest render-test
  (is (str/starts-with?
       (-> (nopandoc/render {}
                            {:kind ::nopandoc/htmlfile
                             :htmlfile "index.html"})
           :body
           slurp)
       "<!DOCTYPE html>")))
