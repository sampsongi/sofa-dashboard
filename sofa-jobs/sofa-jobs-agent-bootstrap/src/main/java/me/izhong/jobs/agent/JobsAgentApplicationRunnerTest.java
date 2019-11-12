package me.izhong.jobs.agent;

import lombok.extern.slf4j.Slf4j;
import me.izhong.jobs.agent.job.ExecGrooyScript;
import me.izhong.jobs.agent.job.IBatch;
import me.izhong.jobs.agent.job.context.ScriptRunContext;
import me.izhong.jobs.agent.log.ConsoleLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

//@ImportResource({ "classpath*:rpc-sofa-boot-starter.xml" })
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class})
@Slf4j
public class JobsAgentApplicationRunnerTest {
    public static void main(String[] args) {
        System.setProperty("spring.main.web-application-type","none");
        SpringApplication.run(JobsAgentApplicationRunnerTest.class, args);

        String file = "D:\\space\\ums\\sofa-dashboard\\sofa-jobs\\sofa-jobs-agent-bootstrap\\src\\main\\groovy\\jobstats.groovy";

        //初始化运行环境
        ScriptRunContext context = new ScriptRunContext();
        context.setJobId(0L);
        context.setTriggerId(0L);
        //传文件路径主要是为了断点
        context.setScriptFile(new File(file));

        context.setTimeout(-1);
        context.setLog(new ConsoleLog());

        Map<String, String> envs = new HashMap<>();
        context.setEnvs(envs);
        Map<String, String> params = new HashMap<>();
        params.put("log","logv");
        context.setParams(params);


        ExecGrooyScript execGrooyScript = new ExecGrooyScript();
        try {
            int r = execGrooyScript.execute(context);
            log.info("执行返回{}", r);
        } catch (Exception e) {
            log.error("",e);
        }
    }

}
