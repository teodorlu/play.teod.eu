/**
 * knus-timer
 * A web component for displaying elapsed time with start/stop/reset controls.
 */

class KnusTimer extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this._startTime = null;
        this._elapsed = 0;
        this._interval = null;
    }

    get elapsed() {
        if (this._startTime) {
            return this._elapsed + (Date.now() - this._startTime);
        }
        return this._elapsed;
    }

    connectedCallback() {
        this.render();
        this._display = this.shadowRoot.querySelector('.timer-display');
    }

    disconnectedCallback() {
        this.stop();
    }

    start() {
        if (this._startTime) return; // Already running
        this._startTime = Date.now();
        this._interval = setInterval(() => this.updateDisplay(), 100);
    }

    stop() {
        if (!this._startTime) return;
        this._elapsed += Date.now() - this._startTime;
        this._startTime = null;
        clearInterval(this._interval);
        this._interval = null;
    }

    reset() {
        this.stop();
        this._elapsed = 0;
        this._startTime = null;
        if (this._display) {
            this._display.textContent = '00:00';
        }
    }

    updateDisplay() {
        const totalMs = this.elapsed;
        const minutes = Math.floor(totalMs / 60000);
        const seconds = Math.floor((totalMs % 60000) / 1000);
        this._display.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    }

    render() {
        this.shadowRoot.innerHTML = `
            <style>
                :host {
                    display: block;
                }
                .timer-display {
                    text-align: center;
                    font-size: 1.5rem;
                    font-family: monospace;
                    color: var(--text-timer);
                    padding: 0.5rem;
                }
            </style>
            <div class="timer-display">00:00</div>
        `;
    }
}

customElements.define('knus-timer', KnusTimer);
