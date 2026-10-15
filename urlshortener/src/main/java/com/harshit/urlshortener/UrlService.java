
package com.harshit.urlshortener;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String saveUrl(String originalUrl) {
       
        String shortCode = generateShortCode();
         Url url = new Url();
        // Set entity fields
        url.setShortCode(shortCode);
        url.setOriginalUrl(originalUrl);

        // Save to PostgreSQL
        urlRepository.save(url);
        return shortCode;
    }

    public String getUrl(String shortCode) {

        Optional<Url> result = urlRepository.findByShortCode(shortCode);
        if (result.isEmpty()) {
            return null;
        }

        Url url = result.get();
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    private String generateShortCode() {
        String shortCode = UUID.randomUUID().toString().substring(0, 6);
        while (urlRepository.existsByShortCode(shortCode)) {
            shortCode = UUID.randomUUID().toString().substring(0, 6);
        }
        return shortCode;
    }
}
