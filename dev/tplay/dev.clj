(ns tplay.dev
  "In-REPL dev environment"
  (:require
   [babashka.process :as p]
   [clojure.java.browse :refer [browse-url]]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.webserver :as clerk-server]))

;; -----------------------------------------------------------------------------
;;           LIVE SERVER
;; -----------------------------------------------------------------------------

(def live-server-opts {:port 3000})
(def live-server-command
  (str/join " " ["npx live-server"
                 "--no-browser"
                 (str "--port=" (:port live-server-opts))]))



;; -----------------------------------------------------------------------------
;;           LIFETIME CONTROL
;; -----------------------------------------------------------------------------

(def lifetimectl
  {:system/live-server {:start #(p/process live-server-command)
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

;; -----------------------------------------------------------------------------
;;           REPL UI
;; -----------------------------------------------------------------------------

(defn preview! []
  (start! :system/live-server)
  (Thread/sleep 500) ; I'm sorry.
  (browse-url (str "http://localhost:" (:port live-server-opts))))

(comment ;; s-:
  (start! :system/live-server)
  (stop! :system/live-server)

  (preview!)

  (clerk/serve! {})
  (clerk/halt!)
  (clerk-server/browse!)

  )
