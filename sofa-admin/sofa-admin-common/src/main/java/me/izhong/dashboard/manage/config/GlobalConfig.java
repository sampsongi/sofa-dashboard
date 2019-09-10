package me.izhong.dashboard.manage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dashboard")
public class GlobalConfig {

    public static final String SALT = "salt@tianru";

}
