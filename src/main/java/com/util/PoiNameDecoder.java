package com.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poi.ConnectManager;
import com.poi.Connector;
import com.pojo.Lnglat;

public class PoiNameDecoder {
	
	private static String query = "https://restapi.amap.com/v3/geocode/regeo?key=66c21b9e3069ae987bf520de3460ddb6&poitype=190300&radius=0&extensions=all&batch=false&roadlevel=0&location=";

	private static String buildUrl(Lnglat destLnglat) {
		
		String dest = destLnglat.getLng() + "," + destLnglat.getLat();
		String url=query+dest;
		return url;
	}
	
	public String getPoiName(Lnglat destLnglat,CloseableHttpClient client) {
		String url=buildUrl(destLnglat);
		
		String result=decodeResult(url,client);
		return result;
	}
	
	public String getPoiName(double[] destLnglat,CloseableHttpClient client) {
		Lnglat lnglat=new Lnglat(destLnglat[0], destLnglat[1]);
		
		return getPoiName(lnglat,client);
	}


	private String decodeResult(String url,CloseableHttpClient client) {
		String result = Connector.getLinkNoProxy(client, url);
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
			JsonNode regeocode=root.get("regeocode");
			JsonNode pois=regeocode.get("pois");
			if(pois.size()==0) {
				return null;
			}
			JsonNode poi=pois.get(0);
			JsonNode poiName=poi.get("name");
			
			return poiName.textValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	
	
}
