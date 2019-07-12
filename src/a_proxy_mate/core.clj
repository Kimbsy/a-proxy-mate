(ns a-proxy-mate.core
  (:gen-class)
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
  [search-term]
  (-> search-term
      api/memoized-search
      api/extract-details
      format-proxy))

(defn -main
  [& args]
  (let [search-term (first args)
        copies (if (second args)
                 (read-string (second args))
                 1)]
    (doseq [i (range (int copies))]
      (-> search-term
          generate-proxy
          tp/print-text))))
