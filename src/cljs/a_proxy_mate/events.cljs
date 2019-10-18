(ns a-proxy-mate.events
  (:require
   [re-frame.core :as re-frame]
   [a-proxy-mate.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::inc-card-copies
 (fn [{:keys [cards] :as db} [_ name]]
   (update-in db [:cards name] inc)))

(re-frame/reg-event-db
 ::dec-card-copies
 (fn [{:keys [cards] :as db} [_ name]]
   (update-in db [:cards name] dec)))

(re-frame/reg-event-db
 ::remove-card
 (fn [{:keys [cards] :as db} [_ name]]
   (assoc-in db [:cards name] 0)))
