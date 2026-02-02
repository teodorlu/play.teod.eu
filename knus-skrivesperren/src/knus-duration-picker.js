/**
 * knus-duration-picker
 * A web component for selecting writing duration with edit/reveal pattern.
 */

class KnusDurationPicker extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this._value = 300; // Default: 5 minutes
    }

    get value() {
        return this._value;
    }

    connectedCallback() {
        this.render();
        this.durationDisplay = this.shadowRoot.querySelector('.duration-display');
        this.durationOptions = this.shadowRoot.querySelector('.duration-options');
        this.durationText = this.shadowRoot.querySelector('.duration-text');
        this.editButton = this.shadowRoot.querySelector('.edit-button');

        // Load saved duration
        this.loadSavedDuration();

        // Edit button shows options
        this.editButton.addEventListener('click', () => {
            this.durationDisplay.classList.add('hidden');
            this.durationOptions.classList.remove('hidden');
        });

        // Duration button clicks
        this.shadowRoot.querySelectorAll('.duration-button').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const seconds = parseInt(e.target.dataset.seconds, 10);
                const label = e.target.textContent;
                this.selectDuration(seconds, label);
            });
        });
    }

    loadSavedDuration() {
        const saved = localStorage.getItem('knus-skrivesperren-duration');
        if (saved) {
            const { seconds, label } = JSON.parse(saved);
            this._value = seconds;
            this.durationText.textContent = `Skriv i ${label}`;
        } else {
            this._value = 300;
            this.durationText.textContent = 'Skriv i 5 min';
        }
    }

    selectDuration(seconds, label) {
        this._value = seconds;
        this.durationText.textContent = `Skriv i ${label}`;

        // Save to localStorage
        localStorage.setItem('knus-skrivesperren-duration', JSON.stringify({ seconds, label }));

        // Hide options, show display
        this.durationOptions.classList.add('hidden');
        this.durationDisplay.classList.remove('hidden');

        // Dispatch change event
        const event = new (window.CustomEvent || CustomEvent)('change', {
            detail: { seconds, label },
            bubbles: true
        });
        this.dispatchEvent(event);
    }

    render() {
        this.shadowRoot.innerHTML = `
            <style>
                :host {
                    display: block;
                }
                .duration-display {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 0.5rem;
                }
                .duration-display.hidden {
                    display: none;
                }
                .duration-text {
                    color: var(--text-primary);
                    font-size: 1.1rem;
                }
                .edit-button {
                    background: transparent;
                    border: none;
                    padding: 0.25rem;
                    cursor: pointer;
                    color: var(--btn-accent);
                    display: flex;
                    align-items: center;
                }
                .edit-button:hover {
                    color: var(--btn-accent-hover);
                }
                .edit-button img {
                    width: 1.2rem;
                    height: 1.2rem;
                }
                .duration-options {
                    display: flex;
                    gap: 0.5rem;
                    justify-content: center;
                }
                .duration-options.hidden {
                    display: none;
                }
                .duration-button {
                    background: transparent;
                    border: 2px solid var(--btn-accent);
                    color: var(--btn-accent);
                    padding: 0.5rem 1rem;
                    font-size: 1rem;
                    border-radius: 4px;
                    cursor: pointer;
                }
                .duration-button:hover {
                    background: var(--btn-accent);
                    color: var(--text-on-dark);
                }
            </style>
            <div class="duration-display">
                <span class="duration-text">Skriv i 5 min</span>
                <button class="edit-button" aria-label="Endre varighet">
                    <img src="./icons/pencil-simple.svg" alt="Rediger">
                </button>
            </div>
            <div class="duration-options hidden">
                <button class="duration-button" data-seconds="5">5 sek</button>
                <button class="duration-button" data-seconds="60">1 min</button>
                <button class="duration-button" data-seconds="300">5 min</button>
            </div>
        `;
    }
}

customElements.define('knus-duration-picker', KnusDurationPicker);
