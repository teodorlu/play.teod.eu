(ns index2
  (:require
   [clojure.java.io :as io]
   [hiccup2.core]
   [org.httpkit.server :as httpkit]))

(hiccup2.core/html [:p "hei"])

;; Goal: reproduce the front page of https://play.teod.eu/ without piping everything into Org-mode!

(defn render-html [path]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (io/file path)})

(defn handler [{:keys [uri]}]
  (case uri
    "/old" (render-html "../index.html")
    "/landing2" (render-html "../landing2/index.html")
    {:status 404
     :body "not found"}))

(defonce server nil)

(defn start! [{:keys [port]}]
  (alter-var-root
   #'server
   (fn [old-server]
     (when old-server (httpkit/close server))
     (httpkit/run-server
      #'handler
      {:port (or port 8998)
       :legacy-return-value? false}))))
#_(start! {})
#_((requiring-resolve 'clojure.java.browse/browse-url) "http://localhost:8998")
