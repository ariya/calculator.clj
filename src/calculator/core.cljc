(ns calculator.core
  #?(:clj (:gen-class))
  (:require [clojure.string :refer (join)]
            [calculator.lexer :refer (tokens)]
            [calculator.parser :refer (syntax-tree)]
            [calculator.evaluator :refer (calc)]))

(defn -main [& args]
  (try
    (->> args (join " ") tokens syntax-tree calc prn)
    (catch #?(:clj Exception)
           #?(:cljs js/Error) e (println (str "Error: " (ex-message e))))))