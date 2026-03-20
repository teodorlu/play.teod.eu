---
description: how to reload Clojure code after making changes
---

When Clojure source files have been modified and you need to reload code in the running REPL, use Invoker's reload commands:

// turbo
1. Reload changed namespaces: `bb nvk reload`
// turbo
2. Reload ALL namespaces: `bb nvk reload :all`

Do NOT use `clojure.core/require :reload` or `clojure.core/require :reload-all` for incremental reloads.
