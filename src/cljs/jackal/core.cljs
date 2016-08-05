(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))
            ;; [jackal.common :as common :refer-macros [mandelbrot-set-iterations]]))


(enable-console-print!)

(def max-iterations 200)
(def scaling-factor 300)
(def range-step-size 0.0025)
(def pixel-offset-x 600)
(def pixel-offset-y 400)
(def range-min -2)
(def range-max 2)
(def canvas-name "fractal-canvas")

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
     (dommy/set-attr! canvas-element :id canvas-name :width 1200 :height 750))))

(defn setup []
  ;; (q/frame-rate 200)
  (q/background 255)
  (q/no-stroke)
  (q/color-mode :hsb))

(defn draw
  []
  (q/translate pixel-offset-x pixel-offset-y)
  (doseq [x (range range-min range-max range-step-size)
          y (range 0 range-max range-step-size)]
    (let [scaled-x (q/floor (scale-pixel-coordinate x))
          scaled-y (q/floor (scale-pixel-coordinate y))]
      (if (and (and (> x -0.5) (< x 0.2))
               (and (> y -0.5) (< y 0.5)))
        (do  ;; areas that are near the center are always in the set, no need to check
          (q/fill 0 255 0 255)
          (q/rect scaled-x scaled-y 0.5 0.5)
          ;; reflecting over y=0, for performance (though a true mandelbrot set
          ;; is not exactly symmetrical)
          (q/rect scaled-x (* -1 scaled-y) 0.5 0.5))
        (let [iterations (numerics/mandelbrot-set-iterations x y max-iterations)
              col (mod (+ iterations 128) 255)]
          (q/fill col 255 (if (= iterations max-iterations) 0 255))
          (q/rect scaled-x scaled-y 0.5 0.5)
          ;; reflecting over y=0, for performance (though a true mandelbrot set
          ;; is not exactly symmetrical)
          (q/rect scaled-x (* -1 scaled-y) 0.5 0.5))))))

(add-canvas)

(q/defsketch mandlebrot-set
  :host canvas-name
  :title "The Mandelbrot Set"
  :setup setup
  :draw draw
  :size [1200 750]
  :middleware [m/fun-mode])

(println "hello world.")
