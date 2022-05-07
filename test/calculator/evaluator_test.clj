(ns calculator.evaluator-test
  (:require [clojure.test :refer (deftest is)]
            [calculator.lexer :refer (tokens)]
            [calculator.parser :refer (syntax-tree)]
            [calculator.evaluator :refer (calc)]))

(defn- compute [str] (-> str tokens syntax-tree calc))

(deftest test-unary
  (is (= (compute "-7") -7.0))
  (is (= (compute "--8") 8.0))
  (is (= (compute "+9") 9.0))
  (is (= (compute "+-3") -3.0)))

(deftest test-binary
  (is (= (compute "1+2") 3.0))
  (is (= (compute "2-3") -1.0))
  (is (= (compute "3 * 4") 12.0))
  (is (= (compute " 4/5") 0.8)))

(deftest test-group
  (is (= (compute "(3 + 5) * -1") -8.0)))
