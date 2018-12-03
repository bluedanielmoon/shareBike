package com.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

@PropertySource("classpath:config/info.properties")
@ConfigurationProperties(prefix="author")
@Repository
public class AppInfo {
	
	
}
