(ns jackal.math.numerics)

;; Newton's Method functions
(defn- y-intercept
  "Find the y-intercept of the line with slope m and coordinate (x,y)"
  [m x y]
  (- y (* m x)))

(defn- x-intercept
  "Find the x-intercept of the line represented by mx+b."
  [m x y]
  (/ (- 0 (y-intercept m x y)) m))

(defn- eval-term-at
  "Evaluate a single term of the polynomial."
  [coefficient power x]
  (*
   coefficient
   (Math/pow x power)))

;; needs refactoring to be more functional
(defn- eval-polynomial-at
  "Evaluate the polynomial at x"
  [poly x]
  (let [length (count poly)]
    (loop [i length
           sum 0]
      (if (> i 0)
        (do
          (recur
           (- i 1)
           (+
            sum
            (eval-term-at (nth poly (- i 1)) (- length i) x))))
        sum))))

(defn- differentiate
  "Differentiate simple polynomials."
  [func]
  (let [length (count func)]
    (drop-last (map-indexed
                (fn [index item] (* item (- (- length index) 1)))
                func))))

(defn- slope-at
  "Find the slope of the polynomial func at x."
  [func x]
  (eval-polynomial-at
   (differentiate func)
   x))

;; Mandelbrot Set functions
(deftype complex [real imag]
  IComplex
  (-real [this] real)
  (-imag [this] imag))

(defn- plus [^complex z1 ^complex z2]
  (let [x1 (.-real z1)
        y1 (.-imag z1)
        x2 (.-real z2)
        y2 (.-imag z2)]
    (complex. (+ x1 x2) (+ y1 y2))))

(defn- times [^complex z1 ^complex z2]
  (let [x1 (.-real z1)
        y1 (.-imag z1)
        x2 (.-real z2)
        y2 (.-imag z2)]
    (complex. (- (* x1 x2) (* y1 y2)) (+ (* x1 y2) (* y1 x2)))))

(defn- abs [^complex z]
  (let [r (.-real z)
        i (.-imag z)]
    (.sqrt js/Math (+ (.pow js/Math r 2) (.pow js/Math i 2)))))

(defn- eval-quadratic-map
  "Evaluate Mandlebrot term"
  [c z]
  (plus (times z z) c))

(defn- build-quadratic-map
  "Evaluate Mandlebrot term"
  [c]
  (partial eval-quadratic-map c))

;;;;;;;;;; Public methods ;;;;;;;;;;;;
(defn mandelbrot-set-iterations
   "Returns number of iterations of Mandelbrot procedure"
   [real imaginary max-iter]
   (let [c (complex. real imaginary)]
     (loop [x 0
            z (complex. 0 0)]
       (if (and
            (< x max-iter)
            (< (abs z) 2))
         (recur (inc x) (eval-quadratic-map c z))
         x))))

(defn mandelbrot-set?
  ([real imaginary]
   (let [max-iter 200]
     (mandelbrot-set? real imaginary max-iter)))
  ([real imaginary max-iter]
   ;; if the maximum number of iterations is hit, the value did not diverge
   (= (mandelbrot-set-iterations real imaginary max-iter) max-iter)))

(defn newtons-method
  "Run Neton's Method for root finding until the error is less than epsilon."
  [func guess epsilon]
  (loop [x guess]
    (let [y (eval-polynomial-at func x)]
      (if (>= y epsilon)
        ;; do newtons method
        (recur
         (x-intercept (slope-at func x) x y))
        x))))
