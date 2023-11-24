#!/usr/bin/env bb

(ns ahoy.server
  (:require 
    [clojure.string :as str]
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

(defn home []
  (->> (fetch-matches url)
       get-index
       wrap-with-200))

(defn match [uri]
  (->> uri
       fetch-and-get-m3u8
       get-match
       wrap-with-200))

(defn m3u8 [uri]
  (->> uri
       get-match
       wrap-with-200))

(defn app [req]
  (let [raw-uri (:uri req)
        uri (if (str/starts-with? raw-uri "/")
                (str/replace-first raw-uri "/" "")
                raw-uri)]
    (println req)
    (cond 
      (str/includes? uri "m3u8") (m3u8 uri)
      (str/includes? uri "http") (match uri)
      :else (home))))

(defn -main [& _]
  (println "Server started")
  (run-server #'app {:port port})
  @(promise))
