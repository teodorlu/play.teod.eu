// Some code to define a <random-page> web component

 async function get_index() {
   const index_url = "/index/big.json";
   return await fetch(index_url).then((response) => response.json());
 }

 function random_item(items) {
   return items[Math.floor(Math.random()*items.length)];
 }

class RandomPageButton extends HTMLButtonElement {
  constructor() {
    super();

    // Element functionality written in here
    this.addEventListener("click", e => {
      // What kind of random content do we want?
      //
      //  - remote references are not that interesting
      //  - old stuff with high potential is nice
      //  - random old stuff is also nice
      //  - perhaps I want some way to deprecate stuff? (i could also just delete it)

      // console.log("Click!")

      get_index().then((data) => {
        const is_reasonable_target = (item) => {
          return (
            item.form !== undefined
              && item.form !== "remote-reference"
          )
        }

        const reasonable_targets = data.filter(is_reasonable_target);

        // console.log(reasonable_targets)

        const item = random_item(reasonable_targets)
        // console.log(item)
        window.location.href = "/" + item.slug + "/"
      })
    })
  }
}

const buttonOptions = { extends: 'button' }

customElements.define("random-page-button", RandomPageButton, buttonOptions)
