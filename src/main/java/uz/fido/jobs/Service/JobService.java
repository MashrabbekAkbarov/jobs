package uz.fido.jobs.Service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import uz.fido.jobs.MainController;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class JobService {

  @Autowired @Lazy private SchedulerFactoryBean schedulerFactoryBean;

  @Autowired private ApplicationContext context;

  @Autowired private JobUtil jobUtil;

  /** Schedule a job by jobName at given date. */
  public boolean scheduleCronJob(
      String jobName, Class<? extends QuartzJobBean> jobClass, Date date, String cronExpression) {
    System.out.println("Request received to scheduleJob");

    String jobKey = jobName;
    String groupKey = MainController.JOB_GROUP;
    String triggerKey = jobName;

    JobDetail jobDetail = jobUtil.createJob(jobClass, true, context, jobKey, groupKey);

    log.info("creating trigger for key :" + jobKey + " at date :" + date);
    Trigger cronTriggerBean = jobUtil.createCronTrigger(triggerKey, date, cronExpression);

    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
      log.info(
          "Job with key jobKey :"
              + jobKey
              + " and group :"
              + groupKey
              + " scheduled successfully for date :"
              + dt);
      return true;
    } catch (SchedulerException e) {
      log.info(
          "SchedulerException while scheduling job with key :"
              + jobKey
              + " message :"
              + e.getMessage());
      e.printStackTrace();
    }

    return false;
  }

  /** Delete the identified Job from the Scheduler - and any associated Triggers. */
  public boolean deleteJob(String jobName) {
    System.out.println("Request received for deleting job.");

    String jobKey = jobName;
    String groupKey = "SampleGroup";

    JobKey jkey = new JobKey(jobKey, groupKey);
    log.info("Parameters received for deleting job : jobKey :" + jobKey);

    try {
      boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
      log.info("Job with jobKey :" + jobKey + " deleted with status :" + status);
      return status;
    } catch (SchedulerException e) {
      log.info(
          "SchedulerException while deleting job with key :"
              + jobKey
              + " message :"
              + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /** Pause a job */
  public boolean pauseJob(String jobName) {
    log.info("Request received for pausing job.");

    String jobKey = jobName;
    String groupKey = "SampleGroup";
    JobKey jkey = new JobKey(jobKey, groupKey);
    log.info("Parameters received for pausing job : jobKey :" + jobKey + ", groupKey :" + groupKey);

    try {
      schedulerFactoryBean.getScheduler().pauseJob(jkey);
      log.info("Job with jobKey :" + jobKey + " paused succesfully.");
      return true;
    } catch (SchedulerException e) {
      log.info(
          "SchedulerException while pausing job with key :"
              + jobName
              + " message :"
              + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
  /** Resume paused job */
  public boolean resumeJob(String jobName) {
    System.out.println("Request received for resuming job.");

    String jobKey = jobName;
    String groupKey = MainController.JOB_GROUP;

    JobKey jKey = new JobKey(jobKey, groupKey);
    log.info("Parameters received for resuming job : jobKey :" + jobKey);
    try {
      schedulerFactoryBean.getScheduler().resumeJob(jKey);
      log.info("Job with jobKey :" + jobKey + " resumed succesfully.");
      return true;
    } catch (SchedulerException e) {
      log.info(
          "SchedulerException while resuming job with key :"
              + jobKey
              + " message :"
              + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public String getJobState(String jobName) {
    System.out.println("JobServiceImpl.getJobState()");

    try {
      String groupKey = MainController.JOB_GROUP;
      JobKey jobKey = new JobKey(jobName, groupKey);

      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);

      List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
      if (triggers != null && triggers.size() > 0) {
        for (Trigger trigger : triggers) {
          Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

          if (Trigger.TriggerState.PAUSED.equals(triggerState)) {
            return "PAUSED";
          } else if (Trigger.TriggerState.BLOCKED.equals(triggerState)) {
            return "BLOCKED";
          } else if (Trigger.TriggerState.COMPLETE.equals(triggerState)) {
            return "COMPLETE";
          } else if (Trigger.TriggerState.ERROR.equals(triggerState)) {
            return "ERROR";
          } else if (Trigger.TriggerState.NONE.equals(triggerState)) {
            return "NONE";
          } else if (Trigger.TriggerState.NORMAL.equals(triggerState)) {
            return "SCHEDULED";
          }
        }
      }
    } catch (SchedulerException e) {
      System.out.println(
          "SchedulerException while checking job with name and group exist:" + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  public boolean isJobWithNamePresent(String jobName) {
    try {
      String groupKey = MainController.JOB_GROUP;
      JobKey jobKey = new JobKey(jobName, groupKey);
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      if (scheduler.checkExists(jobKey)) {
        return true;
      }
    } catch (SchedulerException e) {
      System.out.println(
          "SchedulerException while checking job with name and group exist:" + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }
}
