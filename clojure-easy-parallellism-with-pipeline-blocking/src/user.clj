(ns user)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clerk-start! []
  (require 'nextjournal.clerk)
  (let [clerk-port 7297]
    (nextjournal.clerk/serve! {:browse? true :port clerk-port})))

#_ (nextjournal.clerk/show! "src/easy_parallellism_with_pipeline_blocking.clj")
