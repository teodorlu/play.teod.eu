const STORAGE_KEY = 'knus-skrivesperren-arkiv';

export class DangerouslyWrite extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.timer = null;
        this.elapsedInterval = null;
        this.sessionStartTime = null;
        this.timeout = 5000;
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
        this.messageElement = this.shadowRoot.querySelector('.message');
        this.durationMinutesInput = this.shadowRoot.querySelector('#duration-minutes');
        this.durationSecondsInput = this.shadowRoot.querySelector('#duration-seconds');
        this.startButton = this.shadowRoot.querySelector('#start-button');

        this.inputElement.addEventListener('input', this.handleInput.bind(this));
        this.startButton.addEventListener('click', this.startWriting.bind(this));

        // Attach reset handler to reset buttons
        this.shadowRoot.querySelectorAll('.reset-button').forEach(btn => {
            btn.addEventListener('click', this.reset.bind(this));
        });

        // Get elapsed time display element
        this.elapsedDisplay = this.shadowRoot.querySelector('#elapsed-time');

        // Archive buttons
        this.startArchiveButton = this.shadowRoot.querySelector('#start-archive-button');
        this.successArchiveButton = this.shadowRoot.querySelector('#success-archive-button');
        this.backButton = this.shadowRoot.querySelector('#back-button');
        this.archiveEntriesContainer = this.shadowRoot.querySelector('#archive-entries');

        this.startArchiveButton.addEventListener('click', this.showArchive.bind(this));
        this.successArchiveButton.addEventListener('click', this.showArchive.bind(this));
        this.backButton.addEventListener('click', this.hideArchive.bind(this));

        // Update archive button visibility on start screen
        this.updateStartArchiveButtonVisibility();
    }

    startWriting() {
        // Get duration from inputs (minutes and seconds), convert to milliseconds
        const durationMinutes = parseInt(this.durationMinutesInput.value, 10) || 0;
        const durationSeconds = parseInt(this.durationSecondsInput.value, 10) || 0;
        this.successThreshold = (durationMinutes * 60 + durationSeconds) * 1000;

        // Ensure at least some time
        if (this.successThreshold < 1000) {
            this.successThreshold = 5 * 60 * 1000; // Default to 5 minutes
        }

        // Reset state
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
        this.inputElement.value = '';
        this.inputElement.classList.remove('blurred');

        // Reset elapsed time display (timer starts on first keystroke)
        this.sessionStartTime = null;
        this.elapsedDisplay.textContent = '00:00';
        clearInterval(this.elapsedInterval);

        // Show writing screen
        this.containerElement.classList.remove('start-screen', 'game-over', 'success');
        this.containerElement.classList.add('writing');
        this.inputElement.focus();
    }

    updateElapsedDisplay() {
        if (!this.sessionStartTime) return;
        const elapsed = Date.now() - this.sessionStartTime;
        const minutes = Math.floor(elapsed / 60000);
        const seconds = Math.floor((elapsed % 60000) / 1000);
        this.elapsedDisplay.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    }

    stopElapsedTimer() {
        clearInterval(this.elapsedInterval);
        this.elapsedInterval = null;
    }

    disconnectedCallback() {
        if (this.inputElement) {
            this.inputElement.removeEventListener('input', this.handleInput.bind(this));
        }
        if (this.buttonElement) {
            this.buttonElement.removeEventListener('click', this.reset.bind(this));
        }
    }

    reset() {
        this.inputElement.value = '';
        this.inputElement.classList.remove('blurred');
        this.containerElement.classList.remove('game-over', 'success', 'writing', 'archive');
        this.containerElement.classList.add('start-screen');
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
        this.stopElapsedTimer();
        this.sessionStartTime = null;
        this.elapsedDisplay.textContent = '00:00';
        this.updateStartArchiveButtonVisibility();
    }

    updateStartArchiveButtonVisibility() {
        if (this.hasArchiveEntries()) {
            this.startArchiveButton.classList.remove('hidden');
        } else {
            this.startArchiveButton.classList.add('hidden');
        }
    }

    showArchive() {
        this.renderArchiveEntries();
        this.containerElement.classList.remove('start-screen', 'writing', 'game-over', 'success');
        this.containerElement.classList.add('archive');
    }

    hideArchive() {
        this.containerElement.classList.remove('archive');
        this.containerElement.classList.add('start-screen');
        this.updateStartArchiveButtonVisibility();
    }

    formatDate(isoString) {
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
        const minutes = Math.floor(ms / 60000);
        const seconds = Math.floor((ms % 60000) / 1000);
        if (minutes > 0 && seconds > 0) {
            return `${minutes} min ${seconds} sek`;
        } else if (minutes > 0) {
            return `${minutes} min`;
        } else {
            return `${seconds} sek`;
        }
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
            <div class="archive-entry">
                <div class="archive-entry-header">
                    ${this.formatDate(entry.timestamp)} • ${this.formatDuration(entry.durationMs)}
                </div>
                <div class="archive-entry-text">${this.escapeHtml(entry.text)}</div>
            </div>
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
            // Start elapsed timer on first keystroke
            if (this.sessionStartTime === null) {
                this.sessionStartTime = Date.now();
                this.updateElapsedDisplay();
                this.elapsedInterval = setInterval(() => this.updateElapsedDisplay(), 1000);
            }
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
            this.shadowRoot.querySelector('.container').classList.add('success');
            this.stopElapsedTimer();
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
                this.shadowRoot.querySelector('.container').classList.add('game-over');
                this.stopElapsedTimer();
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
                    height: 100%;
                    position: relative;
                    display: flex;
                    flex-direction: column;
                }
                textarea {
                    width: 100%;
                    flex-grow: 1;
                    min-height: 100px; /* Minimum useful height */
                    border: 2px solid var(--border-input);
                    outline: none;
                    background: var(--bg-transparent);
                    color: var(--text-input);
                    padding: 1rem;
                    font-size: 1rem;
                    resize: none;
                    box-sizing: border-box;
                    border-radius: 8px;
                    transition: filter 5s ease-in;
                    filter: blur(0);
                    font-family: sans-serif;
                    display: block;
                }
                textarea.blurred {
                    filter: blur(10px);
                }
                .message {
                    display: none;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    text-align: center;
                    width: 100%;
                }
                .container.game-over textarea {
                    display: none;
                }
                .container.game-over .message {
                    display: block;
                }
                .success-message {
                    display: none;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    text-align: center;
                    width: 100%;
                }
                .container.success .success-message {
                    display: block;
                }
                .container.success textarea {
                    background: var(--bg-input-success);
                    filter: blur(0);
                }
                h2 {
                    margin-bottom: 1rem;
                    color: var(--text-primary);
                }
                button {
                    padding: 0.5rem 1rem;
                    font-size: 1rem;
                    cursor: pointer;
                    background: var(--btn-danger);
                    color: var(--text-on-dark);
                    border: none;
                    border-radius: 4px;
                    margin: 0.25rem;
                }
                .start-screen-content {
                    display: none;
                    text-align: center;
                    padding: 2rem;
                }
                .container.start-screen .start-screen-content {
                    display: block;
                }
                .container.start-screen textarea,
                .container.start-screen .message,
                .container.start-screen .success-message {
                    display: none;
                }
                .container.writing textarea {
                    display: block;
                }
                .container.writing .start-screen-content {
                    display: none;
                }
                #duration-input {
                    width: 60px;
                    padding: 0.5rem;
                    font-size: 1.2rem;
                    text-align: center;
                    border: 2px solid var(--border-default);
                    border-radius: 4px;
                    margin: 0 0.25rem;
                }
                #start-button {
                    background: var(--btn-primary);
                    padding: 0.75rem 2rem;
                    font-size: 1.2rem;
                    margin-top: 1rem;
                }
                #start-button:hover {
                    background: var(--btn-primary-hover);
                }
                .duration-label {
                    font-size: 1.1rem;
                    color: var(--text-primary);
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
                    background: var(--btn-secondary);
                    margin-top: 1rem;
                }
                .archive-button:hover {
                    background: var(--btn-secondary-hover);
                }
                .archive-button.hidden {
                    display: none;
                }
                .archive-content {
                    display: none;
                    padding: 1rem;
                    overflow-y: auto;
                    flex-grow: 1;
                }
                .container.archive .archive-content {
                    display: block;
                }
                .container.archive textarea,
                .container.archive .message,
                .container.archive .success-message,
                .container.archive .start-screen-content,
                .container.archive .elapsed-time {
                    display: none;
                }
                .archive-entry {
                    background: var(--bg-input);
                    border-radius: 8px;
                    padding: 1rem;
                    margin-bottom: 1rem;
                }
                .archive-entry-header {
                    font-size: 0.85rem;
                    color: var(--text-muted);
                    margin-bottom: 0.5rem;
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
                    margin-bottom: 1rem;
                }
                .archive-header h2 {
                    margin: 0;
                }
                .back-button {
                    background: var(--btn-secondary);
                }
                .back-button:hover {
                    background: var(--btn-secondary-hover);
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
                    <p class="duration-label">
                        skriv i 
                        <input type="number" id="duration-minutes" value="5" min="0" max="60" autofocus tabindex="1"> min
                        <input type="number" id="duration-seconds" value="0" min="0" max="59" tabindex="2"> sek
                    </p>
                    <button id="start-button" tabindex="3">start</button>
                    <br>
                    <button class="archive-button" id="start-archive-button">vis arkiv</button>
                </div>
                <textarea placeholder="skriv her..."></textarea>
                <div class="elapsed-time" id="elapsed-time">00:00</div>
                <div class="message">
                    <h2>skrivesperren lever</h2>
                    <button class="reset-button">skriv på nytt</button>
                </div>
                <div class="success-message">
                    <h2>skrivesperre knust.</h2>
                    <button class="reset-button">start på nytt</button>
                    <button class="archive-button" id="success-archive-button">vis arkiv</button>
                </div>
                <div class="archive-content">
                    <div class="archive-header">
                        <h2>arkiv</h2>
                        <button class="back-button" id="back-button">tilbake</button>
                    </div>
                    <div id="archive-entries"></div>
                </div>
            </div>
        `;
    }
}

customElements.define('dangerously-write', DangerouslyWrite);
