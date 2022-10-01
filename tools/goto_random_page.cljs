(ns goto-random-page
  (:require [clojure.edn :as edn]
            [ajax.core :refer [GET]]))

(defn goto-random-page []
  (GET "/index/big.edn" {:handler (fn [big-index-str]
                                    (let [page (->> big-index-str
                                                    edn/read-string
                                                    (remove #(contains? % :noindex))
                                                    rand-nth)
                                          page-link (str "/" (:slug page) "/")]
                                      (set! (.-href js/window.location) page-link)))}))

(set! (.-goto_random_page js/window) goto-random-page)
