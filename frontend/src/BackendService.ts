export type ShortenedUrl =
    {
        id: number;
        url: string;
        encodedUrl: string;
    }

export class BackendService {
    url: string;

    public constructor() {
        //this.url = import.meta.env.VITE_BACKEND_URL;
        this.url = "http://backend.info"
    }

    public async getShortenedUrls(): Promise<ShortenedUrl[]> {
        const response = await fetch(`${this.url}/api/shortened`);
        if (!response.ok) {
            throw new Error(`Error while getting the shortened: ${response.status}`);
        }

        return await response.json() as Promise<ShortenedUrl[]>
    }

    public getFullUrl(shortenedUrl: ShortenedUrl): string {
        return `${this.url}/${shortenedUrl.encodedUrl}`;
    }
}
