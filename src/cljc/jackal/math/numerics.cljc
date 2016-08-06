(ns jackal.math.numerics)

;;;;;;;;;; Public methods ;;;;;;;;;;;;
(defn mandelbrot-set-iterations
  "Returns number of iterations of Mandelbrot procedure"
  [real imaginary max-iter]
  (loop [x 0
         r 0
         i 0]
    (if (and
         (< x max-iter)
         (< (+ (* r r) (* i i)) 4.0))
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
