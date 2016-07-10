(ns jackal.core
  (:gen-class)
  (:require [jackal.math.newtons-method :as jmath]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (jmath/newtons-method [1 0 -1] 10 0.0001)))
