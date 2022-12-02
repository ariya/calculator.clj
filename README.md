# calculator

This is a simple math expression evaluator written in [Clojure](https://clojure.org).

With [Java](https://adoptium.net) and [Leiningen](https://leiningen.org):
```
$ lein run "2 + (5 * 6 + 10)"
42.0
```

With [Babashka](https://babashka.org/) or [nbb](https://github.com/babashka/nbb):
```
$ bb --classpath src -m calculator.core "2 + (5 * 6 + 10)"
42.0
$ npx nbb --classpath src --main calculator.core "2 + (5 * 6 + 10)"
42.0
```

With [JavaScript](https://www.ecma-international.org/publications-and-standards/standards/ecma-262/) and [Node.js](https://nodejs.org):
```
$ npm install
$ npm run build
$ npm run calc "2 + (5 * 6 + 10)"
42
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

Create a native executable with [native-image](https://www.graalvm.org/22.0/reference-manual/native-image) from [GraalVM](https://www.graalvm.org/):
```bash
$ lein uberjar
Compiling calculator.core
Compiling calculator.evaluator
Compiling calculator.lexer
Compiling calculator.parser
$ native-image --no-fallback -jar target/default+uberjar/calculator-0.1.0-SNAPSHOT-standalone.jar calc
42.0
GraalVM Native Image: Generating 'calc' (executable)...
[1/7] Initializing...
 Version info: 'GraalVM 22.1.0 Java 11 CE'
 C compiler: gcc (linux, x86_64, 9.4.0)
 Garbage collector: Serial GC
[2/7] Performing analysis...
[3/7] Building universe...
[4/7] Parsing methods...
[5/7] Inlining methods...
[6/7] Compiling methods...
[7/7] Creating image...
Finished generating 'calc' in 12.8s.
$ ./calc "2 + (5 * 6 + 10)"
42.0
```

