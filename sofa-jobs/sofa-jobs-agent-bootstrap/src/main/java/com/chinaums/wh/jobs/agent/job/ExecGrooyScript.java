package com.chinaums.wh.jobs.agent.job;


import com.chinaums.wh.jobs.agent.bean.JobsConfigBean;
import com.chinaums.wh.jobs.agent.job.context.ScriptRunContext;
import com.chinaums.wh.jobs.agent.log.AgentLog;
import com.chinaums.wh.jobs.agent.util.ContextUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ExecGrooyScript implements IBatch {
	@Autowired
	private JobsConfigBean configBean;

	@Override
	public String scriptType() {
		return "groovy";
	}

	@Override
	public int execute(ScriptRunContext context) throws Exception {
		if(StringUtils.isBlank(context.getScript())){
			throw new Exception("参数错误 脚本名称不能为空");
		}

		String script = context.getScript();
		AgentLog logger = context.getLog();
		log.info("envs:{} params:{}",context.getEnvs(),context.getParams());
		Binding binding = new Binding();
		binding.setProperty("ac", ContextUtil.getApplicationContext());
		binding.setProperty("log", logger);
		binding.setProperty("envs", context.getEnvs());
		binding.setProperty("params", context.getParams());

		GroovyShell shell = new GroovyShell(binding);

		log.info("执行Groovy脚本");
		Object result = shell.evaluate(script);
		log.info("Groovy脚本返回：{}", result);

		if (result instanceof Integer)
			return (Integer) result;
		else
			return -1;
	}

}
