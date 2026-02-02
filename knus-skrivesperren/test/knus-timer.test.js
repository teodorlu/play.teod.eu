import { test, beforeEach } from 'node:test';
import assert from 'node:assert';
import { JSDOM } from 'jsdom';

// Setup JSDOM environment
const dom = new JSDOM(`<!DOCTYPE html><body></body>`);
global.window = dom.window;
global.document = dom.window.document;
global.HTMLElement = dom.window.HTMLElement;
global.customElements = dom.window.customElements;

// Import the component
await import('../src/knus-timer.js');

// --- Display Tests ---

test('Timer: displays 00:00 initially', () => {
    const element = document.createElement('knus-timer');
    document.body.appendChild(element);

    const display = element.shadowRoot.querySelector('.timer-display');
    assert.ok(display, 'Should have timer display element');
    assert.strictEqual(display.textContent, '00:00');
});

// --- Control Tests ---

test('Timer: has start method', () => {
    const element = document.createElement('knus-timer');
    document.body.appendChild(element);

    assert.strictEqual(typeof element.start, 'function');
});

test('Timer: has stop method', () => {
    const element = document.createElement('knus-timer');
    document.body.appendChild(element);

    assert.strictEqual(typeof element.stop, 'function');
});

test('Timer: has reset method', () => {
    const element = document.createElement('knus-timer');
    document.body.appendChild(element);

    assert.strictEqual(typeof element.reset, 'function');
});

test('Timer: reset sets display back to 00:00', () => {
    const element = document.createElement('knus-timer');
    document.body.appendChild(element);

    // Manually set a different value
    element.shadowRoot.querySelector('.timer-display').textContent = '01:30';

    element.reset();

    assert.strictEqual(element.shadowRoot.querySelector('.timer-display').textContent, '00:00');
});

// --- Time Tracking Tests ---

test('Timer: exposes elapsed property', () => {
    const element = document.createElement('knus-timer');
    document.body.appendChild(element);

    assert.strictEqual(typeof element.elapsed, 'number');
    assert.strictEqual(element.elapsed, 0);
});
