import * as indexBig from "/index/big.js"

export class IkiHello extends HTMLButtonElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.onclick = () => {
      console.log("Iki hello!")
    }
  }
}

const arrayRandNth = (array) => {
  // https://stackoverflow.com/questions/4550505/getting-a-random-value-from-a-javascript-array
  return array[Math.floor(Math.random() * array.length)];
}

export class IkiGotoRandomPage extends HTMLButtonElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.onclick = () => {
      const targetPage = arrayRandNth(
        indexBig.index.filter(page => !page.noindex)
      );
      const targetHref = `/${targetPage.slug}/`;
      window.location.href = targetHref;
    }
  }
}
