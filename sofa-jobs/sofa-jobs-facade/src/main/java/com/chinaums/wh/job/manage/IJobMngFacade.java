package com.chinaums.wh.job.manage;

import com.chinaums.wh.job.model.Job;

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
}
