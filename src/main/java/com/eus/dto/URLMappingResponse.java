package com.eus.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class URLMappingResponse {
    private long id;
    private long email;
    private String longURL;
    private String shortURL;
    private boolean customSlugPresent;
    private boolean deleted;
}
