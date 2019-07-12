(ns a-proxy-mate.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [org.httpkit.client :as http]))

(def base-url "https://api.scryfall.com")
(def fuzzy-url (str base-url "/cards/named?fuzzy="))
(def relevant-details ["name" "mana_cost" "type_line" "oracle_text" "power" "toughness"])
(def max-chars 32)
(def serial-file-descriptor "/dev/serial0")

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

(defn lr-align
  "Construct a string with one part aligned left and the other aligned
  right."
  [left-text right-text]
  (let [l-count (count left-text)
        r-count (count right-text)
        gap-count (max 1 (- max-chars l-count r-count))]
    (str left-text
         (apply str (take gap-count (repeat " ")))
         right-text)))

(defn border
  "Generate a hyphen border."
  []
  (apply str (take max-chars (repeat "-"))))

(defn wrap-line
  [line]
  (->> (partition-all max-chars line)
       (map #(apply str %))
       (map #(str % "\n"))
       (apply str)))

(defn wrap-text
  [text]
  (->> (s/split text #"\n")
       (map wrap-line)
       (apply str)))

(defn replace-funky-chars
  "The printer can't do all characters."
  [text]
  (-> text
      (clojure.string/replace "—" "-")))

(defn format-proxy
  "Format the string for a proxy ready to be printed."
  [{:keys [name mana_cost type_line oracle_text power toughness]}]
  (-> (str (border) "\n"
           (wrap-text (lr-align name mana_cost)) "\n\n"
           (wrap-text type_line) "\n\n"
           (wrap-text oracle_text) "\n\n"
           (when (and power toughness)
             (str power "/" toughness "\n\n"))
           (border) "\n\n")
      replace-funky-chars))

(defn generate-proxy
  "Generate a proxy from a search term."
  [search-term]
  (-> search-term
      memoized-search
      extract-details
      format-proxy))

(defn print-proxy
  "Send a proxy over the serial port to the printer."
  [proxy]
  (spit serial-file-descriptor proxy))

(defn -main
  [& args]
  (let [search-term (first args)
        copies (if (second args)
                 (read-string (second args))
                 1)]
    (doseq [i (range (int copies))]
      (-> search-term
          generate-proxy
          print-proxy))))
