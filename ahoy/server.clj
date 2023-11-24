#!/usr/bin/env bb

(ns ahoy.server
  (:require 
    [ahoy.html :refer [get-index get-match]]
    [ahoy.rest :refer [fetch-matches fetch-and-get-m3u8]]
    [ahoy.config :refer [port url]]
    [org.httpkit.server :refer [run-server]]))


(defn wrap-with-200 [page]
  {:status 200
   :headers {
      "Content-Type" "text/html"
      "Cache-Control" "max-age=7200"
    }
   :body page})

(defn home [_]
  (->> (fetch-matches url)
       get-index
       wrap-with-200))

(defn match [req]
  (->> (subs (:uri req) 1)
       fetch-and-get-m3u8
       get-match
       wrap-with-200))

(defn app [req]
  (let [uri (:uri req)
        handler (if (re-find #"http" uri)
                  match
                  home)]
    (handler req)))

(defn -main [& _]
  (println "Server started")
  (run-server #'app {:port port})
  @(promise))
