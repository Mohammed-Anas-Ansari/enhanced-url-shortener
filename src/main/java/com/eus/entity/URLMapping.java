package com.eus.entity;

import com.eus.enums.ExpirationType;
import com.eus.enums.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class URLMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "URLMappingSequenceTable")
    @TableGenerator(
            name = "URLMappingSequenceTable",
            table = "sequence_table",
            pkColumnName = "SEQUENCE_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "URL_MAPPING_SEQ",
            allocationSize = 1)
    private long id;
    private String email;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String longURL;
    @Column(nullable = false)
    private String shortURL;    // FIXME: add validation of limit up to 255 chars
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean customSlugPresent;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted;
    @CreationTimestamp
    @Column(columnDefinition = "DATETIME(3)")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME(3)")
    private LocalDateTime updatedAt;
    @Enumerated
    private ExpirationType expiryType;
    private int expiryValue;
    private int expiryValueUsed;
    private LocalDateTime expiryDate;   // set it first. If expired, set the deleted to true
    @Enumerated
    private StatusType statusType;   // if an active link meets its expiry date, it'll be marked as deleted with its expiryDate
    private long clickCount;
}
