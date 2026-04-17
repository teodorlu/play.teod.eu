export function isArchivedDoc(doc) {
  return Boolean(doc?.archivedAt)
}

export function filterDocsForPage(docs, page) {
  if (page === 'archival') {
    return docs
  }

  return docs.filter(doc => !isArchivedDoc(doc))
}

export function getArchiveActionLabel(doc) {
  return isArchivedDoc(doc) ? 'Unarchive' : 'Archive'
}

export function toggleArchivedState(doc, now = Date.now()) {
  if (isArchivedDoc(doc)) {
    return { ...doc, archivedAt: null, updatedAt: now }
  }

  return { ...doc, archivedAt: now, updatedAt: now }
}
