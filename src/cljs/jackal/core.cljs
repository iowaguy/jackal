(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(def range-min-x -2.5)
(def range-max-x 2.5)
(def range-min-y -1.7)
(def range-max-y 1.7)
(def screen-width (.-innerWidth js/window))
(def screen-height (.-innerHeight js/window))
(def max-iterations-init 50)
(def scaling-factor (/ screen-width (- range-max-x range-min-x)))
(def range-step-size (/ 1 scaling-factor))
(def rect-size 1)
(def pixel-offset-x (/ screen-width 2))
(def pixel-offset-y (/ screen-height 2))
(def canvas-name "fractal-canvas")
(def zoom-factor 4)

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

(defn setup
  []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  (q/background 128 255 255)
  (q/no-stroke)
  {:xmin range-min-x
   :xmax range-max-x
   :ymin range-min-y
   :ymax range-max-y
   :step range-step-size
   :max-iter max-iterations-init
   :scale scaling-factor
   :x-offset pixel-offset-x
   :y-offset pixel-offset-y})

(defn zoom
  [state event]
  (q/background 128 255 255)
  (q/start-loop)
  (let [new-center-x  (/ (- (:x event) (:x-offset state)) (:scale state))
        new-center-y  (/ (- (:y event) (:y-offset state)) (:scale state))
        new-xrange (/ (- (:xmax state) (:xmin state)) zoom-factor)
        new-yrange (/ (- (:ymax state) (:ymin state)) zoom-factor)
        new-min-x (- new-center-x (/ new-xrange 2))
        new-max-x (+ new-center-x (/ new-xrange 2))
        new-min-y (- new-center-y (/ new-yrange 2))
        new-max-y (+ new-center-y (/ new-yrange 2))
        new-scale (/ screen-width (- new-max-x new-min-x))]

    (-> state
        (assoc :xmin new-min-x)
        (assoc :xmax new-max-x)
        (assoc :ymin new-min-y)
        (assoc :ymax new-max-y)
        (assoc :scale new-scale)
        (update :step #(/ 1 new-scale))
        (update :x-offset #(- pixel-offset-x (* zoom-factor (- (:x event) %))))
        (update :y-offset #(- pixel-offset-y (* zoom-factor (- (:y event) %)))))))

(defn draw
  [state]
  (doseq [x (range (:xmin state) (:xmax state) (:step state))
          y (range (:ymin state) (:ymax state) (:step state))]
    (let [scaled-x (* (:scale state) x)
          scaled-y (* (:scale state) y)
          offset-scaled-x (+ (:x-offset state) scaled-x)
          offset-scaled-y (+ (:y-offset state) scaled-y)
          iterations (numerics/mandelbrot-set-iterations x y (:max-iter state))
          col (mod (+ iterations 128) 255)]
      (q/fill col 255 (if (= iterations (:max-iter state)) 0 255))
      (q/rect offset-scaled-x offset-scaled-y rect-size rect-size)))
  (q/no-loop))

(init-canvas)

(q/defsketch mandlebrot-set
  :host canvas-name
  :title "The Mandelbrot Set"
  :setup setup
  :draw draw
  :size [screen-width screen-height]
  :mouse-clicked zoom
  :middleware [m/fun-mode])

(println "hello world.")
