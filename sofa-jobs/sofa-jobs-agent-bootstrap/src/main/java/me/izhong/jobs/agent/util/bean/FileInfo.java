package me.izhong.jobs.agent.util.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class FileInfo {
    private String fileName;
    private Date modifyTime;
    long size;
}
