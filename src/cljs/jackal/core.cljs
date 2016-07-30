(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [ajax.core :refer [GET]]
            [dommy.core :as dommy :refer-macros [sel1]]))

(enable-console-print!)

(def canvas-dimension-x 3)

(def canvas-dimension-y canvas-dimension-x)

(def scaling-factor 300)

(def pixel-offset-x 600)

(def pixel-offset-y 400)

(defn offset-pixel-coordinate-x
  [x]
  (+ pixel-offset-x x))

(defn offset-pixel-coordinate-y
  [y]
  (+ pixel-offset-y y))

(defn scale-pixel-coordinate
  [x]
  (* scaling-factor x))

(defn add-canvas
  []
  (let [canvas-element (dommy/create-element :canvas)]
    (dommy/append!
     (sel1 :body)
     (dommy/set-attr! canvas-element :id "fractal-canvas" :width 1200 :height 750))))

(defn make-dot
  [ctx x y]
  (let [scaled-x (scale-pixel-coordinate x)
        scaled-y (scale-pixel-coordinate y)
        offset-scaled-x (offset-pixel-coordinate-x scaled-x)
        offset-scaled-y (offset-pixel-coordinate-y scaled-y)]
    (.fillRect ctx offset-scaled-x offset-scaled-y 1 1)))

(defn mandelbrot-set-handler [coords]
  (let [ctx (.getContext (sel1 :#fractal-canvas) "2d")]
    (doall
     (map
      (fn [[a b]] (make-dot ctx a b))
      coords))))

(defn fetch-mandelbrot-set
  [handler]
  (GET "/coordinates" {:response-format :json
                       :handler handler}))

(defn main
  []
  (add-canvas)
  (fetch-mandelbrot-set mandelbrot-set-handler))

(main)
(println "hello world.")

