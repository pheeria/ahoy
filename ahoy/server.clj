#!/usr/bin/env bb

(ns ahoy.server
  (:require 
    [ahoy.all :refer [all-matches]]
    [ahoy.match :refer [the-match]]
    [ahoy.html :refer [get-index get-match]]
    [cheshire.core :refer [parse-string]]
    [org.httpkit.server :refer [run-server]]))

(def matches
  (->> (parse-string (slurp "https://varline.store/api/matchlist") true)
     (filter #(= "Футбол" (:sport %)))
     (remove #(re-find #"Russia" (:league_en %)))
     (map #(select-keys % [:stream :home_en :away_en :home_logo :away_logo]))))

(defn home [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (get-index matches)})

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
  (println "Server started")
  (run-server #'app {:port 6900})
  @(promise))

