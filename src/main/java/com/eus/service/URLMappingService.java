package com.eus.service;

import com.eus.dto.LinkAnalyticRequest;
import com.eus.dto.URLMappingRequest;
import com.eus.dto.URLMappingResponse;
import com.eus.entity.URLMapping;
import com.eus.enums.ExpirationType;
import com.eus.enums.StatusType;
import com.eus.exception.ResourceNotFoundException;
import com.eus.exception.ServiceUnavailableException;
import com.eus.redis.enums.RedisEntry;
import com.eus.redis.service.RedisQueueService;
import com.eus.repository.URLMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class URLMappingService {

    @Value("${application.user.unregistered.email")
    private String unregisteredUserEmail;
    @Value("${application.user.unregistered.expiration-days")
    private int unregisteredUserExpirationDays;

    private final RedisQueueService redisQueueService;
    private final URLMappingRepository urlMappingRepository;
    private final LinkAnalyticService linkAnalyticService;

    public URLMappingResponse createMappingForUnregisteredUser(URLMappingRequest request) {
        String shortURL = getShortURL();
        if (shortURL == null) {
            log.error("Failed to get short URL for unregistered user!");
            throw new ServiceUnavailableException("Please try again later!");
        }
        URLMapping urlMapping = URLMapping.builder()
                .shortURL(shortURL)
                .longURL(request.getLongURL())
                .email(unregisteredUserEmail)
                .expiryType(ExpirationType.NUMBER_OF_DAYS)
                .expiryValue(unregisteredUserExpirationDays)
                .statusType(StatusType.ACTIVE)
                .clickCount(1)
                .build();
        urlMappingRepository.save(urlMapping);
        return URLMappingResponse.builder()
                .longURL(urlMapping.getLongURL())
                .shortURL(urlMapping.getShortURL())
                .build();
    }

    @Transactional
    public URLMappingResponse getOriginalURL(String shortURL, LinkAnalyticRequest linkAnalyticRequest) {
        Optional<URLMapping> optionalURLMapping = urlMappingRepository.findByShortURL(shortURL);
        if (optionalURLMapping.isEmpty()) {
            log.error("Short URL not found!");
            throw new ResourceNotFoundException("Short URL not found!");
        }
        URLMapping urlMapping = optionalURLMapping.get();
        if (urlMapping.getStatusType() == StatusType.INACTIVE) {
            log.warn("The short URL is inactive!");
            throw new ResourceNotFoundException("The short URL is inactive!");
        } else if (urlMapping.getStatusType() == StatusType.SUSPENDED) {
            log.warn("Suspended short URL was attempted to be visited!");
            throw new ResourceNotFoundException("Suspended short URL was attempted to be visited!");
        }
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);
        linkAnalyticService.createLinkAnalytic(shortURL, linkAnalyticRequest);
        updateExpiry(urlMapping);
        return URLMappingResponse.builder()
                .longURL(urlMapping.getLongURL())
                .build();
    }

    private String getShortURL() {
        String shortURL = null;
        for (int i = 0; i < 3; i++) {
            shortURL = redisQueueService.popFromQueueHead(RedisEntry.GENERATED_KEY_QUEUE);
            Optional<URLMapping> optionalURLMapping = urlMappingRepository.findByShortURL(shortURL);
            if (optionalURLMapping.isEmpty()) {
                return shortURL;
            }
        }
        return shortURL;
    }

    public void deleteAllExpiredURL() {
        List<URLMapping> urlMappings = urlMappingRepository.findAllByDeletedAndExpiryType(false, ExpirationType.NUMBER_OF_DAYS);
        LocalDate now = LocalDateTime.now(ZoneOffset.UTC).toLocalDate();
        urlMappings.stream()
                .filter(urlMapping -> ChronoUnit.DAYS.between(now, urlMapping.getCreatedAt()) <= 0)
                .forEach(urlMapping -> {
                    if (ChronoUnit.DAYS.between(now, urlMapping.getCreatedAt()) <= 0) {
                        urlMapping.setDeleted(true);
                        urlMapping.setExpiryDate(LocalDateTime.now(ZoneOffset.UTC));
                    } else {
                        urlMapping.setExpiryValueUsed(urlMapping.getExpiryValueUsed() + 1);
                    }
                    urlMappingRepository.save(urlMapping);
                });
    }

    private void updateExpiry(URLMapping urlMapping) {
        if (urlMapping.getExpiryType() == ExpirationType.NUMBER_OF_CLICKS) {
            // FIXME: add validation to set expiryValue above 0 for NUMBER_OF_CLICKS and DAYS
            urlMapping.setExpiryValueUsed(urlMapping.getExpiryValueUsed() + 1);
            if (urlMapping.getExpiryValue() == urlMapping.getExpiryValueUsed()) {
                urlMapping.setExpiryDate(LocalDateTime.now(ZoneOffset.UTC));
                urlMapping.setStatusType(StatusType.INACTIVE);
            }
            urlMappingRepository.save(urlMapping);
        }
    }
}
