(ns calculator.parser
  (:require [clojure.string :refer [join]]))

(defn- throw-unexpected
  "Throws an exception indicating an unexpected token"
  ([] (throw (#?(:clj Exception.)
              #?(:cljs js/Error.) "Unexpected EOF")))
  ([tokens]
   (throw (#?(:clj Exception.)
           #?(:cljs js/Error.)
           (if (seq tokens)
             (str "Unexpected token at " (-> tokens first :pos))
             "Unexpected EOF")))))

(declare expression)

(defn- number-expression
  "Parses a primary (number) and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (let [token (first tokens)]
    (if (= (:type token) :Number)
      {:tokens (rest tokens) :tree [:Number (join (:str token))]}
      (throw-unexpected tokens))))


;; Group ::= (  Expresion  )
(defn- group-expression
  "Parses a parenthesized expression and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (when (= (:type (first tokens)) :OpenParenthesis)
    (let [expr (expression (rest tokens))]
      (if (= (:type (first (:tokens expr))) :CloseParenthesis)
        {:tokens (rest (:tokens expr)) :tree [:Group (:tree expr)]}
        ;; mismatched parenthesis
        (throw-unexpected (:tokens expr))))))


;; Primary ::= Number
;;             Group
(defn- primary-expression
  "Parses a number or a group and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (let [operator (:type (first tokens))]
    (if (= operator :OpenParenthesis)
      ;; (  Expression )
      (group-expression tokens)
      ;; Number
      (number-expression tokens))))


;; Unary ::= Primary
;;           + Unary
;;           - Unary
(defn- unary-expression
  "Parses a positive/negative unary and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (let [operator (:type (first tokens))]
    (if (contains? #{:Plus :Minus} operator)
      ;; + Unary or - Unary
      (let [unary (unary-expression (rest tokens))]
        {:tokens (:tokens unary) :tree [operator (:tree unary)]})
      ;; Primary
      (primary-expression tokens))))


;; Multiplicative  ::= Unary
;;                     Unary * Multiplicative
;;                     Unary / Multiplicative
(defn- multiplicative-expression
  "Parses a multiplication/division and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (let [unary (unary-expression tokens)
        remaining-tokens (:tokens unary)
        operator (:type (first remaining-tokens))]
    (if-not (contains? #{:Star :Slash} operator)
      ;; Unary
      unary
      ;; Unary * Multiplicative
      ;; Unary / Multiplicative
      (let [mul (multiplicative-expression (rest remaining-tokens))]
        {:tokens (:tokens mul)
         :tree [operator (:tree unary) (:tree mul)]}))))


;; Additive ::= Multiplicative
;;              Multiplicative + Additive
;;              Multiplicative - Additive

(defn- additive-expression
  "Parses an addition/substraction and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (let [mul (multiplicative-expression tokens)
        remaining-tokens (:tokens mul)
        operator (:type (first remaining-tokens))]
    (if-not (contains? #{:Plus :Minus} operator)
      ;; Multiplicative
      mul
      ;; Multiplicative + Additive
      ;; Multiplicative - Additive
      (let [add (additive-expression (rest remaining-tokens))]
        {:tokens (:tokens add)
         :tree [operator (:tree mul) (:tree add)]}))))

(defn- expression
  "Parses an expression and returns the remaining tokens and
   its corresponding syntax tree."
  [tokens]
  (additive-expression tokens))

(defn ^:export syntax-tree
  "Parses an expression and returns the syntax tree"
  [tokens]
  (let [expr (additive-expression tokens)]
    (if-not (seq (:tokens expr))
      (:tree expr)
      (throw-unexpected (:tokens expr)))))