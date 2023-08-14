package com.eus.dto;

import com.eus.enums.ExpirationType;
import com.eus.enums.StatusType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class URLMappingRequest {
    private long id;
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid Email format")
    private long email;
    @NotBlank(message = "Long URL must not be blank")
    @URL(message = "Invalid URL format")
    private String longURL;
    @Length(max = 256, message = "Short URL length must be less than 256 characters")
    private String shortURL;    // must be pass when customSlugPresent
    private boolean customSlugPresent;
    private boolean deleted;
    private ExpirationType expiryType;
    private String expiryValue;
    private StatusType statusType;
}
