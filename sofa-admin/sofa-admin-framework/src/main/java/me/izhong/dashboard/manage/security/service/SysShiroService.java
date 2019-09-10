package me.izhong.dashboard.manage.security.service;

import me.izhong.dashboard.manage.entity.SysUserOnline;
import me.izhong.dashboard.manage.security.session.OnlineSession;
import me.izhong.dashboard.manage.service.SysUserOnlineService;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 会话db操作处理
 */
@Component
public class SysShiroService {
    @Autowired
    private SysUserOnlineService onlineService;

    /**
     * 删除会话
     *
     * @param onlineSession 会话信息
     */
    public void deleteSession(OnlineSession onlineSession) {
        onlineService.deleteByPId(String.valueOf(onlineSession.getId()));
    }

    /**
     * 获取会话信息
     *
     * @param sessionId
     * @return
     */
    public Session getSession(Serializable sessionId) {
        SysUserOnline sysUserOnline = onlineService.selectByPId(String.valueOf(sessionId));
        return sysUserOnline == null ? null : createSession(sysUserOnline);
    }

    private Session createSession(SysUserOnline sysUserOnline) {
        if (sysUserOnline != null) {
            OnlineSession onlineSession = new OnlineSession();
            onlineSession.setId(sysUserOnline.getSessionId());
            onlineSession.setHost(sysUserOnline.getIpAddr());
            onlineSession.setBrowser(sysUserOnline.getBrowser());
            onlineSession.setOs(sysUserOnline.getOs());
            onlineSession.setBrowser(sysUserOnline.getBrowser());
            onlineSession.setDeptName(sysUserOnline.getDeptName());
            onlineSession.setLoginName(sysUserOnline.getLoginName());
            onlineSession.setStartTimestamp(sysUserOnline.getStartTimestamp());
            onlineSession.setLastAccessTime(sysUserOnline.getLastAccessTime());
            onlineSession.setTimeout(sysUserOnline.getExpireTime() == null ? 0L : sysUserOnline.getExpireTime());
            return onlineSession;
        }
        return null;
    }
}
