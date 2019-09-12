package com.chinaums.wh.job.admin.controller;

import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobRegistry;
import com.chinaums.wh.job.manage.impl.core.util.I18nUtil;
import com.chinaums.wh.job.manage.impl.service.XxlJobGroupService;
import com.chinaums.wh.job.manage.impl.service.XxlJobInfoService;
import com.chinaums.wh.job.manage.impl.service.XxlJobRegistryService;
import me.izhong.dashboard.job.core.enums.RegistryConfig;
import me.izhong.dashboard.manage.annotation.AjaxWrapper;
import me.izhong.dashboard.manage.annotation.Log;
import me.izhong.dashboard.manage.constants.BusinessType;
import me.izhong.dashboard.manage.domain.PageModel;
import me.izhong.dashboard.manage.domain.PageRequest;
import me.izhong.dashboard.manage.expection.BusinessException;
import me.izhong.dashboard.manage.expection.job.TaskException;
import me.izhong.dashboard.manage.security.UserInfoContextHelper;
import me.izhong.dashboard.manage.util.Convert;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

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
	public XxlJobInfoService xxlJobInfoService;
	@Resource
	public XxlJobGroupService xxlJobGroupService;
	@Resource
	private XxlJobRegistryService xxlJobRegistryService;

	@RequestMapping
	public String index(Model model) {

		// job group (executor)
		List<XxlJobGroup> list = xxlJobGroupService.selectAll();

		model.addAttribute("list", list);
		return prefix + "/group";
	}

	@RequestMapping("/list")
	@AjaxWrapper
	public PageModel<XxlJobGroup> pageList(HttpServletRequest request, XxlJobGroup ino) {
		return xxlJobGroupService.selectPage(PageRequest.fromRequest(request),ino);
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
	public XxlJobGroup addSave(XxlJobGroup xxlJobGroup) throws SchedulerException, TaskException
	{
		if (xxlJobGroup.getAppName()==null || xxlJobGroup.getAppName().trim().length()==0) {
			throw BusinessException.build(I18nUtil.getString("system_please_input")+"AppName");
		}
		if (xxlJobGroup.getAppName().length()<4 || xxlJobGroup.getAppName().length()>64) {
			throw BusinessException.build(I18nUtil.getString("jobgroup_field_appName_length") );
		}
		if (xxlJobGroup.getTitle()==null || xxlJobGroup.getTitle().trim().length()==0) {
			throw BusinessException.build(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
		}
		if (xxlJobGroup.getAddressType()!=0) {
			if (xxlJobGroup.getAddressList()==null || xxlJobGroup.getAddressList().trim().length()==0) {
				throw BusinessException.build(I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (item==null || item.trim().length()==0) {
					throw BusinessException.build(I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}

		xxlJobGroup.setCreateBy(UserInfoContextHelper.getCurrentLoginName());
		xxlJobGroup.setUpdateBy(UserInfoContextHelper.getCurrentLoginName());
		return xxlJobGroupService.insert(xxlJobGroup);
	}


	/**
	 * 修改调度
	 */
	@GetMapping("/edit/{groupId}")
	public String edit(@PathVariable("groupId") Long groupId, ModelMap mmap)
	{
		mmap.put("group", xxlJobGroupService.selectByPId(groupId));
		return prefix + "/edit";
	}

	/**
	 * 修改保存调度
	 */
	@Log(title = "定时任务分组", businessType = BusinessType.UPDATE)
	@RequiresPermissions("monitor:job:edit")
	@PostMapping("/edit")
	@AjaxWrapper
	public XxlJobGroup editSave(XxlJobGroup xxlJobGroup) throws SchedulerException, TaskException
	{
		if (xxlJobGroup.getAppName()==null || xxlJobGroup.getAppName().trim().length()==0) {
			throw BusinessException.build(I18nUtil.getString("system_please_input")+"AppName");
		}
		if (xxlJobGroup.getAppName().length()<4 || xxlJobGroup.getAppName().length()>64) {
			throw BusinessException.build(I18nUtil.getString("jobgroup_field_appName_length") );
		}
		if (xxlJobGroup.getTitle()==null || xxlJobGroup.getTitle().trim().length()==0) {
			throw BusinessException.build(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
		}
		if (xxlJobGroup.getAddressType() == 0) {
			// 0=自动注册
			List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppName());
			String addressListStr = null;
			if (registryList!=null && !registryList.isEmpty()) {
				Collections.sort(registryList);
				addressListStr = "";
				for (String item:registryList) {
					addressListStr += item + ",";
				}
				addressListStr = addressListStr.substring(0, addressListStr.length()-1);
			}
			xxlJobGroup.setAddressList(addressListStr);
		} else {
			// 1=手动录入
			if (xxlJobGroup.getAddressList()==null || xxlJobGroup.getAddressList().trim().length()==0) {
				throw BusinessException.build(I18nUtil.getString("jobgroup_field_addressType_limit") );
			}
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (item==null || item.trim().length()==0) {
					throw BusinessException.build(I18nUtil.getString("jobgroup_field_registryList_unvalid") );
				}
			}
		}
		xxlJobGroup.setUpdateBy(UserInfoContextHelper.getCurrentLoginName());
		return xxlJobGroupService.update(xxlJobGroup);
	}

	private List<String> findRegistryByAppName(String appNameParam){
		HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
		List<XxlJobRegistry> list = xxlJobRegistryService.findNormal(RegistryConfig.DEAD_TIMEOUT);
		if (list != null) {
			for (XxlJobRegistry item: list) {
				if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
					String appName = item.getRegistryKey();
					List<String> registryList = appAddressMap.get(appName);
					if (registryList == null) {
						registryList = new ArrayList<String>();
					}

					if (!registryList.contains(item.getRegistryValue())) {
						registryList.add(item.getRegistryValue());
					}
					appAddressMap.put(appName, registryList);
				}
			}
		}
		return appAddressMap.get(appNameParam);
	}

	@RequestMapping("/remove")
	@AjaxWrapper
	public Long remove(String ids){

		// valid
		long count = xxlJobInfoService.count();
		if (count > 0) {
			throw BusinessException.build(I18nUtil.getString("jobgroup_del_limit_0") );
		}

		List<XxlJobGroup> allList = xxlJobGroupService.selectAll();
		if (allList.size() == 1) {
			throw BusinessException.build(I18nUtil.getString("jobgroup_del_limit_1") );
		}

		List<Long> idLongs = Convert.toLongList(ids);
		if(idLongs.size() < 1)
			throw BusinessException.build("删除的数量不能小于1");

		return xxlJobGroupService.remove(idLongs);
	}

	@RequestMapping("/find")
	@AjaxWrapper
	public XxlJobGroup loadById(long id){
		return xxlJobGroupService.selectByPId(id);
	}

}
