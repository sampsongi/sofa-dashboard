package me.izhong.jobs.manage.impl.core.model;

import me.izhong.db.common.annotation.AutoId;
import me.izhong.db.common.annotation.PrimaryId;
import me.izhong.db.common.domain.TimedBasedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "sys_djob_log_glue")
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
