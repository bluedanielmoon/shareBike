package com.poi;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pojo.BusLine;
import com.pojo.BusSite;

public class HtmlDecoder {

	public static void main(String[] args) {
		String x = "X2";

		System.out.println(toHexCode(x, "gbk"));
	}

	public static String toHexCode(String str, String charsetName) {
		byte[] bts = null;
		try {
			bts = str.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : bts) {
			if(Character.isAlphabetic(b)){
				sb.append((char) b);
			}else if (Character.isDigit(b)) {
				sb.append(new Integer(b & 0xff - 48));
			} else {
				sb.append("%").append(
						String.format("%2x", new Integer(b & 0xff))
								.toUpperCase());
			}
		}
		return sb.toString();

	}

	public static List<BusLine> decodeCatalog(String htmlPage) {
		Document doc = Jsoup.parse(htmlPage);

		Elements trs = doc.select("table[bgcolor=#CECE73]").select("tbody")
				.select("tr");
		List<BusLine> ls=new ArrayList<BusLine>();
		for (int i = 3; i < trs.size(); i++) {
			Elements tds = trs.get(i).getElementsByTag("td");
			int id = Integer.parseInt(tds.get(0).text());
			String name = tds.get(1).text();

			String quest = toHexCode(name, "gbk");
			quest = UrlBuilder.buildBusSites(quest);
			BusLine bl=new BusLine(id, name, new ArrayList<BusSite>()); 
			ls.add(bl);

		}
		return ls;
	}
	

}
