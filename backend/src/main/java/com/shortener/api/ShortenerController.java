package com.shortener.api;

import com.shortener.domain.ShortenerService;
import com.shortener.persistence.ShortenedUrl;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class ShortenerController {

  @Autowired private ShortenerService shortenerService;

  private record ShortenRequest(String url) {}

  @SneakyThrows
  @PostMapping("/shorten")
  public ResponseEntity<ShortenedUrl> createEncodedUrl(@RequestBody ShortenRequest shortenRequest) {
    String url = shortenRequest.url;
    String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
    String encodedUrl = shortenerService.encodedUrl(decodedUrl);

    ShortenedUrl shortenedUrl = shortenerService.saveOrRetrieve(encodedUrl, decodedUrl);
    return ResponseEntity.status(HttpStatus.CREATED).body(shortenedUrl);
  }

  @GetMapping("/shortened")
  public ResponseEntity<List<ShortenedUrl>> listShortenedUrls() {
    return ResponseEntity.status(HttpStatus.OK).body(shortenerService.getShortenedUrls());
  }

  @GetMapping("/shortened/{encodedUrl}")
  public ResponseEntity<Void> redirectToUrl(@PathVariable String encodedUrl) {
    URI shortenedUrl = shortenerService.getShortenedUrl(encodedUrl);
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(shortenedUrl).build();
  }
}
