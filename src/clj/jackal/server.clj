(ns jackal.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [jackal.math.numerics :as numerics]
            [cheshire.core :refer :all])
  (:gen-class))


(defn pixel-coordinates
  []
  (for [x (range -2 3 0.005)
        y (range -2 3 0.005)]
    (list x y)))

(defn pixel-coordinates-in-set
  []
  (filter
   (fn [[x y]] (numerics/mandelbrot-set? x y))
   (pixel-coordinates)))

(defroutes routes
  (GET "/" _
       {:status 200
        :headers {"Content-Type" "text/html; charset=utf-8"}
        :body (io/input-stream (io/resource "public/index.html"))})
  (GET "/coordinates" []
       {:status 200
        :headers {"Content-Type" "text/json; charset=utf-8"}
        :body (generate-string (pixel-coordinates-in-set))})
  (resources "/"))

(def http-handler
  (-> routes
      (wrap-defaults api-defaults)
      wrap-with-logger
      wrap-gzip))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (run-jetty http-handler {:port port :join? false})))
