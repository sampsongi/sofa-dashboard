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
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
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
    private Long jobId;

    private JobContext jobContext;

    private JobServiceReference jobServiceReference;
    private AgentLog agentLog;

    public ShellCommandJob(String command, Long jobId,AgentLog agentLog){
        this.command = command;
        this.jobId = jobId;
        this.agentLog = agentLog;
    }

    public ShellCommandJob() {
    }

    @Override
    public ReturnT<String> execute(JobContext jobContext) throws Exception {
        this.jobContext = jobContext;

        String run_env = ContextUtil.getRunEnv();
        JobsConfigBean configBean = ContextUtil.getBean(JobsConfigBean.class);
        String scriptDir = configBean.getScriptPath();
        Map<String,String> params = jobContext.getParams();

        agentLog.info("shell command job[{}]  trigger [{}] run with param: {}",
                jobContext.getJobId(),jobContext.getTriggerId(),params);
        agentLog.info("jobId:{}",jobId);
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
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            agentLog.info("开始任务:serverName={} command={}, paramter={}, timeout={}",
                    ContextUtil.getServerName(),command, params, timeout);

            if (StringUtils.isBlank(command) || StringUtils.isBlank(name)) {
                throw new JobExecutionException("参数错误");
            }

            String shellCommand = SHName + scriptDir
                    + "/run.sh " + "--run_env " + run_env
                    + " -DisJobAgent=true -DscriptType=groovy -DsTime=" + dateTime +" -DjobId="+ jobId +" -Dparams=" + params;
            agentLog.info("shellCommand:{}",shellCommand);

            DefaultExecutor shellExecutor = new DefaultExecutor();

            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            shellExecutor.setWatchdog(watchdog);

            shellExecutor.setExitValue(0);

            PumpStreamHandler streamHandler = new PumpStreamHandler(fos);
            shellExecutor.setStreamHandler(streamHandler);

            CommandLine cmdLine = CommandLine.parse(shellCommand);
            int exitValue = shellExecutor.execute(cmdLine);
            log.info("run.sh任务返回:  {}", exitValue);

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
