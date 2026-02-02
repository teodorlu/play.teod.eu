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
await import('../src/knus-archive-entry.js');

// --- Display Tests ---

test('ArchiveEntry: renders timestamp and duration in header', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('timestamp', '2026-01-15T10:30:00.000Z');
    element.setAttribute('duration-ms', '300000');
    document.body.appendChild(element);

    const header = element.shadowRoot.querySelector('.archive-entry-header');
    assert.ok(header, 'Should have header element');
    assert.ok(header.textContent.includes('5 min'), 'Header should show duration');
});

test('ArchiveEntry: renders text content', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('text', 'Test writing content');
    document.body.appendChild(element);

    const text = element.shadowRoot.querySelector('.archive-entry-text');
    assert.ok(text, 'Should have text element');
    assert.strictEqual(text.textContent, 'Test writing content');
});

test('ArchiveEntry: escapes HTML in text content', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('text', '<script>alert("xss")</script>');
    document.body.appendChild(element);

    const text = element.shadowRoot.querySelector('.archive-entry-text');
    assert.ok(!text.innerHTML.includes('<script>'), 'Should escape HTML tags');
});

test('ArchiveEntry: has copy button', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('text', 'Some text');
    document.body.appendChild(element);

    const copyBtn = element.shadowRoot.querySelector('.copy-button');
    assert.ok(copyBtn, 'Should have copy button');
});

// --- Copy Functionality Tests ---

test('ArchiveEntry: clicking copy button dispatches copy event with text', (t, done) => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('text', 'Text to copy');
    document.body.appendChild(element);

    element.addEventListener('copy', (e) => {
        assert.strictEqual(e.detail.text, 'Text to copy');
        done();
    });

    element.shadowRoot.querySelector('.copy-button').click();
});

// --- Formatting Tests ---

test('ArchiveEntry: formats duration correctly for minutes only', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('duration-ms', '180000'); // 3 minutes
    document.body.appendChild(element);

    const header = element.shadowRoot.querySelector('.archive-entry-header');
    assert.ok(header.textContent.includes('3 min'), 'Should format as minutes');
});

test('ArchiveEntry: formats duration correctly for seconds only', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('duration-ms', '45000'); // 45 seconds
    document.body.appendChild(element);

    const header = element.shadowRoot.querySelector('.archive-entry-header');
    assert.ok(header.textContent.includes('45 sek'), 'Should format as seconds');
});

test('ArchiveEntry: formats duration correctly for minutes and seconds', () => {
    const element = document.createElement('knus-archive-entry');
    element.setAttribute('duration-ms', '95000'); // 1 min 35 sek
    document.body.appendChild(element);

    const header = element.shadowRoot.querySelector('.archive-entry-header');
    assert.ok(header.textContent.includes('1 min 35 sek'), 'Should format as minutes and seconds');
});
