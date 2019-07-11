(ns a-proxy-mate.core
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(def base-url "https://api.scryfall.com")
(def fuzzy-url (str base-url "/cards/named?fuzzy="))
(def relevant-details ["name" "mana_cost" "type_line" "oracle_text" "power" "toughness"])
(def max-chars 32)

(defn fuzzy-search
  [term]
  (-> @(http/get (str fuzzy-url (http/url-encode term)))
      :body
      json/read-str))

(def memoized-search (memoize fuzzy-search))

(defn keywordize-key
  [[k v]]
  [(keyword k) v])

(defn extract-details
  [result]
  (->> (-> result
           (select-keys relevant-details))
       (map keywordize-key)
       (into {})))

(defn lr-align
  [left-text right-text]
  (let [l-count (count left-text)
        r-count (count right-text)
        gap-count (max 1 (- max-chars l-count r-count))]
    (str left-text
         (apply str (take gap-count (repeat " ")))
         right-text)))

(defn border
  []
  (apply str (take max-chars (repeat "-"))))

(defn wrap-text
  [text]
  ;; @TODO: need to take existing paragraphs into account (see
  ;; emrakul, the promised end)
  (->> (partition-all max-chars text)
       (map #(apply str %))
       (map #(str % "\n"))
       (apply str)))

(defn format-proxy
  [{:keys [name mana_cost type_line oracle_text power toughness]}]
  (str (border) "\n\n"
       (wrap-text (lr-align name mana_cost)) "\n\n"
       (wrap-text type_line) "\n\n"
       (wrap-text oracle_text) "\n\n"
       (when (and power toughness)
         (str power "/" toughness "\n\n"))
       (border) "\n"))

(defn generate-proxy
  [search-term]
  (-> search-term
      memoized-search
      extract-details
      format-proxy))

;; (println (generate-proxy "lightning bolt"))
;; (println (generate-proxy "ghalta"))

(defn -main
  [& args]
  ;; @TODO: can we straight up write out to the printer on the serial port?
  (println (generate-proxy (first args))))
