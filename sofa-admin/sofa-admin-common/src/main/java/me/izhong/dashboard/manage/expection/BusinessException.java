package me.izhong.dashboard.manage.expection;

import me.izhong.dashboard.web.bean.ResponseContainer;


public class BusinessException extends RuntimeException {

    private String code;

    public BusinessException() {

    }

    public BusinessException(String code, String msg) {
        this(code, msg, null);
    }

    public BusinessException(String code, String msg, Exception e) {
        super(msg, e);
        this.code = code;
    }

    public static BusinessException build(String msg) {
        return new BusinessException(ResponseContainer.FAIL_CODE, msg);
    }

    public static BusinessException build(String msg, Exception e) {
        return new BusinessException(ResponseContainer.FAIL_CODE, msg, e);
    }

    public static BusinessException build(String code, String msg) {
        return new BusinessException(code, msg);
    }

    public static BusinessException build(String code, String msg, Exception e) {
        return new BusinessException(code, msg, e);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
