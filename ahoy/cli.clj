#!/usr/bin/env bb
(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {io.github.lispyclouds/bblgum {:git/sha "b450a7608ef44267b52674011721856d77007fa0"}}})

(ns ahoy.cli
  (:require 
    [ahoy.html :refer [get-match]]
    [bblgum.core :refer [gum]]
    [ahoy.rest :refer [fetch-matches]]
    [ahoy.config :refer [url]]))


(defn wrap-m3u8 [hls]
  (spit "m3u8.html"
        (get-match hls)))

(defn -main [& _]
  (let [choices (->> (fetch-matches url)
                     (reduce #(assoc %1
                                     (str (:home_en %2) " - " (:away_en %2))
                                     (:stream_smart %2))
                             {}))

        title   (->> (keys choices)
                     (gum :choose)
                     :result
                     first)

        hls     (get choices title)]
  (spit (str title ".html")
        (get-match hls))))
