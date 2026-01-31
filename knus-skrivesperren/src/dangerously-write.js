const STORAGE_KEY = 'knus-skrivesperren-arkiv';
const DURATION_STORAGE_KEY = 'knus-skrivesperren-duration';

export class DangerouslyWrite extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.timer = null;
        this.elapsedInterval = null;
        this.sessionStartTime = null;
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
        this.durationDisplay = this.shadowRoot.querySelector('.duration-display');
        this.durationOptions = this.shadowRoot.querySelector('.duration-options');
        this.durationText = this.shadowRoot.querySelector('.duration-text');
        this.editButton = this.shadowRoot.querySelector('.edit-button');

        // Load saved duration from localStorage
        this.loadSavedDuration();

        this.inputElement.addEventListener('input', this.handleInput.bind(this));

        // Edit button shows duration options
        this.editButton.addEventListener('click', () => {
            this.durationDisplay.classList.add('hidden');
            this.durationOptions.classList.remove('hidden');
        });

        // Duration button event delegation
        this.shadowRoot.querySelectorAll('.duration-button').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const seconds = parseInt(e.target.dataset.seconds, 10);
                const label = e.target.textContent;
                this.selectDuration(seconds, label);
            });
        });

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
        window.removeEventListener('hashchange', this.handleHashChange);
    }

    reset() {
        // Clear value and state
        this.inputElement.value = '';
        this.inputElement.classList.remove('blurred');
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
        this.sessionStartTime = null;
        this.elapsedDisplay.textContent = '00:00';

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

    loadSavedDuration() {
        try {
            const saved = localStorage.getItem(DURATION_STORAGE_KEY);
            if (saved) {
                const { seconds, label } = JSON.parse(saved);
                this.selectedDurationSeconds = seconds;
                this.durationText.textContent = `Skriv i ${label}`;
            }
        } catch {
            // Use default if loading fails
        }
    }

    saveDuration(seconds, label) {
        try {
            localStorage.setItem(DURATION_STORAGE_KEY, JSON.stringify({ seconds, label }));
        } catch {
            // Ignore storage errors
        }
    }

    selectDuration(seconds, label) {
        this.selectedDurationSeconds = seconds;
        // Persist selection
        this.saveDuration(seconds, label);
        // Update display text and hide options
        this.durationText.textContent = `Skriv i ${label}`;
        this.durationOptions.classList.add('hidden');
        this.durationDisplay.classList.remove('hidden');
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
        this.stopElapsedTimer();

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
            this.stopElapsedTimer();
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
                this.stopElapsedTimer();
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
                    align-items: center;
                    justify-content: center;
                    gap: 0.5rem;
                }
                .duration-options.hidden {
                    display: none;
                }
                .duration-label {
                    color: var(--text-primary);
                    font-size: 1rem;
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
                    <div class="duration-display">
                        <span class="duration-text">Skriv i 5 min</span>
                        <button class="edit-button" tabindex="1" aria-label="Endre varighet">
                            <img src="./icons/pencil-simple.svg" alt="Rediger">
                        </button>
                    </div>
                    <div class="duration-options hidden">
                        <span class="duration-label">skriv i:</span>
                        <button class="duration-button" data-seconds="5" tabindex="1">5 sek</button>
                        <button class="duration-button" data-seconds="60" tabindex="2">1 min</button>
                        <button class="duration-button" data-seconds="300" tabindex="3">5 min</button>
                    </div>
                    <button id="start-button" tabindex="4">start</button>
                    <button class="archive-button" id="start-archive-button">vis arkiv</button>
                </div>
                <textarea placeholder="skriv her..."></textarea>
                <div class="elapsed-time" id="elapsed-time">00:00</div>
                <div class="failure-message">
                    <h2>skrivesperren vedvarer</h2>
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
                    <div id="archive-entries" class="archive-entries"></div>
                </div>
            </div>
        `;
    }
}

customElements.define('dangerously-write', DangerouslyWrite);
