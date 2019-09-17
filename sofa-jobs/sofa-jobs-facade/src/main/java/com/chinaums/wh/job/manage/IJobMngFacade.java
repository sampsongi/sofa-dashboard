package com.chinaums.wh.job.manage;

import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.LogStatics;
import com.chinaums.wh.job.model.RegistryParam;
import com.chinaums.wh.model.ReturnT;

import java.util.List;

public interface IJobMngFacade {

    PageModel<Job> pageList(PageRequest request, Job ino);

    List<Job> search(String jobKey, String jobName, String jobGroup);

    long count(String jobKey, String jobName, String jobGroup);

    Job findByJobId(Long jobId);

    Job findByJobKey(String jobKey);

    ReturnT<Job> add(Job job);

    ReturnT<Job> remove(Long jobId);

    ReturnT<Job> update(Job job);

    ReturnT<Job> enable(Long jobId);

    ReturnT<Job> disable(Long jobId);

    ReturnT<Job> kill(Long jobId);

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

}
