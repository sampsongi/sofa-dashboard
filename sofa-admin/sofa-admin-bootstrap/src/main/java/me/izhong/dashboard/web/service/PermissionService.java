package me.izhong.dashboard.web.service;

import com.chinaums.wh.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import me.izhong.dashboard.manage.service.SysMenuService;
import me.izhong.dashboard.manage.security.UserInfoContextHelper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Set;

@Service("permission")
@Slf4j
public class PermissionService {
    @Autowired
    private SysMenuService sysMenuService;

    public String hasPermi(String permission) {

        UserInfo user = UserInfoContextHelper.getLoginUser();

        Set<String> menus = sysMenuService.selectPermsByUserId(user.getUserId());

        String x = isPermittedOperator(permission) ? "" : "hidden";
        log.info(x);
        return "";
    }

    public String hasRole(String role) {
        return hasRoleOperator(role) ? "" : "hidden";
    }

    /**
     * 判断用户是否拥有某个权限
     *
     * @param permission 权限字符串
     * @return 结果
     */
    private boolean isPermittedOperator(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }

    /**
     * 判断用户是否拥有某个角色
     *
     * @param role 角色字符串
     * @return 结果
     */
    private boolean hasRoleOperator(String role) {
        return SecurityUtils.getSubject().hasRole(role);
    }

    /**
     * 返回用户属性值
     *
     * @param property 属性名称
     * @return 用户属性值
     */
    public Object getPrincipalProperty(String property) {
        UserInfo u = UserInfoContextHelper.getLoginUser();
        if (u != null) {
            try {
                BeanInfo bi = Introspector.getBeanInfo(u.getClass());
                for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                    if (pd.getName().equals(property) == true) {
                        return pd.getReadMethod().invoke(u, (Object[]) null);
                    }
                }
            } catch (Exception e) {
                log.error("Error reading property [{}] from principal of type [{}]", property,
                        u.getClass().getName());
            }
        }
        return null;
    }
}
