(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

;; (def test-string (str "Newton's Method: " (numerics/newtons-method [1 0 -1] 10 0.0001)))

;; (definterface IComplex
;;   (^Number -real [this])
;;   (^Number -imag [this]))

(deftype complex [real imag]
  IComplex
  (-real [this] real)
  (-imag [this] imag))

(defn plus [^complex z1 ^complex z2]
  (let [x1 (.-real z1)
        y1 (.-imag z1)
        x2 (.-real z2)
        y2 (.-imag z2)]
    (complex. (+ x1 x2) (+ y1 y2))))

(defn times [^complex z1 ^complex z2]
  (let [x1 (.-real z1)
        y1 (.-imag z1)
        x2 (.-real z2)
        y2 (.-imag z2)]
    (complex. (- (* x1 x2) (* y1 y2)) (+ (* x1 y2) (* y1 x2)))))

(defn abs [^complex z]
  (let [r (.-real z)
        i (.-imag z)]
    (.sqrt js/Math (.pow js/Math r 2) (.pow js/Math i 2))))

(defn eval-quadratic-map
  "Evaluate Mandlebrot term"
  [z c]
  (plus (times z z) c))

(defn build-quadratic-map
  "Evaluate Mandlebrot term"
  [c]
  (fn [z] (plus (times z z) c)))

(defn mandelbrot-set-iterations
   "Returns number of iterations of Mandelbrot procedure"
   [real imaginary max-iter]
   (let [quadratic-map (build-quadratic-map (complex. real imaginary))]
     (loop [x 0
            z (complex. 0 0)]
       (if (and
            (< x max-iter)
            (< (abs z) 2))
         (recur (inc x) (quadratic-map z))
         max-iter))))

(defn mandelbrot-set?
  ([real imaginary]
  (let [max-iter 10]
    (mandelbrot-set? real imaginary max-iter)))
  ([real imaginary max-iter]
   (not (= (mandelbrot-set-iterations real imaginary max-iter) max-iter))))

(defn add-canvas
  []
  (let [canvas-element (dommy/create-element :canvas)]
    (dommy/append!
     (sel1 :body)
     (dommy/set-attr! canvas-element :id "fractal-canvas" :width 800 :height 800))))

(defn main
  []
  (add-canvas)
  ;; (println (.-real (complex. 0 1)))
  (println (mandelbrot-set? 1 1))
  (let [ctx (.getContext (sel1 :#fractal-canvas) "2d")]
    (map
     #(.fillRect ctx %1 %2 1 1)
     (filter mandelbrot-set? (for [x (range 0 100) y (range 0 100)] (vector (/ x 100) (/ y 100))))))



    ;; (doseq [x (range 0 800)]
    ;;   (.fillRect ctx x x 1 1))))


(main)
(println "hello world.")

;; scratch
      ;; (.moveTo ctx 0 0)
      ;; (.lineTo ctx 200 100)
      ;; (.stroke ctx)
