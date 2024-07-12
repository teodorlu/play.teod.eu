(ns user)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clerk-start! []
  (let [clerk-serve (requiring-resolve 'nextjournal.clerk/serve!)
        clerk-port 7846]
    (clerk-serve {:browse? true :port clerk-port})))

#_ (nextjournal.clerk/show! "easy_parallellism_with_pipeline_blocking.clj")
