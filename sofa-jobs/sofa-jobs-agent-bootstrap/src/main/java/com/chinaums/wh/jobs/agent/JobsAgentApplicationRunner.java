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
import org.springframework.context.annotation.ImportResource;

import java.io.*;
import java.util.*;

@ImportResource({ "classpath*:rpc-sofa-boot-starter.xml" })
@SpringBootApplication
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
        log.info("args:{}",args.getSourceArgs());
        String scriptType = System.getProperty("scriptType");
        //String scriptType = StringUtil.firstValue(args.getOptionValues("scriptType"));
        if(StringUtils.isNotBlank(scriptType)) {
            log.info("运行run.sh scriptType:{}",scriptType);
            String jobId = System.getProperty("jobId");
            Job job = jobServiceReference.getJobMngFacade().findByJobId(Long.valueOf(jobId));
            String script = job.getGlueSource();

            String envsString = System.getProperty("envs");
            Map<String,String> envs = new HashMap<String,String>();
            if(StringUtils.isNotBlank(envsString))
                envs = JSON.parseObject(envsString, new TypeReference<HashMap<String,String>>(){});

            String paramsString = System.getProperty("params");
            Map<String,String> params = new HashMap<String,String>();
            if(StringUtils.isNotBlank(paramsString))
                params = JSON.parseObject(paramsString, new TypeReference<HashMap<String,String>>(){});

            int code = 0;
            for(IBatch b: batchs){
                if(StringUtils.equalsIgnoreCase(scriptType,b.scriptType())){
                    code = b.execute(script,envs,params);
                    break;
                }
            }
            log.info("run 运行结束 code:{}",code);
            return;
        }
        log.info("启动测试==============");
        long jobId = 1;
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
        AgentLog agentLog = new ConsoleLog();

        ShellCommandJob commandJob = new ShellCommandJob("run.sh",3L,agentLog);
        try {
            //后面考虑缓存 进程id
            //commandJob.execute(context);
        } catch (Exception e) {
            log.error("",e);
        }
    }

}
