# dot-clojure

This is my `.clojure/deps.edn` file providing useful `clj` aliases drawn from a variety of projects.

There are aliases to pull in specific versions of Clojure:
* `:master` -- Clojure 1.11.0-master-SNAPSHOT
* `:1.10.1` -- Clojure 1.10.1
* `:1.10` -- Clojure 1.10.0
* `:1.9` -- Clojure 1.9.0
* ... back to `:1.0` (note: `:1.5` is actually Clojure 1.5.1 to avoid a bug in Clojure 1.5.0)

There are aliases to pull in various useful testing and debugging tools:
* `:test` -- adds both `test` and `src/test/clojure` to your classpath and pulls in the latest stable version of `test.check`
* `:runner` -- pulls in [Cognitect Labs' `test-runner`](https://github.com/cognitect-labs/test-runner) project and runs any tests it can find
* `:readme` -- pulls in the latest stable release of [seancorfield/readme](https://github.com/seancorfield/readme) and runs it on your `README.md` file to treat your examples as tests
* `:eastwood` -- pulls in the latest stable release of [Eastwood](https://github.com/jonase/eastwood) on your `src` and `test` folders; use with `:test` above
* `:expect` -- pulls in the latest stable releases of [Expectations](https://github.com/clojure-expectations/expectations) and [expectations/clojure-test](https://github.com/clojure-expectations/clojure-test) -- the latter is the `clojure.test`-compatible version of the former
* `:bench` -- pulls in the latest stable release of [Criterium](https://github.com/hugoduncan/criterium/) for benchmarking your code
* `:measure` -- pulls in the latest stable release of [Memory Meter](https://github.com/clojure-goes-fast/clj-memory-meter)
* `:outdated` -- pulls in and runs version 1.8.4 of [Depot](https://github.com/Olical/depot) and reports on outdated dependencies
* `:decompile` -- pulls in the latest stable release of [Clojure Goes Fast's decompiler](https://github.com/clojure-goes-fast/clj-java-decompiler); requires JDK 8 (not later)

There are aliases to pull in and start various REPL-related tools:
* `:nrepl` -- pulls in the latest stable release of [nREPL](https://github.com/nrepl/nREPL) and starts an nREPL server on a random available port
* `:nrepl/old` -- pulls in the latest stable release of [org.clojure/tools.nrepl](https://github.com/clojure/tools.nrepl) and starts an nREPL server on port 60606; this is provided to support legacy tooling
* `:socket` -- starts a Socket REPL on port 50505; can be combined with other aliases since this is just a JVM option
* `:socket-zero` -- starts a Socket REPL on an available and displays the selected port number (using a `-e` option); if you want to start a REPL as well, you will need to specify the `-r` option: `clj -A:socket-zero -r`
* `:prepl` -- starts a Socket pREPL on port 40404; can be combined with other aliases since this is just a JVM option; requires a recent Clojure 1.10 build!
* `:proto` -- adds the latest stable release of the [protorepl](https://atom.io/packages/proto-repl) library for Atom/ProtoREPL usage (with `:nrepl` or `:nrepl/old`)
* `:rebel` -- starts a [Rebel Readline](https://github.com/bhauman/rebel-readline) REPL
* `:reflect` -- adds (my fork of) Stuart Halloway's reflector utility (best used with REBL)
* `:rebl-8` -- starts Cognitect's REBL (if you have it installed); compatible with OracleJDK 8 and Azul Zulu OpenJDK 8 (both of which include JavaFX); also `:rebl-8-win` for Windows
* `:rebl-11` -- starts Cognitect's REBL (if you have it installed); compatible with OpenJDK 11 (JavaFX is added as a separate set of dependencies); also `:rebl-11-win` for Windows
* `:comp` -- adds the latest stable release of [compliment](https://github.com/bbatsov/compliment); useful with a Socket REPL for Unravel or Chlorine for Atom

An alias for the [Liquid Clojure editor](https://github.com/mogenslund/liquid):
* `:liquid` -- pulls in and runs the latest stable release of Liquid

For the `add-lib` function (`clojure.tools.deps.alpha.repl/add-lib`):
* `:deps` -- pulls in a branch of [org.clojure/tools.deps.alpha](https://github.com/clojure/tools.deps.alpha); see the example `load-master` function in the comments in my `deps.edn`

For Spec 2:
* `:spec2` -- pulls in [org.clojure/spec-alpha2](https://github.com/clojure/spec-alpha2) via GitHub

An alias to pull in my template creation tool:
* `:new` -- pulls in and runs the latest stable release of [clj-new](https://github.com/seancorfield/clj-new) to create new projects from (Leiningen and other) templates

Aliases to build jar & uberjar files:
* `:uberjar` -- pulls in and runs the latest stable release of my fork of [depstar](https://github.com/seancorfield/depstar) to create an uberjar; `clj -A:uberjar MyProject.jar`; `java -cp MyProject.jar clojure.main -m project.core`
* `:jar` -- pulls in and runs the latest stable release of my fork of [depstar](https://github.com/seancorfield/depstar) to create a "thin" JAR; `clj -A:jar MyProject.jar`; along with a `pom.xml` (created via `clj -Spom`), this can be deployed to Clojars etc (via `mvn deploy:deploy-file ...`)

And install or deploy jar files:
* `:install` -- pulls in and runs the latest stable release of Erik Assum's [deps-deploy](https://github.com/slipset/deps-deploy) and installs the specified JAR file locally, based on your `pom.xml`
* `:deploy` -- pulls in and runs the latest stable release of Erik Assum's [deps-deploy](https://github.com/slipset/deps-deploy) and deploys the specified JAR file to Clojars, based on your `pom.xml` and the `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables

For shell-related stuff:
* `:closh` -- pulls in and runs (from source) version 0.5.0 of the JVM version of [closh](https://github.com/dundalek/closh) which gives you a Clojure-enabled terminal shell (it's wonderful!)

And finally, a gnarly little macro, inspired by Ruby's `-pne` command line option
that lets you process lines of standard input:
* `:pne` -- `cat file-of-numbers.txt | clj -Mpne -e '($ (-> $_ Long/parseLong inc))'`; `$` reads stdin and evaluates the expression repeatedly with `$_` bound to each line, printing the results to stdout.

> Note: if you're using `closh`, you can do the same thing as `:pne` directly in the shell: `cat file-of-numbers.txt |> (run! #(-> % Long/parseLong inc println))`
