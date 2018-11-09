package com.runner;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Component
/**
 * 用来返回退出应用的信号
 * 通过SpringApplication.exit(app)来获取
 * @author daniel
 *
 */
public class Exitor implements ExitCodeGenerator{

	@Override
	public int getExitCode() {
		
		return 52;
	}

}
