(ns a-proxy-mate.scryfall-api
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http])
  (:import
    [java.net URI]
    [javax.net.ssl SNIHostName SNIServerName SSLEngine SSLParameters]))

(def base-url "https://api.scryfall.com")
(def fuzzy-url (str base-url "/cards/named?fuzzy="))
(def relevant-details ["name" "mana_cost" "type_line" "oracle_text" "power" "toughness"])

 (defn sni-configure
  [^SSLEngine ssl-engine ^URI uri]
  (let [^SSLParameters ssl-params (.getSSLParameters ssl-engine)]
    (.setServerNames ssl-params [(SNIHostName. (.getHost uri))])
    (.setSSLParameters ssl-engine ssl-params)))

(def client (http/make-client {:ssl-configurer sni-configure}))

(defn fuzzy-search
  "Hit up scryfall to find a card using the card name fuzzy search."
  [term]
  (-> @(http/get (str fuzzy-url (http/url-encode term)) {:client client})
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
