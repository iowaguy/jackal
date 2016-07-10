(ns jackal.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

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

(defn slope-at
  "Find the slope of the polynomial func at x."
  [func x]
  (eval-polynomial-at
   (differentiate func)
   x))

(defn determine-tangent-line
  "Determine the tangent line at x of the polynomial"
  [poly x]
  (let [slope (slope-at poly x)]
    [slope (y-intercept slope x (eval-polynomial-at poly x))]))

(defn differentiate
  "Differentiate simple polynomials."
  [func]
  (let [length (count func)]
    (drop-last (map-indexed
                (fn [index item] (* item (- (- length index) 1)))
                func))))

(defn x-intercept
  "Find the x-intercept of the line represented by mx+b."
  [m x y]
  (/ (- 0 (y-intercept m x y)) m))


(defn y-intercept
  "Find the y-intercept of the line with slope m and coordinate (x,y)"
  [m x y]
  (- y (* m x)))

;; needs refactoring to be more functional
(defn eval-polynomial-at
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

(defn eval-term-at
  "Evaluate a single term of the polynomial."
  [coefficient power x]
  (*
   coefficient
   (Math/pow x power)))
