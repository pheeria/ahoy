#!/usr/bin/env bb

(ns server
  (:require 
    [all :refer [all-matches]]
    [match :refer [the-match]]
    [org.httpkit.server :refer [run-server]]))

(defn not-found [_]
  {:status 404
   :body "Not Found"})

(defn home [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (all-matches))})

(defn game [stream]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (the-match (subs (:uri stream) 1)))})

(defn app [req]
  (let [uri (:uri req)
        handler (if (re-find #"http" uri)
                  game
                  home)]
    (handler req)))

(defn -main [& _]
  (println "Starting...")
    (run-server #'app {:port 6900})
    @(promise))

