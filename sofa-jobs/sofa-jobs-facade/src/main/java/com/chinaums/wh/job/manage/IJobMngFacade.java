package com.chinaums.wh.job.manage;

import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.model.*;
import com.chinaums.wh.model.ReturnT;

import java.util.Date;
import java.util.List;

public interface IJobMngFacade {

    PageModel<Job> pageList(PageRequest request, Job ino);

    List<Job> search(String jobKey, String jobName, String jobGroup);

    long count(String jobKey, String jobName, String jobGroup);

    Job findByJobId(Long jobId);

    Job findByJobKey(String jobKey);

    ReturnT<String> add(Job job);

    ReturnT<String> remove(Long jobId);

    ReturnT<String> update(Job job);

    ReturnT<String> enable(Long jobId);

    ReturnT<String> disable(Long jobId);

    ReturnT<String> kill(Long jobId);

    ReturnT<Job> start(Long jobId);

    /**
     *  agent注册自己的地址到调度器
     * @param registryParam
     * @return
     */
    ReturnT<String> registryAgent(RegistryParam registryParam);

    /**
     * agent上送执行日志，结果到调度器
     * @param
     * @return
     */
    ReturnT<String> uploadStatics(LogStatics logStatics);

    /**
     * 日志相关
     * @param request
     * @param ino
     * @return
     */
    PageModel<JobLog> logPageList(PageRequest request, JobLog ino);

    JobLog findJobLogByJobLogId(Long jobLogId);

    void clearLog(long jobGroup, long jobId, Date clearBeforeTime, int clearBeforeNum);

    ReturnT<LogResult> catLog(long triggerTime, long logId, int fromLineNum);

    void update(JobLog jobLog);

    /**
     * script
     * @param jobId
     * @return
     */
    List<JobScript> findJobScriptByJobId(Long jobId);

    JobScript findByJobScriptId(String scriptId);

    void addJobScript(JobScript script);

    void removeOldLog(Long jobId, int keeyDays);


    /**
     * group
     * @return
     */
    List<JobGroup> selectAllJobGroup();

    PageModel<JobGroup> selectJobGroupPage(PageRequest request, JobGroup ino);

    JobGroup findJobGroup(Long groupId);

    JobGroup addJobGroup(JobGroup group);

    JobGroup updateJobGroup(JobGroup group);

    long removeJobGroup(List<Long> groupId);
}
