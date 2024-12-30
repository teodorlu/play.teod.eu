(ns tplay.nopandoc.landing-test
  (:require
   [clojure.set :as set]
   [clojure.test :refer [deftest is testing]]
   [tplay.index]
   [tplay.nopandoc.landing :as landing]))

(def the-pages (tplay.index/find-pages))

(deftest page-categories
  (is (set/subset? (set (map landing/category the-pages))
                   #{:other
                     :page-category/deprecated
                     :page-category/forever-incomplete
                     :page-category/noindex
                     :page-category/norwegian
                     :page-category/ready-for-comments
                     :page-category/remote-reference
                     :page-category/wtf-is-this
                     :page-category/wtf-is-this-norwegian})))

(deftest norwegian-page-link
  (testing "page without date"
    (is (= (list [:a {:href "/sannhet-deskriptiv-preskriptiv/"}
                  "Deskriptiv sannhet, preskriptiv sannhet"]
                 nil)
           (landing/norwegian-page-link
            {:id "sannhet-deskriptiv-preskriptiv",
             :title "Deskriptiv sannhet, preskriptiv sannhet",
             :lang :no,
             :form :presentation,
             :readiness :ready-for-comments,
             :uuid "0c0b16ef-47dc-449c-b665-07c278ffec32",
             :category :norwegian}))))
  (testing "page with date"
    (is (= (list [:a {:href "/teknologi-for-produkt-intro/"}
                  "Teknologi for å bygge produkt---en introduksjon"]
                 " (2023-06-13)")
           (landing/norwegian-page-link {:id "teknologi-for-produkt-intro",
                                         :author-url "https://teod.eu",
                                         :created "2023-06-13",
                                         :lang :no,
                                         :readiness :ready-for-comments,
                                         :title "Teknologi for å bygge produkt---en introduksjon",
                                         :uuid "224c548c-b444-4557-86a5-9056a393548f",
                                         :category :norwegian}))))
  )
