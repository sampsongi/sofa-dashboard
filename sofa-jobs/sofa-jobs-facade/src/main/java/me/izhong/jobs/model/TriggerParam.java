package me.izhong.jobs.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TriggerParam implements Serializable{
    private static final long serialVersionUID = 42L;

    private long jobId;

    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private Long executorTimeout;
    private long logId;

    @Override
    public String toString() {
        return "TriggerParam{" +
                "jobId=" + jobId +
                ", executorHandler='" + executorHandler + '\'' +
                ", executorParams='" + executorParams + '\'' +
                ", executorBlockStrategy='" + executorBlockStrategy + '\'' +
                ", executorTimeout=" + executorTimeout +
                ", logId=" + logId +
                '}';
    }

}
