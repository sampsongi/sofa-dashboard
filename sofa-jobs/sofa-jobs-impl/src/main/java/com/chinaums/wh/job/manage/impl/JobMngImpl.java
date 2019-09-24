package com.chinaums.wh.job.manage.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.manage.IJobMngFacade;
import com.chinaums.wh.job.manage.impl.core.cron.CronExpression;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobLog;
import com.chinaums.wh.job.manage.impl.core.route.ExecutorRouteStrategyEnum;
import com.chinaums.wh.job.manage.impl.core.thread.JobScheduleHelper;
import com.chinaums.wh.job.manage.impl.core.util.I18nUtil;
import com.chinaums.wh.job.manage.impl.core.util.JobGroupUtil;
import com.chinaums.wh.job.manage.impl.core.util.JobUtil;
import com.chinaums.wh.job.manage.impl.service.*;
import com.chinaums.wh.job.manage.impl.service.impl.XxlJobServiceImpl;
import com.chinaums.wh.job.model.*;
import com.chinaums.wh.job.type.ExecutorBlockStrategyEnum;
import com.chinaums.wh.job.type.GlueTypeEnum;
import com.chinaums.wh.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@SofaService(interfaceType = IJobMngFacade.class, uniqueId = "${service.unique.id}", bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class JobMngImpl implements IJobMngFacade {

    @Autowired
    private XxlJobRegistryService registryService;

    @Autowired
    private XxlJobInfoService jobInfoService;

    @Autowired
    private XxlJobGroupService jobGroupService;

    @Autowired
    private XxlJobLogService jobLogService;

    @Autowired
    private XxlJobLogGlueService jobLogGlueService;

    @Override
    public PageModel<Job> pageList(PageRequest request, Job ino) {
        return null;
    }

    public List<Job> search(String jobKey, String jobName, String jobGroup) {
        log.info("call search:{}",jobKey);
        return null;
    }

    public long count(String jobKey, String jobName, String jobGroup) {
        return 0;
    }

    @Override
    public Job findByJobId(Long jobId) {
        return null;
    }

    private boolean isNumeric(String str){
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Job findByJobKey(String jobKey) {
        log.info("call findByJobKey:{}",jobKey);
        Job job = new Job();
        job.setJobKey(jobKey);
        job.setJobGroup("group");
        return job;
    }

    @Override
    public ReturnT<String> add(Job job) {

        XxlJobInfo jobInfo = new XxlJobInfo();
        BeanUtils.copyProperties(job,jobInfo);
        // valid
        XxlJobGroup group = jobGroupService.selectByPId(jobInfo.getJobGroup());
        if (group == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "任务分组必填" );
        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "crond必填");
        }
        if (jobInfo.getJobDesc()==null || jobInfo.getJobDesc().trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "描述不能为空" );
        }
        if (jobInfo.getAuthor()==null || jobInfo.getAuthor().trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "作者不能为空");
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "路由配置异常" );
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE,"阻塞策略不能为空" );
        }

        // fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource()!=null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // ChildJobId valid
        if (jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem: childJobIds) {
                if (childJobIdItem!=null && childJobIdItem.trim().length()>0 && isNumeric(childJobIdItem)) {
                    XxlJobInfo childJobInfo = jobInfoService.selectByPId(Long.valueOf(childJobIdItem));
                    if (childJobInfo==null) {
                        return new ReturnT<String>(ReturnT.FAIL_CODE,
                                MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
                    }
                } else {
                    return new ReturnT<String>(ReturnT.FAIL_CODE,
                            MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
                }
            }

            String temp = "";	// join ,
            for (String item:childJobIds) {
                temp += item + ",";
            }
            temp = temp.substring(0, temp.length()-1);

            jobInfo.setChildJobId(temp);
        }

        // addJobScript in db
        jobInfoService.insert(jobInfo);
        if (jobInfo.getJobId() < 1) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add")+I18nUtil.getString("system_fail")) );
        }

        return new ReturnT<String>(String.valueOf(jobInfo.getJobId()));    }

    @Override
    public ReturnT<String> remove(Long jobId) {
        XxlJobInfo xxlJobInfo = jobInfoService.selectByPId(jobId);
        if (xxlJobInfo == null) {
            return ReturnT.SUCCESS;
        }

        jobInfoService.deleteByPId(jobId);
        jobLogService.deleteByPId(jobId);
        jobLogGlueService.deleteByPId(jobId);
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> update(Job job) {

        XxlJobInfo jobInfo = new XxlJobInfo();
        BeanUtils.copyProperties(job,jobInfo);
// valid
        XxlJobGroup group = jobGroupService.selectByPId(jobInfo.getJobGroup());
        if (group == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "任务分组必填" );
        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "crond必填");
        }
        if (jobInfo.getJobDesc()==null || jobInfo.getJobDesc().trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "描述不能为空" );
        }
        if (jobInfo.getAuthor()==null || jobInfo.getAuthor().trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "作者不能为空");
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "路由配置异常" );
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE,"阻塞策略不能为空" );
        }

        // ChildJobId valid
        if (jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem: childJobIds) {
                if (childJobIdItem!=null && childJobIdItem.trim().length()>0 && isNumeric(childJobIdItem)) {
                    XxlJobInfo childJobInfo = jobInfoService.selectByPId(Long.valueOf(childJobIdItem));
                    if (childJobInfo==null) {
                        return new ReturnT<String>(ReturnT.FAIL_CODE,
                                MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
                    }
                } else {
                    return new ReturnT<String>(ReturnT.FAIL_CODE,
                            MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
                }
            }

            String temp = "";	// join ,
            for (String item:childJobIds) {
                temp += item + ",";
            }
            temp = temp.substring(0, temp.length()-1);

            jobInfo.setChildJobId(temp);
        }

        // group valid
        XxlJobGroup jobGroup = jobGroupService.selectByPId(jobInfo.getJobGroup());
        if (jobGroup == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "任务分组异常");
        }

        // stage job info
        XxlJobInfo exists_jobInfo = jobInfoService.selectByPId(jobInfo.getJobId());
        if (exists_jobInfo == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "任务不存在");
        }

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
        if (exists_jobInfo.getTriggerStatus() == 1 && !jobInfo.getJobCron().equals(exists_jobInfo.getJobCron()) ) {
            try {
                Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
                if (nextValidTime == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "任务永远不会执行");
                }
                nextTriggerTime = nextValidTime.getTime();
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, "表达式解析异常"+ e.getMessage());
            }
        }

        exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
        exists_jobInfo.setJobCron(jobInfo.getJobCron());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setAuthor(jobInfo.getAuthor());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
        exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
        exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
        exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
        exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
        exists_jobInfo.setTriggerNextTime(nextTriggerTime);
        jobInfoService.update(exists_jobInfo);


        return ReturnT.SUCCESS;    }

    @Override
    public ReturnT<String> enable(Long jobId) {
        return null;
    }

    @Override
    public ReturnT<String> disable(Long jobId) {
        return null;
    }

    @Override
    public ReturnT<String> kill(Long jobId) {
        return null;
    }

    @Override
    public ReturnT<Job> start(Long jobId) {
        return null;
    }

    public void delete(Job job) {

    }

    public Job edit(Job job) {
        return null;
    }

    public void enable(String jobKey) {

    }

    public void disable(String jobKey) {

    }

    public void pause(String jobKey) {

    }

    public void start(String jobKey) {

    }

    @Override
    public ReturnT<String> registryAgent(RegistryParam registryParam) {
        //记录agent还活着
        long ret = registryService.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            registryService.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> uploadStatics(LogStatics logStatics) {
        //收集agent的日志
        return null;
    }

    @Override
    public PageModel<JobLog> logPageList(PageRequest request, JobLog ino) {
        return null;
    }

    @Override
    public JobLog findJobLogByJobLogId(Long jobLogId) {
        return null;
    }

    @Override
    public void clearLog(long jobGroup, long jobId, Date clearBeforeTime, int clearBeforeNum) {

    }

    @Override
    public ReturnT<LogResult> catLog(long triggerTime, long logId, int fromLineNum) {
        return null;
    }

    @Override
    public void update(JobLog jobLog) {

    }

    @Override
    public List<JobScript> findJobScriptByJobId(Long jobId) {
        return null;
    }

    @Override
    public JobScript findByJobScriptId(String scriptId) {
        return null;
    }

    @Override
    public void addJobScript(JobScript script) {

    }

    @Override
    public void removeOldLog(Long jobId, int keeyDays) {

    }

    /**
     * group
     * @return
     */
    @Override
    public List<JobGroup> selectAllJobGroup() {
        List<XxlJobGroup> gs = jobGroupService.selectAll();
        if(gs != null && gs.size() > 0) {
            return gs.stream().map(e->{
                return JobGroupUtil.toRpcBean(e);
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public PageModel<JobGroup> selectJobGroupPage(PageRequest request, JobGroup ino) {
        XxlJobGroup se = null;
        if(ino !=null) {
            se = JobGroupUtil.toDbBean(ino);
        }
        PageModel<XxlJobGroup> gs = jobGroupService.selectPage(request,se);
        if(gs != null && gs.getRows().size() > 0) {
            List<JobGroup> jgs = gs.getRows().stream().map(e -> {
                return JobGroupUtil.toRpcBean(e);
            }).collect(Collectors.toList());
            return PageModel.instance(gs.getCount(),jgs);
        }
        return null;
    }

    @Override
    public JobGroup findJobGroup(Long groupId) {
        return JobGroupUtil.toRpcBean(jobGroupService.selectByPId(groupId));
    }

    @Override
    public JobGroup addJobGroup(JobGroup group) {
        XxlJobGroup db = jobGroupService.insert(JobGroupUtil.toDbBean(group));
        return JobGroupUtil.toRpcBean(db);
    }

    @Override
    public JobGroup updateJobGroup(JobGroup group) {
        XxlJobGroup db = jobGroupService.update(JobGroupUtil.toDbBean(group));
        return JobGroupUtil.toRpcBean(db);
    }

    @Transactional
    @Override
    public long removeJobGroup(List<Long> groupIds) {
        long c = 0;
        for(Long l:groupIds){
            c += jobGroupService.deleteByPId(l);
        }
        return c;
    }
}
