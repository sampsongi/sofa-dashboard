package com.chinaums.wh.jobs.agent.job;

import java.util.Map;

public interface IBatch {
	String scriptType();
	int execute(String scriptName, Map<String,String> envs, Map<String,String> params) throws Exception;
}
