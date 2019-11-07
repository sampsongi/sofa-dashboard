package me.izhong.jobs.agent.bean;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JobsConfigBean {

    @Value("${config.script.dir}")
    private String scriptPath;
}
