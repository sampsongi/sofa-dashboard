package me.izhong.jobs.agent.service;

import me.izhong.jobs.manage.IJobAgentMngFacade;
import me.izhong.jobs.agent.bean.JobContext;
import me.izhong.jobs.agent.job.ShellCommandJob;
import me.izhong.jobs.agent.log.AgentLog;
import me.izhong.jobs.agent.log.RemoteLog;
import me.izhong.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
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
    public ReturnT<String> trigger(Long jobId, Long triggerId, String script,Map<String, String> envs, Map<String, String> params) {

        if(envs == null)
            envs = new HashMap();

        long timeout = params.get("timeout") == null ? 10 * 60 * 1000 : Long.valueOf(params.get("timeout")).longValue();


        JobContext context = new JobContext(jobId,triggerId,timeout,envs,params);
        context.setJobId(jobId);
        context.setTriggerId(triggerId);
        context.setScript(script);

        ShellCommandJob commandJob = new ShellCommandJob("run.sh");
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
