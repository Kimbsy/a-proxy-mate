(ns a-proxy-mate.scryfall-api
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def base-url "https://api.scryfall.com")
(def fuzzy-url (str base-url "/cards/named?fuzzy="))
(def relevant-details ["name" "mana_cost" "type_line" "oracle_text" "power" "toughness"])

(defn fuzzy-search
  "Hit up scryfall to find a card using the card name fuzzy search."
  [term]
  (-> @(http/get (str fuzzy-url (http/url-encode term)))
      :body
      json/read-str))

;; Memoize the search function so we're not spamming the API during
;; tests.
(def memoized-search (memoize fuzzy-search))

(defn keywordize-key
  [[k v]]
  [(keyword k) v])

(defn extract-details
  "Grab the fields we care about and turn them into a keyword => string
  map."
  [result]
  (->> (-> result
           (select-keys relevant-details))
       (map keywordize-key)
       (into {})))
