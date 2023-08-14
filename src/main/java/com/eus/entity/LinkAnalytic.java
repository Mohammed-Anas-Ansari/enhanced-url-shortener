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
public class LinkAnalytic {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LinkAnalyticSequenceTable")
    @TableGenerator(
            name = "LinkAnalyticSequenceTable",
            table = "sequence_table",
            pkColumnName = "SEQUENCE_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "LINK_ANALYTIC_SEQ",
            allocationSize = 1)
    private long id;
    @Column(nullable = false)
    private String shortURL;
    private String deviceType;
    @CreationTimestamp
    @Column(columnDefinition = "DATETIME(3)")
    private LocalDateTime createdAt;
    /*
    * TODO: URL being visited most by:
    *   days of the week
    *   date of the month
    *   month of the year
    *   time of the day
    * */
}
