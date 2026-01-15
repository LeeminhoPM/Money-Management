package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.services.ExcelService;
import com.baitaplon.moneymanagement.services.ExpenseService;
import com.baitaplon.moneymanagement.services.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelController {
    ExcelService excelService;
    IncomeService incomeService;
    ExpenseService expenseService;

    @GetMapping("/download/income")
    public void downloadIncomeExcel(HttpServletResponse response) throws IOException {
//        Thông báo cho trình duyệt đây là file excel (.xlsx) tránh trả về HTML/JSON
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        Bắt buộc phải cài file có tên là ThuNhap.xlsx
        response.setHeader("Content-Disposition", "attachment; filename=ThuNhap.xlsx");
//        Ghi dữ liệu vào excel
        excelService.writeIncomeToExcel(response.getOutputStream(), incomeService.getCurrentMonthIncomeForCurrentUser());
    }

    @GetMapping("/download/expense")
    public void downloadExpenseExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.excel");
        response.setHeader("Content-Disposition", "attachment; filename=ChiTieu.xlsx");
        excelService.writeExpenseToExcel(response.getOutputStream(), expenseService.getCurrentMonthExpenseForCurrentUser());
    }
}
