package com.eus.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkAnalyticRequest {
    private long clickCount;
    private String deviceType;
}
