package com.chinaums.wh.jobs.agent.job;

import com.chinaums.wh.jobs.agent.job.context.ScriptRunContext;
import com.chinaums.wh.jobs.agent.log.AgentLog;

import java.util.Map;

public interface IBatch {
	String scriptType();
	int execute(ScriptRunContext context) throws Exception;
}
