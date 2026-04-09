package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.ScheduleTransactionDTO;
import com.baitaplon.moneymanagement.entities.CategoryEntity;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import com.baitaplon.moneymanagement.entities.ScheduleTransactionEntity;
import com.baitaplon.moneymanagement.repositories.CategoryRepository;
import com.baitaplon.moneymanagement.repositories.ScheduleTransactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleTransactionService {
    ScheduleTransactionRepository repository;
    CategoryRepository categoryRepository;
    ProfileService profileService;
    IncomeService incomeService;
    ExpenseService expenseService;
    ThreadPoolTaskScheduler taskScheduler;
    Map<String, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    public ScheduleTransactionDTO createCronExpression(ScheduleTransactionDTO scheduleTransactionDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(scheduleTransactionDTO.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy danh mục")
        );

        ScheduleTransactionEntity task = repository.save(toEntity(scheduleTransactionDTO, category, profile));
        restartScheduleTasks(task);
        return toDTO(task);
    }

    public List<ScheduleTransactionDTO> getAllScheduleTransactions() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ScheduleTransactionEntity> scheduleTransactionEntities = repository.findByProfileId(profile.getId());
        return scheduleTransactionEntities.stream().map(this::toDTO).toList();
    }

    public ScheduleTransactionDTO updateCronExpression(String taskId, ScheduleTransactionDTO scheduleTransactionDTO) {
        ScheduleTransactionEntity task = repository.findById(taskId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy task")
        );
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(scheduleTransactionDTO.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy danh mục")
        );
        if (!task.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật mục này");
        }

        task.setTaskName(scheduleTransactionDTO.getTaskName());
        task.setCronExpression(scheduleTransactionDTO.getCronExpression());

        task.setIcon(scheduleTransactionDTO.getIcon());
        task.setAmount(scheduleTransactionDTO.getAmount());
        task.setName(scheduleTransactionDTO.getName());
        task.setDate(LocalDate.now());
        task.setCategory(category);

        ScheduleTransactionEntity updatedTask = repository.save(task);

        restartScheduleTasks(updatedTask);
        return toDTO(updatedTask);
    }

    public void deleteCronExpression(String taskId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ScheduleTransactionEntity task = repository.findById(taskId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy task")
        );
        if (!task.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa mục này");
        }
        repository.delete(task);

        ScheduledFuture<?> oldTask = scheduledFutures.get(task.getId());
        if (oldTask != null) {
            oldTask.cancel(false);
        }
    }

    private void restartScheduleTasks(ScheduleTransactionEntity task) {
        ScheduledFuture<?> oldTask = scheduledFutures.get(task.getId());
        if (oldTask != null) {
            oldTask.cancel(false);
        }

        ScheduledFuture<?> newTask = taskScheduler.schedule(
                () -> {
                    if (task.getType().equals("income")) {
                        incomeService.addIncome(task);
                    } else if (task.getType().equals("expense")) {
                        expenseService.addExpense(task);
                    }
                }, new CronTrigger(task.getCronExpression())
        );
        scheduledFutures.put(task.getId(), newTask);
    }

    private ScheduleTransactionDTO toDTO(ScheduleTransactionEntity scheduleTransactionEntity) {
        return ScheduleTransactionDTO.builder()
                .taskName(scheduleTransactionEntity.getTaskName())
                .cronExpression(scheduleTransactionEntity.getCronExpression())
                .icon(scheduleTransactionEntity.getIcon())
                .amount(scheduleTransactionEntity.getAmount())
                .name(scheduleTransactionEntity.getName())
                .type(scheduleTransactionEntity.getType())
                .categoryId(scheduleTransactionEntity.getCategory().getId())
                .userId(scheduleTransactionEntity.getProfile().getId())
                .build();
    }

    private ScheduleTransactionEntity toEntity(ScheduleTransactionDTO scheduleTransactionDTO, CategoryEntity category, ProfileEntity profile) {
        return ScheduleTransactionEntity.builder()
                .taskName(scheduleTransactionDTO.getTaskName())
                .cronExpression(scheduleTransactionDTO.getCronExpression())
                .icon(scheduleTransactionDTO.getIcon())
                .type(scheduleTransactionDTO.getType())
                .amount(scheduleTransactionDTO.getAmount())
                .name(scheduleTransactionDTO.getName())
                .date(LocalDate.now())
                .category(category)
                .profile(profile)
                .build();
    }
}
