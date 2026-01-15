package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.entities.ProfileEntity;
import com.baitaplon.moneymanagement.services.*;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    ExcelService excelService;
    IncomeService incomeService;
    ExpenseService expenseService;
    EmailService emailService;
    ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeIncomeToExcel(outputStream, incomeService.getCurrentMonthIncomeForCurrentUser());
        emailService.sendEmailWithAttachment(
                profile.getEmail(),
                "Báo cáo thu nhập tháng qua",
                "Bảng tính thu nhập tháng vừa qua của bạn",
                outputStream.toByteArray(),
                "ThuNhap.xlsx"
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeExpenseToExcel(outputStream, expenseService.getCurrentMonthExpenseForCurrentUser());
        emailService.sendEmailWithAttachment(
                profile.getEmail(),
                "Báo cáo chi tiêu tháng qua",
                "Bảng tính chi tiêu tháng vừa qua của bạn",
                outputStream.toByteArray(),
                "ChiTieu.xlsx"
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
