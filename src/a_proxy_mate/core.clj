(ns a-proxy-mate.core
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
))

(def base-url "https://api.scryfall.com")
(def fuzzy-url (str base-url "/cards/named?fuzzy="))
(def relevant-details ["name" "mana_cost" "type_line" "oracle_text" "power" "toughness"])

(defn fuzzy-search
  [term]
  (-> @(http/get (str fuzzy-url (http/url-encode term)))
      :body
      json/read-str))

(defn keywordize-key
  [[k v]]
  [(keyword k) v])

(defn extract-details
  [result]
  (-> result
      (select-keys relevant-details)))

(extract-details (fuzzy-search "lightning bolt"))

#_'("released_at" "flavor_text" "object" "oracle_id" "related_uris" "foil" "lang" "tcgplayer_id" "collector_number" "rarity" "set_name" "oversized" "image_uris" "reserved" "rulings_uri" "games" "digital" "type_line" "uri" "multiverse_ids" "watermark" "reprint" "id" "mtgo_foil_id" "scryfall_set_uri" "prints_search_uri" "booster" "set_uri" "name" "scryfall_uri" "prices" "full_art" "oracle_text" "purchase_uris" "mtgo_id" "layout" "nonfoil" "card_back_id" "cmc" "edhrec_rank" "textless" "highres_image" "legalities" "border_color" "set_search_uri" "set_type" "set" "variation" "story_spotlight" "promo" "color_identity" "frame" "mana_cost" "colors" "artist" "illustration_id")















(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
