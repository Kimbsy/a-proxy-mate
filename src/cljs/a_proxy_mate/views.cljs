(ns a-proxy-mate.views
  (:require
   [re-frame.core :as re-frame]
   [a-proxy-mate.subs :as subs]
   [hiccup-icons.octicons :as o]))

(defn card-row
  [card]
  [:tr {:key (:name card)}
   [:td (:copies card)]
   [:td (:name card)]
   [:td
    [:button.btn.btn-success o/plus]
    [:button.btn.btn-warning o/dash]
    [:button.btn.btn-danger o/trashcan]]])

(defn card-table
  []
  (let [cards @(re-frame/subscribe [::subs/cards])]
    [:table.table
     [:thead
      [:th "copies"]
      [:th "card name"]
      [:th "add/remove/remove-all"]]
     [:tbody
      (map card-row cards)]]))

(defn card-search-box
  []
  [:div.input-group.mb-3
   [:div.input-group-prepend
    [:span.input-group-text "Add Card:"]]
   [:input.form-control {:placeholder "keeper of the..."}]])

(defn main-panel
  []
  [:div
   [:h1 "a-proxy-mate"]
   (card-table)
   (card-search-box)])
