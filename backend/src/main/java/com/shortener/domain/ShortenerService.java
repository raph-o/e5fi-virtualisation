package com.shortener.domain;

import com.shortener.persistence.ShortenedUrl;
import com.shortener.persistence.ShortenedUrlRepository;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortenerService {

  private static final String BASE62 =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  @Autowired private ShortenedUrlRepository shortenedUrlRepository;

  @SneakyThrows
  public String encodedUrl(String url) {
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

  public ShortenedUrl saveOrRetrieve(String encodedUrl, String decodedUrl) {
    return shortenedUrlRepository
        .findByEncodedUrl(encodedUrl)
        .orElseGet(() -> shortenedUrlRepository.save(new ShortenedUrl(decodedUrl, encodedUrl)));
  }

  public List<ShortenedUrl> getShortenedUrls() {
    return shortenedUrlRepository.findAll();
  }

  public URI getShortenedUrl(String encodedUrl) {
    return shortenedUrlRepository
        .findByEncodedUrl(encodedUrl)
        .map(shortenedUrl -> URI.create(shortenedUrl.getUrl()))
        .orElseThrow();
  }
}
