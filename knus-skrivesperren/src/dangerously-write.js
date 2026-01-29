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

        // Start elapsed time tracking
        this.sessionStartTime = Date.now();
        this.updateElapsedDisplay();
        clearInterval(this.elapsedInterval);
        this.elapsedInterval = setInterval(() => this.updateElapsedDisplay(), 1000);

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
        this.containerElement.classList.remove('game-over', 'success', 'writing');
        this.containerElement.classList.add('start-screen');
        this.writingStartTime = null;
        this.accumulatedWritingTime = 0;
        this.hasSucceeded = false;
        this.stopElapsedTimer();
        this.sessionStartTime = null;
        this.elapsedDisplay.textContent = '00:00';
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
        }

        // Calculate total writing time
        const currentSessionTime = Date.now() - this.writingStartTime;
        const totalWritingTime = this.accumulatedWritingTime + currentSessionTime;

        // Check for success (30 seconds of writing)
        if (totalWritingTime >= this.successThreshold) {
            this.hasSucceeded = true;
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
                }
                textarea {
                    width: 100%;
                    height: 100%;
                    min-height: 200px; /* Minimum useful height */
                    border: none;
                    outline: none;
                    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
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
                    background: linear-gradient(135deg, #d4edda 0%, #a8e6cf 100%);
                    filter: blur(0);
                }
                h2 {
                    margin-bottom: 1rem;
                    color: #333;
                }
                button {
                    padding: 0.5rem 1rem;
                    font-size: 1rem;
                    cursor: pointer;
                    background: #ff4d4d;
                    color: white;
                    border: none;
                    border-radius: 4px;
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
                    border: 2px solid #ccc;
                    border-radius: 4px;
                    margin: 0 0.25rem;
                }
                #start-button {
                    background: #4CAF50;
                    padding: 0.75rem 2rem;
                    font-size: 1.2rem;
                    margin-top: 1rem;
                }
                #start-button:hover {
                    background: #45a049;
                }
                .duration-label {
                    font-size: 1.1rem;
                    color: #333;
                }
                .elapsed-time {
                    text-align: center;
                    font-size: 1.5rem;
                    font-family: monospace;
                    color: #666;
                    padding: 0.5rem;
                    margin-top: 0.5rem;
                }
                .container.start-screen .elapsed-time {
                    display: none;
                }
            </style>
            <div class="container start-screen">
                <div class="start-screen-content">
                    <h2>Knus skrivesperren</h2>
                    <p class="duration-label">
                        Skriv i 
                        <input type="number" id="duration-minutes" value="5" min="0" max="60"> min
                        <input type="number" id="duration-seconds" value="0" min="0" max="59"> sek
                    </p>
                    <button id="start-button">Start</button>
                </div>
                <textarea placeholder="Skriv her..."></textarea>
                <div class="elapsed-time" id="elapsed-time">00:00</div>
                <div class="message">
                    <h2>Teksten din er borte</h2>
                    <button class="reset-button">Skriv på nytt</button>
                </div>
                <div class="success-message">
                    <h2>Skrivesperre knust.</h2>
                    <p>Du har skrevet i måltiden. Teksten din er trygg.</p>
                    <button class="reset-button">Start på nytt</button>
                </div>
            </div>
        `;
    }
}

customElements.define('dangerously-write', DangerouslyWrite);
