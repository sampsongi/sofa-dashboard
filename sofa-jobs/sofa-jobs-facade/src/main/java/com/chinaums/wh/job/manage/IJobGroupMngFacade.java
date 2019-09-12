package com.chinaums.wh.job.manage;

import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.ReturnT;

import java.util.List;

public interface IJobGroupMngFacade {

    List<Job> search(String jobKey, String jobName, String jobGroup);

    long count(String jobKey, String jobName, String jobGroup);

    List<JobGroup>
}
