package com.eus.service;

import com.eus.dto.LinkAnalyticRequest;
import com.eus.dto.URLMappingRequest;
import com.eus.dto.URLMappingResponse;
import com.eus.entity.URLMapping;
import com.eus.enums.ExpirationType;
import com.eus.enums.StatusType;
import com.eus.exception.ResourceNotFoundException;
import com.eus.redis.enums.RedisEntry;
import com.eus.redis.service.RedisQueueService;
import com.eus.repository.URLMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            return null;
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

    // TODO: add @Transactional
    public URLMappingResponse getOriginalURL(String shortURL, LinkAnalyticRequest linkAnalyticRequest) {
        Optional<URLMapping> optionalURLMapping = urlMappingRepository.findByShortURL(shortURL);
        if (optionalURLMapping.isEmpty()) {
            log.error("Short URL not found!");
            throw new ResourceNotFoundException("Short URL not found!");
        }
        URLMapping urlMapping = optionalURLMapping.get();
        if (urlMapping.getStatusType() == StatusType.INACTIVE) {
            // TODO: throw 404
        } else if (urlMapping.getStatusType() == StatusType.SUSPENDED) {
            log.warn("Suspended short URL was attempted to be visited!");
            throw new ResourceNotFoundException("Suspended short URL was attempted to be visited!");
        }
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);
        linkAnalyticService.createLinkAnalytic(shortURL, linkAnalyticRequest);
        // TODO: update the expiry
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
                    urlMapping.setDeleted(true);
                    urlMapping.setExpiryDate(LocalDateTime.now(ZoneOffset.UTC));
                    urlMappingRepository.save(urlMapping);
                });
    }

    private void updateExpiry(URLMapping urlMapping) {
        // TODO: update clickCount
        if (urlMapping.getExpiryType() == ExpirationType.NUMBER_OF_CLICKS) {
            // FIXME: add validation to set expiryValue above 0
            if (urlMapping.getExpiryValue() <= urlMapping.getExpiryValueUsed()) {
                urlMapping.setExpiryValueUsed(urlMapping.getExpiryValueUsed() + 1);
            } else if (urlMapping.getExpiryDate() == null) {
                urlMapping.setExpiryDate(LocalDateTime.now(ZoneOffset.UTC));
                urlMapping.setStatusType(StatusType.INACTIVE);
            } else {
                // TODO: throw 404
            }
        }
        // do nothing for NUMBER_OF_DAYS and NONE type
        // TODO: NUMBER_OF_DAYS expiryValueUsed must be updated along with expiry check through cron at 12:10 am UTC
//        else {
//            LocalDate createdAt = urlMapping.getCreatedAt().toLocalDate();
//            LocalDate now = LocalDateTime.now(ZoneOffset.UTC).toLocalDate();
//            long daysUsed = ChronoUnit.DAYS.between(createdAt.plusDays(urlMapping.getExpiryValueUsed()), now);
//        }
        urlMappingRepository.save(urlMapping);
    }
}
