import './knus-duration-picker.js';
import './knus-archive-entry.js';
import './knus-timer.js';

const STORAGE_KEY = 'knus-skrivesperren-arkiv';

export class DangerouslyWrite extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.timer = null;
        this.timeout = 6000; // 1s grace period + 5s blur
        this.successThreshold = 30000; // 30 seconds to win
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
    }

    getArchiveEntries() {
        try {
            const data = localStorage.getItem(STORAGE_KEY);
            return data ? JSON.parse(data) : [];
        } catch {
            return [];
        }
    }

    saveToArchive(text, durationMs) {
        const entries = this.getArchiveEntries();
        entries.push({
            text,
            timestamp: new Date().toISOString(),
            durationMs
        });
        localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
    }

    hasArchiveEntries() {
        return this.getArchiveEntries().length > 0;
    }

    connectedCallback() {
        this.render();
        this.containerElement = this.shadowRoot.querySelector('.container');
        this.inputElement = this.shadowRoot.querySelector('textarea');
        this.selectedDurationSeconds = 300; // Default: 5 minutes (300 seconds)
        this.startButton = this.shadowRoot.querySelector('#start-button');
        this.durationPicker = this.shadowRoot.querySelector('#duration-picker');

        // Sync with duration picker value
        this.durationPicker.addEventListener('change', (e) => {
            this.selectedDurationSeconds = e.detail.seconds;
        });
        // Initialize from picker's current value
        if (this.durationPicker.value) {
            this.selectedDurationSeconds = this.durationPicker.value;
        }

        this.inputElement.addEventListener('input', this.handleInput.bind(this));

        this.startButton.addEventListener('click', this.startWriting.bind(this));

        // Attach reset handler to reset buttons
        this.shadowRoot.querySelectorAll('.reset-button').forEach(btn => {
            btn.addEventListener('click', this.reset.bind(this));
        });

        // Get timer component
        this.elapsedTimer = this.shadowRoot.querySelector('#elapsed-timer');

        // Archive buttons
        this.startArchiveButton = this.shadowRoot.querySelector('#start-archive-button');
        this.successArchiveButton = this.shadowRoot.querySelector('#success-archive-button');
        this.backButton = this.shadowRoot.querySelector('#back-button');
        this.archiveEntriesContainer = this.shadowRoot.querySelector('#archive-entries');

        this.startArchiveButton.addEventListener('click', this.showArchive.bind(this));
        this.successArchiveButton.addEventListener('click', this.showArchive.bind(this));
        this.backButton.addEventListener('click', this.hideArchive.bind(this));

        // Copy button
        this.copyButton = this.shadowRoot.querySelector('#copy-button');
        this.copyButton.addEventListener('click', this.copyText.bind(this));

        // Update archive button visibility on start screen
        this.updateStartArchiveButtonVisibility();

        // Hash-based navigation for browser back/forward
        this.handleHashChange = this.handleHashChange.bind(this);
        window.addEventListener('hashchange', this.handleHashChange);
        this.handleHashChange(); // Initialize from current hash
    }

    startWriting() {
        // Update hash for browser navigation
        window.location.hash = 'skriv';
    }

    startWritingView() {
        // Use selected duration (in seconds), convert to milliseconds
        this.successThreshold = this.selectedDurationSeconds * 1000;

        // Reset state
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
        this.inputElement.value = '';
        this.inputElement.classList.remove('blurred');

        // Reset timer (starts on first keystroke)
        this.elapsedTimer.reset();

        // Show writing screen
        this.containerElement.classList.remove('start-screen', 'game-over', 'success');
        this.containerElement.classList.add('writing');
        this.inputElement.focus();
    }



    disconnectedCallback() {
        if (this.inputElement) {
            this.inputElement.removeEventListener('input', this.handleInput.bind(this));
        }
        window.removeEventListener('hashchange', this.handleHashChange);
    }

    reset() {
        // Clear value and state
        this.inputElement.value = '';
        this.inputElement.classList.remove('blurred');
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
        this.elapsedTimer.reset();

        // Navigate back to start (this will clear timers via returnToStart)
        window.location.hash = '';
    }

    updateStartArchiveButtonVisibility() {
        if (this.hasArchiveEntries()) {
            this.startArchiveButton.classList.remove('hidden');
        } else {
            this.startArchiveButton.classList.add('hidden');
        }
    }

    handleHashChange() {
        const hash = window.location.hash;
        if (hash === '#arkiv') {
            this.showArchiveView();
        } else if (hash === '#skriv') {
            this.startWritingView();
        } else if (hash === '#knust') {
            this.showSuccessView();
        } else if (hash === '#vedvarer') {
            this.showFailureView();
        } else {
            // No hash or unknown hash - return to start screen
            this.returnToStart();
        }
    }

    returnToStart() {
        // Stop any active timers
        clearTimeout(this.timer);
        this.elapsedTimer.stop();

        // Reset to start screen
        this.containerElement.classList.remove('archive', 'writing', 'game-over', 'success');
        this.containerElement.classList.add('start-screen');
        this.updateStartArchiveButtonVisibility();
    }

    showArchive() {
        // Update hash, which triggers hashchange -> showArchiveView
        window.location.hash = 'arkiv';
    }

    showArchiveView() {
        this.renderArchiveEntries();
        this.containerElement.classList.remove('start-screen', 'writing', 'game-over', 'success');
        this.containerElement.classList.add('archive');
    }

    showSuccessView() {
        this.containerElement.classList.remove('start-screen', 'writing', 'game-over', 'archive');
        this.containerElement.classList.add('success');
    }

    showFailureView() {
        this.containerElement.classList.remove('start-screen', 'writing', 'success', 'archive');
        this.containerElement.classList.add('game-over');
    }

    hideArchive() {
        // Clear hash, which triggers hashchange -> returnToStart
        window.location.hash = '';
    }

    copyText() {
        const text = this.inputElement.value;
        navigator.clipboard.writeText(text);
    }

    renderArchiveEntries() {
        const entries = this.getArchiveEntries();

        if (entries.length === 0) {
            this.archiveEntriesContainer.innerHTML = '<p class="empty-archive">ingen lagrede tekster ennå</p>';
            return;
        }

        // Show newest first
        const reversedEntries = [...entries].reverse();

        this.archiveEntriesContainer.innerHTML = reversedEntries.map(entry => `
            <knus-archive-entry
                timestamp="${entry.timestamp}"
                duration-ms="${entry.durationMs}"
                text="${this.escapeHtml(entry.text).replace(/"/g, '&quot;')}"
            ></knus-archive-entry>
        `).join('');
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    handleInput() {
        // If already succeeded, no more danger
        if (this.hasSucceeded) {
            return;
        }

        clearTimeout(this.timer);

        // Track writing time
        if (this.writingStartTime === null) {
            this.writingStartTime = Date.now();
            // Start timer on first keystroke
            this.elapsedTimer.start();
        }

        // Calculate total writing time
        const currentSessionTime = Date.now() - this.writingStartTime;
        const totalWritingTime = this.accumulatedWritingTime + currentSessionTime;

        // Check for success (30 seconds of writing)
        if (totalWritingTime >= this.successThreshold) {
            this.hasSucceeded = true;
            this.saveToArchive(this.inputElement.value, this.successThreshold);
            this.inputElement.classList.remove('blurred');
            this.inputElement.style.transition = 'none';
            void this.inputElement.offsetWidth;
            this.elapsedTimer.stop();
            window.location.hash = 'knust';
            return;
        }

        // Disable transition, reset filter to none, force reflow
        this.inputElement.style.transition = 'none';
        this.inputElement.classList.remove('blurred');
        void this.inputElement.offsetWidth;

        // Re-enable transition
        this.inputElement.style.transition = '';

        if (this.inputElement.value.length > 0) {
            // Force another reflow then start blur animation
            void this.inputElement.offsetWidth;
            this.inputElement.classList.add('blurred');

            this.timer = setTimeout(() => {
                // Accumulate the time spent before timeout
                if (this.writingStartTime !== null) {
                    this.accumulatedWritingTime += Date.now() - this.writingStartTime;
                    this.writingStartTime = null;
                }
                this.elapsedTimer.stop();
                window.location.hash = 'vedvarer';
            }, this.timeout);
        }
    }

    render() {
        this.shadowRoot.innerHTML = `
            <style>
                :host {
                    display: block;
                    width: 100%;
                    flex-grow: 1; /* Take remaining space */
                    height: auto; /* Allow height to be determined by flex container */
                }
                .container {
                    width: 100%;
                    max-width: 65ch;
                    margin: 0 auto;
                    height: 100%;
                    position: relative;
                    display: flex;
                    flex-direction: column;
                }
                textarea {
                    width: 100%;
                    flex-grow: 1;
                    min-height: 100px;
                    border: none;
                    outline: none;
                    background: transparent;
                    color: var(--text-input);
                    padding: 1rem 0;
                    font-size: 1.1rem;
                    line-height: 1.4;
                    resize: none;
                    box-sizing: border-box;
                    border-radius: 0;
                    transition: filter 5s ease-in 1s;
                    filter: blur(0);
                    font-family: Georgia, 'Times New Roman', serif;
                    display: block;
                }
                textarea.blurred {
                    filter: blur(10px);
                }
                .failure-message {
                    display: none;
                    flex-direction: column;
                    align-items: center;
                    gap: 1.5rem;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    text-align: center;
                    width: 100%;
                }
                .container.game-over textarea,
                .container.game-over .elapsed-time {
                    display: none;
                }
                .container.game-over .failure-message {
                    display: flex;
                }
                .success-message {
                    display: none;
                    flex-direction: column;
                    align-items: center;
                    gap: 1.5rem;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    text-align: center;
                    width: 100%;
                }
                .container.success .success-message {
                    display: flex;
                }
                .container.success textarea {
                    background: transparent;
                    filter: blur(0);
                }
                .success-actions {
                    display: flex;
                    gap: 1rem;
                }
                .copy-button {
                    background: transparent;
                    border: 2px solid var(--btn-secondary);
                    color: var(--btn-secondary);
                    display: flex;
                    align-items: center;
                    gap: 0.5rem;
                }
                .copy-button:hover {
                    background: var(--btn-secondary);
                    color: var(--text-on-dark);
                }
                .copy-button img {
                    width: 1rem;
                    height: 1rem;
                }
                h2 {
                    margin: 0;
                    color: var(--text-primary);
                }
                button {
                    padding: 0.5rem 1rem;
                    font-size: 1rem;
                    cursor: pointer;
                    background: var(--btn-primary);
                    color: var(--text-on-dark);
                    border: 2px solid var(--btn-primary);
                    border-radius: 4px;
                }
                button:hover {
                    background: var(--btn-primary-hover);
                    border-color: var(--btn-primary-hover);
                }
                .start-screen-content {
                    display: none;
                    flex-direction: column;
                    align-items: center;
                    justify-content: flex-start;
                    padding-top: 4rem;
                    gap: 1.5rem;
                    flex-grow: 1;
                }
                .container.start-screen .start-screen-content {
                    display: flex;
                }
                .container.start-screen textarea,
                .container.start-screen .failure-message,
                .container.start-screen .success-message {
                    display: none;
                }
                .container.writing textarea {
                    display: block;
                }
                .container.writing .start-screen-content {
                    display: none;
                }
                .duration-button {
                    background: transparent;
                    border: 2px solid var(--btn-accent);
                    color: var(--btn-accent);
                    padding: 0.5rem 1rem;
                    font-size: 1rem;
                }
                .duration-button:hover {
                    background: var(--btn-accent);
                    color: var(--text-on-dark);
                }
                .duration-button.selected {
                    background: var(--btn-accent-selected);
                    border-color: var(--btn-accent-selected);
                    color: var(--text-on-dark);
                }
                #start-button {
                    background: var(--btn-primary);
                    padding: 1rem 3rem;
                    font-size: 1.4rem;
                }
                #start-button:hover {
                    background: var(--btn-primary-hover);
                }
                .elapsed-time {
                    text-align: center;
                    font-size: 1.5rem;
                    font-family: monospace;
                    color: var(--text-timer);
                    padding: 0.5rem;
                    flex-shrink: 0;
                }
                .container.start-screen .elapsed-time {
                    display: none;
                }
                .archive-button {
                    background: transparent;
                    border: 2px solid var(--btn-secondary);
                    color: var(--btn-secondary);
                }
                .archive-button:hover {
                    background: var(--btn-secondary);
                    color: var(--text-on-dark);
                }
                .archive-button.hidden {
                    display: none;
                }
                .archive-content {
                    display: none;
                    flex-direction: column;
                    gap: 1.5rem;
                    padding: 1rem 0;
                    overflow-y: auto;
                    flex-grow: 1;
                }
                .container.archive .archive-content {
                    display: flex;
                }
                .container.archive textarea,
                .container.archive .failure-message,
                .container.archive .success-message,
                .container.archive .start-screen-content,
                .container.archive .elapsed-time {
                    display: none;
                }
                .archive-entries {
                    display: flex;
                    flex-direction: column;
                    gap: 0.6rem;
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
                .archive-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
                .archive-header h2 {
                    margin: 0;
                }
                .back-button {
                    background: transparent;
                    border: 2px solid var(--btn-secondary);
                    color: var(--btn-secondary);
                }
                .back-button:hover {
                    background: var(--btn-secondary);
                    color: var(--text-on-dark);
                }
                .empty-archive {
                    text-align: center;
                    color: var(--text-muted);
                    padding: 2rem;
                }
            </style>
            <div class="container start-screen">
                <div class="start-screen-content">
                    <h2>knus skrivesperren</h2>
                    <knus-duration-picker id="duration-picker"></knus-duration-picker>
                    <button id="start-button" tabindex="4">start</button>
                    <button class="archive-button" id="start-archive-button">vis arkiv</button>
                </div>
                <textarea placeholder="skriv her..."></textarea>
                <knus-timer id="elapsed-timer" class="elapsed-time"></knus-timer>
                <div class="failure-message">
                    <h2>skrivesperren vedvarer</h2>
                    <button class="reset-button">skriv på nytt</button>
                </div>
                <div class="success-message">
                    <h2>skrivesperre knust.</h2>
                    <button class="reset-button">start på nytt</button>
                    <div class="success-actions">
                        <button class="copy-button" id="copy-button">
                            <img src="./icons/copy.svg" alt="">
                            kopier
                        </button>
                        <button class="archive-button" id="success-archive-button">vis arkiv</button>
                    </div>
                </div>
                <div class="archive-content">
                    <div class="archive-header">
                        <h2>arkiv</h2>
                        <button class="back-button" id="back-button">tilbake</button>
                    </div>
                    <div id="archive-entries" class="archive-entries"></div>
                </div>
            </div>
        `;
    }
}

customElements.define('dangerously-write', DangerouslyWrite);
