import { EditorState, Plugin } from 'https://esm.sh/prosemirror-state@1.4.3'
import { EditorView } from 'https://esm.sh/prosemirror-view@1.33.6'
import { schema as basicSchema } from 'https://esm.sh/prosemirror-schema-basic@1.2.2'
import { keymap } from 'https://esm.sh/prosemirror-keymap@1.2.2'
import { baseKeymap, setBlockType } from 'https://esm.sh/prosemirror-commands@1.5.2'
import { history, undo, redo } from 'https://esm.sh/prosemirror-history@1.3.2'

const schema = new basicSchema.constructor({
  nodes: basicSchema.spec.nodes,
  marks: basicSchema.spec.marks,
})

// ── Experiment registry ───────────────────────────────────────────────────

export const EXPERIMENTS = [
  {
    id: 1,
    title: 'Markdown-style heading shortcuts',
    description:
      'Type # + space for H1, ## + space for H2, ### + space for H3. ' +
      'The hash marks are consumed and the line converts to a heading. ' +
      'Try it: type "## " to create an H2.',
  },
  {
    id: 2,
    title: 'Heading-first document',
    description:
      'The first line is always an H1 heading \u2014 it cannot be changed to a paragraph. ' +
      'This creates a \u201ctitle-first\u201d authoring experience. Start typing your title, ' +
      'then press Enter to write body text below.',
  },
  {
    id: 3,
    title: 'Keyboard shortcut heading levels',
    description:
      'Use Cmd/Ctrl+1 for H1, Cmd/Ctrl+2 for H2, Cmd/Ctrl+3 for H3, ' +
      'Cmd/Ctrl+0 to convert back to a paragraph. No visual chrome \u2014 ' +
      'headings are controlled entirely by keyboard.',
  },
]

// ── Experiment 1: Markdown-style heading input rules ──────────────────────

function markdownHeadingHandleTextInput(view, from, to, text) {
  if (text !== ' ' || from !== to) return false

  const { state } = view
  const $from = state.doc.resolve(from)

  if ($from.parent.type.name !== 'paragraph') return false

  const textBefore = $from.parent.textContent.slice(0, $from.parentOffset)
  const match = textBefore.match(/^(#{1,6})$/)
  if (!match) return false

  const level = match[1].length
  const contentStart = $from.start()

  const tr = state.tr
    .setBlockType($from.before(), $from.after(), schema.nodes.heading, { level })
    .delete(contentStart, contentStart + level)

  view.dispatch(tr)
  return true
}

// ── Experiment 2: Heading-first document ──────────────────────────────────

function headingFirstPlugin() {
  return new Plugin({
    appendTransaction(_transactions, _oldState, newState) {
      const firstChild = newState.doc.firstChild
      if (!firstChild) return null
      if (
        firstChild.type === schema.nodes.heading &&
        firstChild.attrs.level === 1
      ) {
        return null
      }
      return newState.tr.setBlockType(
        0,
        firstChild.nodeSize,
        schema.nodes.heading,
        { level: 1 },
      )
    },
  })
}

function headingFirstInitialDoc() {
  return schema.node('doc', null, [
    schema.node('heading', { level: 1 }),
    schema.node('paragraph'),
  ])
}

// ── Experiment 3: Keyboard shortcut heading levels ────────────────────────

function headingKeymapPlugin() {
  const heading = schema.nodes.heading
  const paragraph = schema.nodes.paragraph

  return keymap({
    'Mod-1': setBlockType(heading, { level: 1 }),
    'Mod-2': setBlockType(heading, { level: 2 }),
    'Mod-3': setBlockType(heading, { level: 3 }),
    'Mod-0': setBlockType(paragraph),
  })
}

// ── Shared helpers ────────────────────────────────────────────────────────

function basePlugins() {
  return [
    history(),
    keymap({ 'Mod-z': undo, 'Mod-y': redo, 'Mod-Shift-z': redo }),
    keymap(baseKeymap),
  ]
}

// ── Public API ────────────────────────────────────────────────────────────

let activeView = null

export function showExperiment(number) {
  const exp = EXPERIMENTS.find(e => e.id === number)
  if (!exp) return false

  if (activeView) {
    activeView.destroy()
    activeView = null
  }

  document.getElementById('exp-title').textContent = exp.title
  document.getElementById('exp-desc').textContent = exp.description

  // Build experiment navigation
  const navEl = document.getElementById('exp-nav')
  navEl.innerHTML = ''
  for (const e of EXPERIMENTS) {
    if (e.id === number) {
      const span = document.createElement('span')
      span.textContent = `Exp ${e.id}`
      span.className = 'exp-nav-current'
      navEl.appendChild(span)
    } else {
      const a = document.createElement('a')
      a.textContent = `Exp ${e.id}`
      a.href = `?page=doc-experiment-${e.id}`
      a.className = 'exp-nav-link'
      a.addEventListener('click', ev => {
        ev.preventDefault()
        window.history.pushState(null, '', `?page=doc-experiment-${e.id}`)
        window.dispatchEvent(new PopStateEvent('popstate'))
      })
      navEl.appendChild(a)
    }
  }

  // Create the editor
  const editorEl = document.getElementById('exp-editor')
  editorEl.innerHTML = ''

  let state
  let viewProps = {}

  switch (number) {
    case 1:
      state = EditorState.create({ schema, plugins: basePlugins() })
      viewProps.handleTextInput = markdownHeadingHandleTextInput
      break

    case 2:
      state = EditorState.create({
        doc: headingFirstInitialDoc(),
        plugins: [headingFirstPlugin(), ...basePlugins()],
      })
      break

    case 3:
      state = EditorState.create({
        schema,
        plugins: [headingKeymapPlugin(), ...basePlugins()],
      })
      break

    default:
      return false
  }

  activeView = new EditorView(editorEl, {
    state,
    ...viewProps,
    dispatchTransaction(tr) {
      const next = activeView.state.apply(tr)
      activeView.updateState(next)
    },
  })

  activeView.focus()
  return true
}

export function destroyExperiment() {
  if (activeView) {
    activeView.destroy()
    activeView = null
  }
}
