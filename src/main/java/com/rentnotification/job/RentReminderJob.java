package com.rentnotification.job;

import com.rentnotification.entity.Tenant;
import com.rentnotification.repository.TenantRepository;
import com.rentnotification.service.FirebaseNotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RentReminderJob implements Job {
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private FirebaseNotificationService notificationService;

    @Override
    public void execute(JobExecutionContext context) {
        List<Tenant> tenants = tenantRepository.getTenantsForReminders();
        LocalDate today = LocalDate.now();

        for (Tenant tenant : tenants) {
            try {
                String reminderType;
                if (tenant.getDueDate().equals(today.plusDays(1))) {
                    reminderType = "BEFORE_DUE";
                } else if (tenant.getLastPaymentDate() == null && tenant.getDueDate().equals(today.minusDays(3))) {
                    reminderType = "THREE_DAYS_PAST_DUE";
                } else if (tenant.getLastPaymentDate() == null && tenant.getDueDate().equals(today.minusDays(7))) {
                    reminderType = "SEVEN_DAYS_PAST_DUE";
                } else {
                    continue;
                }

                notificationService.sendRentReminder(
                        tenant.getFcmToken(),
                        tenant.getName(),
                        tenant.getRentAmount(),
                        tenant.getDueDate().toString(),
                        reminderType
                );
            } catch (Exception e) {
                System.err.println("Failed to send reminder to " + tenant.getName() + ": " + e.getMessage());
            }
        }
    }
}
