package uz.fido.jobs.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class Job2 extends QuartzJobBean implements Job {

  @Override
  protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {
    JobDetail jobDetail = jobContext.getJobDetail();
    log.info("JOB2 executed...");
  }
}

