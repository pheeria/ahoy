#!/usr/bin/env bb

(ns ahoy.server
  (:require 
    [clojure.string :refer [includes? starts-with? replace-first]]
    [ahoy.html :refer [get-index get-match]]
    [ahoy.rest :refer [fetch-matches fetch-and-get-m3u8]]
    [ahoy.config :refer [port url]]
    [org.httpkit.server :refer [run-server]]))


(defn wrap-with-200 [period page]
  {:status 200
   :headers {
      "Content-Type" "text/html"
      "Cache-Control" (str "max-age=" period)
    }
   :body page})

(defn home []
  (->> (fetch-matches url)
       get-index
       (wrap-with-200 "7200")))

(defn match [uri]
  (->> uri
       fetch-and-get-m3u8
       get-match
       (wrap-with-200 "10840")))

(defn m3u8 [uri]
  (->> uri
       get-match
       (wrap-with-200 "31536000, immutable")))

(defn app [req]
  (let [raw-uri (:uri req)
        uri (if (starts-with? raw-uri "/")
                (replace-first raw-uri "/" "")
                raw-uri)]
    (println req)
    (cond 
      (includes? uri "m3u8") (m3u8 uri)
      (includes? uri "http") (match uri)
      :else (home))))

(defn -main [& _]
  (println "Server started")
  (run-server #'app {:port port})
  @(promise))
