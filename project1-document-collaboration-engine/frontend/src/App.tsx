import { useEffect, useState } from "react";
import { createDocument, getSummary, listDocuments, type Doc } from "./api";

// Demo credentials wired to the backend's in-memory users (see SecurityConfig).
const USER = "om";
const PASS = "om-password";

export default function App() {
  const [docs, setDocs] = useState<Doc[]>([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [summary, setSummary] = useState("");
  const [error, setError] = useState("");

  async function refresh() {
    try {
      setDocs(await listDocuments(USER, PASS));
    } catch (e) {
      setError(String(e));
    }
  }

  useEffect(() => {
    void refresh();
  }, []);

  async function onCreate() {
    await createDocument(USER, PASS, title, content);
    setTitle("");
    setContent("");
    await refresh();
  }

  return (
    <main style={{ fontFamily: "system-ui", maxWidth: 720, margin: "2rem auto" }}>
      <h1>Document Engine</h1>
      {error && <p style={{ color: "crimson" }}>{error}</p>}
      <section>
        <input placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
        <br />
        <textarea
          placeholder="Content"
          rows={5}
          cols={70}
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <br />
        <button onClick={() => void onCreate()}>Create document</button>
      </section>
      <ul>
        {docs.map((d) => (
          <li key={d.id}>
            <strong>{d.title}</strong> (v{d.version}, owner {d.owner}){" "}
            <button
              onClick={() => void getSummary(USER, PASS, d.id).then(setSummary)}
            >
              Summarize
            </button>
          </li>
        ))}
      </ul>
      {summary && (
        <section>
          <h2>Summary</h2>
          <p>{summary}</p>
        </section>
      )}
    </main>
  );
}
