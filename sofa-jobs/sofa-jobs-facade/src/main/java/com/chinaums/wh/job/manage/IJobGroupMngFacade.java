package com.chinaums.wh.job.manage;

import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.JobGroup;

import java.util.List;

public interface IJobGroupMngFacade {

    List<Job> search(String jobKey, String jobName, String jobGroup);

    long count(String jobKey, String jobName, String jobGroup);

    List<JobGroup> selectAll();

    PageModel<JobGroup> selectPage(PageRequest request, JobGroup ino);

    JobGroup find(Long groupId);

    JobGroup add(JobGroup group);

    JobGroup update(JobGroup group);

    long remove(List<Long> groupId);
}
