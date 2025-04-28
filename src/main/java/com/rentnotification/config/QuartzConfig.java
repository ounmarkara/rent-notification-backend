package com.rentnotification.config;

import com.rentnotification.job.RentReminderJob;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail rentReminderJobDetail() {
        return JobBuilder.newJob(RentReminderJob.class)
                .withIdentity("rentReminderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger rentReminderTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(rentReminderJobDetail())
                .withIdentity("rentReminderTrigger")
                .startAt(new Date(System.currentTimeMillis() + 10_000)) // 10 seconds from now
                .build();
    }

    @Bean
    public JobFactory jobFactory() {
        return new AutoWiringSpringBeanJobFactory();
    }
}