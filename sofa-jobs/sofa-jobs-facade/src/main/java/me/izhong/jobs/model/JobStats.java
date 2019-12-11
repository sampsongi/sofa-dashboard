package me.izhong.jobs.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
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
