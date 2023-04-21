#!/usr/bin/env bb

(ns ahoy.all
  (:require 
    [hiccup2.core :refer [html]]
    [cheshire.core :refer [parse-string]]))

(defn head
  []
  [:head
   [:title "All matches"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:style (str "body { background-color: #0f0f23; }"
                "li {
                  margin: 0.6em;
                  list-style: none;
                  border: 0.1em solid #ebebeb;
                  border-radius: 0.4em;
                  font-size: 1.6em;
                }"
                "a {
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  text-decoration: none;
                  color: #ffffff;
                }"
                "img { height: 3em; margin: 0.4em 2em; }")]])

(defn body
  [matches]
  [:body
   [:ul
    (map
      (fn [{stream :stream home_en :home_en away_en :away_en  home_logo :home_logo away_logo :away_logo}]
        [:li
         [:a {:href (str "/" stream)}
          [:img {:src home_logo :alt away_en}]
          (str home_en " - " away_en)
          [:img {:src away_logo :alt away_en}]]])
      matches)]])
                 
(def matches
  (->> (parse-string (slurp "https://varline.store/api/matchlist") true)
     (filter #(= "Футбол" (:sport %)))
     (remove #(re-find #"Russia" (:league_en %)))
     (map #(select-keys % [:stream :home_en :away_en :home_logo :away_logo]))))


(defn all-matches
  []
  (html 
    {:mode :html :escape-strings? false}
    [:html
     (head)
     (body matches)]))


(comment (spit "index.html"
      (html 
        {:mode :html :escape-strings? false}
        [:html
         (head)
         (body matches)])))
