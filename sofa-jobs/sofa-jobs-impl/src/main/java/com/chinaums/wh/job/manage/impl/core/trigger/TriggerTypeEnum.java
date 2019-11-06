package com.chinaums.wh.job.manage.impl.core.trigger;

public enum TriggerTypeEnum {

    MANUAL("手动"),
    CRON("定时"),
    RETRY("重试");

    private TriggerTypeEnum(String title){
        this.title = title;
    }
    private String title;
    public String getTitle() {
        return title;
    }

}