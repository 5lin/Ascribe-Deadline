// Counter-Down App Service Worker
// 用于缓存策略和离线支持

const CACHE_NAME = 'countdown-app-v1'
const STATIC_ASSETS = [
    '/',
    '/index.html',
    '/assets/'
]

// 安装事件 - 缓存静态资源
self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            console.log('[SW] 缓存静态资源')
            return cache.addAll(STATIC_ASSETS)
        })
    )
    self.skipWaiting()
})

// 激活事件 - 清理旧缓存
self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames
                    .filter((name) => name !== CACHE_NAME)
                    .map((name) => caches.delete(name))
            )
        })
    )
    self.clients.claim()
})

// 请求拦截
self.addEventListener('fetch', (event) => {
    const { request } = event
    const url = new URL(request.url)

    // API 请求 - 网络优先
    if (url.hostname === 'pb.l50.top') {
        event.respondWith(networkFirst(request))
        return
    }

    // 静态资源 - 缓存优先
    event.respondWith(cacheFirst(request))
})

// 缓存优先策略
async function cacheFirst(request) {
    const cached = await caches.match(request)
    if (cached) {
        return cached
    }

    try {
        const response = await fetch(request)
        if (response.ok) {
            const cache = await caches.open(CACHE_NAME)
            cache.put(request, response.clone())
        }
        return response
    } catch (error) {
        console.log('[SW] 获取资源失败:', request.url)
        return new Response('Offline', { status: 503 })
    }
}

// 网络优先策略
async function networkFirst(request) {
    try {
        const response = await fetch(request)
        if (response.ok) {
            const cache = await caches.open(CACHE_NAME)
            cache.put(request, response.clone())
        }
        return response
    } catch (error) {
        const cached = await caches.match(request)
        if (cached) {
            console.log('[SW] 使用缓存的 API 响应')
            return cached
        }
        return new Response(JSON.stringify({ error: 'Offline' }), {
            status: 503,
            headers: { 'Content-Type': 'application/json' }
        })
    }
}
