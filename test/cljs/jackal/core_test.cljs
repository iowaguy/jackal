(ns jackal.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [jackal.math.numerics :as numerics]))

(deftest mandelbrot-set-test
  (is (numerics/mandelbrot-set? 0 0 1000)))

(deftest mandelbrot-set-negative-test
  (is (not (numerics/mandelbrot-set? 1 1 1000))))

(simple-benchmark [x 0 y 0 i 1000] (numerics/mandelbrot-set? x y i) 1000)
(simple-benchmark [x 0 y 0 i 1000] (numerics/mandelbrot-set? x y i) 10000)
(simple-benchmark [x 0 y 0 i 1000] (numerics/mandelbrot-set? x y i) 100000)
