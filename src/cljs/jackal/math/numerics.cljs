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

;;;;;;;;;; Public methods ;;;;;;;;;;;;
(defn mandelbrot-set-iterations
   "Returns number of iterations of Mandelbrot procedure"
   [real imaginary max-iter]
  (loop [x 0
         r 0
         i 0]
    (if (and
         (< x max-iter)
         (< (+ (* r r) (* i i)) 4))
      (recur (inc x)
             (+ real (- (* r r) (* i i)))
             (+ imaginary (+ (* r i) (* r i))))
      x)))

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
