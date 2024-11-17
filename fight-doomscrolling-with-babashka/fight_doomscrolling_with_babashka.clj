;; # Fight doomscrolling with Babashka
;;
;; Endlessly consuming dopamin packets from companies that sell your attention supposedly rots your brain.
;; What can you do instead?

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns fight-doomscrolling-with-babashka
  (:require
   [lookup.core :as lookup]
   [hickory.core :as hickory]))

;; ## Following references rather than consuming from an algorithm.

^{:nextjournal.clerk/visibility {:code :hide}}
(let [relations ((requiring-resolve 'tplay.cli/files->relations) {})]
  (defn page-uuid [page] (or (:page/uuid page) (:uuid page)))
  (def uuid->page (->> relations vals (map (juxt page-uuid identity)) (filter first) (into {})))
  (def bret-victor-uuid "00f83e62-617d-48d4-be92-e9dab6e473ec")
  (def bret-victor (some-> uuid->page (get bret-victor-uuid))))

;; Certain people just have a lot of interesting things to say.

(->> (tplay.cli/files->relations {})
     )

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  ,)
