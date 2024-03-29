class IkiHello extends HTMLButtonElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.onclick = () => {
      console.log("Iki hello!")
    }
  }
}

customElements.define("iki-hello", IkiHello, {extends: "button"})
