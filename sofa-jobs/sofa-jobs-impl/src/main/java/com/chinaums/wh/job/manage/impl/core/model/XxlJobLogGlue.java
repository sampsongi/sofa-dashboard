package com.chinaums.wh.job.manage.impl.core.model;

import com.chinaums.wh.db.common.annotation.AutoId;
import com.chinaums.wh.db.common.annotation.PrimaryId;
import com.chinaums.wh.db.common.domain.TimedBasedEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

@Data
public class XxlJobLogGlue  extends TimedBasedEntity implements Serializable {

	@AutoId
	@PrimaryId
	@Indexed(unique = true)
	private Long jobLongGlueId;// 任务主键ID

	@Indexed(unique = false)
	private Long jobId;

	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	private String glueSource;
	private String glueRemark;
	private String addTime;

}
