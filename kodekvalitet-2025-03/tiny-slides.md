Minimal presentation slides for ClojureScript. Made with [Scittle](https://github.com/babashka/scittle/).

[features](#features) | [quickstart](#quickstart) | [slide format](#slide-format) | [navigation](#navigation) | [dev](#dev) | [about](#about)

# features

- Write your slides in Reagent Hiccup.
- Static web app you can upload to any web host.
- Tiny hackable codebase.
- No setup or config, just clone the repo to start.

Here's a [YouTube video about ClojureScript Tiny Slides](https://www.youtube.com/watch?v=gBt_tBoy1kE).

![Screencast of ClojureScript Tiny Slides](https://mccormick.cx/gfx/blogref/joplin/a69bca99b7a1401fbf8ba6a4157fd9ec.gif)

# quickstart

- Clone this repository
- Edit [`slides.cljs`](./slides.cljs) and add your slides.
- Open [`index.html`](./index.html) in your browser.

To get a live-reloading dev experience you can [Start a `josh` server](#dev).

# slide format

Each slide is a `:section` tag like this:

```clojure
(defn slides [state]
  [:<>
   [:section
    [:h1 "Hello"]
    [:h2 "Your first slide."]]

   [:section
    [:h1 "Slide Two"]
    [:img {:src "https://w.wiki/CAvg"}]
    [:h3 "It's the moon."]]

    ; ...
```

# navigation

Slide navigation keys:

- Next: RightArrow, DownArrow, PageDown, Spacebar, Enter
- Prev: LeftArrow, UpArrow, PageUp
- First: Home, Escape, Q
- Last: End

Or tap/click the right/left side of the screen to go foward/backward.

# dev

Start a live-reloading dev server:

```
echo {} > package.json
npm i cljs-josh
npx josh
```

(Or just `josh` if you have done `npm i -g cljs-josh` to install it globally).

# about

Built at Barcamp London 2024 for [this talk](https://chr15m.github.io/barcamp-whats-the-point-of-lisp/).
