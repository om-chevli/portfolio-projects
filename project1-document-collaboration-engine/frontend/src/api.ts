// Typed API client for the Document Engine backend (Project 1).
// The backend uses HTTP Basic auth in this demo build.

export interface Doc {
  id: string;
  title: string;
  content: string;
  owner: string;
  sharedWith: string[];
  tags: string[];
  version: number;
}

const BASE = "http://localhost:8081/api/documents";

function authHeader(username: string, password: string): string {
  return "Basic " + btoa(`${username}:${password}`);
}

export async function listDocuments(username: string, password: string): Promise<Doc[]> {
  const res = await fetch(BASE, { headers: { Authorization: authHeader(username, password) } });
  if (!res.ok) throw new Error(`list failed: ${res.status}`);
  return res.json();
}

export async function createDocument(
  username: string,
  password: string,
  title: string,
  content: string
): Promise<Doc> {
  const res = await fetch(BASE, {
    method: "POST",
    headers: { Authorization: authHeader(username, password), "Content-Type": "application/json" },
    body: JSON.stringify({ title, content }),
  });
  if (!res.ok) throw new Error(`create failed: ${res.status}`);
  return res.json();
}

export async function getSummary(
  username: string,
  password: string,
  id: string
): Promise<string> {
  const res = await fetch(`${BASE}/${id}/summary`, {
    headers: { Authorization: authHeader(username, password) },
  });
  if (!res.ok) throw new Error(`summary failed: ${res.status}`);
  const data = (await res.json()) as { summary: string };
  return data.summary;
}
