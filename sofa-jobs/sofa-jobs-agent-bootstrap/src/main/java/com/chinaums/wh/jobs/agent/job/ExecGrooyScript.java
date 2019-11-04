package com.chinaums.wh.jobs.agent.job;


import com.chinaums.wh.jobs.agent.bean.JobsConfigBean;
import com.chinaums.wh.jobs.agent.util.ContextUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
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
	public int execute(String scriptName, Map<String,String> envs, Map<String,String> params) throws Exception {
		if(StringUtils.isBlank(scriptName)){
			throw new Exception("参数错误 脚本名称不能为空");
		}

		log.info("envs:{} params:{}",envs,params);
		Binding binding = new Binding();
		binding.setProperty("ac", ContextUtil.getApplicationContext());
		binding.setProperty("log", log);
		binding.setProperty("envs", envs);
		binding.setProperty("params", params);

		GroovyShell shell = new GroovyShell(binding);

		log.info("执行Groovy脚本");
		Object result = shell.evaluate(scriptName);
		log.info("Groovy脚本返回：{}", result);

		if (result instanceof Integer)
			return (Integer) result;
		else
			return -1;
	}

}
