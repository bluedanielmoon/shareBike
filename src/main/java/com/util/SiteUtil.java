package com.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poi.ConnectManager;
import com.poi.Connector;
import com.pojo.Dispatcher;
import com.pojo.GaodePath;
import com.pojo.Lnglat;
import com.pojo.Site;

public class SiteUtil {

	private static String query = "https://restapi.amap.com/v3/direction/driving?key=66c21b9e3069ae987bf520de3460ddb6&"
			+ "extensions=all&output=JSON&strategy=0&";


	private static String buildUrl(Lnglat origin, Lnglat destination) {

		String from = origin.getLng() + "," + origin.getLat();
		String dest = destination.getLng() + "," + destination.getLat();
		String url = query + "origin=" + from + "&destination=" + dest;
		return url;
	}

	private String queryResult(String url,CloseableHttpClient client) {

		String result = null;

		result = Connector.getLinkNoProxy(client, url);
		return result;
	}
	
	private GaodePath decodeResult(String result) {

		GaodePath gPath=new GaodePath();
		if(result==null||result=="") {
			return null;
		}
		ObjectMapper mapper= new ObjectMapper();
		try {
			JsonNode root=mapper.readTree(result);
			if(root==null) {
				return null;
			}
			JsonNode status=root.get("status");
			if(status.asInt()!=1) {
				return null;
			}
			JsonNode route=root.get("route");
			JsonNode paths=route.get("paths");
			if(paths.size()==0) {
				return null;
			}
			JsonNode path=paths.get(0);
			
			JsonNode distance=path.get("distance");
			JsonNode duration=path.get("duration");
			gPath.setDistance(distance.asInt());
			gPath.setDuration(duration.asInt());
			JsonNode steps=path.get("steps");
			
			StringBuilder sBuilder=new StringBuilder();
			for(JsonNode step:steps) {
				JsonNode poliLine=step.get("polyline");
				sBuilder.append(poliLine.textValue());
				sBuilder.append(';');
			}
			List<Lnglat> pathLine= decodePaths(sBuilder);
			gPath.setPaths(pathLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gPath;
	}
	
	private List<Lnglat> decodePaths(StringBuilder sBuilder) {
		String sPaths=sBuilder.toString();
		List<Lnglat> paths=new ArrayList<>();
		String[] ps=sPaths.split(";");
		String[] temp=null;
		for(int i=0;i<ps.length;i++) {
			temp=ps[i].split(",");
			Lnglat llLnglat=new Lnglat(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));
			paths.add(llLnglat);
		}
		return paths;
	}
	
	public GaodePath getPath(Site origin, Site destination,CloseableHttpClient client) {
		Lnglat from=new Lnglat(origin.getLng(), origin.getLat());
		Lnglat to=new Lnglat(destination.getLng(), destination.getLat());
		String url=SiteUtil.buildUrl(from,to);
		SiteUtil sUtil=new SiteUtil();
		
		String result=sUtil.queryResult(url,client);
		GaodePath path=sUtil.decodeResult(result);
		return path;
	}
	public GaodePath getPath(Lnglat from, Site destination,CloseableHttpClient client) {
		Lnglat to=new Lnglat(destination.getLng(), destination.getLat());
		String url=SiteUtil.buildUrl(from,to);
		SiteUtil sUtil=new SiteUtil();
		
		String result=sUtil.queryResult(url,client);
		GaodePath path=sUtil.decodeResult(result);
		return path;
	}
	public GaodePath getPath(Site origin, Dispatcher destination,CloseableHttpClient client) {
		Lnglat from=new Lnglat(origin.getLng(), origin.getLat());
		Lnglat to=new Lnglat(destination.getLng(), destination.getLat());
		String url=SiteUtil.buildUrl(from,to);
		SiteUtil sUtil=new SiteUtil();
		
		String result=sUtil.queryResult(url,client);
		GaodePath path=sUtil.decodeResult(result);
		return path;
	}
	
	public GaodePath getPath(Dispatcher origin, Site destination,CloseableHttpClient client) {
		Lnglat from=new Lnglat(origin.getLng(), origin.getLat());
		Lnglat to=new Lnglat(destination.getLng(), destination.getLat());
		String url=SiteUtil.buildUrl(from,to);
		SiteUtil sUtil=new SiteUtil();
		
		String result=sUtil.queryResult(url,client);
		GaodePath path=sUtil.decodeResult(result);
		return path;
	}

}
