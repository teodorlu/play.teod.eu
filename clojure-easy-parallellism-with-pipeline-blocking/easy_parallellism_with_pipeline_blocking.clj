^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns easy-parallellism-with-pipeline-blocking
  {:nextjournal.clerk/toc true})

^{:nextjournal.clerk/visibility {:code :hide}}
(do
  (require 'nextjournal.clerk)
  (nextjournal.clerk/html [:a {:data-ignore-anchor-click true :href ".."} ".."]))

;; # Easy, explicit parallellism with `pipeline-blocking`
;;
;; If you want a `map` to go faster, you could use `pmap`.
;; But what if you need more control than `pmap` can give you?
;; What if your problem is memory constrained?
;; Read on to discover your options!

^{:nextjournal.clerk/visibility {:result :hide}}
(do
  (refer-clojure :exclude '[time])
  (require
   '[clojure.core.async :as a]
   '[nextjournal.clerk :as clerk]
   '[babashka.fs :as fs]))

;; `pipeline-blocking` is from `clojure.core.async`.
;; And we're going to make our own way to time our code.

;; ## A slow operation and a timer
;;
;; We're going to explore our parallelism interactively.
;; For this we'll need two helpers, a slow operation, and a way to time operations.
;; First, a slow operation.
;;
;; Also, `clojure.core/time` outputs to standard out, so we'll make our own function for timing.

{:nextjournal.clerk/visibility {:result :hide}}

(defn slow [extra-latency-ms op]
  (fn [& args]
    (Thread/sleep extra-latency-ms)
    (apply op args)))

(def slow+ (slow 100 +))

{:nextjournal.clerk/visibility {:result :show}}

(slow+ 1 2)

;; It returns 3!
;; But you, the reader have no reason to know wether it was actually slow or not.

{:nextjournal.clerk/visibility {:result :hide}}

(defn time* [f]
  (let [before (System/currentTimeMillis)
        value (f)
        after (System/currentTimeMillis)
        duration-ms (- after before)]
    {:value value :duration-ms duration-ms}))

(defmacro time
  "An alternative to `clojure.core/time` suitable for notebooks"
  [& body]
  `(time* (fn [] ~@body)))

{:nextjournal.clerk/visibility {:result :show}}

(time (slow+ 1 2))

;; There you go.
;; Now, you, the reader, might assume that what we wrote after `:duration-ms` was how long time it actually took.
;; If you decide to put your faith in us today.

;; ## Single threaded with `clojure.core/map`

(time (map (partial slow+ 10) [100 200 300]))

;; Wait, 0 ms?
;; That was not what we exected.
;; If one op takes about 100 ms, shouldn't three ops take about 300 ms?

;; `(doc map)` gives a clue:
;;
;;     clojure.core/map
;;     ([f] [f coll] [f c1 c2] [f c1 c2 c3] [f c1 c2 c3 & colls])
;;       Returns a lazy sequence consisting of the result of applying f to
;;     […]
;;
;; Lazyness!
;; Right.
;; "Lazyness and impurity is a bad match", timing is impure.
;; We returned a lazy sequence that did no work, then timed no work, then the rendering of the value triggered the realization of the lazy sequence.
;; We could force the lazyness out with a doall in the expression:

(time (doall (map (partial slow+ 10) [100 200 300])))

;; Or avoid the issue with `clojure.core/mapv`, which is eager.

(time (mapv (partial slow+ 10) [100 200 300]))

;; ## Runtime-controlled parallelism with `clojure.core/pmap`

(time (pmap (partial slow+ 10) [100 200 300]))

;; Lazyness gives us yet another smack!

;; `pmapv` isn't provided, so let's create it.

^{:nextjournal.clerk/visibility {:result :hide}}
(def pmapv (comp vec pmap))

(time (pmapv (partial slow+ 10) [100 200 300]))

;; There you go!
;; `pmap` sometimes makes your code go faster.
;; Sometimes that's just great, and exactly what you need.

;; To figure out how much parallelism Clojure decides to use, we _can_ read the manual—but we can also fiddle with the REPL.
;; We're going to work with a function that takes about 10 ms instead of about 100 ms to save some time.

(def timing-1
  (time (mapv (slow 10 inc)
              [10 20 30])))

;; Great!
;; Now with parallelism:

(def timing-2
  (time (pmapv (slow 10 inc)
               [10 20 30])))

;; Speedup is _almost 3_ on _certain systems_:

(double
 (/ (:duration-ms timing-1)
    (:duration-ms timing-2)))

;; Let's plot n, speedup(n) and speedup(n)/n for some values of n.

(defn calculate-pmapv-speedup [op args]
  (double
   (/ (:duration-ms (time (mapv op args)))
      (:duration-ms (time (pmapv op args))))))

;; For our own sanity, can we reproduce something like the previous speedup?

(calculate-pmapv-speedup (slow 10 inc) [10 20 30])

;; Now, let's plot n and speedup for a few different values.

(clerk/caption
 "Speedup for certain values of n"
 (clerk/table (for [n [3 5 10 20 50]]
                (let [speedup (calculate-pmapv-speedup (slow 10 inc) (repeatedly n rand))]
                  {"n" n
                   "speedup" (format "%.2f" speedup)
                   "speedup/n" (format "%.2f" (/ speedup n))}))))

;; On Teodor's personal computer, speedup/n drops from about 1 for n=5 to about 0.5 for n=20.
;; Speedup dropoff can be caused by us reaching `pmap`'s parallellisation limits, or because coordination overhead increases relative to compute time.

;; For a different perspective, let's look at slower operations (40 ms per op) for lower values of n.

(clerk/caption
 "Speedup for certain values of n"
 (clerk/table (for [n [2 3 4 5 6 7 8 12 15 18]]
                (let [speedup (calculate-pmapv-speedup (slow 40 inc) (repeatedly n rand))]
                  {"n" n
                   "speedup" (format "%.2f" speedup)
                   "speedup/n" (format "%.2f" (/ speedup n))}))))

;; [A Stackoverflow answer](https://stackoverflow.com/questions/5021788/how-many-threads-does-clojures-pmap-function-spawn-for-url-fetching-operations)
;; indicates that core.async parallelism is derived from the number of processors on the system:

(+ 2 (.. Runtime getRuntime availableProcessors))

;; ## User-controlled parallellism with `clojure.core.async/pipeline-blocking`

;; In Clojure, we can utilize the core.async library for concurrency and
;; parallelism. Let's explore what that looks like by using core.async to
;; implement the same behavior as the code above.

;; An important concept in core.async is channels. You can think of a channel
;; as a thread-safe FIFO queue. In our current case, we will require an input
;; channel and an output channel, and a function that takes values from the input
;; channel, adds to the values via the `slow+` function, and then puts the
;; resulting values on the output channel. I call these types of functions 'worker'
;; functions since they run concurrently, move values between channels, and usually
;; perform some 'work' on the values.

;; It may help to think about core.async code as pipelines with one or more
;; steps. Channels can be thought of as the lines that data flows through, while
;; worker functions are the components that do some work on the values between
;; channels. In our case, we have a simple pipeline with one step:

^{:nextjournal.clerk/visibility {:result :hide :code :hide}}
(def mermaid-viewer
  {:transform-fn clerk/mark-presented
   :render-fn '(fn [value]
                 (when value
                   [nextjournal.clerk.render/with-d3-require {:package ["mermaid@8.14/dist/mermaid.js"]}
                    (fn [mermaid]
                      [:div {:ref (fn [el] (when el
                                             (.render mermaid (str (gensym)) value #(set! (.-innerHTML el) %))))}])]))})

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  ;; @ruben: ditto for denne: velg hva du liker best av ascii og Mermaid.
  ;;
  ;; Jeg prøvde å få til akkurat samme graf som du har tegnet over, men klarte ikke å tegne med mermaid uten start- og sluttnode.
  )

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/caption
 "A normal core.async workflow: a worker taking items from an input channel, transforming the item, and putting the transformed item on an output channel."
 (clerk/with-viewer mermaid-viewer
   "flowchart LR
    source-- channel in -->worker-- channel out -->sink"))

;; Let's write some code using the core.async library. First, we will define our
;; worker function. It will immediately start a go block, continually take values
;; from the `cin` channel until the channel is closed, add to each of the values
;; using the `slow+` function, and finally put the resulting values on the `cout`
;; channel.
(defn worker [cin cout]
  (a/go-loop []
    (when-some [val (a/<! cin)]
      (a/>! cout (slow+ 10 val))
      (recur))))

;; Now we need to run the worker function with the defined channels and collect
;; the result.
(let [cin (a/to-chan! (range 10))
      cout (a/chan)]
  (time
   ;; Wait for processing to finish then close the out channel. This works
   ;; because go-blocks return a channel that receive a single value when the
   ;; go-block is done
   (a/go (a/<! (worker cin cout))
         (a/close! cout))
   (a/<!! (a/into [] cout))))

;; This works, but it runs on a single thread, we don't have any parallelism yet. Let's
;; spawn some more worker functions to fix that:
(let [cin (a/to-chan! (range 10))
      cout (a/chan)]
  (time (let [worker-chns (doall (repeatedly 3 #(worker cin cout)))]
          ;; Wait for all channels to complete then close the out channel
          (a/go (doseq [c worker-chns]
                  (a/<! c))
                (a/close! cout))
          (a/<!! (a/into [] cout)))))

;; There we go! Much better.

;; Creating and managing these worker functions, as well as correctly handling
;; channels and knowing when to close them, can be a bit tricky. Luckily,
;; core.async provides an excellent helper function in the form of
;; `pipeline-blocking`.

;; `pipeline-blocking` works with an input and an output channel, a number
;; representing the number of concurrent workers, and a transducer to apply to
;; the values flowing between the channels. Transducers are a feature in Clojure
;; that has great interoperability with core.async. We can connect transducers to
;; channels when we create a channel via the `(async/chan buf xf)` function, or we can
;; use other functions such as `pipeline-blocking` to get more control over
;; parallel execution.

;; Let's rewrite the above code using `pipeline-blocking`. The first thing we
;; need is a transducer. One way to define a transducer is by using the `map`
;; function without the collection argument:
(def xf (map (partial slow+ 10)))

;; Then we create some channels and run `pipeline-blocking`:
(let [cin (a/to-chan! (range 10))
      cout (a/chan)]
  (time (a/pipeline-blocking 3 cout xf cin)
        (a/<!! (a/into [] cout))))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  ;; @ruben: sånn som dette går det an å skrive mermaid.js-diagrammer i Clerk.
  ;;
  ;; Koden er basert på Clerk-boka
  ;;
  ;;    https://book.clerk.vision/#loading-libraries
  ;;
  ;; og Mermaid.js-dokumentasjonen
  ;;
  ;;    https://mermaid.js.org/syntax/flowchart.html
  ;;
  ;; Hva tenker du? Skal vi ta med noen sånne?
  )

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/caption
 "pipeline-blocking with 3 worker threads"
 (clerk/with-viewer mermaid-viewer
   "flowchart LR
    source[\"(range 10)\"]-- cin -->worker[\"(pipeline-blocking 3 cout xf cin)\"]-- cout -->sink[\"[10, 11, 12, …]\"]"))

;; Let's see if we can push it a bit...
(let [cin (a/to-chan! (range 500))
      cout (a/chan)]
  (time (a/pipeline-blocking 50 cout xf cin)
        (a/<!! (a/into [] cout))))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/caption
 "Pipeline-blocking with 50 worker threads"
 (clerk/with-viewer mermaid-viewer
   "flowchart LR
    source[\"(range 500)\"]-- cin -->worker[\"(pipeline-blocking 50 cout xf cin)\"]-- cout -->sink[\"[10, 11, 12, …]\"]"))

;; ...and that's it!
;; In my view, `pipeline-blocking` offers a lot of benefits. We don't have to manage worker functions, and
;; we can utilize transducers, which can be reused in many different contexts without needing knowledge
;; of the underlying data structure.

;; Sometimes, we do need the extra control that custom worker functions provide, but I suspect that
;; `pipeline-blocking` is sufficient in many scenarios.

;; You might have noticed that there are a couple of other pipeline-related
;; functions, namely `pipeline-async` and `pipeline`. The difference between
;; these is that `pipeline-blocking` and `pipeline` use `async/thread` under
;; the hood, while `pipeline-async` uses `async/go`. You can see that in action
;; [here](https://github.com/clojure/core.async/blob/aa6b951301fbdcf5a13cdaaecb4b1a908dc8a978/src/main/clojure/clojure/core/async.clj#L548).

;; The names of the functions suggest their proper usage: if you have blocking
;; operations such as blocking HTTP requests or, in our case, the `slow+`
;; function, then use `pipeline-blocking`. If you have operations that run
;; asynchronously, then you may use `pipeline-async` to take advantage of the
;; core.async go block scheduler and thread pool. An example of an asynchronous
;; operation could be a function doing an HTTP request that returns immediately,
;; but accepts a callback where you can put the result value on a channel in the
;; callback. If you are doing compute-intensive operations, use `pipeline`.

;; The reason for differentiating between these functions is because of a common
;; pitfall when using go blocks: blocking operations will starve the thread pool
;; (which has a default of 8 threads). Unlike in Golang where goroutines are
;; preempted on I/O calls and other events, this is not the case in Clojure. Go
;; blocks in Clojure are only preempted when we perform a take or put on a
;; channel. This means that a go block performing a blocking HTTP request will
;; block that thread until the operation is done. If enough of these are
;; running at the same time, the core.async thread pool for go blocks will run
;; out of threads.

;; The ideal solution to such a situation is to make the HTTP request
;; asynchronously so that we can still use go blocks without starving the thread
;; pool, but a much simpler solution is to spin up dedicated threads with
;; `async/thread`. `async/go` and `async/thread` blocks are pretty much
;; interchangeable with the exception of some core.async functions which have
;; their own versions, such as `async/<!!` for `async/thread` blocks and
;; `async/<!` for `async/go` blocks. Unless a very large number of threads are
;; needed, `async/thread` and `pipeline-blocking` work perfectly fine. If you
;; need several hundreds of threads, then it might be a good idea to consider
;; `async/go` and `pipeline-async`.

;; Here is a good blog post that explores the topic a bit more:
;; https://eli.thegreenplace.net/2017/clojure-concurrency-and-blocking-with-coreasync/

;; ## Colophon and thanks
;;
;; [clojure/core.async](https://github.com/clojure/core.async) is made by the Clojure core team, with contributors.
;; This document was written with [Clerk](https://clerk.vision/), by [Nextjournal](https://nextjournal.com/).
;; The code that inspired this document was run on [Babashka](https://babashka.org/), by [Michiel Borkent](https://www.michielborkent.nl/).
;; The programmers writing this documents were inspired by the Norwegian Clojure community ([#clojure-norway on Clojurians](https://clojurians.slack.com/archives/C061XGG1W), [Clojure/Oslo Meetup](https://www.meetup.com/clojure-oslo/)).
;;
;; Special thanks to Terje, who nudged the authors towards this way of thinking [five months prior to us writing this documents](https://clojurians.slack.com/archives/C061XGG1W/p1707646189467349?thread_ts=1707477511.195369&cid=C061XGG1W).
;;
;; This document was written by
;; [Ruben S. Sevaldson](https://github.com/rubensseva) and [Teodor Heggelund](https://play.teod.eu/).
;; Thank you for reading!

^{:nextjournal.clerk/visibility {:result :hide :code :hide}}
(comment
  (clerk/build! {:paths [(fs/file-name *file*)] :out-path "."})
  (clerk/clear-cache!)
  :rcf)
