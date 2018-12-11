package com.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.poi.ConnectManager;
import com.pojo.GaodePath;
import com.pojo.Lnglat;
import com.pojo.Route;
import com.pojo.Site;
import com.service.RouteServ;
import com.util.MapperUtil;
import com.util.SiteUtil;

@Component
public class SiteHelper {
	@Autowired
	private RouteServ routeServ;
	
	public static void main(String[] args) {
		List<Lnglat> ls=new ArrayList<>();
		ls.add(new Lnglat(123.123, 12.12));
		ls.add(new Lnglat(123.123, 12.12));
		ls.add(new Lnglat(123.123, 12.12));
		ls.add(new Lnglat(123.123, 12.12));
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		ObjectWriter writer=mapper.writer();
		try {
			String s=mapper.writeValueAsString(ls);
			System.out.println(s);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Lnglat> readRoute(Route route) {
		if(route==null) {
			return null;
		}
		String s_path=route.getPath();
		ObjectMapper mapper=new ObjectMapper();
		JavaType listType=mapper.getTypeFactory().constructCollectionType(List.class, Lnglat.class);
		
		try {
			List<Lnglat> paths = mapper.readValue(s_path, listType);
			if(paths!=null) {
				return paths;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 读取站点通勤路径信息，并写入数据库
	 */
	public void readRouteFileAddToBase(){
		Map<Integer, Map<Integer, Object>> maps=MapperUtil.readIntMapIntMapData("./siteMap.txt");
		
		ObjectMapper mapper=new ObjectMapper();
		//不写这一句由于双引号，数据库插不进去
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
		
		List<Route> list= new ArrayList<>();
		
		Set<Integer> sets=maps.keySet();
		Iterator<Integer> iterator=sets.iterator();
		int count=0;
		while(iterator.hasNext()) {
			Integer i=iterator.next();
			Map<Integer, Object> map=maps.get(i);
			for(Integer j:map.keySet()) {	
				try {
					if(map.get(j)==null) {
						continue;
					}
					String data=map.get(j).toString();
					data=data.replaceAll("=", ":");
					GaodePath path=mapper.readValue(data, GaodePath.class);
					
					Route route=new Route();
					route.setFromId(i);
					route.setToId(j);
					route.setDistance(path.getDistance());
					route.setDuration(path.getDuration());
					route.setPath(mapper.writeValueAsString(path.getPaths()));
					list.add(route);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			routeServ.clearTable();
			boolean flag=routeServ.patchAddPoi(list);
			if(flag) {
				list.clear();
			}
			count++;
			if(count==10) {
				count=0;
			}
		}
		
		
		
	}
	
	/**
	 * 根据输入的站点，进行从每一个站点到另一个站点的路线的采集
	 * @param sites
	 */
	public void getPath(List<Site> sites) {
		
		CloseableHttpClient client=ConnectManager.getClient();
		SiteUtil siteUtil=new SiteUtil();
		Map<Integer, Map<Integer, GaodePath>> maps=new HashMap<>();
		double i=0;
		System.out.println("共有站点"+sites.size());
		for(Site site:sites) {
			System.out.println("准备采集"+site.getId());
			Map<Integer, GaodePath> map=new HashMap<>();
			for(Site siteTo:sites) {
				GaodePath path=siteUtil.getPath(site, siteTo, client);
				
				map.put(siteTo.getId(), path);
			}
			maps.put(site.getId(), map);
			System.out.println("完毕"+site.getId()+",采集"+map.size()+",完成了"+(i/sites.size()));
			i++;
		}
		MapperUtil.writeIntMapIntMapData("./siteMap.txt",maps,GaodePath.class);
		
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
