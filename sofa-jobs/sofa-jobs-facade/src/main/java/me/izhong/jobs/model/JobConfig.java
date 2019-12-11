package me.izhong.jobs.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class JobConfig implements Serializable {

    private Long configId;
    private String configName;
    private String configKey;
    private String configValue;
    private String configType;
}
