(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))


(enable-console-print!)

(def max-iterations 1000)

(def scaling-factor 300)

(def range-step-size 0.0025)

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

(defn setup []
  (q/frame-rate 200) ;; frame rate set to 1 frame per 10 seconds
  (q/background 255)
  (q/no-stroke)
  (q/color-mode :hsb)
  -2)

(defn update-state
  [x]
  (let [new-x (+ x 0.01)]
    (if (> new-x 2) -2 new-x)))

(defn draw
  [old-x]
  (doseq [x (range old-x (+ old-x 0.05) range-step-size)
          y (range -2 2 range-step-size)]
    (let [iterations (numerics/mandelbrot-set-iterations x y max-iterations)
          scaled-x (scale-pixel-coordinate x)
          scaled-y (scale-pixel-coordinate y)
          offset-scaled-x (offset-pixel-coordinate-x scaled-x)
          offset-scaled-y (offset-pixel-coordinate-y scaled-y)
          col (mod (+ iterations 128) 255)]
      (q/fill col 255 (if (= iterations max-iterations) 0 255))
      (q/rect offset-scaled-x offset-scaled-y 0.5 0.5))))

(add-canvas)

(q/defsketch mandlebrot-set
  :host "fractal-canvas"
  :title "The Mandelbrot Set"
  :settings #(q/smooth 20) ;; Turn on anti-aliasing
  :setup setup
  :update update-state
  :draw draw
  :size [1200 750]
  :middleware [m/fun-mode])

(println "hello world.")
