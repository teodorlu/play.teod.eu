export function getPageFromSearch(search) {
  const page = new URLSearchParams(search).get('page')
  return page || null
}

export function getDocIdFromSearch(search) {
  const docId = new URLSearchParams(search).get('docid')
  return docId || null
}

export function buildNavigationUrl(href, docId) {
  const url = new URL(href, 'http://localhost')

  url.searchParams.delete('site')
  url.searchParams.delete('page')
  url.searchParams.delete('docid')

  if (docId) {
    url.searchParams.set('page', 'doc')
    url.searchParams.set('docid', docId)
  } else {
    url.searchParams.set('page', 'all')
  }

  return `${url.pathname}${url.search}${url.hash}`
}

export function buildExperimentPageUrl(href, page) {
  const url = new URL(href, 'http://localhost')

  url.searchParams.delete('site')
  url.searchParams.delete('page')
  url.searchParams.delete('docid')

  if (page) {
    url.searchParams.set('page', page)
  }

  return `${url.pathname}${url.search}${url.hash}`
}
