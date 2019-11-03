package com.chinaums.wh.jobs.agent.job;

import com.chinaums.wh.jobs.agent.bean.JobContext;
import com.chinaums.wh.jobs.agent.bean.JobsConfigBean;
import com.chinaums.wh.jobs.agent.exp.JobExecutionException;
import com.chinaums.wh.jobs.agent.log.AgentLog;
import com.chinaums.wh.jobs.agent.service.JobServiceReference;
import com.chinaums.wh.jobs.agent.util.ContextUtil;
import com.chinaums.wh.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 执行script脚本的定时任务，通过run.sh调起来
 * 日志通过dubbo上送到统一调度平台
 */
@Slf4j
public class ShellCommandJob extends IJobHandler {

    private String SHName = "/bin/sh ";
    private String command;
    private String script;

    private JobContext jobContext;

    private JobServiceReference jobServiceReference;
    private AgentLog agentLog;

    public ShellCommandJob(String command, String script,AgentLog agentLog){
        this.command = command;
        this.script = script;
        this.agentLog = agentLog;
    }

    public ShellCommandJob() {
    }

    @Override
    public ReturnT<String> execute(JobContext jobContext) throws Exception {
        this.jobContext = jobContext;
        JobsConfigBean configBean = ContextUtil.getBean(JobsConfigBean.class);
        String scriptDir = configBean.getScriptPath();
        Map<String,String> params = jobContext.getParams();

        agentLog.info("shell command job[{}]  trigger [{}] run with param: {}",
                jobContext.getJobId(),jobContext.getTriggerId(),params);
        agentLog.info("script:{}",script);
        // JobRunLog jobLog = new JobRunLog();
        // 定时任务日志通过接口送到 jobs-bootstrap
        // jobServiceReference.getJobMngFacade().uploadStatics();
        try {
            String logging = null;
        FileOutputStream fos = null;

//            String path = ContextUtil.getBean(JobsConfigBean.class)
//                    .getJobLogPath();
//            String appPath = ContextUtil.getBean(JobsConfigBean.class)
//                    .getJobAppPath();

            if (params == null)
                params = new HashMap<>();
            long timeout = jobContext.getTimeout();
            String name = "test job";

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String time = new SimpleDateFormat("HHmmss").format(new Date());

//            String logFile = path + "/" + date + "/" + name + "." + time
//                    + ".log";
//
//            jobLog.setJobName(ctx.getJobDetail().getKey().getName());
//            jobLog.setJobGroup(ctx.getJobDetail().getKey().getGroup());
//            jobLog.setDescription(ctx.getJobDetail().getDescription());
//            jobLog.setFireInstanceId(ctx.getFireInstanceId());
//            jobLog.setServerName(ContextUtil.getServerName());
//            jobLog.setRunStartTime(new Date());
//            jobLog.setLogFile(logFile);

            agentLog.info("开始任务:serverName={} command={}, paramter={}, timeout={}",
                    ContextUtil.getServerName(),command, params, timeout);

            if (StringUtils.isBlank(command) || StringUtils.isBlank(name)) {
                throw new JobExecutionException("参数错误");
            }

//            File appPathFile = new File(appPath);
//            File commandFile = new File(appPathFile, command);
//            if (!commandFile.getParentFile().getCanonicalPath()
//                    .startsWith(appPathFile.getCanonicalPath())) {
//                throw new JobExecutionException("不允许执行该路径下的命令");
//            }
//            log.info("执行脚本文件:{}",commandFile.getAbsolutePath());
//            if (!commandFile.exists()) {
//                throw new JobExecutionException("命令文件不存在");
//            }

//            File logFilePath = new File(logFile);
//            FileUtils.forceMkdir(logFilePath.getParentFile());

//            fos = new FileOutputStream(logFilePath);

            String shellCommand = SHName + scriptDir
                    + "/run.sh " + params;
            agentLog.info("shellCommand:{}",shellCommand);

            DefaultExecutor shellExecutor = new DefaultExecutor();

            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            shellExecutor.setWatchdog(watchdog);

            shellExecutor.setExitValue(0);

            PumpStreamHandler streamHandler = new PumpStreamHandler(fos);
            shellExecutor.setStreamHandler(streamHandler);

            CommandLine cmdLine = CommandLine.parse(shellCommand);
            int exitValue = shellExecutor.execute(cmdLine);
            log.info("任务返回:  {}", exitValue);

//            jobLog.setRunEndTime(new Date());
//            jobLog.setRunUsedTime(jobLog.getRunEndTime().getTime()
//                    - jobLog.getRunStartTime().getTime());
//            jobLog.setRunResultCode("" + exitValue);
//
//            if (!"N".equals(logging)) {
//                jobLog = configService.saveJobRunLog(jobLog);
//            }
        } catch (Exception e) {
            log.error("任务失败", e);
            /*jobLog.setRunEndTime(new Date());
            jobLog.setRunUsedTime(jobLog.getRunEndTime().getTime()
                    - jobLog.getRunStartTime().getTime());
            jobLog.setRunResultMsg(StringUtils.left(e.getMessage(), 200));
            if (e instanceof ExecuteException) {
                jobLog.setRunResultCode("" + ((ExecuteException) e).getExitValue());
            }

            if (!"N".equals(logging)) {
                jobLog = configService.saveJobRunLog(jobLog);
            }
            String env = ContextUtil.getRunEnv();
            String machine = ContextUtil.getServerName();

            alertService.alert(
                    "[Netpay " + env + " " + machine +  "]定时任务失败："
                            + ctx.getJobDetail().getKey().getName(),
                    e.getMessage(), new File(jobLog.getLogFile()));

            throw new JobExecutionException(e);*/
        }finally {
            /*if(null != fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }*/
        }
        return ReturnT.SUCCESS;
    }

}
