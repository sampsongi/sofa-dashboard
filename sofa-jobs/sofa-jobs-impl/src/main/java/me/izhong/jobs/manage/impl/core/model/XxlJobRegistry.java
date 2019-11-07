package me.izhong.jobs.manage.impl.core.model;

import me.izhong.db.common.domain.TimedBasedEntity;
import lombok.Data;
import me.izhong.db.common.annotation.AutoId;
import me.izhong.db.common.annotation.PrimaryId;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "sys_djob_registry")
public class XxlJobRegistry  extends TimedBasedEntity implements Serializable {

    @AutoId
    @PrimaryId
    @Indexed(unique = true)
    private Long jobRegistryId;

    private String registryGroup;
    private String registryKey;
    private String registryValue;

}
