;; # I don't understand my own code 😂
;;
;; Let's fix that.

(ns teod.play.understand
  (:require
   [teod.play.cli :as cli]
   [teod.play.api :as api]
   [eu.teod.clerk-utils :as hammertime]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]))

;; Can I query for the relations from code?

(hammertime/doc cli/cmd-relations)

;; That did _not help_.

(->> (cli/files->relations {})
     vals
     (sort-by :slug))

;; There we go!

(defn big-table [x]
  (v/with-viewer (dissoc v/table-viewer :page-size)
    x))

(defn assoc-url [relation]
  (let [{:keys [slug]} relation]
    (assoc relation
           :link
           (clerk/html [:a {:href (str "https://play.teod.eu/" slug)} slug]))))

^{::clerk/width :full}
(big-table
 (->> (cli/files->relations {})
      vals
      (sort-by :slug)
      (map assoc-url)))


^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
