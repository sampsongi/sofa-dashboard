package me.izhong.jobs.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class JobStats implements Serializable {
	private Long statsId;
	private String key;
	private String type;
	private String value1;
	private String value2;
	private String value3;
	private String value4;
	private String value5;
	private Date expireTime;

}
