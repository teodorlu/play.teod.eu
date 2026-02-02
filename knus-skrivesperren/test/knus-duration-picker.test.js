import { test, beforeEach } from 'node:test';
import assert from 'node:assert';
import { JSDOM } from 'jsdom';

// Setup JSDOM environment
const dom = new JSDOM(`<!DOCTYPE html><body></body>`);
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

// Import the component
await import('../src/knus-duration-picker.js');

// --- Display Tests ---

test('DurationPicker: displays current duration text', () => {
    localStorage.clear();
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    const text = element.shadowRoot.querySelector('.duration-text');
    assert.ok(text, 'Should have duration text element');
    assert.ok(text.textContent.includes('5 min'), 'Default should be 5 min');
});

test('DurationPicker: displays edit button', () => {
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    const editBtn = element.shadowRoot.querySelector('.edit-button');
    assert.ok(editBtn, 'Should have edit button');
});

// --- Edit Mode Tests ---

test('DurationPicker: clicking edit shows options and hides display', () => {
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    const display = element.shadowRoot.querySelector('.duration-display');
    const options = element.shadowRoot.querySelector('.duration-options');
    const editBtn = element.shadowRoot.querySelector('.edit-button');

    // Initially: display visible, options hidden
    assert.ok(!display.classList.contains('hidden'), 'Display should be visible initially');
    assert.ok(options.classList.contains('hidden'), 'Options should be hidden initially');

    // Click edit
    editBtn.click();

    // After: display hidden, options visible
    assert.ok(display.classList.contains('hidden'), 'Display should be hidden after edit click');
    assert.ok(!options.classList.contains('hidden'), 'Options should be visible after edit click');
});

test('DurationPicker: has duration option buttons', () => {
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    const buttons = element.shadowRoot.querySelectorAll('.duration-button');
    assert.ok(buttons.length >= 3, 'Should have at least 3 duration options');
});

// --- Selection Tests ---

test('DurationPicker: selecting option updates display text', () => {
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    // Click edit to show options
    element.shadowRoot.querySelector('.edit-button').click();

    // Click a duration option (e.g., 1 min)
    const oneMinBtn = element.shadowRoot.querySelector('[data-seconds="60"]');
    oneMinBtn.click();

    const text = element.shadowRoot.querySelector('.duration-text');
    assert.ok(text.textContent.includes('1 min'), 'Display should show selected duration');
});

test('DurationPicker: selecting option hides options and shows display', () => {
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    const display = element.shadowRoot.querySelector('.duration-display');
    const options = element.shadowRoot.querySelector('.duration-options');

    // Click edit to show options
    element.shadowRoot.querySelector('.edit-button').click();

    // Click a duration option
    element.shadowRoot.querySelector('[data-seconds="60"]').click();

    assert.ok(!display.classList.contains('hidden'), 'Display should be visible after selection');
    assert.ok(options.classList.contains('hidden'), 'Options should be hidden after selection');
});

test('DurationPicker: selecting option dispatches change event', (t, done) => {
    localStorage.clear();
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    element.addEventListener('change', (e) => {
        assert.strictEqual(e.detail.seconds, 60);
        assert.strictEqual(e.detail.label, '1 min');
        done();
    });

    // Click edit and select
    element.shadowRoot.querySelector('.edit-button').click();
    element.shadowRoot.querySelector('[data-seconds="60"]').click();
});

// --- Persistence Tests ---

test('DurationPicker: saves selection to localStorage', () => {
    localStorage.clear();
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    // Select 1 min
    element.shadowRoot.querySelector('.edit-button').click();
    element.shadowRoot.querySelector('[data-seconds="60"]').click();

    const saved = JSON.parse(localStorage.getItem('knus-skrivesperren-duration'));
    assert.strictEqual(saved.seconds, 60);
    assert.strictEqual(saved.label, '1 min');
});

test('DurationPicker: loads saved duration on init', () => {
    localStorage.clear();
    localStorage.setItem('knus-skrivesperren-duration', JSON.stringify({ seconds: 5, label: '5 sek' }));

    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    const text = element.shadowRoot.querySelector('.duration-text');
    assert.ok(text.textContent.includes('5 sek'), 'Should display saved duration');
});

// --- Value Property Tests ---

test('DurationPicker: exposes value property with selected seconds', () => {
    localStorage.clear();
    const element = document.createElement('knus-duration-picker');
    document.body.appendChild(element);

    assert.strictEqual(element.value, 300, 'Default value should be 300 seconds');

    // Select 5 sek
    element.shadowRoot.querySelector('.edit-button').click();
    element.shadowRoot.querySelector('[data-seconds="5"]').click();

    assert.strictEqual(element.value, 5, 'Value should update after selection');
});
