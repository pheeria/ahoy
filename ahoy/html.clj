#!/usr/bin/env bb

(ns ahoy.html
  (:require 
    [hiccup2.core :refer [html]]
    [clojure.string :refer [replace]]))


(def index-page (slurp "public/index.html"))
(def match-page (slurp "public/match.html"))

(defn html->str [source]
  (str
    (html
        {:mode :html :escape-strings? false}
        source)))

(defn unordered-list [matches]
  [:ul
   (map
     (fn [{stream :stream stream_smart :stream_smart
           home_en :home_en away_en :away_en
           home_logo :home_logo away_logo :away_logo}]
       [:li
        [:a {:href (str "/" (or stream_smart stream))}
         [:img {:src home_logo :alt away_en}]
         (str home_en " - " away_en)
         [:img {:src away_logo :alt away_en}]]])
     matches)])

(defn get-index [matches]
  (->> matches
       unordered-list
       html->str
       (replace index-page #"<REPLACE>")))

(defn get-match [match]
  (if (nil? match)
        match-page
        (replace match-page #"<REPLACE>" match)))

(or "" "he")
