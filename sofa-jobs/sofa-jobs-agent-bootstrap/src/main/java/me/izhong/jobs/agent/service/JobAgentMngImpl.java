package me.izhong.jobs.agent.service;

import me.izhong.jobs.agent.bean.JobsConfigBean;
import me.izhong.jobs.manage.IJobAgentMngFacade;
import me.izhong.jobs.agent.bean.JobContext;
import me.izhong.jobs.agent.job.ShellCommandJob;
import me.izhong.jobs.agent.log.AgentLog;
import me.izhong.jobs.agent.log.RemoteLog;
import me.izhong.jobs.model.LogResult;
import me.izhong.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
//@SofaService(interfaceType = IJobAgentMngFacade.class, uniqueId = "${service.unique.id}", bindings = { @SofaServiceBinding(bindingType = "bolt") })
public class JobAgentMngImpl implements IJobAgentMngFacade {

    @Autowired
    private JobsConfigBean jobsConfigBean;

    @Override
    public ReturnT<String> kill(Long jobId, Long triggerId) {
        return null;
    }

    @Override
    public ReturnT<String> trigger(Long jobId, Long triggerId, String script,Map<String, String> envs, Map<String, String> params) {

        if(envs == null)
            envs = new HashMap();

        long timeout = params.get("timeout") == null ? 10 * 60 * 1000 : Long.valueOf(params.get("timeout")).longValue();


        JobContext context = new JobContext(jobId,triggerId,timeout,envs,params);
        context.setJobId(jobId);
        context.setTriggerId(triggerId);
        context.setScript(script);

        ShellCommandJob commandJob = new ShellCommandJob("run.sh");
        try {
            //后面考虑缓存 进程id
            commandJob.execute(context);
        } catch (Exception e) {
            log.error("",e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public LogResult catLog(long triggerTime, Long jobId, Long logId, int fromLineNum) {

        String dir = jobsConfigBean.getLogDir();
        // filePath/yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");	// avoid concurrent problem, can not be static
        File logFilePath = new File(dir, sdf.format(triggerTime));

        // filePath/yyyyMMdd/3_9999.log
        String logFileName = logFilePath.getPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(logId))
                .concat(".txt");

        // valid log file
        if (logFileName==null || logFileName.trim().length()==0) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
        }
        File logFile = new File(logFileName);

        if (!logFile.exists()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
        }

        // read file
        StringBuffer logContentBuffer = new StringBuffer();
        int toLineNum = 0;
        LineNumberReader reader = null;
        try {
            //reader = new LineNumberReader(new FileReader(logFile));
            reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
            String line = null;

            while ((line = reader.readLine())!=null) {
                toLineNum = reader.getLineNumber();		// [from, to], start as 1
                if (toLineNum >= fromLineNum) {
                    logContentBuffer.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        // result
        LogResult logResult = new LogResult(fromLineNum, toLineNum, logContentBuffer.toString(), false);
        return logResult;
    }
}
