package com.baitaplon.moneymanagement.services;

import com.baitaplon.moneymanagement.dto.ExpenseDTO;
import com.baitaplon.moneymanagement.entities.ProfileEntity;
import com.baitaplon.moneymanagement.repositories.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {
    ProfileRepository profileRepository;
    EmailService emailService;
    ExpenseService expenseService;

    @NonFinal
    @Value("${money-management.frontend.url}")
    String frontEndUrl;

//    Lên lịch gửi email vào mỗi 22h mỗi ngày với múi giờ (Asia/Ho_Chi_Minh) GMT+7
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyIncomeExpenseReminder() {
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Xin chào " + profile.getFullName() + "<br><br>"
                    + "Đây là lời nhắc để bạn thêm các khoản thu chi ngày hôm nay<br>"
                    + "<a href=" + frontEndUrl
                    + " style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'>"
                    + "Đi đến Trợ lý tài chính</a>"
                    + "<br><br>Xin cảm ơn vì đã lựa chọn dịch vụ của chúng tôi,<br>Trợ lý tài chính";
            emailService.sendEmail(profile.getEmail(), "Nhắc nhở hằng ngày: Thêm chi tiêu của bạn", body);
        }
    }

//    Lên lịch gửi email vào mỗi 23h mỗi ngày với múi giờ (Asia/Ho_Chi_Minh) GMT+7
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyExpenseSummary() {
        log.info("Sending Daily Expense Summary");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> expenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            if (!expenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%'>");
                table.append("<tr style='background-color:#f2f2f2;'>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>No</th>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>Tên</th>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>Trị giá</th>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>Danh mục</th>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>Ngày</th>"
                + "</tr>");
                int i = 1;
                for (ExpenseDTO expense : expenses) {
                    table.append("<tr style='background-color:#f2f2f2;'>" +
                            "<th style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</th>")
                            .append("<th style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</th>")
                            .append("<th style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</th>")
                            .append("<th style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A").append("</th>")
                            .append("<th style='border:1px solid #ddd;padding:8px;'>").append(expense.getDate()).append("</th>")
                            .append("</tr>");
                }
                table.append("</table>");
                String body = "Xin chào " + profile.getFullName() + "<br><br>Đây là tổng hợp chi tiêu của bạn trong ngày hôm nay<br><br>"
                        + table + "<br><br>Xin cảm ơn vì đã lựa chọn dịch vụ của chúng tôi,<br>Trợ lý tài chính";
                emailService.sendEmail(profile.getEmail(), "Tổng hợp chi tiêu hàng ngày của bạn", body);
            }
        }
        log.info("Đã gửi email xong");
    }
}
