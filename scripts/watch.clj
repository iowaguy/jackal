(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'jackal.core
   :output-to "out/jackal.js"
   :output-dir "out"})
