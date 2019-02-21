package com.xju.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {

	@Autowired
	private LoginIntercepter loginIntercepter;

	// @Override
	// public void addResourceHandlers(ResourceHandlerRegistry registry) {
	// 其他静态资源，与本文关系不大
	// registry.addResourceHandler("/upload/**").addResourceLocations("file:"+
	// TaleUtils.getUplodFilePath()+"upload/");
	// 需要配置1：----------- 需要告知系统，这是要被当成静态文件的！
	// 第一个方法设置访问路径前缀，第二个方法设置资源路径
	// registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	// }

//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/index").setViewName("index.html");
//	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// excludePathPatterns,除了这里面的不拦截其他都拦截
		registry.addInterceptor(loginIntercepter).addPathPatterns("/**").excludePathPatterns("/login.html")
				.excludePathPatterns("/css/**").excludePathPatterns("/fonts/**").excludePathPatterns("/image/**")
				.excludePathPatterns("/js/**");
	}
}
