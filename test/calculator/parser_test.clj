(ns calculator.parser-test
  (:require [clojure.test :refer (deftest is)]
            [calculator.lexer :refer (tokens)]
            [calculator.parser :refer (syntax-tree)]))

(defn- parse [str] (-> str tokens syntax-tree))

(deftest test-addition
  (is (= (parse "1+2") [:Plus [:Number "1"] [:Number "2"]]))
  (is (= (parse "1 + 2+3") [:Plus [:Number "1"] [:Plus [:Number "2"] [:Number "3"]]])))

(deftest test-substraction
  (is (= (parse "4-5") [:Minus [:Number "4"] [:Number "5"]]))
  (is (= (parse "4-5  -6") [:Minus [:Number "4"] [:Minus [:Number "5"] [:Number "6"]]])))

(deftest test-multiplication
  (is (= (parse "6 * 7") [:Star [:Number "6"] [:Number "7"]]))
  (is (= (parse "6 *7*8") [:Star [:Number "6"] [:Star [:Number "7"] [:Number "8"]]])))

(deftest test-division
  (is (= (parse "7/ 8") [:Slash [:Number "7"] [:Number "8"]]))
  (is (= (parse " 7/8 /9") [:Slash [:Number "7"] [:Slash [:Number "8"] [:Number "9"]]])))

(deftest test-unary
  (is (= (parse "-42") [:Minus [:Number "42"]]))
  (is (= (parse "+3") [:Plus [:Number "3"]]))
  (is (= (parse "--1") [:Minus [:Minus [:Number "1"]]]))
  (is (= (parse "+-7") [:Plus [:Minus [:Number "7"]]]))
  (is (= (parse "- + 0") [:Minus [:Plus [:Number "0"]]])))

(deftest test-group
  (is (= (parse "( 0 )") [:Group [:Number "0"]]))
  (is (= (parse "(-42)") [:Group [:Minus [:Number "42"]]])))

(deftest test-op-precedence
  (is (= (parse "1*2+3") [:Plus [:Star [:Number "1"] [:Number "2"]] [:Number "3"]]))
  (is (= (parse "1-2*3") [:Minus [:Number "1"] [:Star [:Number "2"] [:Number "3"]]]))
  (is (= (parse "4/5-6") [:Minus [:Slash [:Number "4"] [:Number "5"]] [:Number "6"]]))
  (is (= (parse "4+5/6") [:Plus [:Number "4"] [:Slash [:Number "5"] [:Number "6"]]]))
  (is (= (parse "(7+7)/-3") [:Slash [:Group [:Plus [:Number "7"] [:Number "7"]]] [:Minus [:Number "3"]]])))

(deftest test-incomplete-input
  (is (thrown-with-msg? Exception #"Unexpected EOF" (parse "6 +")))
  (is (thrown-with-msg? Exception #"Unexpected EOF" (parse "-")))
  (is (thrown-with-msg? Exception #"Unexpected EOF" (parse "(1"))))

(deftest test-stray-token
  (is (thrown-with-msg? Exception #"Unexpected token at 4" (parse "7+8 9")))
  (is (thrown-with-msg? Exception #"Unexpected token at 9" (parse "(1.2 + 3 4"))))