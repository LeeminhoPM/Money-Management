package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.IncomeDTO;
import com.baitaplon.moneymanagement.entities.CategoryEntity;
import com.baitaplon.moneymanagement.entities.IncomeEntity;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import com.baitaplon.moneymanagement.repositories.CategoryRepository;
import com.baitaplon.moneymanagement.repositories.IncomeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IncomeService {
    ProfileService profileService;
    CategoryRepository categoryRepository;
    IncomeRepository incomeRepository;

    //    Service method
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId()).orElseThrow(() ->
                new RuntimeException("Danh mục không tồn tại")
        );

        IncomeEntity income = toEntity(incomeDTO, profile, category);
        return toDTO(incomeRepository.save(income));
    }

    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> expenses = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return expenses.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(String incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity income = incomeRepository.findById(incomeId).orElseThrow(() ->
                new RuntimeException("Không tìm thấy khoản chi tiêu")
        );
        if (!income.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa mục này");
        }
        incomeRepository.delete(income);
    }

    //    Helper method
    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .category(categoryEntity)
                .profile(profileEntity)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                .categoryName(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getName() : null)
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
