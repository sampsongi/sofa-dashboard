package me.izhong.jobs.admin.controller;

import me.izhong.db.common.annotation.AjaxWrapper;
import me.izhong.jobs.admin.service.JobServiceReference;
import me.izhong.jobs.model.Job;
import me.izhong.jobs.model.JobScript;
import me.izhong.jobs.type.GlueTypeEnum;
import me.izhong.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/monitor/djob")
public class JobCodeController {

	private String prefix = "monitor/djob";

	@Autowired(required = false)
	private JobServiceReference jobServiceReference;

	@RequestMapping("/code")
	public String index(HttpServletRequest request, Model model, long jobId) {
		Job jobInfo = jobServiceReference.jobService.findByJobId(jobId);
		List<JobScript> jobLogGlues = jobServiceReference.jobService.findJobScriptByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException("任务未找到");
		}

		// Glue类型-字典
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return prefix + "/jobcode";
	}
	
	@RequestMapping("/code/save")
	@AjaxWrapper
	public ReturnT<String> save(Model model, long id, String glueSource, String glueRemark) {
		// valid
		if (glueRemark==null) {
			return new ReturnT<String>(500, "缺少备注");
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return new ReturnT<String>(500, "备注长度4-100");
		}
		Job exists_jobInfo = jobServiceReference.jobService.findByJobId(id);
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, "任务不存在");
		}
		
		// updateJobGroup new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdateTime(new Date());
		jobServiceReference.jobService.update(exists_jobInfo);

		// log old code
		JobScript xxlJobLogGlue = new JobScript();
		xxlJobLogGlue.setJobId(exists_jobInfo.getJobId());
		xxlJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		xxlJobLogGlue.setGlueSource(glueSource);
		xxlJobLogGlue.setGlueRemark(glueRemark);
		jobServiceReference.jobService.addJobScript(xxlJobLogGlue);

		// removeJobGroup code backup more than 30
		jobServiceReference.jobService.removeOldLog(exists_jobInfo.getJobId(), 30);

		return ReturnT.SUCCESS;
	}
	
}
