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

    public void createCronExpression(ScheduleTransactionDTO scheduleTransactionDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(scheduleTransactionDTO.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy danh mục")
        );

        ScheduleTransactionEntity task = repository.save(ScheduleTransactionEntity.builder()
                .taskName(scheduleTransactionDTO.getTaskName())
                .cronExpression(scheduleTransactionDTO.getCronExpression())
                .icon(scheduleTransactionDTO.getIcon())
                .type(scheduleTransactionDTO.getType())
                .amount(scheduleTransactionDTO.getAmount())
                .name(scheduleTransactionDTO.getName())
                .date(LocalDate.now())
                .category(category)
                .profile(profile)
                .build());

        restartScheduleTasks(task);
    }

    public void updateCronExpression(String taskId, ScheduleTransactionDTO scheduleTransactionDTO) {
        ScheduleTransactionEntity task = repository.findById(taskId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy task")
        );
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(scheduleTransactionDTO.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy danh mục")
        );
        if (task.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật mục này");
        }

        task.setTaskName(scheduleTransactionDTO.getTaskName());
        task.setCronExpression(scheduleTransactionDTO.getCronExpression());

        task.setIcon(scheduleTransactionDTO.getIcon());
        task.setAmount(scheduleTransactionDTO.getAmount());
        task.setName(scheduleTransactionDTO.getName());
        task.setDate(LocalDate.now());
        task.setCategory(category);

        restartScheduleTasks(repository.save(task));
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
}
