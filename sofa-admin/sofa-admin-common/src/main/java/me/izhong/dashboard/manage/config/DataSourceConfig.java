package me.izhong.dashboard.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties("spring.datasource")
public class DataSourceConfig {


    private String url;
    private String username;
    private String password;

//    @Bean
//    public DataSource getDataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(url);
//        dataSource.setLoginName(loginName);// 用户名
//        dataSource.setPassword(password);// 密码
//        return dataSource;
//    }


}
