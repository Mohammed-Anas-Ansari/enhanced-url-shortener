package com.eus.service;

import com.eus.dto.LinkAnalyticRequest;
import com.eus.entity.LinkAnalytic;
import com.eus.repository.LinkAnalyticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkAnalyticService {

    private final LinkAnalyticRepository linkAnalyticRepository;

    public void createLinkAnalytic(String shortURL, LinkAnalyticRequest request) {
        LinkAnalytic linkAnalytic = LinkAnalytic.builder()
                .shortURL(shortURL)
                .deviceType(request.getDeviceType())
                .build();
        linkAnalyticRepository.save(linkAnalytic);
    }
}
