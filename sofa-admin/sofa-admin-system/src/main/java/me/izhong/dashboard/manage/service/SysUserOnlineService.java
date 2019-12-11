package me.izhong.dashboard.manage.service;

import me.izhong.db.common.service.CrudBaseService;
import me.izhong.dashboard.manage.entity.SysUserOnline;

import java.util.Date;
import java.util.List;

public interface SysUserOnlineService extends CrudBaseService<String,SysUserOnline> {

    public void batchDeleteOnline(List<String> sessionIds);

    public void saveOnline(SysUserOnline online);

    public void forceLogout(String sessionId);

    public List<SysUserOnline> selectOnlineByLastAccessTime(Date lastAccessTime);
}
