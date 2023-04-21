#!/usr/bin/env bb

(ns ahoy.rest
  (:require 
    [cheshire.core :refer [parse-string]]))


(defn fetch-matches []
  (->> (parse-string (slurp "https://varline.store/api/matchlist") true)
     (filter #(= "Футбол" (:sport %)))
     (remove #(re-find #"Russia" (:league_en %)))
     (map #(select-keys % [:stream :home_en :away_en :home_logo :away_logo]))))

(defn fetch-and-get-m3u8 [stream-link]
  (->> (slurp stream-link)
       (re-find #"http.*\.m3u8")))
