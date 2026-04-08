package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.dto.ScheduleTransactionDTO;
import com.baitaplon.moneymanagement.services.ScheduleTransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleTransactionController {
    ScheduleTransactionService scheduleTransactionService;

    @PostMapping
    public ResponseEntity<ScheduleTransactionDTO> addScheduleTransaction(@RequestBody ScheduleTransactionDTO scheduleTransactionDTO) {
        ScheduleTransactionDTO response = scheduleTransactionService.createCronExpression(scheduleTransactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleTransactionDTO>> getAllScheduleTransactions() {
        List<ScheduleTransactionDTO> response = scheduleTransactionService.getAllScheduleTransactions();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleTransactionDTO> updateScheduleTransaction(@RequestBody ScheduleTransactionDTO scheduleTransactionDTO, @PathVariable String id) {
        ScheduleTransactionDTO response = scheduleTransactionService.updateCronExpression(id, scheduleTransactionDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleTransaction(@PathVariable String id) {
        scheduleTransactionService.deleteCronExpression(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
