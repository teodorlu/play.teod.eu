(ns preview
  (:require
   [replicant.string]
   [org.httpkit.server :as httpkit]
   [clojure.java.browse]))

(defn handler [req]
  (let [pages (:site/pages req)]
    (cond (and (= (:request-method req) :get)
               (contains? pages (:uri req)))
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (str "<!DOCTYPE html>\n"
                      (replicant.string/render ((get pages (:uri req)))))}

          :else
          {:status 404})))

(defonce !server (atom nil))

(defn stop-server! [] (swap! !server #(do (when % (httpkit/server-stop! %)) nil)))

(defn start-server! [inject]
  (stop-server!)
  (reset! !server (httpkit/run-server (comp #'handler inject)
                                      {:legacy-return-value? false
                                       :port 7799})))

(defn browse! [inject]
  (when-not @!server (start-server! inject))
  (clojure.java.browse/browse-url "http://localhost:7799"))
