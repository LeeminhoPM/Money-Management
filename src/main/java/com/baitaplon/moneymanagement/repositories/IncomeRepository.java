package com.baitaplon.moneymanagement.repositories;

import com.baitaplon.moneymanagement.entities.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, String> {
    //    SELECT * FROM tbl_incomes WHERE profile_id = ? ORDER BY date DESC
    List<IncomeEntity> findByProfileIdOrderByDateDesc(String profileId);

    //    SELECT * FROM tbl_incomes WHERE profile_id = ? ORDER BY date DESC LIMIT 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(String profileId);

    //    Custom query method
//    Có thể dùng tên class entity thay vì tên bảng trong database
//    @Param dùng để bind tới biến trong lệnh @Query
    @Query("SELECT SUM(e.amount) FROM IncomeEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") String profileId);

    //    SELECT * FROM tbl_incomes WHERE profile_id = ?1 AND DATE BETWEEN ?2 AND ?3 AND name LIKE %?4%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            String profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    //    SELECT * FROM tbl_incomes WHERE profile_id = ?1 AND DATE BETWEEN ?2 AND ?3
    List<IncomeEntity> findByProfileIdAndDateBetween(String profileId, LocalDate startDate, LocalDate endDate);
}
