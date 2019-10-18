(ns a-proxy-mate.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [a-proxy-mate.events :as events]
   [a-proxy-mate.views :as views]
   [a-proxy-mate.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
