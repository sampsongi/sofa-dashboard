package com.chinaums.wh.job.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class JobGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupName;
    private String remark;
    private Long groupId;
    private Long order;
    private List<String> groupUrl;

    private String createBy;
    private String updateBy;
}
