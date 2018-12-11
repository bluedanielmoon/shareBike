package com.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.util.PropertyUtil;

@Component
public class InitState {
	
	public void init() {
		
		Properties properties=new Properties();
	
		
		//开发下用这个地址，如果实际部署，要变为classes文件下
		String propFile="/Users/daniel/projects/java/shareBike/src/main/resources/config/info.properties";

		try {
			//properties.load(this.getClass().getResourceAsStream(propFile));
			properties.load(new FileInputStream(new File(propFile)));
			
			PropertyUtil.InitState(properties);
			
			Set<Object> keys=properties.keySet();
			
			
			String s_area=properties.getProperty("area");
			String[] list_area=s_area.split(",");
			double[] double_area=new double[] {Double.parseDouble(list_area[0]),Double.parseDouble(list_area[1]),
					Double.parseDouble(list_area[2]),Double.parseDouble(list_area[3])};
			
			State.setAREA(double_area);
			
			String s_site=(String) properties.get("siteType");
			State.setSITE_NORMAL_TYPE(Integer.parseInt(s_site));
			
			String s_remend=(String) properties.get("remendType");
			State.setSITE_REMEND_TYPE(Integer.parseInt(s_remend));
			
			String s_bus=(String) properties.get("busType");
			State.setPOI_BUS(Integer.parseInt(s_bus));
			
			String s_sbuway=(String) properties.get("subwayType");
			State.setPOI_SUBWAY(Integer.parseInt(s_sbuway));
			

			
			String s_truckT=(String) properties.get("truckDispatch");
			State.setTRUCK_TYPE(Integer.parseInt(s_truckT));
			
			String s_truckS=(String) properties.get("truckSpeed");
			State.setTRUCK_SPEED(Integer.parseInt(s_truckS));
			
			String s_truckC=(String) properties.get("truckCapacity");
			State.setTRUCK_CAPACITY(Integer.parseInt(s_truckC));
			
			String s_triT=(String) properties.get("triCycleDispatch");
			State.setTRICYCLE_TYPE(Integer.parseInt(s_triT));
			
			String s_triS=(String) properties.get("triCycleSpeed");
			State.setTRICYCLE_SPEED(Integer.parseInt(s_triS));
			
			String s_triC=(String) properties.get("triCycleCapacity");
			State.setTRICYCLE_CAPACITY(Integer.parseInt(s_triC));
			
			
			String s_manT=(String) properties.get("manDispatch");
			State.setMAN_TYPE(Integer.parseInt(s_manT));
			
			String s_manS=(String) properties.get("manSpeed");
			State.setMAN_SPEED(Integer.parseInt(s_manS));
			
			String s_manC=(String) properties.get("manCapacity");
			State.setMAN_CAPACITY(Integer.parseInt(s_manC));
			
			String s_loadTime=(String) properties.get("loadBikeSeconds");
			State.setLOAD_UNIT_TIME(Integer.parseInt(s_loadTime));
			
	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		InitState initState=new InitState();
		initState.init();
	}
}
