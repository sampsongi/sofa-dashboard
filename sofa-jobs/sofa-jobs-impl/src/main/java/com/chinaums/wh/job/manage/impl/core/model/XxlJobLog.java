package com.chinaums.wh.job.manage.impl.core.model;

import lombok.Data;
import me.izhong.dashboard.manage.annotation.AutoId;
import me.izhong.dashboard.manage.annotation.PrimaryId;
import me.izhong.dashboard.manage.domain.TimedBasedEntity;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

@Data
public class XxlJobLog  extends TimedBasedEntity implements Serializable {

	@AutoId
	@PrimaryId
	@Indexed(unique = true)
	private long jobLogId;
	
	// job info
	private long jobGroup;
	private long jobId;

	// execute info
	private String executorAddress;
	private String executorHandler;
	private String executorParam;
	private String executorShardingParam;
	private int executorFailRetryCount;
	
	// trigger info
	private Date triggerTime;
	private int triggerCode;
	private String triggerMsg;
	
	// handle info
	private Date handleTime;
	private int handleCode;
	private String handleMsg;

	// alarm info
	private int alarmStatus;

}
