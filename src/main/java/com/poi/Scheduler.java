package com.poi;

import java.io.IOException;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;

import com.pojo.BusLine;
import com.pojo.BusSite;
import com.util.FilesUtil;

public class Scheduler {

	public static void main(String[] args) {
		startWork();
	}

	public static void startWork() {
		CloseableHttpClient client = ConnectManager.getClient();
		String url = UrlBuilder.buildBusline();

		String result = Connector.getLinkNoProxy(client, url);
		List<BusLine> list=HtmlDecoder.decodeCatalog(result);
		
		String file=FilesUtil.DEFAULT_BIKE_FILE+"lin2.txt";
		FilesUtil.writeListToFile(file,list, BusLine.class);
		

		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
