package me.izhong.jobs.manage;

import me.izhong.model.ReturnT;

import java.util.Map;

public interface IJobAgentMngFacade {

    ReturnT<String> kill(Long jobId, Long triggerId);

    ReturnT<String> trigger(Long jobId, Long triggerId, String script,Map<String, String> envs, Map<String,String> params);

}
