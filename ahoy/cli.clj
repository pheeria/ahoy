#!/usr/bin/env bb

(ns ahoy.cli
  (:require 
    [bblgum.core :refer [gum]]
    [ahoy.html :refer [get-match]]
    [ahoy.rest :refer [fetch-matches fetch-and-get-m3u8]]))

(defn -main [& _]
  (let [choices (->> (fetch-matches)
                     (reduce #(assoc %1
                                     (str (:home_en %2) " - " (:away_en %2))
                                     (:stream %2))
                             {}))

        title   (->> (keys choices)
                     (gum :choose)
                     :result
                     first)

        hls     (->> (get choices title)
                     fetch-and-get-m3u8)]
  (spit (str title ".html")
        (get-match hls))))
