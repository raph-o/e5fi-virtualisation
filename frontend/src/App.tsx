import {useEffect, useState} from 'react';
import {BackendService, type ShortenedUrl} from './BackendService';
import './App.css';

const service = new BackendService();

function App() {
    const [urls, setUrls] = useState<ShortenedUrl[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        service.getShortenedUrls()
            .then(setUrls)
            .catch(console.error)
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="container">
            <h1>Mes URLs Raccourcies</h1>

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