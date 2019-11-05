package com.chinaums.wh.jobs.agent.launch;

import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.jobs.agent.job.IBatch;
import com.chinaums.wh.jobs.agent.job.context.ScriptRunContext;
import com.chinaums.wh.jobs.agent.log.ConsoleLog;
import com.chinaums.wh.jobs.agent.log.RemoteLog;
import com.chinaums.wh.jobs.agent.service.JobServiceReference;
import com.chinaums.wh.jobs.agent.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AgentScriptRunner implements ApplicationRunner {

    @Autowired
    private JobServiceReference jobServiceReference;

    @Autowired
    private List<IBatch> batchs;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String scriptType = System.getProperty("scriptType");
        //String scriptType = StringUtil.firstValue(args.getOptionValues("scriptType"));
        if(StringUtils.isNotBlank(scriptType)) {
            log.info("运行run.sh scriptType:{}",scriptType);
            Long jobId = Long.valueOf(System.getProperty("jobId"));
            if(jobId ==null) {
                throw new RuntimeException("jobId不能为空");
            }
            Long triggerId = Long.valueOf(System.getProperty("triggerId"));
            if(triggerId ==null) {
                //throw new RuntimeException("triggerId不能为空");
            }
            Job job = jobServiceReference.getJobMngFacade().findByJobId(jobId);
            String script = job.getGlueSource();
            log.info("测试脚本:{}",script);

            String envsString = System.getProperty("envs");
            log.info("envs:{}",envsString);
            Map<String,String> envs = StringUtil.parseParams(envsString);

            String paramsString = System.getProperty("params");
            log.info("params:{}",paramsString);
            Map<String,String> params = StringUtil.parseParams(paramsString);
            //log.info("params2:{}",params);

            ScriptRunContext context = new ScriptRunContext();
            context.setJobId(jobId);
            context.setScript(script);
            try {
                context.setTimeout(Long.valueOf(System.getProperty("jobId")));
            } catch (Exception e) {
                context.setTimeout(-1);
            }
            String loggerString = System.getProperty("logger");
            if(StringUtils.equalsIgnoreCase(loggerString,"console")) {
                context.setLog(new ConsoleLog());
            } else {
                context.setLog(new RemoteLog(jobId,triggerId));
            }
            context.setLog(new ConsoleLog());
            context.setEnvs(envs);
            context.setParams(params);


            int code = 0;
            boolean find = false;
            for(IBatch b: batchs){
                if(StringUtils.equalsIgnoreCase(scriptType,b.scriptType())){
                    find = true;
                    code = b.execute(context);
                    break;
                }
            }
            if(!find) {
                log.info("没有找到对应的脚本类型");
            }
            log.info("run 运行结束 code:{}",code);
            return;
        }
    }
}
