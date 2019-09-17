package com.chinaums.wh.job.manage;

import com.chinaums.wh.job.model.JobScript;

import java.util.List;

public interface IJobScriptMngFacade {

    List<JobScript> findByJobId(Long jobId);

    JobScript findByJobScriptId(String scriptId);

    void add(JobScript script);

    void removeOld(Long jobId, int keeyDays);

}
