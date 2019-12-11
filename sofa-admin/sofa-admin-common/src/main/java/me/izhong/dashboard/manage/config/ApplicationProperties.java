package me.izhong.dashboard.manage.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    @Getter
    @Setter
    /*
      是否开启人机验证
     */
    public boolean enableBotCaptcha = true;
    @Getter
    public final Luosimao luosimao = new Luosimao();
    @Getter
    public final Jianzhou jianzhou = new Jianzhou();

    /**
     * 建周配置
     */
    @Data
    public class Jianzhou {
        private String account;
        private String md5password;
    }

    /**
     * 螺丝帽配置
     */
    @Data
    public class Luosimao {
        private String captchaVerifyUrl;
        private String apiKey;
    }
}
