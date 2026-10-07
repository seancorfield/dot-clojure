# dot-clojure

This is my personal `.config/clojure/deps.edn` (or `.clojure/deps.edn`) file providing useful `clj` aliases drawn from a variety of projects. It is published to GitHub so I can keep all my computers sync'd up -- and to provide a range of examples that folks new to the Clojure CLI might find helpful.

**The default Clojure version in use here is 1.13.0-alpha8!**

**I highly recommend ensuring you have [the latest Clojure CLI](https://clojure.org/releases/tools) installed!**

> The latest Clojure CLI was 1.12.6.1673 (September 7th, 2026) when I last updated this file.

Since the release of the [Clojure CLI REPL](https://github.com/clojure/clojure-cli.repl), my `.cljconf/org.clojure` folder is here, containing my personal configuration based on the [examples provided](https://github.com/clojure/clojure-cli.repl/tree/main/examples/.cljconf/org.clojure). This includes logic to add Portal and `rephrase` middleware if they are on the classpath. _This has replaced my former custom REPL startup code._

There is also a `.cljconf/org.corfield` folder containing my `deps-new.edn` defaults, which selects Babashka for builds and LazyTest for the test runner.

In addition, my `.config/clojure/tools/` (`.clojure/tools/`) folder is also here, containing the tools that I've installed globally, via the Clojure CLI -- see [Tool installation and invocation](https://clojure.org/reference/clojure_cli#tool_install) in the Clojure CLI Reference. As I add global tools, I am removing them as aliases.

The main alias I use here is `:repl` which starts various combinations of REPL tooling via the Clojure CLI REPL.

_Since it is my personal file, it may make assumptions about my own environment. I make no effort at backward-compatibility and may add, delete, or change aliases as they benefit me personally. Caveat Programmer!_

**If you want a really well-documented, well-maintained alternative that actually tracks versions of tools, I would recommend you use the [Practicalli Clojure `deps.edn`](https://github.com/practicalli/clojure-deps-edn) project instead!**

With that caveat out of the way, here is some basic documentation about my tools and aliases (there are additional examples in the comments in the `deps.edn` file itself). _Note: I have recently cleaned this file up and removed a lot of aliases I no longer use!_

I start a REPL using `clojure -M:repl` with several additional aliases to bring in various tooling, such as Portal, `rephrase`, and CIDER nREPL middleware.

There is also a `bin/repl` bash script that runs
`clojure "$@" -M:1.13:allow-attach-self:portal:test:cider-nrepl:rephrase:repl`
to start an nREPL server with CIDER middleware, and then a client to that nREPL 
server, with Portal available (and `clojure.tools.logging`, if
present, patched to `tap>` all log messages for Portal, also `logging4j2` -- my log4j2 wrapper).

The `:allow-attach-self` alias sets the JVM property
`-Djdk.attach.allowAttachSelf` for JDK 21+ so that
nREPL can stop evaluation threads.

## Basic Tools

These are installed via `clojure -Ttools install ...` and usable via `clojure -T` with the tool name.

* `antq` -- the outdated dependencies checker:
  * `clojure -Tantq outdated` -- check the current project's dependencies,
  * `clojure -A:deps -Tantq help/doc` -- for more information and other functions.
* `clj-watson` -- a software composition analysis scanner, based on the National Vulnerability Database: [clj-watson](https://github.com/clj-holmes/clj-watson)
  * `clojure -Tclj-watson scan :deps-edn-path '"deps.edn"' :output '"stdout"'`
* `new` -- the latest version of [deps-new](https://github.com/seancorfield/deps-new) to create new CLI/`deps.edn` projects: _[This uses a different (simpler!) templating system to `clj-new`, below, and therefore does not recognize Leiningen or Boot templates!]_
  * `clojure -Tnew app :name myname/myapp` -- creates a new `deps.edn`-based application project,
  * `clojure -Tnew lib :name myname/mylib` -- creates a new `deps.edn`-based library project,
  * `clojure -Tnew template :name myname/mytemplate` -- creates a new `deps.edn`-based template project,
  * `clojure -A:somealias -Tnew create :template some/thing :name myname/myapp` -- locates a template for `some/thing` on the classpath, based on `:somealias`, and uses it to create a new `deps.edn`-based project,
  * `clojure -A:deps -Tnew help/doc` -- for more information and other functions.

And the older `clj-new` tool:

* `clj-new` -- a recent stable release of [clj-new](https://github.com/seancorfield/clj-new) to create new projects from (Leiningen and other) templates:
  * `clojure -Tclj-new app :name myname/myapp` -- creates a new `deps.edn`-based application project (using `tools.build` for the uberjar),
  * `clojure -Tclj-new lib :name myname/mylib` -- creates a new `deps.edn`-based library project (using `tools.build` for the jar),
  * `clojure -Tclj-new template :name myname/mytemplate` -- creates a new `deps.edn`-based template project (using `tools.build` for the jar),
  * `clojure -Tclj-new create :template something :name myname/myapp` -- locates a template for `something` and uses it to create a new project (which might be `deps.edn`-based or `lein`-based, depending on the template),
  * `clojure -A:deps -Tclj-new help/doc` -- for more information and other functions.

More tools will be added to this section over time (as more tools add `:tools/usage` to their `deps.edn` files).

## Basic Aliases

Deploy jar files (if you don't have a `build.clj` file):
* `:deploy` -- pulls in and runs a recent stable release of Borkdude's [deps-deploy](https://github.com/babashka/deps-deploy) and deploys the specified JAR file to Clojars, based on your `pom.xml` and the `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables; `clojure -X:deploy :artifact '"MyProject.jar"'`

There are aliases to pull in various useful testing and debugging tools:
* `:test` -- adds both `test` and `src/test/clojure` to your classpath and pulls in the latest stable version of `test.check`
* `:lazy` -- adds a recent stable release of [NoahTheDuke's Lazytest](https://github.com/NoahTheDuke/lazytest) for more expressive and powerful testing; can be used as `clojure -M:test:lazy` to run just Lazytest tests, or `clojure -X:test:lazy:runner` to run both Lazytest and `clojure.test` tests (via my fork of Cognitect's `test-runner` project)
* `:runner` -- pulls in my fork of [Cognitect Labs' `test-runner`](https://github.com/cognitect-labs/test-runner) project and runs any tests it can find
* `:splint` -- pulls in and runs a recent stable release of [Splint](https://github.com/NoahTheDuke/splint) on your project or specific files
* `:check` -- pulls in [Athos' Check](https://github.com/athos/clj-check) project to compile all your namespaces to check for syntax errors and reflection warnings like `lein check`
* `:bench` -- pulls in a recent stable release of [Criterium](https://github.com/hugoduncan/criterium/) for benchmarking your code

* `:this` -- adds the current directory as a `:local/root` dependency so that you can use `help/doc` on namespaces within your project, e.g., `clojure -X:deps:this help/doc :ns my.app.core`
* `:no-main` -- adds an empty `:main-opts` so that you can run `clojure -M:test:no-main ...` in projects that combine the test deps with the test runner (instead of having them separate as this `deps.edn` has them). Because `:main-opts` is "last one wins", this allows you to essentially override (or remove) any `:main-opts` from aliases, so you can manually specify your own main options on the command-line.

There are aliases to pull in and start various REPL-related tools:
* `:repl`, `:serve`, `:attach` -- aliases for the [Clojure CLI REPL](https://github.com/clojure/clojure-cli.repl) server and client. **`:repl` is my primary way to start an interactive REPL session, with additional aliases for adding middleware etc.**
* `:portal` -- pulls in a recent stable release of the [Portal](https://github.com/djblue/portal) data visualization tool -- see the Portal web site for usage options
* `:rephrase` -- adds the latest stable release of [rephrase](https://github.com/seancorfield/rephrase), which provides nREPL middleware to rephrase error messages into more beginner-friendly versions.

* `:cider-nrepl` -- starts a (headless) CIDER-enhanced [nREPL server](https://nrepl.org/) on a random available port; `clojure -M:cider-nrepl`; when used with `:repl` or `:serve` above, will add the CIDER middleware.

* `:nrepl` -- starts a (headless) [nREPL server](https://nrepl.org/) on a random available port; `clojure -M:nrepl`
* `:classes` -- adds the `classes` folder to your classpath to pick up compiled code (e.g., see https://clojure.org/guides/dev_startup_time)

There are aliases to pull in specific versions of Clojure:
* `:1.13` -- Clojure 1.13.0-alpha8 -- see [changes to Clojure in the 1.13 Alpha releases](https://clojure.org/releases/devchangelog#_release_1_13_x)
* `:1.12` -- Clojure 1.12.6 -- see [changes to Clojure in version 1.12.6](https://github.com/clojure/clojure/blob/master/changes.md)
  * `:1.12.0` -- Clojure 1.12.0
  * `:1.12.1` -- Clojure 1.12.1
  * `:1.12.2` -- Clojure 1.12.2
  * `:1.12.3` -- Clojure 1.12.3
  * `:1.12.4` -- Clojure 1.12.4
  * `:1.12.5` -- Clojure 1.12.5
* `:1.11` -- Clojure 1.11.4
  * `:1.11.3` -- Clojure 1.11.3
  * `:1.11.2` -- Clojure 1.11.2
  * `:1.11.1` -- Clojure 1.11.1
  * `:1.11.0` -- Clojure 1.11.0
* `:1.10` -- Clojure 1.10.3
  * `:1.10.2` -- Clojure 1.10.2
  * `:1.10.1` -- Clojure 1.10.1
  * `:1.10.0` -- Clojure 1.10.0
* `:1.9` -- Clojure 1.9.0
* `:1.8` -- Clojure 1.8.0
* ... back to `:1.0` (note: `:1.5` is actually Clojure 1.5.1 to avoid a bug in Clojure 1.5.0, and `:1.2` is 1.2.1)

> Note: the `:master` alias has been removed since it is rarely different from the most recent (alpha) release of Clojure.

To work with the Polylith command-line tool:
* `:poly` -- the latest (stable) release of [Polylith's `poly` tool](https://github.com/polyfy/polylith), as a library from Clojars -- example usage:
  * `clojure -M:poly shell` -- start an interactive Polylith shell,
  * `clojure -M:poly info :loc` -- display information about a Polylith workspace, including lines of code,
  * `clojure -M:poly create component name:user` -- create a `user` component in a Polylith workspace,
  * `clojure -M:poly test :dev` -- run tests in the `dev` project context, in a Polylith workspace.

> Note: the _EXPERIMENTAL_ `:add-libs` alias has been removed -- use the [`clojure.repl.deps`](https://clojure.github.io/clojure/branch-master/clojure.repl-api.html#clojure.repl.deps) in Clojure 1.12.0 or later instead!

## My Clojure CLI REPL Setup

The updated `hooks.clj` file installs an `uptime` function in the `user` namespace, which allows you to easily see how long your REPL has been running, in a human-readable format.

The updated `middleware.clj` file conditionally provides middleware for Portal (this will not be needed with the next version of Portal that will support `{:all-evals true}` config in nREPL).

* If both Portal and `org.clojure/tools.logging` are on the classpath, it patches `tools.logging` to also `tap>` every log message in a format that Portal understands and can display (usually with the ability to go to the file/line listed in the log entry).
* If both Portal and `com.github.seancorfield/logging4j2` are on the classpath, it patches `logging4j2` to also `tap>` every log message in a format that Portal understands and can display (usually with the ability to go to the file/line listed in the log entry).

# License

Copyright © 2018-2026 Sean Corfield

Distributed under the Apache Software License version 2.0.
