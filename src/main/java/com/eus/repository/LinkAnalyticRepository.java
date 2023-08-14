package com.eus.repository;

import com.eus.entity.LinkAnalytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkAnalyticRepository extends JpaRepository<LinkAnalytic, Long> {
}
