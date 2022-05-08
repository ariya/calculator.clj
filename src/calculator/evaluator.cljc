(ns calculator.evaluator)

(defmulti calc
  "Traverses the syntax tree, performs the evaluation,
   and return the result."
  first)

(defmethod calc :default [node]
  (throw (Exception. (str "Unknown node type " (first node)))))

(defmethod calc :Number [node] (-> node second Double/parseDouble))

(defmethod calc :Group [node] (-> node second calc))

(defmethod calc :Star [node] (reduce * (map calc (rest node))))

(defmethod calc :Slash [node] (reduce / (map calc (rest node))))

(defn- is-unary? [node] (= (count node) 2))

(defmethod calc :Plus [node]
  (if (is-unary? node)
    (-> node second calc)
    (reduce + (map calc (rest node)))))

(defmethod calc :Minus [node]
  (if (is-unary? node)
    (-> node second calc -)
    (reduce - (map calc (rest node)))))