;; # Introduction to `clj-nats`
;;
;; This document gives a brief introduction to NATS, a system for persistent messaging, and clj-nats, a Clojure library for working with NATS.
;;
;; Note: `clj-nats` is still under development, and its API is subject to change.

{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(ns introduction-to-clj-nats
  (:require [babashka.fs :as fs]
            [nextjournal.clerk :as clerk]))

{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Why care about NATS for Clojure?
;;
;; Clojure encourages data oriented design.
;; NATS lets you work with persistent or ephemeral queues.

;; ## Installing `nats-server` (required)
;;
;; NATS is distributed as a small, statically linked binary.
;; Install `nats-server` on your system, then run it with
;;
;;     $ nats-server
;;
;; You don't need any configuration to get started using NATS.
;;
;; To install nats-server, see https://github.com/nats-io/nats-server.
;;
;; On Mac/homebrew, you can
;;
;;     brew install nats-server

;; ## Installing `nats` (optional)
;;
;; `nats` is a small tool for poking at `nats-server` data.
;; It lets you write messages, read messages and follow changes to streams.

;; ## NATS topics
;;
;; NATS messages are organized under topics.
;; Topics can be nested.

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (clerk/build! {:paths [(fs/file-name *file*)] :out-path "."})
  (clerk/clear-cache!)
  ,)
