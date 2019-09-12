package com.chinaums.wh.job.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Job implements Serializable {
    private String jobName;
    private String jobKey;
    private String jobGroup;
}
