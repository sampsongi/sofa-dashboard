package com.chinaums.wh.job.manage.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.manage.IJobMngFacade;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import com.chinaums.wh.job.manage.impl.core.util.JobGroupUtil;
import com.chinaums.wh.job.manage.impl.core.util.JobInfoUtil;
import com.chinaums.wh.job.manage.impl.service.*;
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
        XxlJobInfo se = null;
        if(ino != null) {
            se = JobInfoUtil.toDbBean(ino);
        }
        PageModel<XxlJobInfo> gs = jobInfoService.selectPage(request,se);
        if(gs != null && gs.getRows().size() > 0) {
            List<Job> jgs = gs.getRows().stream().map(e -> JobInfoUtil.toRpcBean(e)).collect(Collectors.toList());
            return PageModel.instance(gs.getCount(),jgs);
        }
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
        return JobInfoUtil.toRpcBean(jobInfoService.selectByPId(jobId));
    }

    @Override
    public ReturnT<String> add(Job job) {
        XxlJobInfo jobInfo = new XxlJobInfo();
        BeanUtils.copyProperties(job, jobInfo);
        return jobInfoService.addJob(jobInfo);
    }

    @Override
    public ReturnT<String> remove(Long jobId) {
        return jobInfoService.removeJob(jobId);
    }

    @Override
    public ReturnT<String> update(Job job) {
        XxlJobInfo jobInfo = new XxlJobInfo();
        BeanUtils.copyProperties(job,jobInfo);
        return jobInfoService.updateJob(jobInfo);
    }

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
        //jobInfoService.
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> start(Long jobId) {
        return ReturnT.SUCCESS;
    }

    public void delete(Job job) {

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
