# UI Patterns and Entrypoint

The application prioritizes a low-friction "entrypoint" to encourage immediate action.

## Duration Selection Evolution
The interface was evolved to reduce decision fatigue at the moment of starting:

1.  **Initial**: Granular minute and second inputs (too complex).
2.  **Simplified**: A single toggle button cycling between 1 and 5 minutes.
3.  **Reveal and Select Pattern**: To keep the start screen clean, the current duration is displayed with an edit button. Clicking "edit" (pencil) reveals discrete options: `5 sek`, `1 min`, and `5 min`.
    -   **Pencil Icon**: Provides a standard "edit" affordance without cluttering the main screen.
    -   **Selection Highlighting**: Once an option is clicked, the selection is updated, persisted, and the options list hides again.
    -   **Color Coding**: Secondary buttons (outline style) and accent buttons use the same green theme as primary buttons, but with lighter visual weight to differentiate configuration from the solid green "Start" action.
    -   **Unit Formatting**: Labels follow the strict `[quantity] [unit]` pattern (e.g., `5 sek`).

## Navigation and State
The application is a single-page application (SPA) that uses URL hashes to manage main views:

-   `#skriv`: The active writing session.
-   `#arkiv`: The history of successful sessions.
-   `#knust`: The success message after crushing writer's block.
-   `#vedvarer`: The failure message when writer's block persists.
-   (empty): The start screen.

### Browser Compatibility
By listening to the `hashchange` event, the component handles the browser's "Back" and "Forward" buttons correctly:
-   Navigating back from a session returns the user to the start screen.
-   Active timers are automatically cleared when the hash changes away from `#skriv`.

### Visual Inspection Strategy
When performing visual UI inspections, **navigate directly to hash URLs** rather than clicking through the UI. This is faster and more reliable:
-   `/knus-skrivesperren/` → Start screen
-   `/knus-skrivesperren/#skriv` → Writing view
-   `/knus-skrivesperren/#knust` → Success message
-   `/knus-skrivesperren/#vedvarer` → Failure message
-   `/knus-skrivesperren/#arkiv` → Archive view

## Archive Content Layout
The archive view is optimized for readability and clean vertical rhythm:
-   **Flex Layout**: The `.archive-content` container uses `display: flex` and `flex-direction: column` with a `gap: 1.5rem`.
-   **Entry List**: Individual entries are contained in an `.archive-entries` list with a `gap: 0.6rem`.
-   **Entry Cards**: Each entry card (`.archive-entry`) uses a flex column with a smaller `gap: 0.4rem` between the header (date/duration) and the written text.
-   **Header**: The archive header uses `justify-content: space-between` to separate the title and the "back" button at the edges of the container.

## Status Overlays
For critical feedback states, the application uses symmetric overlay patterns:
-   **Symmetric Naming**: High-impact messages are classed as `.success-message` and `.failure-message`.
-   **Absolute Centering**: Both overlays are decoupled from the document flow using `position: absolute` with a `transform: translate(-50%, -50%)` centering strategy. Internal content is managed via `display: flex` with a `1.5rem` gap to maintain consistency with the rest of the application's spacing.
-   **State Toggles**: The overlays are shown/hidden by matching the parent `.container` state class (`.success` or `.game-over`), which are applied by the hash handler when navigation to `#knust` or `#vedvarer` occurs.

## Layout Audit (2026-01-31)
A comprehensive spacing audit was conducted to verify consistency across routing states. Key learnings about overlay clutter and spacing mismatches are documented in [Layout Audit](./layout_audit.md).

## Shadow DOM Encapsulation
The entire application is encapsulated within a Shadow DOM, shielding its internal state and styles while allowing it to be easily dropped into any page (like `index.html`) as a custom tag `<dangerously-write>`.
