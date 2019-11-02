package com.chinaums.wh.jobs.agent.job;

import com.chinaums.wh.jobs.agent.service.JobServiceReference;
import com.chinaums.wh.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 执行script脚本的定时任务，通过run.sh调起来
 * 日志通过dubbo上送到统一调度平台
 */
@Slf4j
public class ShellCommandJob extends IJobHandler {

    private long jobId;
    private long triggerId;
    private long glueUpdatetime;
    private String gluesource;
    private String glueType;


    private JobServiceReference jobServiceReference;

    public ShellCommandJob(long jobId, long triggerId, String script){
        this.jobId = jobId;
        this.triggerId = triggerId;
        this.gluesource = script;
    }

    public ShellCommandJob() {
    }

    @Override
    public ReturnT<String> execute(Map<String,String> params) throws Exception {
        log.info("shell command job[{}]  trigger [{}] run with param: {}",jobId,triggerId,params);
        // JobRunLog jobLog = new JobRunLog();
        // 定时任务日志通过接口送到 jobs-bootstrap
        // jobServiceReference.getJobMngFacade().uploadStatics();
        try {
            /* String logging = null;
        FileOutputStream fos = null;

            String path = ContextUtil.getBean(JobsConfigBean.class)
                    .getJobLogPath();
            String appPath = ContextUtil.getBean(JobsConfigBean.class)
                    .getJobAppPath();

            String command = ctx.getJobDetail().getJobDataMap()
                    .getString("command");
            String parameter = ctx.getJobDetail().getJobDataMap()
                    .getString("parameter");
            if (parameter == null)
                parameter = "";
            int timeout = ctx.getJobDetail().getJobDataMap()
                    .getIntValue("timeout");
            String name = ctx.getJobDetail().getKey().getName();
            logging = ctx.getJobDetail().getJobDataMap().getString("logging");
            String countStr = ctx.getJobDetail().getJobDataMap()
                    .getString("count");
            long count = 0;
            if (countStr != null)
                count = Long.parseLong(countStr);
            ctx.getJobDetail().getJobDataMap().put("count", "" + (count + 1));

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String time = new SimpleDateFormat("HHmmss").format(new Date());

            String logFile = path + "/" + date + "/" + name + "." + time
                    + ".log";

            jobLog.setJobName(ctx.getJobDetail().getKey().getName());
            jobLog.setJobGroup(ctx.getJobDetail().getKey().getGroup());
            jobLog.setDescription(ctx.getJobDetail().getDescription());
            jobLog.setFireInstanceId(ctx.getFireInstanceId());
            jobLog.setServerName(ContextUtil.getServerName());
            jobLog.setRunStartTime(new Date());
            jobLog.setLogFile(logFile);

            if (!"N".equals(logging)) {
                jobLog = configService.saveJobRunLog(jobLog);
            }

            log.info("开始任务:serverName={} command={}, paramter={}, timeout={}, log={}",
                    ContextUtil.getServerName(),command, parameter, timeout, logFile);

            if (StringUtils.isBlank(command) || StringUtils.isBlank(path)
                    || StringUtils.isBlank(name)) {
                throw new JobExecutionException("参数错误");
            }

            File appPathFile = new File(appPath);
            File commandFile = new File(appPathFile, command);
            if (!commandFile.getParentFile().getCanonicalPath()
                    .startsWith(appPathFile.getCanonicalPath())) {
                throw new JobExecutionException("不允许执行该路径下的命令");
            }
            log.info("执行脚本文件:{}",commandFile.getAbsolutePath());
            if (!commandFile.exists()) {
                throw new JobExecutionException("命令文件不存在");
            }

            File logFilePath = new File(logFile);
            FileUtils.forceMkdir(logFilePath.getParentFile());

            fos = new FileOutputStream(logFilePath);

            String shellCommand = "/bin/sh " + commandFile.getAbsolutePath()
                    + " " + parameter;

            shellExecutor = new DefaultExecutor();

            watchdog = new ExecuteWatchdog(timeout);
            shellExecutor.setWatchdog(watchdog);

            shellExecutor.setExitValue(0);

            PumpStreamHandler streamHandler = new PumpStreamHandler(fos);
            shellExecutor.setStreamHandler(streamHandler);

            CommandLine cmdLine = CommandLine.parse(shellCommand);
            int exitValue = shellExecutor.execute(cmdLine);
            log.info("任务返回:  {}", exitValue);

            jobLog.setRunEndTime(new Date());
            jobLog.setRunUsedTime(jobLog.getRunEndTime().getTime()
                    - jobLog.getRunStartTime().getTime());
            jobLog.setRunResultCode("" + exitValue);

            if (!"N".equals(logging)) {
                jobLog = configService.saveJobRunLog(jobLog);
            }*/
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
