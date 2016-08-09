(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(def range-min -3)
(def range-max 3)
(def screen-width (.-innerWidth js/window))
(def screen-height (.-innerHeight js/window))
(def max-iterations 50)
(def scaling-factor (/ screen-width (- range-max range-min)))
(def range-step-size (/ 1 scaling-factor))
(def rect-size 1)
(def pixel-offset-x (/ screen-width 2))
(def pixel-offset-y (/ screen-height 2))
(def canvas-name "fractal-canvas")
(def actual-range (range range-min range-max range-step-size))

(defn offset-pixel-coordinate-x
  [x]
  (+ pixel-offset-x x))

(defn offset-pixel-coordinate-y
  [y]
  (+ pixel-offset-y y))

(defn scale-pixel-coordinate
  [val]
  (* val scaling-factor))

(defn init-canvas
  []
  (let [canvas-element (dommy/create-element :canvas)]
    (dommy/append!
     (sel1 :body)
     (dommy/set-attr! canvas-element
                      :id canvas-name
                      :width screen-width
                      :height screen-height))))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  (q/background 128 255 255)
  (q/no-stroke))

(defn draw
  []
  (doseq [x (range range-min range-max range-step-size)
          y (range 0 range-max range-step-size)]
    (let [scaled-x (* scaling-factor x)
          scaled-y (* scaling-factor y)
          offset-scaled-x (+ pixel-offset-x scaled-x)
          offset-scaled-y (+ pixel-offset-y scaled-y)
          y-inv (q/floor (+ (* -1 scaled-y) pixel-offset-y))
          iterations (numerics/mandelbrot-set-iterations x y max-iterations)
          col (mod (+ iterations 128) 255)]
      (q/fill col 255 (if (= iterations max-iterations) 0 255))
      (q/rect offset-scaled-x y-inv rect-size rect-size)
      (q/rect offset-scaled-x offset-scaled-y rect-size rect-size)))
  (q/no-loop))

(init-canvas)

(q/defsketch mandlebrot-set
  :host canvas-name
  :title "The Mandelbrot Set"
  :setup setup
  :draw draw
  :size [screen-width screen-height]
  :middleware [m/fun-mode])

(println "hello world.")
