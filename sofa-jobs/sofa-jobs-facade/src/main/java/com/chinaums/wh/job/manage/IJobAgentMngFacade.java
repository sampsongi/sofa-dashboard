package com.chinaums.wh.job.manage;

import com.chinaums.wh.model.ReturnT;

import java.util.Map;

public interface IJobAgentMngFacade {

    ReturnT<String> kill(Long jobId, Long triggerId);

    ReturnT<String> trigger(Long jobId, Long triggerId, String script, Map<String,String> params);

}
