(ns a-proxy-mate.core
  (:require [a-proxy-mate.scryfall-api :as api]
            [a-proxy-mate.thermal-printer :as tp]))

(defn format-proxy
  "Format the string for a proxy ready to be printed."
  [{:keys [name mana_cost type_line oracle_text power toughness]}]
  (-> (str (tp/border) "\n"
           (tp/wrap-text (tp/lr-align name mana_cost)) "\n\n"
           (tp/wrap-text type_line) "\n\n"
           (tp/wrap-text oracle_text) "\n\n"
           (when (and power toughness)
             (str power "/" toughness "\n\n"))
           (tp/border) "\n\n")
      tp/replace-funky-chars
      tp/bold))

(defn generate-proxy
  "Generate a proxy from a search term."
  [card-name]
  (-> card-name
      api/memoized-search
      api/extract-details
      format-proxy))

(defn print-proxy-handler
  "Handle a request from the server to print a number of copies of a
  card."
  [card-name copies]
  (doseq [i (range copies)]
    (-> card-name
        generate-proxy
        tp/print-text)))

(defn print-decklist-handler
  "Handle a request from the server to print a whole decklist."
  [decklist]
  (map (fn [row]
         (let [[_ copies card-name] (re-matches #"(^\d+) (.*)" row)]
           (print-proxy-handler card-name (read-string copies))))
       decklist))
