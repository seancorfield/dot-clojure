# dot-clojure

This is my personal `.clojure/deps.edn` file providing useful `clj` aliases drawn from a variety of projects. It is published to GitHub so I can keep all my computers sync'd up -- and to provide a range of examples that folks new to the Clojure CLI might find helpful.

**Several git dependencies here assume you have at least Clojure CLI 1.10.3.933!**

In addition, my `.clojure/tools/` folder is also here, containing the tools that I've installed globally, via the latest Clojure CLI (was 1.11.1.1129 when I last updated this) -- see [Tool installation and invocation](https://clojure.org/reference/deps_and_cli#tool_install) in the Deps and CLI Reference. As I add global tools, I am removing them as aliases.

The main alias I use here is `:dev/repl` which starts various combinations of REPL tooling. See [**The `:dev/repl` Alias**](#the-devrepl-alias) below for more details.

_Since it is my personal file, it may make assumptions about my own environment. For example, it uses `"RELEASE"` for several tools so I can always get the latest stable version of any dev/test tool I use. I make no effort at backward-compatibility and may add, delete, or change aliases as they benefit me personally. Caveat Programmer!_

**If you want a really well-documented, well-maintained alternative that actually tracks versions of tools, I would recommend you use the [Practicalli Clojure `deps.edn`](https://github.com/practicalli/clojure-deps-edn) project instead!**

With that caveat out of the way, here is some basic documentation about my tools and aliases (there are additional examples in the comments in the `deps.edn` file itself). _Note: I have recently cleaned this file up and removed a lot of aliases I no longer use!_

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
* `poly` -- a recent **master** version of [Polylith's `poly` tool](https://github.com/polyfy/polylith) for working with Polylith projects:
  * `clojure -Tpoly shell` -- start an interactive Polylith shell,
  * `clojure -Tpoly info :loc true` -- display information about a Polylith workspace, including lines of code,
  * `clojure -Tpoly create c user` -- create a `user` component in a Polylith workspace,
  * `clojure -Tpoly test :dev true` -- run tests in the `dev` project context, in a Polylith workspace,
  * `clojure -A:deps -Tpoly help/doc` -- for more information and other functions.

And the older `clj-new` tool:

* `clj-new` -- the latest stable release of [clj-new](https://github.com/seancorfield/clj-new) to create new projects from (Leiningen and other) templates:
  * `clojure -Tclj-new app :name myname/myapp` -- creates a new `deps.edn`-based application project (using `tools.build` for the uberjar),
  * `clojure -Tclj-new lib :name myname/mylib` -- creates a new `deps.edn`-based library project (using `tools.build` for the jar),
  * `clojure -Tclj-new template :name myname/mytemplate` -- creates a new `deps.edn`-based template project (using `tools.build` for the jar),
  * `clojure -Tclj-new create :template something :name myname/myapp` -- locates a template for `something` and uses it to create a new project (which might be `deps.edn`-based or `lein`-based, depending on the template),
  * `clojure -A:deps -Tclj-new help/doc` -- for more information and other functions.

More tools will be added to this section over time (as more tools add `:tools/usage` to their `deps.edn` files).

## Basic Aliases

Deploy jar files (if you don't have a `build.clj` file):
* `:deploy` -- pulls in and runs the latest stable release of Erik Assum's [deps-deploy](https://github.com/slipset/deps-deploy) and deploys the specified JAR file to Clojars, based on your `pom.xml` and the `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables; `clojure -X:deploy :artifact '"MyProject.jar"'`

There are aliases to pull in various useful testing and debugging tools:
* `:test` -- adds both `test` and `src/test/clojure` to your classpath and pulls in the latest stable version of `test.check`
* `:runner` -- pulls in [Cognitect Labs' `test-runner`](https://github.com/cognitect-labs/test-runner) project and runs any tests it can find
* `:eastwood` -- pulls in the latest stable release of [Eastwood](https://github.com/jonase/eastwood) on your `src` and `test` folders; use with `:test` above
* `:check` -- pulls in [Athos' Check](https://github.com/athos/clj-check) project to compile all your namespaces to check for syntax errors and reflection warnings like `lein check`
* `:expect` -- pulls in the latest stable release of [expectations/clojure-test](https://github.com/clojure-expectations/clojure-test) -- the `clojure.test`-compatible version of Expectations
* `:bench` -- pulls in the latest stable release of [Criterium](https://github.com/hugoduncan/criterium/) for benchmarking your code

There are aliases to pull in and start various REPL-related tools:
* `:dev/repl` -- depending on what is on your classpath, start Rebel Readline, with a Socket REPL (on "port 0" which will dynamically select an available port and print it out), but `SOCKET_REPL_PORT` env var overrides, saves port to `.socket-repl-port` file for next time);
  * usage:
    * `clj -M:portal:dev/repl` -- basic REPL with Portal or
    * `clojure -M:rebel:dev/repl` -- Rebel Readline REPL or
    * `clojure -M:rebel:portal:dev/repl` -- ...with Portal or
    * `clojure -M:nrepl:dev/repl` -- basic nREPL server or
    * `clojure -M:nrepl:portal:dev/repl` -- basic nREPL server with Portal middleware or
    * `clojure -M:cider-nrepl:dev/repl` -- CIDER nREPL server or
    * `clojure -M:cider-nrepl:portal:dev/repl` -- CIDER nREPL server with Portal middleware or
  * Also works with Figwheel Main (now that I've started doing ClojureScript!):
    * `clojure -M:portal:fig:build:dev/repl` or
* `:classes` -- adds the `classes` folder to your classpath to pick up compiled code (e.g., see https://clojure.org/guides/dev_startup_time)
* `:socket` -- starts a Socket REPL on port 50505; can be combined with other aliases since this is just a JVM option
* `:rebel` -- starts a [Rebel Readline](https://github.com/bhauman/rebel-readline) REPL
* `:nrepl` -- starts a (headless) [nREPL server](https://nrepl.org/) on a random available port; `clojure -M:nrepl`
* `:cider-nrepl` -- starts a (headless) CIDER-enhanced [nREPL server](https://nrepl.org/) on a random available port; `clojure -M:cider-nrepl`

* `:jedi-time` -- adds `datafy`/`nav` support for Java Time objects via [jedi-time](https://github.com/jimpil/jedi-time)
* `:portal` -- pulls in the latest stable release of the [Portal](https://github.com/djblue/portal) data visualization tool -- see the Portal web site for usage options
* `:reflect` -- adds Stuart Halloway's reflector utility (best used with Portal)

There are aliases to pull in specific versions of Clojure:
* `:master` -- Clojure 1.12.0-master-SNAPSHOT
* `:1.12` -- Clojure 1.12.0-alpha1
* `:1.11` -- Clojure 1.11.1 -- see [changes to Clojure in version 1.11.1](https://github.com/clojure/clojure/blob/master/changes.md)
  * `:1.11.0` -- Clojure 1.11.0
* `:1.10` -- Clojure 1.10.3
  * `:1.10.2` -- Clojure 1.10.2
  * `:1.10.1` -- Clojure 1.10.1
  * `:1.10.0` -- Clojure 1.10.0
* `:1.9` -- Clojure 1.9.0
* `:1.8` -- Clojure 1.8.0
* ... back to `:1.0` (note: `:1.5` is actually Clojure 1.5.1 to avoid a bug in Clojure 1.5.0, and `:1.2` is 1.2.1)

For the _EXPERIMENTAL_ `add-libs` function (`clojure.tools.deps.alpha.repl/add-libs`):
* `:add-libs` -- pulls in the `add-lib3` branch of [org.clojure/tools.deps.alpha](https://github.com/clojure/tools.deps.alpha); see the example `load-master` function in the comments in my `deps.edn`; this was previously called `:deps` but I realized that conflicted with the default `:deps` alias in the Clojure CLI install; be aware that `add-libs` is unsupported and likely to break or go away as `tools.deps.alpha` and Clojure both evolve. _[recently renamed from `:add-lib` to `:add-libs` to reflect the name change in the `add-lib3` branch!]_

## The `:dev/repl` Alias

The `:dev/repl` alias uses `load-file` to load the [`dev.clj` file](https://github.com/seancorfield/dot-clojure/blob/develop/dev.clj) from this repo. That does a number of things (see the `start-repl` docstring for more details):

* Starts a Socket REPL server (with the port selected via an environment variable, a JVM property, or a dot-file created on a previous run) -- unless `SOCKET_REPL_PORT=none` which suppresses starting it.
* If both Portal and `org.clojure/tools.logging` are on the classpath, it patch `tools.logging` to also `tap>` every log message in a format that Portal understands and can display (usually with the ability to go to the file/line listed in the log entry); call `(dev/toggle-logging!)` to turn this `tap>`'ing on and off.
* If Portal 0.33.0 or later is on the classpath, use the Portal middleware with nREPL (if CIDER or nREPL are on the classpath).
* Starts [Figwheel Main](https://github.com/bhauman/figwheel-main), if present on the classpath, else
* Starts [Rebel Readline](https://github.com/bhauman/rebel-readline), if present on the classpath, else
* Starts a CIDER-enhanced [nREPL Server](https://nrepl.org/), if `cider-nrepl` is present on the classpath, else
* Starts an [nREPL Server](https://nrepl.org/), if present on the classpath.

_Note 1: since the `dev.clj` code uses `requiring-resolve`, it requires at least Clojure 1.10.0!_

_Note 2: the `:dev/repl` alias assumes the `dev.clj` file can be loaded from `~/.clojure/dev.clj` which is not correct for XDG systems (it'll be `~/.config/clojure`)._

_Note 3: if the Portal middleware is added to nREPL/CIDER, all evaluated results will be `tap>`'d (if the Portal UI is open and listening); my [VS Code/Calva setup](https://github.com/seancorfield/vscode-calva-setup) has additional configuration for working with Portal when the middleware is enabled!_

## Use with Figwheel

If you are doing ClojureScript development with Figwheel (`figwheel-main`) then you can do something like:

```
clojure -M:portal:fig:build:dev/repl
```

You'll get the regular Figwheel build REPL (for ClojureScript, which uses Rebel Readline) and a browser open on your application, plus a Socket REPL on an available port (or whatever your env says, for Clojure evaluation).

Connect to the Socket REPL, write your code as `.cljc` files, and you'll have the full power of your editor, Portal, and Figwheel! What you evaluate in your editor will be treated as Clojure code (and can be `tap>`'d into Portal, for example). What you evaluate at the REPL itself will be treated as ClojureScript code (and will affect your application instead).

# License

Copyright © 2018-2022 Sean Corfield

Distributed under the Apache Software License version 2.0.
