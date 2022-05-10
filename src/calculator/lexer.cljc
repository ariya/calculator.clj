(ns calculator.lexer)

(defn- codepoint-count
  [str]
  #?(:clj (.codePointCount str 0 (count str)))
  #?(:cljs (count str)))

(defn- string->codepoints
  [str]
  (vec (map (fn [i] (.codePointAt str i))
            (range 0 (codepoint-count str)))))

(defn- is-whitespace?
  "Returns true if the Unicode codepoint represents a whitespace."
  [cp]
  (or (= cp 9)   ;; tab
      (= cp 10)  ;; line feed
      (= cp 13)  ;; carriage return
      (= cp 32)  ;; space
      ))

(defn- is-digit?
  "Returns true if the Unicode codepoint represents a decimal digit."
  [cp]
  (and (>= cp 48) (<= cp 57))) ;; 0..9

;; Why not clojurestring/triml? We may have different whitespaces definition

(defn- count-whitespaces
  "Count whitespaces at the beginning of the codepoints vector."
  [codepoints]
  (count (take-while is-whitespace? codepoints)))

(defn- integer-token
  "Scans an integer number at the beginning of the input,
   returns the total count of characters represented by the number,
   or `0` if no integer number is recognized."
  [codepoints]
  (count (take-while is-digit? codepoints)))

(defn- fractional-token
  "Scans the fractional part of a number at the beginning of the input,
   returns the total count of characters represented by that fractional,
   or `0` if no fractional part is recognized."
  [codepoints]
  (if (= (first codepoints) 46) ;; decimal point
    (-> codepoints rest integer-token inc)
    0))

(defn- number-token
  "Scans a number (integer/floating-point) at the beginning of the input,
   returns the total count of characters represented by that number,
   or `0` if no number is recognized."
  [codepoints]
  (let [integer-len (integer-token codepoints)]
    (+ integer-len (->> codepoints (drop integer-len) fractional-token))))

(defn- operator-token
  "Scans an operator at the beginning of the input string,
   returns the operator or `nil` if no operator is recognized."
  [codepoints]
  (case (first codepoints)
    40 :OpenParenthesis
    41 :CloseParenthesis
    42 :Star
    43 :Plus
    45 :Minus
    47 :Slash
    nil))

(defn- peek-token
  "Scans a token at the beginning of the input string,
   returns the token or `nil` if no valid token is recognized."
  [codepoints]
  (let [space-len (count-whitespaces codepoints)
        trimmed (drop space-len codepoints)
        op (operator-token trimmed)]
    (if op
      {:type op :pos space-len :len 1 :str (-> trimmed first char)}
      (let [num-len (number-token trimmed)]
        (if (pos? num-len)
          {:type :Number :pos space-len :len num-len
           :str (map char (take num-len trimmed))}
          (when (seq trimmed)
            (throw (#?(:clj Exception.)
                    #?(:cljs js/Error.)
                    (str "Invalid character " (-> trimmed first char))))))))))

(defn ^:export tokens
  "Tokenizes a string and returns the tokens as a vector."
  [expr]
  (loop [codepoints (string->codepoints expr)
         index 0
         tokens []]
    (let [token (peek-token codepoints)]
      (if (nil? token)
        tokens
        (let [delta (+ (:pos token) (:len token))]
          (recur (drop delta codepoints)
                 (+ index delta)
                 (conj tokens (update-in token [:pos] + index))))))))