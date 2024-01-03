#!/usr/bin/env bb

(ns ahoy.rest
  (:require 
    [cheshire.core :refer [parse-string]]))

(defn fetch-matches [url]
  (let [today (.getDayOfYear (java.time.LocalDateTime/now))]
    (->> (parse-string (slurp url) true)
       (filter #(= "Футбол" (:sport %)))
       (filter #(= today (.getDayOfYear (java.time.ZonedDateTime/parse (:start %)))))
       (remove #(re-find #"Russia|Brasil|Saudi|Austria|Ukraine" (:league_en %)))
       (map #(select-keys % [:stream :stream_smart :home_en :away_en :home_logo :away_logo])))))

(defn fetch-and-get-m3u8 [stream-link]
  (->> (slurp stream-link)
       (re-find #"http.*\.m3u8")))
