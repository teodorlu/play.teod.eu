;; # I don't understand my own code 😂
;;
;; Let's fix that.

(ns tplay.dev.understand
  {:nextjournal.clerk/toc true}
  (:require
   [tplay.cli :as cli]
   [eu.teod.clerk-utils :as hammertime]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]
   [babashka.fs :as fs]))

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

;; ## All the pages

^{::clerk/width :full}
(big-table
 (->> (cli/files->relations {})
      vals
      (sort-by :slug)
      (map assoc-url)))

;; ## Only pages where `:builder` is set

^{::clerk/width :full}
(big-table
 (->> (cli/files->relations {})
      vals
      (filter :builder)
      (sort-by :slug)
      (map assoc-url)))

;; For the pages where `:builder` is set, what is the folder content?

^{::clerk/width :full}
(big-table
 (->> (cli/files->relations {})
      vals
      (filter :builder)
      (sort-by :slug)
      (map #(assoc % :dir (sort (map str (fs/list-dir (:slug %))))))))

;; yess!
;;
;; jeg kan tydeligvis sette `:builder :no-op`, men jeg har kun satt det for `scicloj-libraries`.

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
