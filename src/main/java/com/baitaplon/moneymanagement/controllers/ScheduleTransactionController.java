package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.dto.ScheduleTransactionDTO;
import com.baitaplon.moneymanagement.services.ScheduleTransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleTransactionController {
    ScheduleTransactionService scheduleTransactionService;

    @PostMapping
    public ResponseEntity<String> addScheduleTransaction(@RequestBody ScheduleTransactionDTO scheduleTransactionDTO) {
        scheduleTransactionService.createCronExpression(scheduleTransactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đặt lịch thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateScheduleTransaction(@RequestBody ScheduleTransactionDTO scheduleTransactionDTO, @PathVariable String id) {
        scheduleTransactionService.updateCronExpression(id, scheduleTransactionDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Cập nhật đặt lịch thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleTransaction(@PathVariable String id) {
        scheduleTransactionService.deleteCronExpression(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
