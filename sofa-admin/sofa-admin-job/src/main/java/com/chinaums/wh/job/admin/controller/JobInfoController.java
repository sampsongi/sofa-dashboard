package com.chinaums.wh.job.admin.controller;

import com.chinaums.wh.job.admin.service.JobServiceReference;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import com.chinaums.wh.job.manage.impl.core.route.ExecutorRouteStrategyEnum;
import com.chinaums.wh.job.manage.impl.core.thread.JobTriggerPoolHelper;
import com.chinaums.wh.job.manage.impl.core.trigger.TriggerTypeEnum;
import com.chinaums.wh.job.manage.impl.service.XxlJobGroupService;
import com.chinaums.wh.job.manage.impl.service.XxlJobService;
import me.izhong.dashboard.job.core.biz.model.ReturnT;
import me.izhong.dashboard.job.core.enums.ExecutorBlockStrategyEnum;
import me.izhong.dashboard.job.core.glue.GlueTypeEnum;
import me.izhong.dashboard.manage.annotation.AjaxWrapper;
import me.izhong.dashboard.manage.domain.PageModel;
import me.izhong.dashboard.manage.domain.PageRequest;
import me.izhong.dashboard.quartz.domain.SysJob;
import me.izhong.dashboard.quartz.util.CronUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
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
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	// 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	// 阻塞处理策略-字典

		jobServiceReference.jobGroupService.count()
		// 执行器列表
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupService.selectAll();

		// filter group
		List<XxlJobGroup> jobGroupList = jobGroupList_all;
		if (jobGroupList==null || jobGroupList.size()==0) {
		//	throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);
		return prefix + "/job";
	}

	@RequestMapping("/list")
	@AjaxWrapper
	public PageModel<XxlJobInfo> pageList(HttpServletRequest request, XxlJobInfo ino) {
		return xxlJobService.pageList(PageRequest.fromRequest(request),ino);
	}
	
	@RequestMapping("/add")
	@AjaxWrapper
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}
	
	@RequestMapping("/update")
	@AjaxWrapper
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}
	
	@RequestMapping("/remove")
	@AjaxWrapper
	public ReturnT<String> remove(int id) {
		return xxlJobService.remove(id);
	}
	
	@RequestMapping("/stop")
	@AjaxWrapper
	public ReturnT<String> pause(int id) {
		return xxlJobService.stop(id);
	}
	
	@RequestMapping("/start")
	@AjaxWrapper
	public ReturnT<String> start(int id) {
		return xxlJobService.start(id);
	}
	
	@RequestMapping("/trigger")
	@AjaxWrapper
	//@PermissionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
		return ReturnT.SUCCESS;
	}

	@PostMapping("/checkCronExpressionIsValid")
	@ResponseBody
	public boolean checkCronExpressionIsValid(SysJob job)
	{
		return CronUtil.isValid(job.getCronExpression());
	}
	
}
