package com.baitaplon.moneymanagement.repositories;

import com.baitaplon.moneymanagement.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
//    Tìm email kể cả giá trị null
//    SELECT * FROM tbl_profiles WHERE email = ?
    Optional<ProfileEntity> findByEmail(String email);

    //    SELECT * FROM tbl_profiles WHERE activation_token = ?
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
