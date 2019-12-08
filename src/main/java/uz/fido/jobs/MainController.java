package uz.fido.jobs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.fido.jobs.Service.JobService;
import uz.fido.jobs.job.Job1;
import uz.fido.jobs.job.Job2;
import uz.fido.jobs.job.Job3;


import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Controller
public class MainController {

  @Autowired
  @Lazy
  SchedulerFactoryBean schedulerFactoryBean;

  @Autowired
  private ApplicationContext context;

  @Autowired
  @Lazy
  JobService jobService;

  //private Scheduler scheduler;
  // private Trigger trigger;
  //private JobDetail jobDetail;
  private boolean jobcreated = false;
  public static final String JOB_GROUP = "SampleGroup";

  private List<Row> list = new ArrayList<>();
  //private Map<Integer, JobDetail> map = new HashMap<>();

  public MainController(){
    list.add(new Row(1, "job 1", "Report collector", "/start/1", "/stop/1", Job1.class));
    list.add(new Row(2, "job 2", "Email service", "/start/2", "/stop/2", Job2.class));
    list.add(new Row(3, "job 3", "Sync service", "/start/3", "/stop/3", Job3.class));
    log.info("Constructor....");
  }

  @PostConstruct
  public void onInit(){
    log.info("Post Constructor....");
  }

  @RequestMapping("/start/{id}")
  public String start(@PathVariable Integer id){
    Integer target = getRowIndex(id);
    if (target == null) {
      return "redirect:/";
    }
    Row row = list.get(target);
    if (!jobService.isJobWithNamePresent(row.getName())) {
        jobcreated = jobService.scheduleCronJob(row.getName(), row.getAClass(), new Date(), "/1 * * ? * * *");
        if (jobcreated) log.info("Job created with name " + row.getName());
        else log.info("Error in job creation");
    }else{
        if(jobService.resumeJob(row.getName()))
          log.info("resumed....");
        else log.info("error in resuming...");
    }
    //scheduler.start();
    return "redirect:/";
  }


  @RequestMapping("/stop/{id}")
  public  String stop(@PathVariable Integer id){
    Integer target = getRowIndex(id);
    if (target == null) {
      return "redirect:/";
    }
    Row row = list.get(target);
    log.info("jobKey" + row.getName());
    if(jobService.pauseJob(row.getName())) log.info("paused..");
    else log.info("error in pausing");

    return "redirect:/";
  }

  @RequestMapping("/")
  public String index(Model model) {
    model.addAttribute("title", "Quartz test");
    model.addAttribute("jobs", list);
    return "index";
  }

  @Data
  @AllArgsConstructor
  private class Row {
    private int id;
    private String name;
    private String description;
    private String start;
    private String stop;
    private Class<? extends QuartzJobBean> aClass;
  }

  private int getRowIndex(int id) {
    Integer target = null;
    for (int index = 0; index < list.size(); index++) {
      Row row = list.get(index);
      if (row.getId() == id) {
        target = index;
      }
    }
    return target;
  }
}
