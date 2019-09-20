package com.chinaums.wh.job.admin.controller;

import com.chinaums.wh.common.util.CronUtil;
import com.chinaums.wh.db.common.util.PageRequestUtil;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.job.admin.service.JobServiceReference;
import com.chinaums.wh.db.common.annotation.AjaxWrapper;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.JobGroup;
import com.chinaums.wh.job.type.GlueTypeEnum;
import com.chinaums.wh.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/monitor/djob")
public class JobInfoController {

	private String prefix = "monitor/djob";

	@Resource
	private JobServiceReference jobServiceReference;

	@GetMapping()
	public String job( Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup)
	{
		// 枚举-字典
		//model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	// 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		//model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	// 阻塞处理策略-字典

		// 执行器列表
		List<JobGroup> jobGroupList_all =  jobServiceReference.jobGroupService.selectAll();

		// filter group
		List<JobGroup> jobGroupList = jobGroupList_all;
		if (jobGroupList==null || jobGroupList.size()==0) {
		//	throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);
		return prefix + "/job";
	}

	@RequestMapping("/list")
	@AjaxWrapper
	public PageModel<Job> pageList(HttpServletRequest request, Job ino) {
		return jobServiceReference.jobService.pageList(PageRequestUtil.fromRequest(request),ino);
	}
	
	@RequestMapping("/add")
	@AjaxWrapper
	public ReturnT<String> add(Job jobInfo) {
		return jobServiceReference.jobService.add(jobInfo);
	}
	
	@RequestMapping("/update")
	@AjaxWrapper
	public ReturnT<String> update(Job jobInfo) {
		return jobServiceReference.jobService.update(jobInfo);
	}
	
	@RequestMapping("/remove")
	@AjaxWrapper
	public ReturnT<String> remove(Long id) {
		return jobServiceReference.jobService.remove(id);
	}
	
	@RequestMapping("/stop")
	@AjaxWrapper
	public ReturnT<String> stop(Long id) {
		return jobServiceReference.jobService.kill(id);
	}
	
	@RequestMapping("/start")
	@AjaxWrapper
	public ReturnT<Job> start(Long id) {
		return jobServiceReference.jobService.start(id);
	}
	
	@RequestMapping("/trigger")
	@AjaxWrapper
	public ReturnT<Job> triggerJob(Long id, String executorParam) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}
		return jobServiceReference.jobService.start(id);
		//JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
	}

	@PostMapping("/checkCronExpressionIsValid")
	@ResponseBody
	public boolean checkCronExpressionIsValid(String CronExpression)
	{
		return CronUtil.isValid(CronExpression);
	}
	
}
