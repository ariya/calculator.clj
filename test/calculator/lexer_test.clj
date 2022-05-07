(ns calculator.lexer-test
  (:require [clojure.test :refer (deftest is)]
            [calculator.lexer :refer (tokens)]))

(defn- first-token [expression] (-> expression tokens first))

(deftest test-integer
  (is (= :Number (:type (first-token "7"))))
  (is (= :Number (:type (first-token "  42")))))

(deftest test-floating-point
  (is (= :Number (:type (first-token " 3.14159"))))
  (is (= :Number (:type (first-token " .62"))))
  (is (= :Number (:type (first-token " 2.")))))

(deftest test-operator
  (is (= :Plus (:type (first-token " + "))))
  (is (= :Plus (:type (first-token "+-"))))
  (is (= :Minus (:type (first-token "-"))))
  (is (= :Star (:type (first-token " * "))))
  (is (= :Slash (:type (first-token " / "))))
  (is (= :OpenParenthesis (:type (first-token "("))))
  (is (= :CloseParenthesis (:type (first-token ")")))))

(deftest test-invalid
  (is (thrown-with-msg? Exception #"Invalid character ?" (tokens "?A"))))