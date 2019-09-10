package me.izhong.dashboard.manage.expection.user;

import me.izhong.dashboard.manage.expection.BusinessException;

public class UserBlockedException extends BusinessException {


    public static final String KEY = "USER_BLOCK";

    public UserBlockedException() {
        super(KEY, "用户已禁用");
    }

    public UserBlockedException(String message) {
        super(KEY, message);
    }
}
