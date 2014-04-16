package com.foresty.quartz;

import com.foresty.DomainConfig;
import com.foresty.quartz.job.EventRollingJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.TimeZone;

/**
 * Created by EveningSun on 14-3-29.
 */
@Configuration
@ComponentScan("com.foresty.quartz")
@Import(DomainConfig.class)
public class QuartzConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public JobDetailFactoryBean eventRollingJobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(EventRollingJob.class);
        jobDetailFactoryBean.setDurability(true);
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean eventRollingTriggerFactoryBean() {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setName("RollingEventTrigger");
        cronTriggerFactoryBean.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        cronTriggerFactoryBean.setJobDetail(eventRollingJobDetailFactoryBean().getObject());
//        cronTriggerFactoryBean.setCronExpression("0 0 4 * * ?");
        cronTriggerFactoryBean.setCronExpression("0 0/5 * * * ?");

        return cronTriggerFactoryBean;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setJobDetails(new JobDetail[]{eventRollingJobDetailFactoryBean().getObject()});
        schedulerFactoryBean.setTriggers(new Trigger[]{eventRollingTriggerFactoryBean().getObject()});

        return schedulerFactoryBean;
    }

    private static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
            ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }
}
