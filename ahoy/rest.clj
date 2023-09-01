#!/usr/bin/env bb

(ns ahoy.rest
  (:require 
    [cheshire.core :refer [parse-string]]))


(defn fetch-matches []
  (let [today (.getDayOfYear (java.time.LocalDateTime/now))]
    (->> (parse-string (slurp "https://varline.store/api/matchlist") true)
       (filter #(= "Футбол" (:sport %)))
       (filter #(= today (.getDayOfYear (java.time.ZonedDateTime/parse (:start %)))))
       (remove #(re-find #"Russia|Brasil|Turkey|Saudi|Austria|Ukraine" (:league_en %)))
       (map #(select-keys % [:stream :home_en :away_en :home_logo :away_logo])))))

(defn fetch-and-get-m3u8 [stream-link]
  (->> (slurp stream-link)
       (re-find #"http.*\.m3u8")))
