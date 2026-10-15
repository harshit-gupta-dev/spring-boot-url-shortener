package com.harshit.urlshortener;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class HelloController {

    private final UrlService urlService;

    public HelloController(UrlService urlService) {
        this.urlService = urlService;
    }
     
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from URL Shortener";
    }

    @PostMapping("/urls")
    public ResponseEntity<Map<String, String>> getURLS(
            @RequestBody Map<String, String> body) {
        
        String url = body.get("originalUrl");
    
        // Validate input
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "originalUrl is required"));
        }

        String shortCode = urlService.saveUrl(url);
        String shortUrl = "http://localhost:8080/" + shortCode;

        // Create response
        Map<String, String> response = Map.of(
            "shortCode", shortCode,
            "shortUrl", shortUrl,
            "originalUrl", url
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getUrl(shortCode);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(302)
        .header("Location", originalUrl)
        .build();
    }
}