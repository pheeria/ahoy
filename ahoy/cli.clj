#!/usr/bin/env bb

(ns ahoy.cli
  (:require 
    [bblgum.core :refer [gum]]
    [hiccup2.core :refer [html]]
    [cheshire.core :refer [parse-string]]))


(def events
  (parse-string (slurp "https://varline.store/api/matchlist")
                     true))

(def choices
  (->> events
     (filter #(= "Футбол" (:sport %)))
     (map #(select-keys % [:stream :home_en :away_en]))
     (reduce #(assoc %1
                     (str (:home_en %2) " - " (:away_en %2))
                     (:stream %2))
             {})))

(def title
  (->> (keys choices)
       (gum :choose)
       :result
       first))

(def hls
  (->> (get choices title)
       slurp
       (re-find #"http.*\.m3u8")))

(defn head [title]
  [:head
   [:title title]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:style "html, body, video { margin: 0; padding: 0; width: 100%; height: 100%; background: #1c1c1c; }"]
   [:script {:src "https://cdn.jsdelivr.net/npm/hls.js@1"}]])

(defn body [video-source]
  [:body
   [:video {:id "video" :controls true}]
   [:script (str "var video = document.getElementById('video');"
                 "var videoSrc = '" video-source "';"
                 "if (Hls.isSupported()) {"
                 "  var hls = new Hls();"
                 "  hls.loadSource(videoSrc);"
                 "  hls.attachMedia(video);"
                 "}")]])

(spit "cracked.html"
              (html 
                {:mode :html :escape-strings? false}
                [:html
                 (head title)
                 (body hls)]))
