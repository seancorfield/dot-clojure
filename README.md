# dot-clojure

This is my `.clojure/deps.edn` file providing useful `clj` aliases drawn from a variety of projects.

There are aliases to pull in specific versions of Clojure:
* `:1.8` -- Clojure 1.8.0
* `:1.9` -- Clojure 1.9.0
* `:master` -- Clojure 1.10.0-master-SNAPSHOT

There are aliases to pull in various useful testing and debugging tools:
* `:test` -- adds both `test` and `src/test/clojure` to your classpath and pulls in the latest stable version of `test.check`
* `:runner` -- pulls in [Cognitect Labs' `test-runner`](https://github.com/cognitect-labs/test-runner) project and runs any tests it can find
* `:expect` -- pulls in the latest stable release of [Expectations](https://github.com/clojure-expectations/expectations)
* `:bench` -- pulls in the latest stable release of [Criterium](https://github.com/hugoduncan/criterium/) for benchmarking your code
* `:measure` -- pulls in the latest stable release of [Memory Meter](https://github.com/clojure-goes-fast/clj-memory-meter)

There are aliases to pull in and start various REPL-related tools:
* `:nrepl` -- starts an nREPL server on port 60606
* `:socket` -- starts a Socket REPL on port 50505
* `:proto` -- adds the latest stable release of the [protorepl](https://atom.io/packages/proto-repl) library for Atom/ProtoREPL usage (with `:nrepl`)
* `:rebel` -- starts a [Rebel Readline](https://github.com/bhauman/rebel-readline) REPL

And finally an alias to pull in my template creation tool:
* `:new` -- pulls in and runs [clj-new](https://github.com/seancorfield/clj-new) to create new projects from (Leiningen and other) templates
