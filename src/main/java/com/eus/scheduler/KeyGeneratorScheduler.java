package com.eus.scheduler;

import com.eus.service.KeyGeneratorService;
import com.eus.service.URLMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class KeyGeneratorScheduler {

    private final KeyGeneratorService keyGeneratorService;
    private final URLMappingService urlMappingService;

    @Scheduled(fixedRateString = "${application.scheduler.threshold-check}", initialDelayString = "${application.scheduler.threshold-check}")
    public void generateKey() {
        log.info("generateKey Scheduler started...");
        keyGeneratorService.generateKeyOnQueueBeingUnderThreshold();
        log.info("generateKey Scheduler completed...");
    }

    // TODO: create scheduler (either here or in a separate expiration class) for disabling expired short_url

    @Scheduled(cron = "0 5 0 * * *", zone = "UTC")
    public void deleteAllExpiredURL() {
        log.info("deleteAllExpiredURL Scheduler started...");
        urlMappingService.deleteAllExpiredURL();
        log.info("deleteAllExpiredURL Scheduler completed...");
    }
}
