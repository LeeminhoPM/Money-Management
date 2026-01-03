package com.baitaplon.moneymanagement.repositories;

import com.baitaplon.moneymanagement.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
//    SELECT * FROM tbl_categories WHERE profile_id = ?
    List<CategoryEntity> findByProfileId(String profileId);

    //    SELECT * FROM tbl_categories WHERE profile_id = ? AND id = ?
    Optional<CategoryEntity> findByIdAndProfileId(String id, String profileId);

    //    SELECT * FROM tbl_categories WHERE profile_id = ? AND type = ?
    List<CategoryEntity> findByProfileIdAndType(String profileId, String type);

    Boolean existsByProfileIdAndName(String profileId, String name);
}
