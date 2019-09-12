package com.chinaums.wh.job.manage;

import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.LogStatics;
import com.chinaums.wh.job.model.RegistryParam;
import com.chinaums.wh.job.model.ReturnT;

import java.util.List;

public interface IJobMngFacade {

    List<Job> search(String jobKey, String jobName, String jobGroup);

    long count(String jobKey, String jobName, String jobGroup);

    Job findByJobKey(String jobKey);

    void add(Job job);

    void delete(Job job);

    Job edit(Job job);

    void enable(String jobKey);

    void disable(String jobKey);

    void pause(String jobKey);

    void start(String jobKey);

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
