package me.izhong.jobs.manage;

import me.izhong.jobs.model.LogResult;
import me.izhong.model.ReturnT;

import java.util.Map;

public interface IJobAgentMngFacade {

    ReturnT<String> kill(Long jobId, Long triggerId);

    ReturnT<String> trigger(Long jobId, Long triggerId, Map<String, String> envs, Map<String,String> params);

    LogResult catLog(long triggerTime, Long jobId,  Long logId, int fromLineNum);

}
