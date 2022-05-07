(ns calculator.core
  (:gen-class)
  (:require [clojure.string :refer (join)]
            [calculator.lexer :refer (tokens)]
            [calculator.parser :refer (syntax-tree)]
            [calculator.evaluator :refer (calc)]))

(defn -main [& args]
  (try
    (->> args (join " ") tokens syntax-tree calc prn)
    (catch Exception e (println (str "Error: " (ex-message e))))))