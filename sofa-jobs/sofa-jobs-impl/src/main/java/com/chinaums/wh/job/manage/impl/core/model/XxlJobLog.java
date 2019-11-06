package com.chinaums.wh.job.manage.impl.core.model;

import com.chinaums.wh.db.common.domain.TimedBasedEntity;
import lombok.Data;
import com.chinaums.wh.db.common.annotation.AutoId;
import com.chinaums.wh.db.common.annotation.PrimaryId;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "sys_djob_log")
public class XxlJobLog  extends TimedBasedEntity implements Serializable {

	@AutoId
	@PrimaryId
	@Indexed(unique = true)
	private Long jobLogId;

	private String jobDesc;
	private Long jobGroupId;
	private Long jobId;

	// execute info
	private String executorAddress;
	private String executorHandler;
	private String executorParam;
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
