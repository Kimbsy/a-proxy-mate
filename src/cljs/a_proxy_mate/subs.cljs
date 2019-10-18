(ns a-proxy-mate.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::cards
 (fn [{:keys [cards] :as db}]
   (into {} (filter #(> (second %) 0)
                    cards))))
