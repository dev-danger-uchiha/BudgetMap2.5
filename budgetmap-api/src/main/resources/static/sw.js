const CACHE_NAME = 'budgetmap-cache-v1';
const urlsToCache = [
  '/',
  '/index.html',
  '/login.html',
  '/manifest.json',
  '/images/pwa-icon.png'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        return cache.addAll(urlsToCache);
      })
  );
});

self.addEventListener('fetch', event => {
  const url = new URL(event.request.url);

  // 1. Stale-While-Revalidate para llamadas a la API (solo GET)
  if (url.pathname.startsWith('/api/') && event.request.method === 'GET') {
    event.respondWith(
      caches.open('budgetmap-api-cache-v1').then(cache => {
        return cache.match(event.request).then(cachedResponse => {
          const fetchPromise = fetch(event.request).then(networkResponse => {
            if (networkResponse && networkResponse.ok) {
              cache.put(event.request, networkResponse.clone());
            }
            return networkResponse;
          }).catch(err => {
            console.warn('Network API fetch failed, serving from cache if available', err);
            return cachedResponse; // O devuelve null/undefined si tampoco hay en caché
          });
          
          // Devuelve lo que haya en caché inmediatamente, y en background actualiza
          return cachedResponse || fetchPromise;
        });
      })
    );
    return;
  }

  // 2. Cache-First dinámico para otros recursos (imágenes, scripts externos)
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        if (response) {
          return response;
        }
        return fetch(event.request).then(networkResponse => {
          // Cachear dinámicamente imágenes para futuras visitas
          if (networkResponse && networkResponse.ok && event.request.destination === 'image') {
            const responseClone = networkResponse.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(event.request, responseClone));
          }
          return networkResponse;
        }).catch(err => {
          console.warn('Network fetch failed', err);
        });
      })
  );
});

self.addEventListener('activate', event => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});
