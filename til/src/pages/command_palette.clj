(ns pages.command-palette)

(def css
  "body { font-family: system-ui, sans-serif; margin: 2rem; }

    #search { font-size: 1rem; padding: 0.4rem; width: 20rem; }

    #menu {
      list-style: none;
      margin: 0;
      padding: 0;
      border: 1px solid #ccc;
      width: 20rem;
      max-height: 12rem;
      overflow-y: auto;
    }

    #menu:empty { border: none; }

    #menu li {
      padding: 0.4rem 0.6rem;
      cursor: default;
    }

    #menu li[aria-selected=\"true\"] {
      background: #0060df;
      color: #fff;
    }")

(def js
  "const items = [
    \"Apple\", \"Apricot\", \"Banana\", \"Blueberry\", \"Cherry\",
    \"Date\", \"Fig\", \"Grape\", \"Kiwi\", \"Lemon\",
    \"Mango\", \"Orange\", \"Papaya\", \"Peach\", \"Pear\",
    \"Pineapple\", \"Plum\", \"Raspberry\", \"Strawberry\", \"Watermelon\"
  ];

  const search = document.getElementById(\"search\");
  const menu = document.getElementById(\"menu\");
  let activeIndex = -1;

  function render(filtered) {
    menu.innerHTML = \"\";
    activeIndex = -1;
    filtered.forEach((item, i) => {
      const li = document.createElement(\"li\");
      li.textContent = item;
      li.role = \"option\";
      li.id = \"opt-\" + i;
      li.setAttribute(\"aria-selected\", \"false\");
      li.addEventListener(\"click\", () => pick(item));
      menu.appendChild(li);
    });
  }

  function highlight(index) {
    const opts = menu.children;
    if (activeIndex >= 0 && activeIndex < opts.length)
      opts[activeIndex].setAttribute(\"aria-selected\", \"false\");
    activeIndex = index;
    if (activeIndex >= 0 && activeIndex < opts.length) {
      opts[activeIndex].setAttribute(\"aria-selected\", \"true\");
      opts[activeIndex].scrollIntoView({ block: \"nearest\" });
      search.setAttribute(\"aria-activedescendant\", opts[activeIndex].id);
    } else {
      search.removeAttribute(\"aria-activedescendant\");
    }
  }

  function pick(value) {
    search.value = value;
    menu.innerHTML = \"\";
  }

  search.addEventListener(\"input\", () => {
    const q = search.value.toLowerCase();
    render(q ? items.filter(i => i.toLowerCase().includes(q)) : items);
  });

  search.addEventListener(\"keydown\", (e) => {
    const opts = menu.children;
    if (!opts.length) return;

    if (e.key === \"ArrowDown\") {
      e.preventDefault();
      highlight(activeIndex < opts.length - 1 ? activeIndex + 1 : 0);
    } else if (e.key === \"ArrowUp\") {
      e.preventDefault();
      highlight(activeIndex > 0 ? activeIndex - 1 : opts.length - 1);
    } else if (e.key === \"Enter\" && activeIndex >= 0) {
      e.preventDefault();
      pick(opts[activeIndex].textContent);
    } else if (e.key === \"Escape\") {
      menu.innerHTML = \"\";
    }
  });

  // Show all items on focus
  search.addEventListener(\"focus\", () => {
    if (!menu.children.length) render(items);
  });")

(defn render []
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "Filter menu"]
    [:style css]]
   [:body
    [:input#search {:type "text" :placeholder "Type to filter…"
                    :autocomplete "off" :role "combobox"
                    :aria-expanded "true" :aria-controls "menu"}]
    [:ul#menu {:role "listbox"}]
    [:script js]]])
