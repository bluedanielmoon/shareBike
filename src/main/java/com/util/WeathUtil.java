package com.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.impl.client.CloseableHttpClient;
import com.poi.ConnectManager;
import com.poi.Connector;
import com.poi.UrlBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.Weather;

/**
 * 我的API密钥：6s4ijc5jygohjpja
 * 
 * 
 * 我的用户ID：U0D4912611
 * 
 * @author Administrator
 *
 */
public class WeathUtil {

	private static ObjectMapper mapper = new ObjectMapper();
	private static String dateform = "yyyy-MM-dd HH:mm:ss";

	public static Weather getWeather() {
		CloseableHttpClient client = ConnectManager.getDefaultClient();
		String url = UrlBuilder.test();
		String result = Connector.getLinkNoProxy(client, url);
		try {

			JsonNode nod = mapper.readTree(result).get("results").get(0);
			JsonNode nowData = nod.get("now");
			JsonNode time = nod.get("last_update");

			Weather weather = new Weather();
			weather.setCode(Integer.parseInt(nowData.get("code").textValue()));
			weather.setWeather(nowData.get("text").textValue());
			weather.setTempature(Integer.parseInt(nowData.get("temperature")
					.textValue()));

			String strTime = time.textValue();
			StringBuilder sb = new StringBuilder();
			String weathTime = sb.append(strTime.substring(0, 10)).append(' ')
					.append(strTime.substring(11, 19)).toString();

			Date date = new SimpleDateFormat(dateform).parse(weathTime);
			weather.setTime(date);

			return weather;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(getWeather());
	}

}
