package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.dto.IncomeDTO;
import com.baitaplon.moneymanagement.services.IncomeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IncomeController {
    IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO expense = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getIncomes() {
        List<IncomeDTO> incomes = incomeService.getAllIncomeForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeDTO> updateIncome(@RequestBody IncomeDTO incomeDTO, @PathVariable String id) {
        IncomeDTO response = incomeService.updateIncome(id, incomeDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable String id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
