package com.baitaplon.moneymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;
    String type;
    String icon;
//    Tương tự như lazy loading trong asp.net FetchType.LAZY chỉ tải đối tượng khi gọi đến nó
    @ManyToOne(fetch = FetchType.LAZY)
//    Tên cột khóa ngoại
    @JoinColumn(name = "profile_id", nullable = false)
    ProfileEntity profile;
}
