package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.CategoryDTO;
import com.baitaplon.moneymanagement.entities.CategoryEntity;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import com.baitaplon.moneymanagement.repositories.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    ProfileService profileService;
    CategoryRepository categoryRepository;

//    Service
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByProfileIdAndName(profile.getId(), categoryDTO.getName())) {
            throw new RuntimeException("Danh mục có tên này đã tồn tại");
        }

        CategoryEntity categoryEntity = toEntity(categoryDTO, profile);

        return toDTO(categoryRepository.save(categoryEntity));
    }

    public List<CategoryDTO> getCategoriesForCurrentProfile() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentProfile(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileIdAndType(profile.getId(), type);
        return categories.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(String id, CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findByIdAndProfileId(id, profile.getId()).orElseThrow(
                () -> new RuntimeException("Danh mục không tìm thấy hoặc không được phép truy cập")
        );
        category.setName(categoryDTO.getName());
        category.setIcon(categoryDTO.getIcon());
        category.setType(categoryDTO.getType());

        return toDTO(categoryRepository.save(category));
    }

//    Helper (Dùng mapstruct cho nhanh)
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileEntity) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profileEntity)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
