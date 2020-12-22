# dot-clojure

This is my personal `.clojure/deps.edn` file providing useful `clj` aliases drawn from a variety of projects. It is published to GitHub so I can keep all my computers sync'd up -- and to provide a range of examples that folks new to the Clojure CLI might find helpful.

The main alias I use here is `:dev` which starts various combinations of REPL tooling. See [**The `:dev` Alias**](#the-dev-alias) below for more details.

_Since it is my personal file, it may make assumptions about my own environment. For example, it uses `"RELEASE"` for several tools so I can always get the latest stable version of any dev/test tool I use. I make no effort at backward-compatibility and may add, delete, or change aliases as they benefit me personally. Caveat Programmer!_

**If you want a really well-documented, well-maintained alternative that actually tracks versions of tools, I would recommend you use the [Practicalli Clojure `deps.edn`](https://github.com/practicalli/clojure-deps-edn) project instead!**

## Basic Aliases

With that caveat out of the way, here is some basic documentation about my aliases (there are additional examples in the comments in the `deps.edn` file itself):

An alias to pull in my template creation tool:
* `:new` -- pulls in and runs the latest stable release of [clj-new](https://github.com/seancorfield/clj-new) to create new projects from (Leiningen and other) templates

Aliases to build jar & uberjar files:
* `:uberjar` -- pulls in and runs the latest stable release of [depstar](https://github.com/seancorfield/depstar) to create an uberjar; `clojure -X:uberjar :jar MyProject.jar :main-class project.core`; `java -jar MyProject.jar`
* `:jar` -- pulls in and runs the latest stable release of [depstar](https://github.com/seancorfield/depstar) to create a "thin" JAR; `clojure -X:jar :jar MyProject.jar`; along with a `pom.xml`, this can be deployed to Clojars etc (via `clojure -M:deploy ...` -- see below)

And install or deploy jar files:
* `:install` -- pulls in and runs the latest stable release of Erik Assum's [deps-deploy](https://github.com/slipset/deps-deploy) and installs the specified JAR file locally, based on your `pom.xml`
* `:deploy` -- pulls in and runs the latest stable release of Erik Assum's [deps-deploy](https://github.com/slipset/deps-deploy) and deploys the specified JAR file to Clojars, based on your `pom.xml` and the `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables

There are aliases to pull in various useful testing and debugging tools:
* `:test` -- adds both `test` and `src/test/clojure` to your classpath and pulls in the latest stable version of `test.check`
* `:runner` -- pulls in [Cognitect Labs' `test-runner`](https://github.com/cognitect-labs/test-runner) project and runs any tests it can find
* `:readme` -- pulls in the latest stable release of [seancorfield/readme](https://github.com/seancorfield/readme) and runs it on your `README.md` file to treat your examples as tests
* `:eastwood` -- pulls in the latest stable release of [Eastwood](https://github.com/jonase/eastwood) on your `src` and `test` folders; use with `:test` above
* `:check` -- pulls in [Athos' Check](https://github.com/athos/clj-check) project to compile all your namespaces to check for syntax errors and reflection warnings like `lein check`
* `:expect` -- pulls in the latest stable releases of [Expectations](https://github.com/clojure-expectations/expectations) and [expectations/clojure-test](https://github.com/clojure-expectations/clojure-test) -- the latter is the `clojure.test`-compatible version of the former
* `:bench` -- pulls in the latest stable release of [Criterium](https://github.com/hugoduncan/criterium/) for benchmarking your code
* `:decompile` -- pulls in the latest stable release of [Clojure Goes Fast's decompiler](https://github.com/clojure-goes-fast/clj-java-decompiler); requires JDK 8 (not later)
* `:measure` -- pulls in the latest stable release of [Memory Meter](https://github.com/clojure-goes-fast/clj-memory-meter)
* `:outdated` -- pulls in and runs the latest stable release of [antq](https://github.com/antq/antq) and reports on outdated dependencies

There are aliases to pull in and start various REPL-related tools:
* `:dev` -- depending on what is on your classpath, start Cognitect's REBL or Reveal or Rebel Readline (or a plain Clojure REPL), with a Socket REPL (on port 50505, but `SOCKET_REPL_PORT` env var overrides, saves port to `.socket-repl-port` file for next time); if Reveal is started, adds an auto-table view for `tap>`'d values; usage: `clj -M:rebl:dev` or `clj -M:reveal:dev` or `clojure -M:rebel:dev` or `clojure -M:rebel:reveal:dev` (for both of them together)
* `:nrepl` -- pulls in the latest stable release of [nREPL](https://github.com/nrepl/nREPL) and starts an nREPL server on a random available port
* `:socket` -- starts a Socket REPL on port 50505; can be combined with other aliases since this is just a JVM option
* `:socket-rebl` -- starts a Socket REPL on port 50123; assumes you have Cognitect's REBL on your classpath (see `:rebl` below); everything sent to this Socket REPL will also be `submit`ted to the REBL
* `:socket-zero` -- starts a Socket REPL on an available and displays the selected port number (using a `-e` option); if you want to start a REPL as well, you will need to specify the `-r` option: `clj -M:socket-zero -r`
* `:prepl` -- starts a Socket pREPL on port 40404; can be combined with other aliases since this is just a JVM option; requires a recent Clojure 1.10 build!
* `:rebel` -- starts a [Rebel Readline](https://github.com/bhauman/rebel-readline) REPL

* `:rebl` -- starts [Cognitect's REBL](https://github.com/cognitect-labs/REBL-distro) (assumes latest [Cognitect dev-tools](https://cognitect.com/dev-tools/) installed)
* `:reflect` -- adds Stuart Halloway's reflector utility (best used with REBL)
* `:reveal` -- pulls in the latest stable release of the [Reveal](https://github.com/vlaaad/reveal) data visualization tool -- see the Reveal web site for usage options
* `:portal` -- pulls in the latest stable release of the [Portal](https://github.com/djblue/portal) data visualization tool -- see the Portal web site for usage options

* `:comp` -- adds the latest stable release of [compliment](https://github.com/bbatsov/compliment); useful with a Socket REPL for Unravel or Chlorine for Atom

There are aliases to pull in specific versions of Clojure:
* `:master` -- Clojure 1.10.2-master-SNAPSHOT
* `:1.10.2` -- Clojure 1.10.2-alpha2
* `:1.10.1` -- Clojure 1.10.1
* `:1.10` -- Clojure 1.10.0
* `:1.9` -- Clojure 1.9.0
* ... back to `:1.0` (note: `:1.5` is actually Clojure 1.5.1 to avoid a bug in Clojure 1.5.0)

For the _EXPERIMENTAL_ `add-libs` function (`clojure.tools.deps.alpha.repl/add-libs`):
* `:add-libs` -- pulls in the `add-lib3` branch of [org.clojure/tools.deps.alpha](https://github.com/clojure/tools.deps.alpha); see the example `load-master` function in the comments in my `deps.edn`; this was previously called `:deps` but I realized that conflicted with the default `:deps` alias in the Clojure CLI install; be aware that `add-libs` is unsupported and likely to break or go away as `tools.deps.alpha` and Clojure both evolve. _[recently renamed from `:add-lib` to `:add-libs` to reflect the name change in the `add-lib3` branch!]_

For `tools.deps.graph`:
* `:graph` -- pulls in a recent version of [tools.deps.graph](https://github.com/clojure/tools.deps.graph) to help you visualize dependency graphs

For Spec 2 (unstable, buggy -- not ready for production use):
* `:spec2` -- pulls in [org.clojure/spec-alpha2](https://github.com/clojure/spec-alpha2) via GitHub

An alias for the [Liquid Clojure editor](https://github.com/mogenslund/liquid):
* `:liquid` -- pulls in and runs the latest stable release of Liquid

For shell-related stuff:
* `:closh` -- pulls in and runs (from source) version 0.5.0 of the JVM version of [closh](https://github.com/dundalek/closh) which gives you a Clojure-enabled terminal shell (it's wonderful!)

And finally, a gnarly little macro, inspired by Ruby's `-pne` command line option
that lets you process lines of standard input:
* `:pne` -- `cat file-of-numbers.txt | clojure -M:pne -e '($ (-> $_ Long/parseLong inc))'`; `$` reads stdin and evaluates the expression repeatedly with `$_` bound to each line, printing the results to stdout.

> Note: if you're using `closh`, you can do the same thing as `:pne` directly in the shell: `cat file-of-numbers.txt |> (run! #(-> % Long/parseLong inc println))`

## The `:dev` Alias

The `:dev` alias uses `load-file` to load the [`dev.clj` file](https://github.com/seancorfield/dot-clojure/blob/develop/dev.clj) from this repo. That does a number of things (see the `start-repl` docstring for more details):

* Starts a Socket REPL server (with the port selected via an environment variable, a JVM property, or a dot-file created on a previous run).
* Starts [Cognitect's REBL](https://github.com/cognitect-labs/REBL-distro), if present on the classpath, else
* Starts [Reveal](https://github.com/vlaaad/reveal/), if present on the classpath, else
* Starts [Rebel Readline](https://github.com/bhauman/rebel-readline), if present on the classpath.

If both Reveal and Rebel Readline are present on the classpath, it starts both of them, using Rebel Readline for the primary REPL, with everything input there appearing in Reveal. In addition, everything `tap>`'d will be displayed inside Reveal.

If the `dev.clj` starts Reveal, it also `tap>`'s a Reveal view into it, which you can activate by right-clicking and selecting the `view` option (instead of right-click, you can press `space` which the name of the view -- `right-click > view` -- is highlighted).

This view provides a number of features that apply automatically to anything that is `tap>`'d -- the view automatically updates each time a new value is submitted.

If a `Var` is submitted, the view `deref`'s it so that the associated value is displayed (along with the metadata from the `Var` itself). If a function or namespace value is submitted, the docstring is displayed, if available.

The view shows two panels:

* Metadata as a hash map. This also contains `:_class` with the `class` of the value submitted. If a `Var` was submitted, and its value has metadata, that will be present as `:_meta`.
* The underlying value itself, displayed as follows:
  * Strings are displayed in their "raw" form so they are laid out as they would print (without `"` and with `\n` as actual newlines etc),
  * `java.net.URL`'s are rendered inline as web pages, so you can browse documentation easily, for example. If you are using this with either my [Atom/Chlorine setup](https://github.com/seancorfield/atom-chlorine-setup) or my [VS Code/Clover setup](https://github.com/seancorfield/vscode-clover-setup), the `ctrl-; j` key will show the Javadoc page for the type of an expression if it is part of the Java standard library, and the `ctrl-; ?` key will show the ClojureDocs page for any symbol that is part of Clojure's core libraries.
  * Things that are not `seqable?` are displayed as a table with one row containing that value in the first column and its string representation in the second column.
  * Anything else is assumed to be some sort of sequence or collection, and is displayed in a table, with a row for each value in the collection:
    * A hash map is treated as a collection of `MapEntry`'s which are displayed with a column for the key and a column for the value (and thus one row for each key/value pair of the hash map).
    * A collection of maps is displayed with a column for each key (based on the first map in the sequence, like `clojure.pprint/print-table`).
    * A collection of indexed values is displayed with each row showing one of those values with up to 1,024 columns for the indexed elements (based on the number of elements in the first value in the sequence: an arbitrary, large limit to avoid problems with infinite sequences). The column headings are the indices of those elements.
