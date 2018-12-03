package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

import net.bytebuddy.asm.Advice.This;

public class PropertyUtil {

	private static Properties prop;
	private static String propFile="/Users/daniel/projects/java/shareBike/src/main/resources/config/info.properties";


	public static void InitState(Properties prop) {
		PropertyUtil.prop = prop;
	}
	
	//下面是生产环境下的做法，即生产环境下要换成去classes里面去获取

//	public static void updateProp(String key, String value) {
//		prop.put(key, value);
//		
//
//		OutputStream outputStream;
//		try {
//			outputStream = new FileOutputStream(new File(PropertyUtil.class.getResource(propFile).getPath()));
//			prop.store(outputStream, "");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	public static void updateProp(String key, String value) {
		prop.put(key, value);
		

		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(new File(propFile));
			prop.store(outputStream, "");
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(new File(propFile)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PropertyUtil.InitState(properties);
		PropertyUtil.updateProp("busType", "999");

	}

}
