(ns goto-random-page
  (:require [clojure.edn :as edn]
            [ajax.core :refer [GET]]))

(defn goto-random-page []
  (GET "/index/big.edn" {:handler (fn [big-index-str]
                                    (let [page (->> big-index-str
                                                    edn/read-string
                                                    (remove #(contains? % :noindex))
                                                    rand-nth)]
                                      (set! (.-href js/window.location) (str "/" (:slug page) "/"))))}))

(set! (.-goto_random_page js/window) goto-random-page)
