(ns a-proxy-mate.server
  (:gen-class)
  (:require [muuntaja.core :as m]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.spec]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]
            [a-proxy-mate.core :as core]))

(defonce sys (atom nil))

(def app
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc  true
             :swagger {:info {:title       "a-proxy-mate"
                              :description "Printing MTG proxies for fun and profit"}}
             :handler (swagger/create-swagger-handler)}}]
     ["/print" {:swagger {:tags ["Print"]}}
      ["/proxy"
       {:post {:summary    "Print a number of proxies for a given card"
               :parameters {:query {:card-name string?
                                    :copies    int?}}
               :handler    (fn [{{{:keys [card-name copies]} :query} :parameters}]
                             (core/print-proxy-handler card-name copies))}}]
      ["/decklist"
       {:post {:summary    "Print a number of proxies for a list of given cards"
               :parameters {:body coll?}
               :handler    (fn [{{:keys [body]} :parameters}]vector
                             (core/print-decklist-handler body))}}]]]

    {:exception pretty/exception
     :data      {:coercion   reitit.coercion.spec/coercion
                 :muuntaja   m/instance
                 :middleware [swagger/swagger-feature
                              parameters/parameters-middleware
                              muuntaja/format-negotiate-middleware
                              muuntaja/format-response-middleware
                              exception/exception-middleware
                              muuntaja/format-request-middleware
                              coercion/coerce-response-middleware
                              coercion/coerce-request-middleware
                              multipart/multipart-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path   "/"
      :config {:validationUrl    nil
               :operationsSorter "alpha"}})
    (ring/create-default-handler))))

(defn start
  []
  (reset! sys (jetty/run-jetty #'app {:port 3000 :join? false}))
  (println "server running on port 3000"))

(defn stop
  []
  (clojure.pprint/pprint @sys)
  (.stop @sys))

(defn restart
  []
  (stop)
  (start))

(defn -main
  "Start the server."
  [& _]
  (start))
