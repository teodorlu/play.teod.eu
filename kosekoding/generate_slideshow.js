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
