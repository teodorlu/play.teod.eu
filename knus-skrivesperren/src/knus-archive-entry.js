/**
 * knus-archive-entry
 * A web component for displaying a single archive entry with copy functionality.
 */

class KnusArchiveEntry extends HTMLElement {
    static get observedAttributes() {
        return ['timestamp', 'duration-ms', 'text'];
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    connectedCallback() {
        this.render();
        this.shadowRoot.querySelector('.copy-button').addEventListener('click', () => {
            const text = this.getAttribute('text') || '';
            // Dispatch copy event for parent to handle (or copy directly)
            const event = new (window.CustomEvent || CustomEvent)('copy', {
                detail: { text },
                bubbles: true
            });
            this.dispatchEvent(event);
            // Also copy to clipboard
            navigator.clipboard?.writeText(text);
        });
    }

    attributeChangedCallback() {
        if (this.shadowRoot.innerHTML) {
            this.render();
        }
    }

    formatDate(isoString) {
        if (!isoString) return '';
        const date = new Date(isoString);
        return date.toLocaleString('nb-NO', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    formatDuration(ms) {
        const totalMs = parseInt(ms, 10) || 0;
        const minutes = Math.floor(totalMs / 60000);
        const seconds = Math.floor((totalMs % 60000) / 1000);
        if (minutes > 0 && seconds > 0) {
            return `${minutes} min ${seconds} sek`;
        } else if (minutes > 0) {
            return `${minutes} min`;
        } else {
            return `${seconds} sek`;
        }
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text || '';
        return div.innerHTML;
    }

    render() {
        const timestamp = this.getAttribute('timestamp') || '';
        const durationMs = this.getAttribute('duration-ms') || '0';
        const text = this.getAttribute('text') || '';

        this.shadowRoot.innerHTML = `
            <style>
                :host {
                    display: block;
                }
                .archive-entry {
                    background: var(--bg-input);
                    border-radius: 8px;
                    padding: 1rem;
                    display: flex;
                    flex-direction: column;
                    gap: 0.4rem;
                }
                .archive-entry-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    font-size: 0.85rem;
                    color: var(--text-muted);
                }
                .archive-entry-text {
                    white-space: pre-wrap;
                    font-family: sans-serif;
                    font-size: 0.95rem;
                    color: var(--text-primary);
                }
                .copy-button {
                    background: transparent;
                    border: 2px solid var(--btn-secondary);
                    color: var(--btn-secondary);
                    display: flex;
                    align-items: center;
                    gap: 0.5rem;
                    padding: 0.5rem 1rem;
                    font-size: 1rem;
                    cursor: pointer;
                    border-radius: 4px;
                }
                .copy-button:hover {
                    background: var(--btn-secondary);
                    color: var(--text-on-dark);
                }
                .copy-button img {
                    width: 1rem;
                    height: 1rem;
                }
            </style>
            <div class="archive-entry">
                <div class="archive-entry-header">
                    <span>${this.formatDate(timestamp)} • ${this.formatDuration(durationMs)}</span>
                    <button class="copy-button" aria-label="Kopier">
                        <img src="./icons/copy.svg" alt="">
                    </button>
                </div>
                <div class="archive-entry-text">${this.escapeHtml(text)}</div>
            </div>
        `;
    }
}

customElements.define('knus-archive-entry', KnusArchiveEntry);
