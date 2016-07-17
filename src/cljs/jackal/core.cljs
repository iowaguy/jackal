(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]))


(enable-console-print!)

;; (defonce app-state (atom {:text "Hello Chestnut!"}))

;; (defn root-component [app owner]
;;   (reify
;;     om/IRender
;;     (render [_]
;;       (dom/div nil (dom/h1 nil (:text app))))))

;; (om/root
;;  root-component
;;  app-state
;;  {:target (js/document.getElementById "app")})

;; (def test-string (str "Newton's Method: " (numerics/newtons-method [1 0 -1] 10 0.0001)))

(def canvas-dimension-x 3)

(def canvas-dimension-y canvas-dimension-x)

(def scaling-factor 300)

;; (def range-step-size (/ 1 scaling-factor))
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

(defn make-dot
  [ctx x y]
  (let [scaled-x (scale-pixel-coordinate x)
        scaled-y (scale-pixel-coordinate y)
        offset-scaled-x (offset-pixel-coordinate-x scaled-x)
        offset-scaled-y (offset-pixel-coordinate-y scaled-y)]
    (.fillRect ctx offset-scaled-x offset-scaled-y 1 1)))

(defn pixel-coordinates
  []
  (for [x (range -2 canvas-dimension-x range-step-size)
        y (range -2 canvas-dimension-y range-step-size)]
    (list x y)))

;; (def scaled-pixel-coordinates
;;   (map
;;    (fn [x y]
;;      (do (println x y)
;;          (list (/ x scaling-factor) (/ y scaling-factor))))
;;    pixel-coordinates))

(defn pixel-coordinates-in-set
  []
  (filter
   (fn [[x y]] (numerics/mandelbrot-set? x y))
   (pixel-coordinates)))

(defn main
  []
  (add-canvas)
  (let [ctx (.getContext (sel1 :#fractal-canvas) "2d")]
    (doall
     (map
      (fn [[a b]] (make-dot ctx a b))
      (pixel-coordinates-in-set)))))


(main)
(println "hello world.")

;; scratch
      ;; (.moveTo ctx 0 0)
      ;; (.lineTo ctx 200 100)
      ;; (.stroke ctx)

;; issue 1: Mandlebrot-set-iterations should break before hitting max-iter for diverging values
;; issue 2: find way to handle scaling issues; need to iterate through a lot of numbers
