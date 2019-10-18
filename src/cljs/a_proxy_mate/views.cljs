(ns a-proxy-mate.views
  (:require
   [re-frame.core :as re-frame]
   [a-proxy-mate.events :as events]
   [a-proxy-mate.subs :as subs]
   [hiccup-icons.octicons :as o]))

(defn card-row
  [[name copies]]
  [:tr {:key name}
   [:td copies]
   [:td name]
   [:td
    [:button.btn.btn-lg.btn-success
     {:style {:margin "0 5px"}
      :onClick #(re-frame/dispatch [::events/inc-card-copies name])}
     o/plus]
    [:button.btn.btn-lg.btn-warning
     {:style {:margin "0 5px"}
      :onClick #(re-frame/dispatch [::events/dec-card-copies name])}
     o/dash]
    [:button.btn.btn-lg.btn-danger
     {:style {:margin "0 5px"}
      :onClick #(re-frame/dispatch [::events/remove-card name])}
     o/trashcan]]])

(defn card-table
  []
  (let [cards @(re-frame/subscribe [::subs/cards])]
    [:table.table
     [:thead
      [:tr
       [:th "copies"]
       [:th "card name"]
       [:th "add | remove | remove-all"]]]
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
  [:div.container
   [:h1
    [:code "a-proxy-mate"]]
   [:p [:i "> just as good as the real thing"]]
   (card-table)
   (card-search-box)])
