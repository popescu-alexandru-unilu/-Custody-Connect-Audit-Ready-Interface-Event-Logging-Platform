import axios from 'axios';

function resolveBase(): string {
  const raw = (process.env.REACT_APP_API_BASE as string | undefined)?.trim();
  if (!raw) return '/api/v1';

  // Accept absolute URLs or absolute paths as-is
  if (raw.startsWith('http://') || raw.startsWith('https://') || raw.startsWith('/')) return raw;

  // If someone set only a port like ":3001/api/v1", prefix with current host
  if (raw.startsWith(':')) {
    const { protocol, hostname } = window.location;
    return `${protocol}//${hostname}${raw}`;
  }

  // Ensure leading slash if it looks like a relative path (e.g., "api/v1")
  return raw.startsWith('/') ? raw : `/${raw}`;
}

export const API_BASE = resolveBase();

// Helpful debug log so the full URL is obvious in the console
// eslint-disable-next-line no-console
console.log('[API] Base URL =', API_BASE);

export const api = axios.create({ baseURL: API_BASE });

// Trace requests to make it easy to see the final URL in dev tools
api.interceptors.request.use((config) => {
  try {
    const method = (config.method || 'GET').toUpperCase();
    const base = config.baseURL || '';
    const path = typeof config.url === 'string' ? config.url : '';
    // eslint-disable-next-line no-console
    console.debug(`[API] ${method} ${base}${path}`);
  } catch { /* ignore */ }
  return config;
});

// In development, auto-detect common bases to self-heal misaligned context roots
if (process.env.NODE_ENV === 'development') {
  const uniq = (arr: string[]) => Array.from(new Set(arr));
  const candidates = uniq([
    API_BASE,
    // Try common context-root variant if not already included
    '/custody-connect/api/v1',
    '/api/v1',
  ]);

  (async () => {
    for (const base of candidates) {
      try {
        // Quick probe; if it returns 200, adopt this base
        const url = base.endsWith('/health') ? base : `${base}/health`;
        const res = await axios.get(url, { timeout: 1500 });
        if (res.status >= 200 && res.status < 300) {
          if (api.defaults.baseURL !== base) {
            api.defaults.baseURL = base;
            // eslint-disable-next-line no-console
            console.log('[API] Probed base OK =', base);
          }
          break;
        }
      } catch {
        // try next candidate
      }
    }
  })();
}

export const downloadBlob = (data: Blob, filename: string) => {
  const url = URL.createObjectURL(data);
  const a = document.createElement('a'); a.href = url; a.download = filename; a.click(); URL.revokeObjectURL(url);
};
