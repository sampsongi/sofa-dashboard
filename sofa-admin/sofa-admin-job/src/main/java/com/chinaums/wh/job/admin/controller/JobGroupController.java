package com.chinaums.wh.job.admin.controller;

import com.chinaums.wh.db.common.annotation.AjaxWrapper;
import com.chinaums.wh.db.common.util.PageRequestUtil;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.job.admin.service.JobServiceReference;
import com.chinaums.wh.job.model.JobGroup;
import me.izhong.dashboard.manage.annotation.Log;
import me.izhong.dashboard.manage.constants.BusinessType;
import com.chinaums.wh.db.common.exception.BusinessException;
import com.chinaums.wh.common.util.Convert;
import me.izhong.dashboard.manage.security.UserInfoContextHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/monitor/djob/group")
public class JobGroupController {

	private String prefix = "monitor/djob/group";

	@Autowired(required = false)
	private JobServiceReference jobServiceReference;

	@RequestMapping
	public String index(Model model) {
//
//		List<JobGroup> list = jobServiceReference.jobService.selectAllJobGroup();
//
//		model.addAttribute("list", list);
		return prefix + "/group";
	}

	@RequestMapping("/list")
	@AjaxWrapper
	public PageModel<JobGroup> pageList(HttpServletRequest request, JobGroup ino) {
		return jobServiceReference.jobService.selectJobGroupPage(PageRequestUtil.fromRequest(request),ino);
	}

	/**
	 * 新增调度
	 */
	@GetMapping("/add")
	public String add() {
		return prefix + "/add";
	}

	/**
	 * 新增保存调度
	 */
	@Log(title = "定时任务分组", businessType = BusinessType.ADD)
	@RequiresPermissions("monitor:job:group:add")
	@PostMapping("/add")
	@AjaxWrapper
	public JobGroup addSave(JobGroup jg) throws BusinessException
	{
		if (jg.getGroupName()==null || jg.getGroupName().trim().length()==0) {
			throw BusinessException.build("GroupName不能为空");
		}
		if (jg.getGroupName().length()<4 || jg.getGroupName().length()>64) {
            throw BusinessException.build("GroupName长度4-64");
		}


		jg.setCreateBy(UserInfoContextHelper.getCurrentLoginName());
		jg.setUpdateBy(UserInfoContextHelper.getCurrentLoginName());
		return jobServiceReference.jobService.addJobGroup(jg);
	}


	/**
	 * 修改调度
	 */
	@GetMapping("/edit/{groupId}")
	public String edit(@PathVariable("groupId") Long groupId, ModelMap mmap)
	{
		mmap.put("group", jobServiceReference.jobService.findJobGroup(groupId));
		return prefix + "/edit";
	}

	/**
	 * 修改保存调度
	 */
	@Log(title = "定时任务分组", businessType = BusinessType.UPDATE)
	@RequiresPermissions("monitor:job:group:edit")
	@PostMapping("/edit")
	@AjaxWrapper
	public JobGroup editSave(JobGroup JobGroup) throws BusinessException
	{
		if (JobGroup.getGroupName()==null || JobGroup.getGroupName().trim().length()==0) {
			throw BusinessException.build("GroupName不能为空");
		}
		if (JobGroup.getGroupName().length()<4 || JobGroup.getGroupName().length()>64) {
			throw BusinessException.build("GroupName长度需要在4-64之间");
		}

		JobGroup.setUpdateBy(UserInfoContextHelper.getCurrentLoginName());
		return jobServiceReference.jobService.updateJobGroup(JobGroup);
	}

//	private List<String> findRegistryByAppName(String appNameParam){
//		HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
//		List<XxlJobRegistry> list = xxlJobRegistryService.findNormal(RegistryConfig.DEAD_TIMEOUT);
//		if (list != null) {
//			for (XxlJobRegistry item: list) {
//				if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
//					String appName = item.getRegistryKey();
//					List<String> registryList = appAddressMap.get(appName);
//					if (registryList == null) {
//						registryList = new ArrayList<String>();
//					}
//
//					if (!registryList.contains(item.getRegistryValue())) {
//						registryList.add(item.getRegistryValue());
//					}
//					appAddressMap.put(appName, registryList);
//				}
//			}
//		}
//		return appAddressMap.get(appNameParam);
//	}

	@RequestMapping("/remove")
	@AjaxWrapper
	public Long remove(String ids){

		// valid
//		long count = jobServiceReference.jobGroupService.count();
//		if (count > 0) {
//			throw BusinessException.build(I18nUtil.getString("jobgroup_del_limit_0") );
//		}
//
//		List<JobGroup> allList = JobGroupService.selectAllJobGroup();
//		if (allList.size() == 1) {
//			throw BusinessException.build(I18nUtil.getString("jobgroup_del_limit_1") );
//		}

		List<Long> idLongs = Convert.toLongList(ids);
		if(idLongs.size() < 1)
			throw BusinessException.build("删除的数量不能小于1");

		return jobServiceReference.jobService.removeJobGroup(idLongs);
	}

	@RequestMapping("/find")
	@AjaxWrapper
	public JobGroup loadById(long id){
		return jobServiceReference.jobService.findJobGroup(id);
	}

}
