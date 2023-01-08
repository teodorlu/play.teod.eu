(ns drew.repl)

(require '[compojure.core :refer :all])

(defroutes myapp
  (GET "/" [] "Hello World"))

(myapp {:uri "/" :request-method :get})
;; => {:status 200, :headers {"Content-Type" "text/html; charset=utf-8"}, :body "Hello World"}

;; when I run this in my REPL, I don't get a crash -- i get the map above.
