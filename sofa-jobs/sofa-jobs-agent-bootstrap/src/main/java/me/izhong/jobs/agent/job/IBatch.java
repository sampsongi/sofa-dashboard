package me.izhong.jobs.agent.job;

import me.izhong.jobs.agent.job.context.ScriptRunContext;

public interface IBatch {
	String scriptType();
	int execute(ScriptRunContext context) throws Exception;
}
