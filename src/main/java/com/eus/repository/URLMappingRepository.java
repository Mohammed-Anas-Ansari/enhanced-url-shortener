package com.eus.repository;

import com.eus.entity.URLMapping;
import com.eus.enums.ExpirationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface URLMappingRepository extends JpaRepository<URLMapping, Long> {

    Optional<URLMapping> findByShortURL(String shortURL);
    List<URLMapping> findAllByDeletedAndExpiryType(boolean isDeleted, ExpirationType expirationType);
}
