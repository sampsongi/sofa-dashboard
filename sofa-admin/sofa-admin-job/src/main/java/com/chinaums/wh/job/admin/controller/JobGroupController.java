package com.chinaums.wh.job.admin.controller;

import com.chinaums.wh.db.common.annotation.AjaxWrapper;
import com.chinaums.wh.db.common.util.PageRequestUtil;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.job.admin.service.JobServiceReference;
import com.chinaums.wh.job.model.JobGroup;
import me.izhong.dashboard.manage.annotation.Log;
import me.izhong.dashboard.manage.constants.BusinessType;
import com.chinaums.wh.db.common.exception.BusinessException;
import me.izhong.dashboard.manage.expection.job.TaskException;
import com.chinaums.wh.common.util.Convert;
import me.izhong.dashboard.manage.security.UserInfoContextHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * job group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/monitor/djob/group")
public class JobGroupController {

	private String prefix = "monitor/djob/group";

	@Resource
	private JobServiceReference jobServiceReference;

	@RequestMapping
	public String index(Model model) {

		// job group (executor)
		List<JobGroup> list = jobServiceReference.jobGroupService.selectAll();

		model.addAttribute("list", list);
		return prefix + "/group";
	}

	@RequestMapping("/list")
	@AjaxWrapper
	public PageModel<JobGroup> pageList(HttpServletRequest request, JobGroup ino) {
		return jobServiceReference.jobGroupService.selectPage(PageRequestUtil.fromRequest(request),ino);
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
	@RequiresPermissions("monitor:job:add")
	@PostMapping("/add")
	@AjaxWrapper
	public JobGroup addSave(JobGroup JobGroup) throws BusinessException
	{
		if (JobGroup.getGroupName()==null || JobGroup.getGroupName().trim().length()==0) {
			throw BusinessException.build("GroupName不能为空");
		}
		if (JobGroup.getGroupName().length()<4 || JobGroup.getGroupName().length()>64) {
            throw BusinessException.build("GroupName长度4-64");
		}
		
//		if (JobGroup.getAddressType()!=0) {
//			if (JobGroup.getAddressList()==null || JobGroup.getAddressList().trim().length()==0) {
//				throw BusinessException.build(I18nUtil.getString("jobgroup_field_addressType_limit") );
//			}
//			String[] addresss = JobGroup.getAddressList().split(",");
//			for (String item: addresss) {
//				if (item==null || item.trim().length()==0) {
//					throw BusinessException.build(I18nUtil.getString("jobgroup_field_registryList_unvalid") );
//				}
//			}
//		}

		JobGroup.setCreateBy(UserInfoContextHelper.getCurrentLoginName());
		JobGroup.setUpdateBy(UserInfoContextHelper.getCurrentLoginName());
		return jobServiceReference.jobGroupService.add(JobGroup);
	}


	/**
	 * 修改调度
	 */
	@GetMapping("/edit/{groupId}")
	public String edit(@PathVariable("groupId") Long groupId, ModelMap mmap)
	{
		mmap.put("group", jobServiceReference.jobGroupService.find(groupId));
		return prefix + "/edit";
	}

	/**
	 * 修改保存调度
	 */
	@Log(title = "定时任务分组", businessType = BusinessType.UPDATE)
	@RequiresPermissions("monitor:job:edit")
	@PostMapping("/edit")
	@AjaxWrapper
	public JobGroup editSave(JobGroup JobGroup) throws BusinessException
	{
		if (JobGroup.getGroupName()==null || JobGroup.getGroupName().trim().length()==0) {
			throw BusinessException.build("GroupName不能为空");
		}
		if (JobGroup.getGroupName().length()<4 || JobGroup.getGroupName().length()>64) {
			throw BusinessException.build("GroupName长度4-64");
		}
//		if (JobGroup.getAddressType() == 0) {
//			// 0=自动注册
//			List<String> registryList = findRegistryByAppName(JobGroup.getAppName());
//			String addressListStr = null;
//			if (registryList!=null && !registryList.isEmpty()) {
//				Collections.sort(registryList);
//				addressListStr = "";
//				for (String item:registryList) {
//					addressListStr += item + ",";
//				}
//				addressListStr = addressListStr.substring(0, addressListStr.length()-1);
//			}
//			JobGroup.setAddressList(addressListStr);
//		} else {
//			// 1=手动录入
//			if (JobGroup.getAddressList()==null || JobGroup.getAddressList().trim().length()==0) {
//				throw BusinessException.build(I18nUtil.getString("jobgroup_field_addressType_limit") );
//			}
//			String[] addresss = JobGroup.getAddressList().split(",");
//			for (String item: addresss) {
//				if (item==null || item.trim().length()==0) {
//					throw BusinessException.build(I18nUtil.getString("jobgroup_field_registryList_unvalid") );
//				}
//			}
//		}
		JobGroup.setUpdateBy(UserInfoContextHelper.getCurrentLoginName());
		return jobServiceReference.jobGroupService.update(JobGroup);
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
//		List<JobGroup> allList = JobGroupService.selectAll();
//		if (allList.size() == 1) {
//			throw BusinessException.build(I18nUtil.getString("jobgroup_del_limit_1") );
//		}

		List<Long> idLongs = Convert.toLongList(ids);
		if(idLongs.size() < 1)
			throw BusinessException.build("删除的数量不能小于1");

		return jobServiceReference.jobGroupService.remove(idLongs);
	}

	@RequestMapping("/find")
	@AjaxWrapper
	public JobGroup loadById(long id){
		return jobServiceReference.jobGroupService.find(id);
	}

}
