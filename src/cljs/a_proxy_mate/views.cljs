(ns a-proxy-mate.views
  (:require
   [re-frame.core :as re-frame]
   [a-proxy-mate.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     ]))
