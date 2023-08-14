package com.eus.controller;

import com.eus.dto.LinkAnalyticRequest;
import com.eus.dto.URLMappingRequest;
import com.eus.dto.URLMappingResponse;
import com.eus.service.URLMappingService;
import com.eus.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class URLShortenerController {

    @Value("${application.domain}")
    private String domain;

    private final UserProfileService userProfileService;
    private final URLMappingService urlMappingService;

    @GetMapping("/v1")
    public String m1() {
//        userProfileService.saveSample();
        return "anas";
    }

    @PostMapping("/short_")
    public ResponseEntity<URLMappingResponse> createMapping(@Valid @RequestBody URLMappingRequest request) {
        URLMappingResponse mappingForUnregisteredUser = urlMappingService.createMappingForUnregisteredUser(request);
        if (mappingForUnregisteredUser == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.created(URI.create(domain + "/" + mappingForUnregisteredUser.getShortURL())).body(mappingForUnregisteredUser);
    }

    @PostMapping("/{shortURL}")
    public ResponseEntity<URLMappingResponse> getOriginalURL(@PathVariable String shortURL,
                                                             @RequestBody LinkAnalyticRequest linkAnalyticRequest) {
        URLMappingResponse originalURL = urlMappingService.getOriginalURL(shortURL, linkAnalyticRequest);
        if (originalURL == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(originalURL);
    }
}
