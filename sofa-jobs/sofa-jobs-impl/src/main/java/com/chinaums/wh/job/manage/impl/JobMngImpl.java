package com.chinaums.wh.job.manage.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.chinaums.wh.job.manage.IJobMngFacade;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.LogStatics;
import com.chinaums.wh.job.model.RegistryParam;
import com.chinaums.wh.job.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SofaService(interfaceType = IJobMngFacade.class, uniqueId = "${service.unique.id}", bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class JobMngImpl implements IJobMngFacade {
    public List<Job> search(String jobKey, String jobName, String jobGroup) {
        log.info("call search:{}",jobKey);
        return null;
    }

    public long count(String jobKey, String jobName, String jobGroup) {
        return 0;
    }

    public Job findByJobKey(String jobKey) {
        log.info("call findByJobKey:{}",jobKey);
        Job job = new Job();
        job.setJobKey(jobKey);
        job.setJobGroup("group");
        return job;
    }

    public void add(Job job) {

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
        return null;
    }

    @Override
    public ReturnT<String> uploadStatics(LogStatics logStatics) {
        //收集agent的日志
        return null;
    }
}
