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
    private readonly listShortened = "/api/shortened";
    private readonly shorten = "/api/shorten";
    private readonly redirect = "/api/shortened/{encodedUrl}";

    public async getShortenedUrls(): Promise<ShortenedUrl[]> {
        const response = await fetch(this.listShortened);
        if (!response.ok) {
            throw new Error(`Error while getting the shortened: ${response.status}`);
        }

        return await response.json() as Promise<ShortenedUrl[]>;
    }

    public getFullUrl(shortenedUrl: ShortenedUrl): string {
        return this.redirect.replace("{encodedUrl}", shortenedUrl.encodedUrl);
    }

    public async shortenUrl(shortenUrlRequest: ShortenUrlRequest): Promise<ShortenedUrl> {
        const response = await fetch(this.shorten, {
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
