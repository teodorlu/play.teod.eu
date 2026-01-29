import { test } from 'node:test';
import assert from 'node:assert';
import { JSDOM } from 'jsdom';

// Setup JSDOM environment for the test
const dom = new JSDOM(`<!DOCTYPE html><body><dangerously-write></dangerously-write></body>`);
global.window = dom.window;
global.document = dom.window.document;
global.HTMLElement = dom.window.HTMLElement;
global.customElements = dom.window.customElements;

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
