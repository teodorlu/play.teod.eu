import { EditorState } from 'https://esm.sh/prosemirror-state@1.4.3'
import { EditorView } from 'https://esm.sh/prosemirror-view@1.33.6'
import { schema as basicSchema } from 'https://esm.sh/prosemirror-schema-basic@1.2.2'
import { keymap } from 'https://esm.sh/prosemirror-keymap@1.2.2'
import { baseKeymap, toggleMark, chainCommands, exitCode } from 'https://esm.sh/prosemirror-commands@1.5.2'
import { history, undo, redo } from 'https://esm.sh/prosemirror-history@1.3.2'
import {
  filterDocsForPage,
  getArchiveActionLabel,
  isArchivedDoc,
  toggleArchivedState,
} from './archive.js'
import {
  getDocIdFromSearch,
  buildExperimentPageUrl,
  buildNavigationUrl,
  getPageFromSearch,
} from './navigation.js'
import { EXPERIMENTS, showExperiment, destroyExperiment } from './experiments.js'
import {
  DOCUMENT_STORE_NAME,
  openWriterDB,
  storeImageFile,
  hydrateStoredImages,
  serializeStoredImages,
} from './image-storage.js'

const imageSpec = {
  inline: true,
  attrs: {
    src: {},
    alt: { default: null },
    title: { default: null },
    imageId: { default: null },
  },
  group: 'inline',
  draggable: true,
  parseDOM: [{
    tag: 'img[src]',
    getAttrs(dom) {
      return {
        src: dom.getAttribute('src'),
        title: dom.getAttribute('title'),
        alt: dom.getAttribute('alt'),
        imageId: dom.getAttribute('data-image-id'),
      }
    },
  }],
  toDOM(node) {
    const attrs = {
      src: node.attrs.src,
      alt: node.attrs.alt,
      title: node.attrs.title,
    }

    if (node.attrs.imageId) {
      attrs['data-image-id'] = node.attrs.imageId
    }

    return ['img', attrs]
  },
}

const schema = new basicSchema.constructor({
  nodes: basicSchema.spec.nodes.update('image', imageSpec),
  marks: basicSchema.spec.marks,
})

// ── IndexedDB ──────────────────────────────────────────────────────────────

const STORE_NAME = DOCUMENT_STORE_NAME

function openDB() {
  return openWriterDB(indexedDB)
}

async function getAllDocs() {
  const db = await openDB()
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readonly')
    const req = tx.objectStore(STORE_NAME).index('updatedAt').getAll()
    req.onsuccess = e => resolve([...e.target.result].reverse())
    req.onerror = e => reject(e.target.error)
  })
}

async function getDoc(id) {
  const db = await openDB()
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readonly')
    const req = tx.objectStore(STORE_NAME).get(id)
    req.onsuccess = e => resolve(e.target.result)
    req.onerror = e => reject(e.target.error)
  })
}

async function saveDoc(doc) {
  const db = await openDB()
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite')
    const req = tx.objectStore(STORE_NAME).put(doc)
    req.onsuccess = e => resolve(e.target.result)
    req.onerror = e => reject(e.target.error)
  })
}

function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 8)
}

// ── State ──────────────────────────────────────────────────────────────────

let pmView = null
let currentDocId = null
let currentListPage = 'all'
let saveTimeout = null
let transientObjectUrls = []

// ── DOM refs ───────────────────────────────────────────────────────────────

const listViewEl = document.getElementById('list-view')
const editorViewEl = document.getElementById('editor-view')
const listTitleEl = document.getElementById('list-title')
const listPageNavEl = document.getElementById('list-page-nav')
const docListEl = document.getElementById('doc-list')
const emptyStateEl = document.getElementById('empty-state')
const newDocBtn = document.getElementById('new-doc-btn')
const experimentLinksEl = document.getElementById('experiment-links')
const backBtn = document.getElementById('back-btn')
const titleInput = document.getElementById('doc-title-input')
const insertImageBtn = document.getElementById('insert-image-btn')
const insertImageInput = document.getElementById('insert-image-input')
const editorEl = document.getElementById('editor')
const experimentViewEl = document.getElementById('experiment-view')
const expBackBtn = document.getElementById('exp-back-btn')

// ── Views ──────────────────────────────────────────────────────────────────

async function showList(page = 'all') {
  await flushSave()
  clearTransientObjectUrls()
  destroyExperiment()

  if (pmView) {
    pmView.destroy()
    pmView = null
  }
  currentDocId = null
  currentListPage = page

  editorViewEl.style.display = 'none'
  experimentViewEl.style.display = 'none'
  listViewEl.style.display = ''
  renderListPageNav(page)
  renderExperimentLinks()

  const docs = filterDocsForPage(await getAllDocs(), page)
  docListEl.innerHTML = ''

  if (docs.length === 0) {
    emptyStateEl.textContent = page === 'archival'
      ? 'No archived documents yet.'
      : 'No documents yet.'
    emptyStateEl.style.display = ''
    return
  }

  emptyStateEl.style.display = 'none'
  for (const doc of docs) {
    docListEl.appendChild(renderDocListItem(doc, page))
  }
}

function renderListPageNav(page) {
  listTitleEl.textContent = page === 'archival' ? 'Archive' : 'Writer'
  listPageNavEl.innerHTML = ''

  const link = document.createElement('a')
  link.className = 'list-page-link'

  if (page === 'archival') {
    link.textContent = 'All documents'
    link.href = buildExperimentPageUrl(window.location.href, null)
    link.addEventListener('click', event => {
      event.preventDefault()
      showListPage('all')
    })
  } else {
    link.textContent = 'Archive'
    link.href = buildExperimentPageUrl(window.location.href, 'archival')
    link.addEventListener('click', event => {
      event.preventDefault()
      showListPage('archival')
    })
  }

  listPageNavEl.appendChild(link)
}

function renderDocListItem(doc, page) {
  const li = document.createElement('li')
  li.className = 'doc-row'

  const button = document.createElement('button')
  button.type = 'button'
  button.className = 'doc-row-open'
  button.innerHTML =
    `<div class="doc-title">${escapeHtml(doc.title || 'Untitled')}${renderArchivedBadge(doc, page)}</div>` +
    `<div class="doc-meta">${formatDocDate(doc.updatedAt)}</div>`
  button.addEventListener('click', () => openDoc(doc.id))

  const action = document.createElement('button')
  action.type = 'button'
  action.className = 'doc-row-action'
  action.textContent = getArchiveActionLabel(doc)
  action.addEventListener('click', async event => {
    event.stopPropagation()
    await toggleDocArchive(doc.id)
  })

  li.append(button, action)
  return li
}

function renderArchivedBadge(doc, page) {
  if (page !== 'archival' || !isArchivedDoc(doc)) {
    return ''
  }

  return ' <span class="doc-badge">Archived</span>'
}

function formatDocDate(updatedAt) {
  return new Date(updatedAt).toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

async function toggleDocArchive(id) {
  const doc = await getDoc(id)
  if (!doc) {
    await renderLocation({ normalizeUrl: true })
    return
  }

  await saveDoc(toggleArchivedState(doc))
  await showList(currentListPage)
}

async function showListPage(page, { replace = false } = {}) {
  await showList(page)
  const nextUrl = buildExperimentPageUrl(window.location.href, page === 'archival' ? 'archival' : null)
  const currentUrl = `${window.location.pathname}${window.location.search}${window.location.hash}`

  if (nextUrl === currentUrl) {
    return
  }

  const method = replace ? 'replaceState' : 'pushState'
  window.history[method](null, '', nextUrl)
}

function renderExperimentLinks() {
  experimentLinksEl.innerHTML = ''

  const label = document.createElement('p')
  label.className = 'list-subtle-links-label'
  label.textContent = 'Heading experiments'
  experimentLinksEl.appendChild(label)

  const items = document.createElement('ul')
  items.className = 'list-subtle-links-items'

  for (const experiment of EXPERIMENTS) {
    const item = document.createElement('li')
    const link = document.createElement('a')
    link.className = 'list-subtle-link'
    link.href = buildExperimentPageUrl(window.location.href, `doc-experiment-${experiment.id}`)
    link.textContent = experiment.title
    link.addEventListener('click', event => {
      event.preventDefault()
      window.history.pushState(null, '', link.href)
      window.dispatchEvent(new PopStateEvent('popstate'))
    })
    item.appendChild(link)
    items.appendChild(item)
  }

  experimentLinksEl.appendChild(items)
}

async function openDoc(id) {
  const doc = await getDoc(id)
  if (!doc) {
    await showList(currentListPage)
    return
  }

  await showEditor(doc, false)
  syncHistoryState(doc.id)
}

async function createDoc() {
  const doc = {
    id: generateId(),
    title: '',
    content: null,
    createdAt: Date.now(),
    updatedAt: Date.now(),
  }
  await saveDoc(doc)
  await showEditor(doc, true)
  syncHistoryState(doc.id)
}

async function showEditor(doc, isNew) {
  if (currentDocId && currentDocId !== doc.id) {
    await flushSave()
  }
  destroyExperiment()

  currentDocId = doc.id
  clearTransientObjectUrls()
  listViewEl.style.display = 'none'
  experimentViewEl.style.display = 'none'
  editorViewEl.style.display = 'block'

  titleInput.value = doc.title || ''

  if (pmView) {
    pmView.destroy()
    pmView = null
  }

  editorEl.innerHTML = ''

  let state
  if (doc.content) {
    try {
      const hydrated = await hydrateStoredImages(doc.content)
      transientObjectUrls.push(...hydrated.objectUrls)
      const node = schema.nodeFromJSON(hydrated.content)
      state = EditorState.create({ doc: node, plugins: makePlugins() })
    } catch {
      state = emptyEditorState()
    }
  } else {
    state = emptyEditorState()
  }

  pmView = new EditorView(editorEl, {
    state,
    dispatchTransaction(tr) {
      const next = pmView.state.apply(tr)
      pmView.updateState(next)
      if (tr.docChanged) scheduleSave()
    },
  })

  if (isNew) {
    titleInput.focus()
  } else {
    pmView.focus()
  }
}

async function renderLocation({ normalizeUrl = false } = {}) {
  const page = getPageFromSearch(window.location.search)

  if (page) {
    if (page === 'all') {
      await showList('all')
      return
    }

    if (page === 'archival') {
      await showList('archival')
      return
    }

    if (page === 'doc') {
      const docId = getDocIdFromSearch(window.location.search)

      if (!docId) {
        await showList(currentListPage)
        if (normalizeUrl) {
          syncHistoryState(null, { replace: true })
        }
        return
      }

      const doc = await getDoc(docId)
      if (!doc) {
        await showList(currentListPage)
        if (normalizeUrl) {
          syncHistoryState(null, { replace: true })
        }
        return
      }

      await showEditor(doc, false)
      return
    }

    const match = page.match(/^doc-experiment-(\d+)$/)
    if (match) {
      showExperimentPage(parseInt(match[1], 10))
      return
    }
  }

  await showList('all')
  if (normalizeUrl) {
    syncHistoryState(null, { replace: true })
  }
}

function showExperimentPage(num) {
  flushSave()
  clearTransientObjectUrls()

  if (pmView) {
    pmView.destroy()
    pmView = null
  }
  currentDocId = null

  listViewEl.style.display = 'none'
  editorViewEl.style.display = 'none'
  experimentViewEl.style.display = 'block'

  if (!showExperiment(num)) {
    experimentViewEl.style.display = 'none'
    showList()
  }
}

// ── ProseMirror helpers ────────────────────────────────────────────────────

function makePlugins() {
  const { em, strong } = schema.marks

  return [
    history(),
    keymap({
      'Mod-b': toggleMark(strong),
      'Ctrl-b': toggleMark(strong),
      'Mod-i': toggleMark(em),
      'Ctrl-i': toggleMark(em),
    }),
    keymap({ 'Mod-Shift-i': openImagePickerCommand }),
    keymap({ 'Mod-z': undo, 'Mod-y': redo, 'Mod-Shift-z': redo }),
    keymap(baseKeymap),
  ]
}

function emptyEditorState() {
  return EditorState.create({ schema, plugins: makePlugins() })
}

// ── Auto-save ──────────────────────────────────────────────────────────────

function scheduleSave() {
  clearTimeout(saveTimeout)
  saveTimeout = setTimeout(flushSave, 800)
}

async function flushSave() {
  clearTimeout(saveTimeout)
  if (!currentDocId) return
  const existing = await getDoc(currentDocId)
  if (!existing) return
  const content = pmView ? serializeStoredImages(pmView.state.doc.toJSON()) : existing.content
  await saveDoc({
    ...existing,
    title: titleInput.value,
    content,
    updatedAt: Date.now(),
  })
}

// ── Image insertion ─────────────────────────────────────────────────────────

function openImagePickerCommand(state, dispatch, view) {
  if (!view) return false
  queueMicrotask(() => pickImage())
  return true
}

function pickImage() {
  insertImageInput.value = ''
  insertImageInput.click()
}

function insertImage(src, alt, imageId = null) {
  if (!pmView) return

  const imageNode = schema.nodes.image.create({ src, alt, imageId })
  const insert = chainCommands(exitCode, (state, dispatch) => {
    const { from, to } = state.selection
    dispatch(state.tr.replaceSelectionWith(imageNode).scrollIntoView())
    return true
  })

  insert(pmView.state, pmView.dispatch, pmView)
  pmView.focus()
  scheduleSave()
}

function clearTransientObjectUrls() {
  for (const url of transientObjectUrls) {
    URL.revokeObjectURL(url)
  }
  transientObjectUrls = []
}

// ── Utilities ─────────────────────────────────────────────────────────────

function escapeHtml(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function syncHistoryState(docId, { replace = false } = {}) {
  const nextUrl = buildNavigationUrl(window.location.href, docId)
  const currentUrl = `${window.location.pathname}${window.location.search}${window.location.hash}`

  if (nextUrl === currentUrl) {
    return
  }

  const state = docId ? { docId } : null
  const method = replace ? 'replaceState' : 'pushState'
  window.history[method](state, '', nextUrl)
}

// ── Event listeners ────────────────────────────────────────────────────────

newDocBtn.addEventListener('click', createDoc)
backBtn.addEventListener('click', async () => {
  await showList(currentListPage)
  syncHistoryState(null)
})
expBackBtn.addEventListener('click', async () => {
  destroyExperiment()
  experimentViewEl.style.display = 'none'
  syncHistoryState(null)
  await showList()
})

titleInput.addEventListener('input', scheduleSave)
titleInput.addEventListener('keydown', e => {
  if (e.key === 'Enter') {
    e.preventDefault()
    if (pmView) pmView.focus()
  }
})

insertImageBtn.addEventListener('click', pickImage)
insertImageInput.addEventListener('change', async event => {
  const [file] = event.target.files || []
  if (!file || !file.type.startsWith('image/')) return

  const stored = await storeImageFile(file)
  const objectUrl = URL.createObjectURL(file)
  transientObjectUrls.push(objectUrl)
  insertImage(objectUrl, stored.alt, stored.imageId)
})

// Save before leaving the page
window.addEventListener('beforeunload', () => {
  flushSave()
  clearTransientObjectUrls()
})

window.addEventListener('popstate', () => {
  renderLocation().catch(console.error)
})

// ── Init ───────────────────────────────────────────────────────────────────

renderLocation({ normalizeUrl: true }).catch(console.error)
