package com.shortener.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {

  boolean existsByEncodedUrl(String encodedUrl);
  Optional<ShortenedUrl> findByEncodedUrl(String encodedUrl);
}
