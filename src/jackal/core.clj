(ns jackal.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn newtons-method
  [func guess epsilon]
  ())

(defn differentiate
  [func]
  ())

(defn differentiate-at
  [x]
  ())

(defn tangent-line-root
  [m b]
  (/ (- 0 b) m))

(defn eval-polynomial-at
  [poly x]
  (let [length (count poly)]
    (loop [i length
           sum 0]
      (if (> i 0)
        (do
          (println sum)
          (recur
           (- i 1)
           (+
            sum
            (eval-term-at (nth poly (- i 1)) (- length i) x))))
        sum))))

(defn eval-term-at
  [coefficient power x]
  (*
   coefficient
   (Math/pow x power)))
