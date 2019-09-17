package me.izhong.dashboard.manage.expection.user;

import com.chinaums.wh.db.common.exception.BusinessException;

public class UserPasswordRetryLimitExceedException extends BusinessException {


    public static final String KEY = "USER_PASSWORD_EXCEED";

    public UserPasswordRetryLimitExceedException() {
        super(KEY, "密码次数超限");
    }

    public UserPasswordRetryLimitExceedException(String message) {
        super(KEY, message);
    }

    public UserPasswordRetryLimitExceedException(long count) {
        super(KEY, "错误次数" + count);
    }
}
