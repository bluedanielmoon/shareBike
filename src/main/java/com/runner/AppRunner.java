package com.runner;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.init.InitPoi;
import com.pojo.AppInfo;
import com.util.FilesUtil;

@Component
/**
 * Spring上下文加载完后执行，可以用来初始化本应用一些条件
 * @author Administrator
 *
 */
public class AppRunner implements ApplicationRunner{
	
	@Autowired
	private AppInfo info;
	
	@Autowired
	private InitPoi initer;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("App start!");	
		System.out.println("Author:"+info.getName());
		System.out.println("Date:"+new Date());
		System.out.println("Age:"+info.getAge());
		
		initer.initBus();
		initer.initSubway();
	}

}
