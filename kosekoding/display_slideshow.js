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

let slideshow = document.getElementById("slideshow");

const state = { current: 0, slides: [{ title: "", body: "" }] };

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

document.addEventListener("keydown", (event) => {
  if (
    event.key === "k" ||
    event.key === "ArrowRight" ||
    event.key === "ArrowDown"
  ) {
    slideIncrement(1);
  } else if (
    event.key === "j" ||
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
