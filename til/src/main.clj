(ns main
  (:require
   [preview]
   [site]))

(defn preview! [] (preview/start-server! #'site/inject))
(defn browse! [] (preview/browse! #'site/inject))
