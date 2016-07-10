(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.newtons-method :as jmath]))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(println "Hello world!")

(println (jmath/newtons-method [1 0 -1] 10 0.0001)))
