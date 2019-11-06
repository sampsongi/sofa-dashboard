package me.izhong.dashboard.agent.test;


import com.chinaums.wh.jobs.agent.job.ExecGrooyScript;
import com.chinaums.wh.jobs.agent.job.context.ScriptRunContext;
import com.chinaums.wh.jobs.agent.log.ConsoleLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
@Slf4j
public class ScriptTest {

    @Test
	public void testAggregate() throws Exception {
        log.info("==========test groovy..");

        String file = "/Users/jimmy/space/tianru/sofa-dashboard/sofa-jobs/sofa-jobs-agent-bootstrap/src/main/groovy/test.groovy";

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
        int r = execGrooyScript.execute(context);
        log.info("执行返回{}",r);
    }
}
