// STUFF WITHOUYT SIDE EFFECTS (haskell-pure)

const create_slides = (slideshow) => {
  const nodes = slideshow.children;
  const slides = [];
  for (var i = 0; i < nodes.length; i++) {
    if (!nodes[i]) {
      continue;
    }

    let lastIndex = slides.length - 1;
    if (nodes[i].tagName === "H1") {
      let slide = { title: nodes[i].innerText, body: "" };
      slides.push(slide);
    } else if (nodes[i].tagName === "P" && slides[lastIndex]) {
      slides[lastIndex].body += nodes[i].innerText + "\n";
    }
  }
  return slides;
};

// STUFF WITH SOFT SIDE EFFECTS (clojure-pure)

const view = (slide) => {
  const container = document.createElement("div");
  const slideHead = document.createElement("h2");
  slideHead.innerText = slide.title;
  container.appendChild(slideHead);

  const slideBody = document.createElement("p");
  slideBody.innerText = slide.body;
  container.appendChild(slideBody);

  return container;
};

const render = () => {
  // console.log("before:", state.slides);
  slideshow.innerHTML = "";
  // console.log("after:", state.slides);
  slideshow.appendChild(view(state.slides[state.current]));
};

const slideIncrement = (delta) => {
  if (state.slides[state.current + delta]) {
    state.current = state.current + delta;
    render();
  }
};


// SIDE EFFECTS! BE CAREFUL! impure chaos beware ❤️‍🔥

let slideshow = document.getElementById("slideshow");

const state = { current: 0, slides: [{ title: "", body: "" }] };

document.addEventListener("keydown", (event) => {
  if (
    event.key === "j" ||
    event.key === "ArrowRight" ||
    event.key === "ArrowDown"
  ) {
    slideIncrement(1);
  } else if (
    event.key === "k" ||
    event.key === "ArrowLeft" ||
    event.key === "ArrowUp"
  ) {
    slideIncrement(-1);
  }
});

// For debugging - catch key codes.
// document.addEventListener("keydown", (event) => console.log(event))

document.addEventListener("keydown", (event) => {
  if (event.key === "p") {
    state.slides = create_slides(slideshow);
    render();
  }
});
