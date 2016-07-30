(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]
            [quil.core :as q :include-macros true]))


(enable-console-print!)

(def canvas-dimension-x 3)

(def canvas-dimension-y canvas-dimension-x)

(def scaling-factor 400)

(def range-step-size 0.005)

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
  (q/frame-rate 0.1) ;; frame rate set to 1 frame per 10 seconds
  (q/background 255))

(defn draw []
  (q/fill 150 150 150)
  (doseq [x (range -2 2 range-step-size)
          y (range -2 2 range-step-size)]
    (if (first (numerics/mandelbrot-set? x y))
      (let [scaled-x (scale-pixel-coordinate x)
            scaled-y (scale-pixel-coordinate y)
            offset-scaled-x (offset-pixel-coordinate-x scaled-x)
            offset-scaled-y (offset-pixel-coordinate-y scaled-y)]
        (q/rect offset-scaled-x offset-scaled-y range-step-size range-step-size)))))

(add-canvas)

(q/defsketch mandlebrot-set
  :host "fractal-canvas"
  :title "The Mandelbrot Set"
  :settings #(q/smooth 2)             ;; Turn on anti-aliasing
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [1200 750])                    ;; You struggle to beat the golden ratio

(println "hello world.")
