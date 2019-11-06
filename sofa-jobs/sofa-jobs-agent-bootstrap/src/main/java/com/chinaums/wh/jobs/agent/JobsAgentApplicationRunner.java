package com.chinaums.wh.jobs.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.jobs.agent.bean.JobContext;
import com.chinaums.wh.jobs.agent.job.IBatch;
import com.chinaums.wh.jobs.agent.job.ShellCommandJob;
import com.chinaums.wh.jobs.agent.log.AgentLog;
import com.chinaums.wh.jobs.agent.log.ConsoleLog;
import com.chinaums.wh.jobs.agent.service.JobServiceReference;
import com.chinaums.wh.jobs.agent.util.StringUtil;
import com.chinaums.wh.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import java.io.*;
import java.util.*;

@ImportResource({ "classpath*:rpc-sofa-boot-starter.xml" })
@SpringBootApplication
@ComponentScan(value = {"com.chinaums,me.izhong"})
@Slf4j
public class JobsAgentApplicationRunner implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(JobsAgentApplicationRunner.class, args);
    }

    @Autowired
    private JobServiceReference jobServiceReference;

    @Autowired
    private List<IBatch> batchs;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String scriptType = System.getProperty("scriptType");
        if(StringUtils.isNotBlank(scriptType)) {
            return;
        }

        log.info("args:{}",args.getSourceArgs());

        log.info("启动测试==============");
        long jobId = 3;
        long triggerId = 3;

//        String classPath = this.getClass().getClassLoader().getResource("").getPath();
//        File srcPath = new File(classPath).getParentFile().getParentFile();
//        log.info("当前路径：{}",srcPath.getAbsolutePath());
//        File groovyFile = new File(srcPath.getAbsolutePath() + "/src/main/groovy/test.groovy");
//        if(!groovyFile.exists()) {
//            log.error("文件不存在");
//            System.exit(-1);
//        }
//        String script = FileUtils.readFileToString(groovyFile,"utf-8");
        HashMap params = new HashMap();
        params.put("log","/usr");

        HashMap envs = new HashMap();
        envs.put("os","mac");
        JobContext context = new JobContext(jobId,triggerId,3000,envs,params);

        ShellCommandJob commandJob = new ShellCommandJob("run.sh");
        try {
            //后面考虑缓存 进程id
            commandJob.execute(context);
        } catch (Exception e) {
            log.error("",e);
        }
    }

}
