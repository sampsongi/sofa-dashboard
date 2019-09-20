package com.chinaums.wh.job.admin.controller;


import com.chinaums.wh.common.util.DateUtil;
import com.chinaums.wh.db.common.annotation.AjaxWrapper;

import com.chinaums.wh.db.common.util.PageRequestUtil;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.job.admin.service.JobServiceReference;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.JobGroup;
import com.chinaums.wh.job.model.JobLog;
import com.chinaums.wh.job.model.LogResult;
import com.chinaums.wh.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/joblog")
public class JobLogController {
	private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

	@Resource
	private JobServiceReference jobServiceReference;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Long jobId) {

		// 执行器列表
		List<JobGroup> jobGroupList_all =  jobServiceReference.jobGroupService.selectAll();

		// filter group
		List<JobGroup> jobGroupList = jobGroupList_all;


		model.addAttribute("JobGroupList", jobGroupList);

		// 任务
		if (jobId > 0) {
			Job jobInfo = jobServiceReference.jobService.findByJobId(jobId);
			if (jobInfo == null) {
				throw new RuntimeException("未找到");
			}

			model.addAttribute("jobInfo", jobInfo);

		}

		return "joblog/joblog.index";
	}

	
	@RequestMapping("/pageList")
	@AjaxWrapper
	public PageModel<JobLog> pageList(HttpServletRequest request, JobLog jLog, String filterTime) {

		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (filterTime!=null && filterTime.trim().length()>0) {
			String[] temp = filterTime.split(" - ");
			if (temp!=null && temp.length == 2) {
//				triggerTimeStart = DateUtil.parseDateTime(temp[0]);
//				triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
			}
		}
		
		return jobServiceReference.jobService.logPageList(PageRequestUtil.fromRequest(request),jLog);
	}

	@RequestMapping("/logDetailPage")
	public String logDetailPage(long id, Model model){

		// base check
		ReturnT<String> logStatue = ReturnT.SUCCESS;
		JobLog jobLog = jobServiceReference.jobService.findJobLogByJobLogId(id);
		if (jobLog == null) {
            throw new RuntimeException("日志异常");
		}

        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
        model.addAttribute("logId", jobLog.getJobLogId());
		return "joblog/joblog.detail";
	}

	@RequestMapping("/logDetailCat")
	@ResponseBody
	public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum){
		try {
//			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
//			ReturnT<LogResult> logResult = executorBiz.log(triggerTime, logId, fromLineNum);

			ReturnT<LogResult> logResult = jobServiceReference.jobService.catLog(triggerTime, logId, fromLineNum);
			// is end
            if (logResult.getContent()!=null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                JobLog jobLog = jobServiceReference.jobService.findJobLogByJobLogId(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

			return logResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}

	@RequestMapping("/logKill")
	@ResponseBody
	public ReturnT<String> logKill(long id){
		// base check
		JobLog jobLog = jobServiceReference.jobService.findJobLogByJobLogId(id);
		Job jobInfo = jobServiceReference.jobService.findByJobId(jobLog.getJobId());
		if (jobInfo==null) {
			return new ReturnT<String>(500, "任务不存在");
		}
		if (ReturnT.SUCCESS_CODE != jobLog.getTriggerCode()) {
			return new ReturnT<String>(500, "日志执行异常，不kill");
		}

		// request of kill
		ReturnT<String> runResult = null;
		try {
			runResult = jobServiceReference.jobService.kill(jobInfo.getJobId());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			runResult = new ReturnT<String>(500, e.getMessage());
		}

		if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
			jobLog.setHandleCode(ReturnT.FAIL_CODE);
			jobLog.setHandleMsg((runResult.getMsg()!=null?runResult.getMsg():""));
			jobLog.setHandleTime(new Date());
			jobServiceReference.jobService.update(jobLog);
			return new ReturnT<String>(runResult.getMsg());
		} else {
			return new ReturnT<String>(500, runResult.getMsg());
		}
	}

	@RequestMapping("/clearLog")
	@ResponseBody
	public ReturnT<String> clearLog(long jobGroup, long jobId, int type){

		Date clearBeforeTime = null;
		int clearBeforeNum = 0;
		if (type == 1) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -1);	// 清理一个月之前日志数据
		} else if (type == 2) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -3);	// 清理三个月之前日志数据
		} else if (type == 3) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -6);	// 清理六个月之前日志数据
		} else if (type == 4) {
			clearBeforeTime = DateUtil.addYears(new Date(), -1);	// 清理一年之前日志数据
		} else if (type == 5) {
			clearBeforeNum = 1000;		// 清理一千条以前日志数据
		} else if (type == 6) {
			clearBeforeNum = 10000;		// 清理一万条以前日志数据
		} else if (type == 7) {
			clearBeforeNum = 30000;		// 清理三万条以前日志数据
		} else if (type == 8) {
			clearBeforeNum = 100000;	// 清理十万条以前日志数据
		} else if (type == 9) {
			clearBeforeNum = 0;			// 清理所有日志数据
		} else {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "类型异常");
		}

		jobServiceReference.jobService.clearLog(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
		return ReturnT.SUCCESS;
	}

}
