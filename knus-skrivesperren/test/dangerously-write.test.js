import { test, beforeEach } from 'node:test';
import assert from 'node:assert';
import { JSDOM } from 'jsdom';

// Setup JSDOM environment for the test
const dom = new JSDOM(`<!DOCTYPE html><body><dangerously-write></dangerously-write></body>`);
global.window = dom.window;
global.document = dom.window.document;
global.HTMLElement = dom.window.HTMLElement;
global.customElements = dom.window.customElements;

// Mock localStorage
const localStorageData = {};
global.localStorage = {
    getItem: (key) => localStorageData[key] ?? null,
    setItem: (key, value) => { localStorageData[key] = value; },
    removeItem: (key) => { delete localStorageData[key]; },
    clear: () => { Object.keys(localStorageData).forEach(k => delete localStorageData[k]); }
};

// Import does side-effect of defining element
await import('../src/dangerously-write.js');

test('DangerouslyWrite: clears text after timeout', (t, done) => {
    const element = document.createElement('dangerously-write');
    element.timeout = 10; // Override for speed
    document.body.appendChild(element); // Trigger connectedCallback

    // Wait for render
    const textarea = element.shadowRoot.querySelector('textarea');
    assert.ok(textarea, 'Textarea should be rendered');

    // Simulate typing
    textarea.value = 'hello';
    textarea.dispatchEvent(new dom.window.Event('input'));

    assert.strictEqual(textarea.value, 'hello');

    setTimeout(() => {
        const container = element.shadowRoot.querySelector('.container');
        assert.ok(container.classList.contains('game-over'), 'Should be in game over state');
        // display: none check is flaky in JSDOM dependent on how styles are registered

        // Reset
        const button = element.shadowRoot.querySelector('button');
        button.click();

        assert.ok(!container.classList.contains('game-over'), 'Should leave game over state');
        assert.strictEqual(textarea.value, '', 'Text should be cleared after reset');

        done();
    }, 20);
});

test('DangerouslyWrite: resets timer on new input', (t, done) => {
    const element = document.createElement('dangerously-write');
    element.timeout = 20;
    document.body.appendChild(element);

    const textarea = element.shadowRoot.querySelector('textarea');

    // First input
    textarea.value = 'h';
    textarea.dispatchEvent(new dom.window.Event('input'));

    setTimeout(() => {
        // Second input before timeout
        textarea.value = 'he';
        textarea.dispatchEvent(new dom.window.Event('input'));
    }, 10);

    setTimeout(() => {
        assert.strictEqual(textarea.value, 'he'); // Should NOT be cleared
    }, 25);

    setTimeout(() => {
        const container = element.shadowRoot.querySelector('.container');
        assert.ok(container.classList.contains('game-over'));
        done();
    }, 40);
});

test('DangerouslyWrite: unblurs text when user continues typing', (t, done) => {
    const element = document.createElement('dangerously-write');
    element.timeout = 50;
    document.body.appendChild(element);

    const textarea = element.shadowRoot.querySelector('textarea');

    // First input - should add blurred class
    textarea.value = 'h';
    textarea.dispatchEvent(new dom.window.Event('input'));
    assert.ok(textarea.classList.contains('blurred'), 'Should be blurred after first input');

    setTimeout(() => {
        // Second input - should remove and re-add blurred class (unblur then blur)
        // The transition is disabled, class removed, then transition re-enabled and class added
        textarea.value = 'he';
        textarea.dispatchEvent(new dom.window.Event('input'));

        // After input, blurred class should be present (animation restarted)
        assert.ok(textarea.classList.contains('blurred'), 'Should be blurred after second input');
        // Transition should be reset (empty string means using CSS default)
        assert.strictEqual(textarea.style.transition, '', 'Transition should be re-enabled');

        done();
    }, 20);
});

// --- Storage Tests ---

test('Storage: getArchiveEntries returns empty array when no data', () => {
    localStorage.clear();
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    const entries = element.getArchiveEntries();
    assert.deepStrictEqual(entries, []);
});

test('Storage: saveToArchive stores entry with correct format', () => {
    localStorage.clear();
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    element.saveToArchive('test text', 300000);

    const entries = element.getArchiveEntries();
    assert.strictEqual(entries.length, 1);
    assert.strictEqual(entries[0].text, 'test text');
    assert.strictEqual(entries[0].durationMs, 300000);
    assert.ok(entries[0].timestamp.match(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z$/),
        'Timestamp should be ISO format');
});

test('Storage: saveToArchive appends to existing entries', () => {
    localStorage.clear();
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    element.saveToArchive('first', 60000);
    element.saveToArchive('second', 120000);

    const entries = element.getArchiveEntries();
    assert.strictEqual(entries.length, 2);
    assert.strictEqual(entries[0].text, 'first');
    assert.strictEqual(entries[1].text, 'second');
});

test('Storage: hasArchiveEntries returns false when empty', () => {
    localStorage.clear();
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    assert.strictEqual(element.hasArchiveEntries(), false);
});

test('Storage: hasArchiveEntries returns true when entries exist', () => {
    localStorage.clear();
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    element.saveToArchive('test', 60000);
    assert.strictEqual(element.hasArchiveEntries(), true);
});

test('Storage: formatDuration handles minutes only', () => {
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    assert.strictEqual(element.formatDuration(120000), '2 min');
    assert.strictEqual(element.formatDuration(300000), '5 min');
});

test('Storage: formatDuration handles seconds only', () => {
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    assert.strictEqual(element.formatDuration(30000), '30 sek');
    assert.strictEqual(element.formatDuration(5000), '5 sek');
});

test('Storage: formatDuration handles minutes and seconds', () => {
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    assert.strictEqual(element.formatDuration(90000), '1 min 30 sek');
    assert.strictEqual(element.formatDuration(185000), '3 min 5 sek');
});

test('Storage: escapeHtml escapes special characters', () => {
    const element = document.createElement('dangerously-write');
    document.body.appendChild(element);

    assert.strictEqual(element.escapeHtml('<script>alert("xss")</script>'),
        '&lt;script&gt;alert("xss")&lt;/script&gt;');
    assert.strictEqual(element.escapeHtml('a & b'), 'a &amp; b');
});

test('Storage: success saves text to archive', (t, done) => {
    localStorage.clear();
    const element = document.createElement('dangerously-write');
    element.timeout = 100; // Prevent game-over during test
    document.body.appendChild(element);

    // Start writing session (this reads from inputs and may reset threshold)
    element.startWriting();
    // Override threshold after start
    element.successThreshold = 10;

    const textarea = element.shadowRoot.querySelector('textarea');

    // First input starts the timer
    textarea.value = 'my successful text';
    textarea.dispatchEvent(new dom.window.Event('input'));

    setTimeout(() => {
        // Second input should trigger success (>10ms elapsed)
        textarea.value = 'my successful text!';
        textarea.dispatchEvent(new dom.window.Event('input'));

        // Verify success state and archive
        assert.strictEqual(element.hasSucceeded, true, 'Should be in success state');

        const entries = element.getArchiveEntries();
        assert.strictEqual(entries.length, 1, 'Should have one archive entry');
        assert.strictEqual(entries[0].text, 'my successful text!', 'Archived text should match');

        done();
    }, 15);
});
