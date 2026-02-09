import type React from 'react';
import {useEffect, useState} from 'react';
import {BackendService, type ShortenedUrl} from './BackendService';
import './App.css';

const service = new BackendService();

function App() {
    const [urls, setUrls] = useState<ShortenedUrl[]>([]);
    const [loading, setLoading] = useState(true);

    const [newUrl, setNewUrl] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        service.getShortenedUrls()
            .then(setUrls)
            .catch(console.error)
            .finally(() => setLoading(false));
    }, []);

    const handleCreate = async () => {
        const trimmed = newUrl.trim();
        if (!trimmed) return;

        setSubmitting(true);
        setError(null);

        try {
            const created = await service.shortenUrl({url: trimmed});

            setUrls(prev => {
                const hasId = prev.some(u => u.id === created.id);
                const hasEncoded = prev.some(u => u.encodedUrl === created.encodedUrl);
                if (hasId || hasEncoded) return prev;
                return [...prev, created];
            });

            setNewUrl('');
        } catch (e) {
            console.error(e);
            setError(e instanceof Error ? e.message : 'Erreur inconnue');
        } finally {
            setSubmitting(false);
        }
    };

    const handleSubmit = (e: React.SyntheticEvent) => {
        e.preventDefault();
        void handleCreate();
    };

    return (
        <div className="container">
            <h1>Mes URLs Raccourcies</h1>

            <form onSubmit={handleSubmit} style={{display: 'flex', gap: 8, marginBottom: 16}}>
                <input
                    type="url"
                    placeholder="Colle une URL à raccourcir…"
                    value={newUrl}
                    onChange={(e) => setNewUrl(e.target.value)}
                    style={{flex: 1}}
                />
                <button
                    type="submit"
                    disabled={submitting || !newUrl.trim()}
                    title={!newUrl.trim() ? 'Entre une URL' : undefined}
                >
                    {submitting ? 'Ajout…' : 'Raccourcir'}
                </button>
            </form>

            {error && <p style={{marginTop: -8, marginBottom: 16}}>{error}</p>}

            <div className="table-wrapper">
                {loading ? (
                    <p>Chargement...</p>
                ) : (
                    <table className="url-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Original</th>
                            <th>Raccourci (Cliquable)</th>
                        </tr>
                        </thead>
                        <tbody>
                        {urls.map((item) => (
                            <tr key={item.id}>
                                <td>{item.id}</td>
                                <td className="truncate">{item.url}</td>
                                <td>
                                    <a href={service.getFullUrl(item)} target="_blank" rel="noreferrer">
                                        {item.encodedUrl}
                                    </a>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}

export default App;
