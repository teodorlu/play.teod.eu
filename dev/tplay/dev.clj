(ns tplay.dev
  (:require
   [babashka.process :as p]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.webserver :as clerk-server]))

(def lifetimectl
  {:system/live-server {:start #(p/process "npx live-server --port=3000")
                        :stop p/destroy}})

(defonce system (atom nil))

(defn stop! [k]
  (when-let [instance (get @system k)]
    (when-let [stop-fn (get-in lifetimectl [k :stop])]
      (stop-fn instance)
      (swap! system dissoc k))))

(defn start! [k]
  (set! *print-namespace-maps* false)
  (stop! k)
  (swap! system assoc k ((get-in lifetimectl [k :start]))))

;; Lifetime controls
(comment ;; s-:
  (start! :system/live-server)
  (stop! :system/live-server)

  (clerk/serve! {})
  (clerk/halt!)
  (clerk-server/browse!)

  )
