package com.chinaums.wh.jobs.agent.job;


import com.chinaums.wh.jobs.agent.bean.JobsConfigBean;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Component
@Slf4j
public class ExecGrooyScript implements IBatch {
	@Autowired
	private JobsConfigBean configBean;

	@Override
	public int execute(String[] options) throws Exception {
		if (options.length < 1) {
			throw new Exception("参数错误，参数应该为: <Script File Name> <Params>");
		}

		String path = configBean.getGroovyFileDir() + "/" + options[0];
		File file = new File(path);
		if (new File(path).getCanonicalPath()
				.startsWith(file.getParentFile().getCanonicalPath()) == false) {
			throw new Exception(
					"不允许执行该目录下的文件：" + file.getParentFile().getCanonicalPath());
		}

		String[] groovyArgs = Arrays.copyOfRange(options, 1, options.length);

		Binding binding = new Binding();
		binding.setProperty("ac", Main.getApplicationContext());
		binding.setProperty("log", log);
		binding.setProperty("args", groovyArgs);

		GroovyShell shell = new GroovyShell(binding);

		log.info("执行Groovy脚本：{}", file.getCanonicalPath());
		Object result = shell.evaluate(file);
		log.info("Groovy脚本返回：{}", result);

		if (result instanceof Integer)
			return (Integer) result;
		else
			return -1;
	}

}
