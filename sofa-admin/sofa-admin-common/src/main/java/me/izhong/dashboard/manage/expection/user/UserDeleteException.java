package me.izhong.dashboard.manage.expection.user;

import com.chinaums.wh.db.common.exception.BusinessException;

public class UserDeleteException extends BusinessException {


    public static final String KEY = "USER_DELETE";

    public UserDeleteException() {
        super(KEY, "用户已删除");
    }

    public UserDeleteException(String message) {
        super(KEY, message);
    }
}
