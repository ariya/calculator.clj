# calculator

This is a simple math expression evaluator written in [Clojure](https://clojure.org).

Requirements: [Java](https://adoptium.net), [Leiningen](https://leiningen.org).

Try it:
```
$ lein run "2 + (5 * 6 + 10)"
42.0
```

Supported features:

* Integer and floating-point literals
* Additions, subtractions, multiplications, divisions
* Parenthesized sub-expressions

TODO:

* Floating-point literals with exponents (e.g. `6.022E23`)
* Modulus/remainder
* Trigonometric functions
* More functions
