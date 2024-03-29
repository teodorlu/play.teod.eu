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

export class IkiGotoRandomPage extends HTMLButtonElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.onclick = () => {
      console.log("iki goto random page!")
    }
  }
}
