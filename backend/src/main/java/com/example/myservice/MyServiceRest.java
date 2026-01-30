package com.example.myservice;

import com.example.myservice.persistence.ShortenedUrl;
import com.example.myservice.persistence.ShortenedUrlRepository;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyServiceRest {

  private static final String BASE62 =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  @Autowired private ShortenedUrlRepository shortenedUrlRepository;

  public record ShortenRequest(String url) {}

  @SneakyThrows
  @PostMapping("/api/shorten")
  public ResponseEntity<String> createEncodedUrl(@RequestBody ShortenRequest shortenRequest) {
    String url = shortenRequest.url;
    String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
    String encodedUrl = encodedUrl(decodedUrl);

    if (!shortenedUrlRepository.existsByEncodedUrl(encodedUrl)) {
      shortenedUrlRepository.save(new ShortenedUrl(decodedUrl, encodedUrl));
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(encodedUrl);
  }

  @GetMapping("/api/shortened")
  public ResponseEntity<List<ShortenedUrl>> listShortenedUrls() {
    return ResponseEntity.status(HttpStatus.OK).body(shortenedUrlRepository.findAll());
  }

  @GetMapping("/{encodedUrl}")
  public ResponseEntity<?> redirectToUrl(@PathVariable String encodedUrl) {
    Optional<ShortenedUrl> shortenedUrl = shortenedUrlRepository.findByEncodedUrl(encodedUrl);
    return shortenedUrl
        .map(
            u ->
                ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(URI.create(u.getUrl()))
                    .build())
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @SneakyThrows
  private String encodedUrl(String url) {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    messageDigest.update(url.getBytes(StandardCharsets.UTF_8));
    byte[] digest = messageDigest.digest();

    byte[] firstBytes = new byte[6];
    System.arraycopy(digest, 0, firstBytes, 0, 6);

    BigInteger value = new BigInteger(1, firstBytes);

    if (value.equals(BigInteger.ZERO)) {
      return String.valueOf(BASE62.charAt(0));
    }

    StringBuilder encoded = new StringBuilder();
    BigInteger base = BigInteger.valueOf(62);

    while (value.compareTo(BigInteger.ZERO) > 0) {
      BigInteger[] divRem = value.divideAndRemainder(base);
      encoded.append(BASE62.charAt(divRem[1].intValue()));
      value = divRem[0];
    }

    return encoded.reverse().toString();
  }
}
