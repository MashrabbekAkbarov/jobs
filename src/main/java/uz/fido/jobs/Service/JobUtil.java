package uz.fido.jobs.Service;

import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JobUtil {
    private Trigger trigger;

    public JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                                         ApplicationContext context, String jobName, String jobGroup){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);

        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("myKey", "myValue");
        factoryBean.setJobDataMap(jobDataMap);
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();

        /*
        JobDetail jobDetail =
                JobBuilder.newJob(jobClass)
                        .usingJobData(jobName, jobGroup)
                        .storeDurably(isDurable)
                        .requestRecovery(true)
                        .build();

        return jobDetail;
        */
    }
    public Trigger createCronTrigger(String triggerName, Date startTime, String cronExpression){
        trigger =
                TriggerBuilder.newTrigger()
                        .withIdentity(triggerName)
                        .startAt(startTime)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();
        return trigger;
    }

}
