#!/usr/bin/env bb

(require '[babashka.deps])

(babashka.deps/add-deps
 '{:deps {org.clj-commons/hickory {:mvn/version "0.7.5"}
          no.cjohansen/lookup {:mvn/version "2024.11.03"}}})

(require
 '[hickory.core :as hickory]
 '[lookup.core :as lookup])

(def refs-url "https://worrydream.com/refs/")

(def ref-anchor
  (->> refs-url
       slurp
       hickory/parse
       hickory/as-hiccup
       (lookup/select :a)
       rand-nth))

(println "Visiting reference:" (lookup/get-text ref-anchor))

(require 'clojure.java.browse)
(defn resolve-url [url] (str refs-url url))
(clojure.java.browse/browse-url (-> ref-anchor lookup/attrs :href resolve-url))
