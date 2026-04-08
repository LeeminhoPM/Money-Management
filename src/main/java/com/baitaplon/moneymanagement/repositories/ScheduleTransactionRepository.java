package com.baitaplon.moneymanagement.repositories;

import com.baitaplon.moneymanagement.entities.ScheduleTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleTransactionRepository extends JpaRepository<ScheduleTransactionEntity, String> {
    List<ScheduleTransactionEntity> findByProfileId(String profileId);
}
