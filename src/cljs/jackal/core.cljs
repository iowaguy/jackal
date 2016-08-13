(ns jackal.core
  (:require [clojure.browser.repl :as repl]
            [jackal.math.numerics :as numerics]
            [dommy.core :as dommy :refer-macros [sel1]]))
            ;; [quil.core :as q :include-macros true]
            ;; [quil.middleware :as m]))

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
  ;; (q/frame-rate 60)
  ;; (q/color-mode :hsb)
  ;; (q/background 128 255 255)
  ;; (q/no-stroke)
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
  ;; (q/background 128 255 255)
  ;; (q/start-loop)
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

(defn hsv-to-rbg
  [h s v]
  (let [i (.floor js/Math (/ h 60))
        ;; s (/ s 100)
        ;; v (/ v 100)
        f (- (/ h 60) i)
        p (* v (- 1 s))
        q (* v (- 1 (* f s)))
        t (* v (- 1 (* (- 1 f) s)))
        c (mod i 6)
        new-map (cond
                  (= c 0) {:r v :g t :b p}
                  (= c 1) {:r q :g v :b p}
                  (= c 2) {:r p :g v :b t}
                  (= c 3) {:r p :g q :b v}
                  (= c 4) {:r t :g p :b v}
                  (= c 5) {:r v :g p :b q})]

    (-> new-map
        ;; (update :r #(.round js/Math (* 255 %)))
        ;; (update :g #(.round js/Math (* 255 %)))
        ;; (update :b #(.round js/Math (* 255 %))))))
        (update :r #(.round js/Math (* 255 %)))
        (update :g #(.round js/Math (* 255 %)))
        (update :b #(.round js/Math (* 255 %))))))

(defn draw-point
  [img col x y offset darkness]
  (let [data (.-data img)
        pos (.floor js/Math (+ (* 4 y screen-width) (* 4 x)))
        rgb-map (hsv-to-rbg (/ (* 360 col) 50) 255 darkness)]
    ;; (aset data @offset col)
    ;; (aset data (+ @offset 1) 255)
    ;; (aset data (+ @offset 2) darkness)
    ;; (aset data (+ @offset 3) 255)
    ;; (swap! offset #(+ % 4))

    ;; (aset data pos col)
    ;; (aset data (+ pos 1) 255)
    ;; (aset data (+ pos 2) darkness)
    ;; (aset data (+ pos 3) 255)))
    (aset data pos (:b rgb-map))
    (aset data (+ pos 1) (:g rgb-map))
    (aset data (+ pos 2) (:r rgb-map))
    (aset data (+ pos 3) 255)))

(def logBase (/ 1.0 (.log js/Math 2.0)))
(def logHalfBase (* (.log js/Math 0.5) logBase))

(defn smooth-color
  [x y iter]
  (- (+ 5 iter) logHalfBase (* (.log js/Math (.log js/Math (+ x y))) logBase)))

(defn draw-set
  [state ctx img]
  (let [offset (atom 0)]
    (doseq [x (range (:xmin state) (:xmax state) (:step state))
            y (range (:ymin state) (:ymax state) (:step state))]
      (let [scaled-x (* (:scale state) x)
            scaled-y (* (:scale state) y)
            offset-scaled-x (+ (:x-offset state) scaled-x)
            offset-scaled-y (+ (:y-offset state) scaled-y)
            iterations (numerics/mandelbrot-set-iterations x y (:max-iter state))
            ;; col (smooth-color offset-scaled-x offset-scaled-y (/ iterations 9))]
            col (mod (+ (/ iterations 10) 1) 255)]
        (draw-point img col offset-scaled-x offset-scaled-y offset (if (= iterations (:max-iter state)) 0 (/ (* 10 col) 50)))))
    (.putImageData ctx img 0 0)))


;; (q/fill col 255 (if (= iterations (:max-iter state)) 0 255))
      ;; (q/rect offset-scaled-x offset-scaled-y rect-size rect-size)))
  ;; (q/no-loop))

;; (init-canvas)

(defn main
  []
  (init-canvas)
  (let [ctx (.getContext (sel1 :#fractal-canvas) "2d")
        img (.createImageData ctx screen-width screen-height)]
    (draw-set (setup) ctx img)))



;; (q/defsketch mandlebrot-set
;;   :host canvas-name
;;   :title "The Mandelbrot Set"
;;   :setup setup
;;   :draw draw
;;   :size [screen-width screen-height]
;;   :mouse-clicked zoom
;;   :middleware [m/fun-mode])
(main)

(println "hello world.")
