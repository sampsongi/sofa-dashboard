package com.chinaums.wh.jobs.agent.service;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.chinaums.wh.job.manage.IJobAgentMngFacade;
import com.chinaums.wh.jobs.agent.bean.JobContext;
import com.chinaums.wh.jobs.agent.job.ShellCommandJob;
import com.chinaums.wh.jobs.agent.log.AgentLog;
import com.chinaums.wh.jobs.agent.log.RemoteLog;
import com.chinaums.wh.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
//@SofaService(interfaceType = IJobAgentMngFacade.class, uniqueId = "${service.unique.id}", bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class JobAgentMngImpl implements IJobAgentMngFacade {

    @Override
    public ReturnT<String> kill(Long jobId, Long triggerId) {
        return null;
    }

    @Override
    public ReturnT<String> trigger(Long jobId, Long triggerId, String script, Map<String, String> params) {

        HashMap envs = new HashMap();
        envs.put("os","mac");


        JobContext context = new JobContext(jobId,triggerId,3000,envs,params);
        AgentLog agentLog = new RemoteLog(context);

        ShellCommandJob commandJob = new ShellCommandJob("/bin/sh run.sh",jobId,agentLog);
        try {
            //后面考虑缓存 进程id
            commandJob.execute(context);
        } catch (Exception e) {
            log.error("",e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}
