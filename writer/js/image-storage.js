const DB_NAME = 'writer'
const DOCUMENT_STORE_NAME = 'documents'
const IMAGE_STORE_NAME = 'images'
const DB_VERSION = 2
const IMAGE_SRC_PREFIX = 'writer-image:'

function requestToPromise(request) {
  return new Promise((resolve, reject) => {
    request.onsuccess = event => resolve(event.target.result)
    request.onerror = event => reject(event.target.error)
  })
}

function defaultImageIdFactory() {
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 8)
}

export function createStoredImageSrc(imageId) {
  return `${IMAGE_SRC_PREFIX}${imageId}`
}

export function parseStoredImageSrc(src) {
  if (typeof src !== 'string' || !src.startsWith(IMAGE_SRC_PREFIX)) {
    return null
  }

  return src.slice(IMAGE_SRC_PREFIX.length) || null
}

export function openWriterDB(indexedDBRef = indexedDB) {
  return new Promise((resolve, reject) => {
    const request = indexedDBRef.open(DB_NAME, DB_VERSION)

    request.onupgradeneeded = event => {
      const db = event.target.result

      if (!db.objectStoreNames.contains(DOCUMENT_STORE_NAME)) {
        const store = db.createObjectStore(DOCUMENT_STORE_NAME, { keyPath: 'id' })
        store.createIndex('updatedAt', 'updatedAt', { unique: false })
      }

      if (!db.objectStoreNames.contains(IMAGE_STORE_NAME)) {
        db.createObjectStore(IMAGE_STORE_NAME, { keyPath: 'id' })
      }
    }

    request.onsuccess = event => resolve(event.target.result)
    request.onerror = event => reject(event.target.error)
  })
}

export async function storeImageFile(
  file,
  {
    indexedDBRef = indexedDB,
    idFactory = defaultImageIdFactory,
    now = () => Date.now(),
  } = {},
) {
  const db = await openWriterDB(indexedDBRef)
  const imageId = idFactory()
  const record = {
    id: imageId,
    blob: file,
    type: file.type || '',
    name: file.name || '',
    createdAt: now(),
  }

  const tx = db.transaction(IMAGE_STORE_NAME, 'readwrite')
  await requestToPromise(tx.objectStore(IMAGE_STORE_NAME).put(record))

  return {
    imageId,
    storedSrc: createStoredImageSrc(imageId),
    alt: file.name || '',
  }
}

export async function getStoredImage(imageId, { indexedDBRef = indexedDB } = {}) {
  const db = await openWriterDB(indexedDBRef)
  const tx = db.transaction(IMAGE_STORE_NAME, 'readonly')
  return requestToPromise(tx.objectStore(IMAGE_STORE_NAME).get(imageId))
}

export async function hydrateStoredImages(
  node,
  { indexedDBRef = indexedDB, urlRef = URL } = {},
) {
  if (!node || typeof node !== 'object') {
    return { content: node, objectUrls: [] }
  }

  if (node.type === 'image') {
    const imageId = node.attrs?.imageId || parseStoredImageSrc(node.attrs?.src)
    if (!imageId) {
      return { content: node, objectUrls: [] }
    }

    const record = await getStoredImage(imageId, { indexedDBRef })
    if (!record?.blob) {
      return { content: node, objectUrls: [] }
    }

    const objectUrl = urlRef.createObjectURL(record.blob)
    return {
      content: {
        ...node,
        attrs: {
          ...node.attrs,
          src: objectUrl,
          imageId,
        },
      },
      objectUrls: [objectUrl],
    }
  }

  if (!Array.isArray(node.content)) {
    return { content: node, objectUrls: [] }
  }

  const results = await Promise.all(
    node.content.map(child => hydrateStoredImages(child, { indexedDBRef, urlRef })),
  )

  return {
    content: {
      ...node,
      content: results.map(result => result.content).filter(Boolean),
    },
    objectUrls: results.flatMap(result => result.objectUrls),
  }
}

export function serializeStoredImages(node) {
  if (!node || typeof node !== 'object') {
    return node
  }

  if (node.type === 'image') {
    const imageId = node.attrs?.imageId || parseStoredImageSrc(node.attrs?.src)
    if (!imageId) {
      return node
    }

    return {
      ...node,
      attrs: {
        ...node.attrs,
        src: createStoredImageSrc(imageId),
        imageId,
      },
    }
  }

  if (!Array.isArray(node.content)) {
    return node
  }

  return {
    ...node,
    content: node.content.map(child => serializeStoredImages(child)).filter(Boolean),
  }
}

export {
  DB_NAME,
  DB_VERSION,
  DOCUMENT_STORE_NAME,
  IMAGE_STORE_NAME,
}
