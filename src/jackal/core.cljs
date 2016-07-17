(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]))

(defonce conn
  ;; (def env (browser/repl-env)) ;; create a new environment
  ;; (repl/repl env) ;; start the REPL
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

;; (def test-string (str "Newton's Method: " (numerics/newtons-method [1 0 -1] 10 0.0001)))

(def canvas-dimension-x 2)

(def canvas-dimension-y canvas-dimension-x)

(def scaling-factor 100)

;; (def range-step-size (/ 1 scaling-factor))
(def range-step-size 0.01)

(defn add-canvas
  []
  (let [canvas-element (dommy/create-element :canvas)]
    (dommy/append!
     (sel1 :body)
     (dommy/set-attr! canvas-element :id "fractal-canvas" :width 1000 :height 1000))))

(defn make-dot
  [ctx x y]
  (.fillRect ctx (* scaling-factor x) (* scaling-factor y) 1 1))

(defn pixel-coordinates
  []
  (for [x (range 0 canvas-dimension-x range-step-size)
        y (range 0 canvas-dimension-y range-step-size)]
    (list x y)))

;; (def scaled-pixel-coordinates
;;   (map
;;    (fn [x y]
;;      (do (println x y)
;;          (list (/ x scaling-factor) (/ y scaling-factor))))
;;    pixel-coordinates))

(defn scaled-pixel-coordinates-in-set
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
      (scaled-pixel-coordinates-in-set)))))


(main)
(println "hello world.")

;; scratch
      ;; (.moveTo ctx 0 0)
      ;; (.lineTo ctx 200 100)
      ;; (.stroke ctx)

;; issue 1: Mandlebrot-set-iterations should break before hitting max-iter for diverging values
;; issue 2: find way to handle scaling issues; need to iterate through a lot of numbers
