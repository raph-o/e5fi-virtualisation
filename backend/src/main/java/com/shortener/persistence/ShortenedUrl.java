package com.shortener.persistence;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ShortenedUrl implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 2048)
  private String url;

  @Column(nullable = false)
  private String encodedUrl;

  public ShortenedUrl(String url, String encodedUrl) {
    this.url = url;
    this.encodedUrl = encodedUrl;
  }
}
