package com.rentnotification.config;

import com.rentnotification.job.RentReminderJob;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetailFactoryBean rentReminderJobDetail() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(RentReminderJob.class);
        factory.setDurability(true);
        return factory;
    }

    @Bean
    public CronTriggerFactoryBean rentReminderTrigger(JobDetail rentReminderJobDetail) {
        CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
        factory.setJobDetail(rentReminderJobDetail);
        factory.setCronExpression("0 0 9 * * ?"); // Run daily at 9 AM
        return factory;
    }

    @Bean
    public AutoWiringSpringBeanJobFactory jobFactory() {
        return new AutoWiringSpringBeanJobFactory();
    }
}