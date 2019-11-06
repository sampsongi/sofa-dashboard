package com.chinaums.wh.job.manage.impl.core.trigger;

import com.chinaums.wh.common.util.IpUtil;
import com.chinaums.wh.job.manage.IJobAgentMngFacade;
import com.chinaums.wh.job.manage.impl.JobAgentServiceReference;
import com.chinaums.wh.job.manage.impl.core.conf.XxlJobAdminConfig;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobLog;
import com.chinaums.wh.job.manage.impl.core.route.ExecutorRouteStrategyEnum;
import com.chinaums.wh.job.manage.impl.core.util.SpringUtil;
import com.chinaums.wh.job.model.TriggerParam;
import com.chinaums.wh.job.type.ExecutorBlockStrategyEnum;
import com.chinaums.wh.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XxlJobTrigger {
    private static Logger logger = LoggerFactory.getLogger(XxlJobTrigger.class);

    public static ReturnT<String> trigger(long jobId, TriggerTypeEnum triggerType, int failRetryCount,  String executorParam) {
        // load data
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().selectByPId(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
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
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogService().insert(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getJobId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getJobId());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        if(jobInfo.getGlueUpdatetime() != null)
            triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
//        triggerParam.setBroadcastIndex(index);
//        triggerParam.setBroadcastTotal(total);
//
//        // 3、init address
//        String address = null;
//        ReturnT<String> routeAddressResult = null;
//        if (group.getRegistryList()!=null && !group.getRegistryList().isEmpty()) {
//            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
//                if (index < group.getRegistryList().size()) {
//                    address = group.getRegistryList().get(index);
//                } else {
//                    address = group.getRegistryList().get(0);
//                }
//            } else {
//                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
//                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
//                    address = routeAddressResult.getContent();
//                }
//            }
//        } else {
//            routeAddressResult = new ReturnT<String>(ReturnT.FAIL_CODE, "地址为空");
//        }

        // 4、trigger remote executor
//        ReturnT<String> triggerResult = null;
//        if (address != null) {
        ReturnT<String>  triggerResult = runExecutor(triggerParam, null);
//        } else {
//            triggerResult = new ReturnT<String>(ReturnT.FAIL_CODE, null);
//        }

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
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorParam(executorParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);

        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
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
        ReturnT<String> runResult = ReturnT.SUCCESS;
        logger.info("调度远程执行器执行：{} : {}",triggerParam, address);
        try {
            IJobAgentMngFacade sr = SpringUtil.getBean(JobAgentServiceReference.class).jobAgentService;

            String pa = triggerParam.getExecutorParams();
            Map<String, String> params = new HashMap<String, String>() {{
                put("data", pa);
            }};
            sr.trigger(triggerParam.getJobId(), triggerParam.getLogId(), triggerParam.getGlueSource(), params);

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
