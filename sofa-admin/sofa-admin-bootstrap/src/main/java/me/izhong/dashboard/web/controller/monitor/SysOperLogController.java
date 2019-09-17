package me.izhong.dashboard.web.controller.monitor;

import com.chinaums.wh.db.common.annotation.AjaxWrapper;
import me.izhong.dashboard.manage.annotation.Log;
import me.izhong.dashboard.manage.constants.BusinessType;
import me.izhong.dashboard.manage.entity.SysOperLog;
import me.izhong.dashboard.manage.security.config.PermissionConstants;
import me.izhong.dashboard.manage.service.SysOperLogService;
import me.izhong.dashboard.manage.util.ExcelUtil;
import me.izhong.dashboard.manage.domain.PageModel;
import me.izhong.dashboard.manage.domain.PageRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/monitor/operlog")
public class SysOperLogController {

    private String prefix = "monitor/operlog";

    @Autowired
    private SysOperLogService sysOperLogService;

    @RequiresPermissions(PermissionConstants.OperLog.VIEW)
    @GetMapping()
    public String operlog() {
        return prefix + "/operlog";
    }

    @RequiresPermissions(PermissionConstants.OperLog.VIEW)
    @PostMapping("/list")
    @AjaxWrapper
    public PageModel list(HttpServletRequest request, SysOperLog sysOperLog) {
        return sysOperLogService.selectPage(PageRequest.fromRequest(request), sysOperLog);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions(PermissionConstants.OperLog.EXPORT)
    @PostMapping("/export")
    @AjaxWrapper
    public String export(HttpServletRequest request, SysOperLog sysOperLog) {
        List<SysOperLog> list = sysOperLogService.selectList(PageRequest.fromRequestIgnorePageSize(request), sysOperLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        return util.exportExcel(list, "操作日志");
    }

    @RequiresPermissions(PermissionConstants.OperLog.REMOVE)
    @PostMapping("/remove")
    @AjaxWrapper
    public long remove(String ids) {
        return sysOperLogService.deleteByPIds(ids);
    }

    @RequiresPermissions(PermissionConstants.OperLog.VIEW)
    @GetMapping("/detail/{operId}")
    public String detail(@PathVariable("operId") Long operId, ModelMap mmap) {
        mmap.put("operLog", sysOperLogService.selectByPId(operId));
        return prefix + "/detail";
    }

    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @RequiresPermissions(PermissionConstants.OperLog.REMOVE)
    @PostMapping("/clean")
    @AjaxWrapper
    public void clean() {
        sysOperLogService.clearAll();
    }

}
