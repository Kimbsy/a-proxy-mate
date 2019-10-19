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
 (fn [db [_ name]]
   (update-in db [:cards name] inc)))

(re-frame/reg-event-db
 ::dec-card-copies
 (fn [db [_ name]]
   (update-in db [:cards name] dec)))

(re-frame/reg-event-db
 ::remove-card
 (fn [db [_ name]]
   (assoc-in db [:cards name] 0)))

(re-frame/reg-event-db
 ::search-changed
 (fn [db [_ value]]
   (assoc db :current-search-value value)))

(re-frame/reg-event-db
 ::search-submitted
 (fn [{:keys [current-search-value] :as db} _]
   (if (< 0 (count current-search-value))
     (-> db
         (assoc-in [:cards current-search-value] 1)
         (assoc :current-search-value ""))
     db)))
