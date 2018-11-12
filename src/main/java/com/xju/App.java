package com.xju;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext app=SpringApplication.run(App.class, args);
			
		System.out.println("go");
	}

}
