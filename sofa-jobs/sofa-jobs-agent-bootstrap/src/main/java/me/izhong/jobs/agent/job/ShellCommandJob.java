package me.izhong.jobs.agent.job;

import lombok.Cleanup;
import me.izhong.jobs.agent.bean.JobContext;
import me.izhong.jobs.agent.bean.JobsConfigBean;
import me.izhong.jobs.agent.exp.JobExecutionException;
import me.izhong.jobs.agent.util.ContextUtil;
import me.izhong.model.ReturnT;
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
    public ShellCommandJob(String command) {
        this.command = command;
    }

    @Override
    public ReturnT<String> execute(JobContext jobContext) throws Exception {
        Long jobId = jobContext.getJobId();
        Long triggerId = jobContext.getTriggerId();

        String run_env = ContextUtil.getRunEnv();
        JobsConfigBean configBean = ContextUtil.getBean(JobsConfigBean.class);
        String scriptDir = configBean.getScriptPath();

        Map<String, String> envs = new HashMap<>();
        envs.put("os","win");

        Map<String, String> params = jobContext.getParams();

        log.info("shell command job[{}]  trigger [{}] run with param: {}",
                jobContext.getJobId(), jobContext.getTriggerId(), params);
        log.info("jobId:{}  trigger:{}", jobId);
        // JobRunLog jobLog = new JobRunLog();
        // 定时任务日志通过接口送到 jobs-bootstrap
        // jobServiceReference.getJobMngFacade().uploadStatics();
        try {
            if (params == null)
                params = new HashMap<>();
            long timeout = jobContext.getTimeout();

            String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            log.info("开始任务:serverName={} command={}, paramter={}, timeout={}",
                    ContextUtil.getServerName(), command, params, timeout);

            if (StringUtils.isBlank(command)) {
                throw new JobExecutionException("参数错误");
            }

            String shellCommand = SHName + scriptDir
                    + "/"+ command + "--run_env " + run_env
                    + " -DisJobAgent=true -DscriptType=groovy -DsTime=" + dateTime
                    + " -DjobId=" + jobId + " -DtriggerId=" + triggerId
                    + " -Denvs=" + envs + " -Dparams=" + params;
            log.info("shellCommand:{}", shellCommand);


            DefaultExecutor shellExecutor = new DefaultExecutor();

            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            shellExecutor.setWatchdog(watchdog);

            shellExecutor.setExitValue(0);

            File logFile = new File(configBean.getLogDir());
            if(!logFile.exists()) {
                throw new JobExecutionException("日志文件路径不存在:"+configBean.getLogDir());
            }
            String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
            logFile = new File(configBean.getLogDir() + File.separator + dateString + File.separator + +jobId + "_" + triggerId + ".txt");
            if(!logFile.exists()) {
                log.info("文件不存在 {} 创建",logFile.getAbsolutePath());
                boolean isSc = logFile.createNewFile();
                if(!isSc){
                    log.info("文件创建失败");
                }
            }
            @Cleanup
            FileOutputStream fos = new FileOutputStream(logFile);
            PumpStreamHandler streamHandler = new PumpStreamHandler(fos);
            shellExecutor.setStreamHandler(streamHandler);

            log.info("run.sh任务开始执行");
            CommandLine cmdLine = CommandLine.parse(shellCommand);
            int exitValue = shellExecutor.execute(cmdLine);
            log.info("run.sh任务返回:  {}", exitValue);
            return ReturnT.SUCCESS;
        } catch (Throwable e) {
            log.error("任务失败", e);
            return ReturnT.FAIL;
        } finally {

        }
    }

}
