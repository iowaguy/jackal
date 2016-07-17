(ns jackal.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [jackal.core-test]
   [jackal.common-test]))

(enable-console-print!)

(doo-tests 'jackal.core-test
           'jackal.common-test)
