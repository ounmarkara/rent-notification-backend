package com.rentnotification.job;

import com.rentnotification.entity.Tenant;
import com.rentnotification.repository.TenantRepository;
import com.rentnotification.service.FirebaseNotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        for (Tenant tenant : tenants) {
            try {
                // Send notification for testing, ignore due_date conditions
                notificationService.sendRentReminder(
                        tenant.getFcmToken(),
                        tenant.getName(),
                        tenant.getRentAmount(),
                        tenant.getDueDate().toString(),
                        "TEST_REMINDER"
                );
            } catch (Exception e) {
                System.err.println("Failed to send reminder to " + tenant.getName() + ": " + e.getMessage());
            }
        }
    }
}