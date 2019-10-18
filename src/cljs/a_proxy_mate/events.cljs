(ns a-proxy-mate.events
  (:require
   [re-frame.core :as re-frame]
   [a-proxy-mate.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
