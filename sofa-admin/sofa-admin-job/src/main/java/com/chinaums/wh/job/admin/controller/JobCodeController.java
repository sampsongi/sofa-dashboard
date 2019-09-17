package com.chinaums.wh.job.admin.controller;

import com.chinaums.wh.job.admin.service.JobServiceReference;
import com.chinaums.wh.job.model.Job;
import com.chinaums.wh.job.model.JobScript;
import com.chinaums.wh.job.type.GlueTypeEnum;
import com.chinaums.wh.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/jobcode")
public class JobCodeController {
	
	@Resource
	private JobServiceReference jobServiceReference;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, long jobId) {
		Job jobInfo = jobServiceReference.jobService.findByJobId(jobId);
		List<JobScript> jobLogGlues = jobServiceReference.jobScriptService.findByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException("任务未找到");
		}

		// Glue类型-字典
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "jobcode/jobcode.index";
	}
	
	@RequestMapping("/save")
	@ResponseBody
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
		
		// update new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdateTime(new Date());
		jobServiceReference.jobService.edit(exists_jobInfo);

		// log old code
		JobScript xxlJobLogGlue = new JobScript();
		xxlJobLogGlue.setJobId(exists_jobInfo.getJobId());
		xxlJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		xxlJobLogGlue.setGlueSource(glueSource);
		xxlJobLogGlue.setGlueRemark(glueRemark);
		jobServiceReference.jobScriptService.add(xxlJobLogGlue);

		// remove code backup more than 30
		jobServiceReference.jobScriptService.removeOld(exists_jobInfo.getJobId(), 30);

		return ReturnT.SUCCESS;
	}
	
}
