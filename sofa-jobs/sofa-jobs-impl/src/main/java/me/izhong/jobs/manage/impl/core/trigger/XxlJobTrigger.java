package me.izhong.jobs.manage.impl.core.trigger;

import me.izhong.common.util.IpUtil;
import me.izhong.jobs.manage.IJobAgentMngFacade;
import me.izhong.jobs.manage.impl.JobAgentServiceReference;
import me.izhong.jobs.manage.impl.core.conf.XxlJobAdminConfig;
import me.izhong.jobs.manage.impl.core.model.XxlJobGroup;
import me.izhong.jobs.manage.impl.core.model.XxlJobInfo;
import me.izhong.jobs.manage.impl.core.model.XxlJobLog;
import me.izhong.jobs.manage.impl.core.route.ExecutorRouteStrategyEnum;
import me.izhong.jobs.manage.impl.core.util.SpringUtil;
import me.izhong.jobs.model.TriggerParam;
import me.izhong.jobs.type.ExecutorBlockStrategyEnum;
import me.izhong.model.ReturnT;
import me.izhong.jobs.type.ExecutorBlockStrategyEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XxlJobTrigger {
    private static Logger logger = LoggerFactory.getLogger(XxlJobTrigger.class);

    public static ReturnT<String> trigger(long jobId, TriggerTypeEnum triggerType, int failRetryCount,  String executorParam) {
        // load data
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().selectByPId(jobId);
        if (jobInfo == null) {
            logger.warn("trigger fail, jobId invalid，jobId={}", jobId);
            return ReturnT.FAIL;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount>=0?failRetryCount: (jobInfo.getExecutorFailRetryCount() == null ? 0: jobInfo.getExecutorFailRetryCount().intValue());
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupService().selectByPId(jobInfo.getJobGroupId());
        return processTrigger(group, jobInfo, finalFailRetryCount, triggerType, executorParam);
    }

    private static ReturnT<String> processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, String executorParam){

        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);  // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy

        // 1、save log-id
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroupId(jobInfo.getJobGroupId());
        jobLog.setJobDesc(jobInfo.getJobDesc());
        jobLog.setJobId(jobInfo.getJobId());
        jobLog.setTriggerTime(new Date());
        jobLog = XxlJobAdminConfig.getAdminConfig().getXxlJobLogService().insert(jobLog);
        logger.debug("job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getJobId());
        triggerParam.setLogId(jobLog.getJobLogId());

        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        if(jobInfo.getGlueUpdatetime() != null)
            triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());

        ReturnT<String>  triggerResult = runExecutor(triggerParam, null);

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append("触发类型：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append("管理IP：").append(IpUtil.getHostIp());
        triggerMsgSb.append("<br>").append("AddressList类型：").append(group.getAddressList() );
        triggerMsgSb.append("<br>").append("RegistryList地址：").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append("路由策略：").append(executorRouteStrategyEnum.getTitle());
        triggerMsgSb.append("<br>").append("阻塞策略：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append("超时时间：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append("重试次数：").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ "触发调度" +"<<<<<<<<<<< </span><br>")
                .append(triggerResult.getMsg()!=null?triggerResult.getMsg():"");

        // 6、save log trigger-info
        jobLog.setExecutorAddress("");
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        if(StringUtils.isNotBlank(executorParam))
            jobLog.setExecutorParam(executorParam);
        else
            jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);

        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        logger.info("保存jobLog triggerMsgSb:{}",triggerMsgSb.toString());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogService().update(jobLog);

        logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
        return triggerResult;
    }

    /**
     * run executor
     * @param triggerParam
     * @param address
     * @return
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address){
        ReturnT<String> runResult;
        logger.info("调度远程执行器执行：{} : {}",triggerParam, address);
        try {
            IJobAgentMngFacade sr = SpringUtil.getBean(JobAgentServiceReference.class).jobAgentService;

            String pa = triggerParam.getExecutorParams();
            Map<String, String> params = new HashMap<String, String>() {{
                put("data", pa);
            }};
            logger.info("远程调用 jobId:{}",triggerParam.getJobId());
            //dubbo 远程调用
            runResult = sr.trigger(triggerParam.getJobId(), triggerParam.getLogId(), triggerParam.getGlueSource(), params);
            logger.info("远程调用应答:{}",runResult);
            if(runResult == null) {
                runResult = ReturnT.FAIL;
            }
            StringBuffer runResultSB = new StringBuffer("触发调度：");
            runResultSB.append("<br>address：").append(address);
            runResultSB.append("<br>code：").append(runResult.getCode());
            runResultSB.append("<br>msg：").append(runResult.getMsg());

            runResult.setMsg(runResultSB.toString());
            return runResult;
        } catch (Exception e) {
            logger.error("job trigger error, please check ...", e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, e.toString());
        }
    }

}
