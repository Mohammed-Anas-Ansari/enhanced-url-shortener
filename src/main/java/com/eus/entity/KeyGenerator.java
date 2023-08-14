package com.eus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class KeyGenerator {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "keyGeneratorSequenceTable")
    @TableGenerator(
            name = "keyGeneratorSequenceTable",
            table = "sequence_table",
            pkColumnName = "SEQUENCE_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "KEY_GEN_SEQ",
            allocationSize = 1)
    private long id;
    @Column(unique = true)
    private String keyPrefix;
    @Column(columnDefinition = "boolean default false")
    private boolean isUsed;
    @CreationTimestamp
    @Column(columnDefinition = "DATETIME(3)")
    private LocalDateTime createdAt;
}
