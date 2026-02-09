declare global {
    interface Window {
        __APP_CONFIG__?: {
            BACKEND_URL?: string;
        };
    }
}

export type ShortenedUrl =
    {
        id: number;
        url: string;
        encodedUrl: string;
    }

export type ShortenUrlRequest =
    {
        url: string;
    }

export class BackendService {
    url: string;

    public constructor() {
        this.url = window.__APP_CONFIG__?.BACKEND_URL
            ?? import.meta.env.VITE_BACKEND_URL
            ?? "";
    }

    public async getShortenedUrls(): Promise<ShortenedUrl[]> {
        const response = await fetch(`${this.url}/api/shortened`);
        if (!response.ok) {
            throw new Error(`Error while getting the shortened: ${response.status}`);
        }

        return await response.json() as Promise<ShortenedUrl[]>;
    }

    public getFullUrl(shortenedUrl: ShortenedUrl): string {
        return `${this.url}/${shortenedUrl.encodedUrl}`;
    }

    public async shortenUrl(shortenUrlRequest: ShortenUrlRequest): Promise<ShortenedUrl> {
        const response = await fetch(`${this.url}/api/shorten`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(shortenUrlRequest),
        });
        if (!response.ok) {
            throw new Error(`Error while creating the shortened: ${response.status}`);
        }

        return await response.json() as Promise<ShortenedUrl>;
    }
}
