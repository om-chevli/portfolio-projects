// Shared TypeScript client for the API Platform (Project 2).
// The same client works from a web app, a mobile webview, or an Office add-in
// task pane — all of them are just HTTPS + JSON consumers of the public API.
// Internal endpoints additionally require the X-Api-Key header and must only
// be called from trusted (non-shipped) code, never from a client bundle.

export interface PublicItem {
  id: string;
  name: string;
  category: string;
  price: number;
  inStock: boolean;
}

const PUBLIC_BASE = "http://localhost:8082/api/public/v1/items";

export async function listPublicItems(category?: string): Promise<PublicItem[]> {
  const url = category ? `${PUBLIC_BASE}?category=${encodeURIComponent(category)}` : PUBLIC_BASE;
  const res = await fetch(url);
  if (!res.ok) throw new Error(`list failed: ${res.status}`);
  return res.json();
}

export async function getPublicItem(id: string): Promise<PublicItem> {
  const res = await fetch(`${PUBLIC_BASE}/${encodeURIComponent(id)}`);
  if (!res.ok) throw new Error(`get failed: ${res.status}`);
  return res.json();
}
