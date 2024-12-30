(ns tplay.nopandoc
  (:require
   [clojure.java.io :as io]
   [hiccup2.core]
   [org.httpkit.server :as httpkit]
   [tplay.index]
   [tplay.nopandoc.landing]))

;; Goals:
;;
;; - Replace the landing page on https://play.teod.eu/
;; - Insert the nice principles component from https://go.teod.eu/ up top
;; - Retain a "good enough" index of the stuff below
;; - Avoid piping Org-mode syntax through Pandoc.

(defn render-htmlfile [path]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (io/file path)})

(def the-pages
  {"/old"
   {:kind ::htmlfile
    :htmlfile "index.html"}

   "/landing3"
   {:kind ::fn
    :fn #'tplay.nopandoc.landing/handler}})

(defn render [{:keys [the-pages]}
              {:as page :keys [kind]}]
  (case kind
    ::htmlfile (render-htmlfile (:htmlfile page))
    ::fn ((:fn page) {:the-pages the-pages})
    {:status 500
     :body (str "Unsupported kind: " kind)}))

(defn index [pages]
  (list [:p "Pages:"]
        [:ul
         (for [uri (keys pages)]
           [:li [:a {:href uri} uri]])]))

(defn hiccup? [response]
  (or (vector? response)
      (list? response)
      (string? response)))

(defn ->http-response [response]
  (if (hiccup? response)
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str "<!DOCTYPE html>"
                (hiccup2.core/html {}
                  [:html [:body response]]))}
    response))

(defn handler [{:as req :keys [uri]}]
  (-> (cond
        (= uri "/")
        (index the-pages)

        (contains? the-pages uri)
        (render {:the-pages (:the-pages req)}
                (get the-pages uri))

        :else
        {:status 404
         :body "not found"})
      ->http-response))

(defonce server nil)

(defn ^:export start! [{:keys [port]}]
  (alter-var-root
   #'server
   (fn [old-server]
     (when old-server (httpkit/server-stop! server))
     (httpkit/run-server
      (fn [req]
        (-> req
            (assoc :the-pages (tplay.index/find-pages))
            handler))
      {:port (or port 8998)
       :legacy-return-value? false}))))
#_(start! {})
#_((requiring-resolve 'clojure.java.browse/browse-url) "http://localhost:8998")
