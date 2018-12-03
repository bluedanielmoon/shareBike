package com.poi;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
/**
 * 
 * Client要从最外面传入，在传入的下方关掉。client里面会自动处理，不需要为每个链接新建client
 * original link:https://mwx.mobike.com/mobike-api/rent/nearbyBikesInfo.do?
 * latitude=34.3412700000&longitude=108.9398400000&errMsg=getMapCenterLocation
 */
public class Connector {

	private static final int REQUEST_TIME_SECONDS = 3000;// get a link from link
	// pool
	private static final int CONNECT_TIME_OUT = 5000;// build connect time
	private static final int SOCKET_TIME_OUT = 5000;// data tranfer time
	private static final int TEXT_MAX_SECONDS = 120;// test max wait Seconds
	private static final int LINK_MAX_SECONDS = 30;// getLink max wait Seconds
	public Connector() {

	}

	public static void main(String[] args) {
		CloseableHttpClient client = ConnectManager.getClient();
		try {
			System.out.println(123);
			String url = UrlBuilder.buildBusline();
			System.out.println(url);
			
			String result=Connector.getLinkNoProxy(client, url);
			System.out.println(result);
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void config(HttpRequestBase request, HttpHost proxy,
			URI uri, boolean withPxoxy) {
		request.setURI(uri);
		request.setHeader(
				"Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		request.setHeader("Accept-Encoding", "gzip, deflate, br");
		request.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		request.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; MI 4S Build/LMY47V)"
						+ " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.146 Mobile "
						+ "Safari/537.36 XiaoMi/MiuiBrowser/9.1.3");
		RequestConfig config = null;
		if (withPxoxy) {

			config = RequestConfig.custom().setProxy(proxy)
					.setConnectTimeout(CONNECT_TIME_OUT)
					.setSocketTimeout(SOCKET_TIME_OUT)
					.setConnectionRequestTimeout(REQUEST_TIME_SECONDS)
					.setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		} else {
			config = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT)
					.setSocketTimeout(SOCKET_TIME_OUT)
					.setConnectionRequestTimeout(REQUEST_TIME_SECONDS)
					.setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		}
		request.setConfig(config);

	}
	/**
	 * 官方推荐的用来清除过期链接和空闲连接的线程，但是不能解决所有问题（有个卵用？）
	 * @author Administrator
	 *
	 */
	static class ClearMoniter implements Runnable {

		private final PoolingHttpClientConnectionManager connMgr;
		private volatile boolean shutdown;

		public ClearMoniter(PoolingHttpClientConnectionManager connMgr) {
			super();
			this.connMgr = connMgr;
		}

		@Override
		public void run() {
			try {
				while (!shutdown) {
					synchronized (this) {
						wait(5000);
						// Close expired connections
						connMgr.closeExpiredConnections();
						// Optionally, close connections
						// that have been idle longer than 30 sec
						connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
					}
				}
			} catch (InterruptedException ex) {
				// terminate
			}
		}

		public void shutdown() {
			shutdown = true;
			synchronized (this) {
				notifyAll();
			}
		}

	}
	static class Abortor implements Runnable {
		private HttpGet get;
		private boolean stop=false;
		private int count=0;
		private int maxSeconds;

		public Abortor(HttpGet get,int maxSeconds) {
			this.get = get;
			this.maxSeconds=maxSeconds;
		}

		@Override
		public void run() {
			try {
				while(!stop&&count<maxSeconds){
					count++;
					Thread.sleep(1000);
				}
				
				//System.out.println("***************abort***********");
				this.get.abort();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		public void stop(){
			stop=true;
		}

	}
	/**
	 *  最复杂的函数，心累，测试链接
	 * @param httpclient
	 * @param ip
	 * @param port
	 * @param url
	 * @return
	 */
	public static boolean test(CloseableHttpClient httpclient, String ip,
			int port, String url) {
		/**
		 * 因为服务端关闭连接，客户端可能出现卡住的现象，官方也推荐要对这种现象进行abort。
		 * 考虑到测试正常返回结果（无论成功还是失败）就立即结束，或者达到一定时间后，自行进行abort
		 * @author daniel
		 *
		 */
		HttpGet httpget = new HttpGet();
		Abortor ab=new Abortor(httpget,TEXT_MAX_SECONDS);
		CloseableHttpResponse response = null;
		
		try {
			HttpHost proxy = new HttpHost(ip, port);
			config(httpget, proxy, URI.create(url), true);
			//System.out.println("Executing request " + httpget.getRequestLine());
			
			//用于中断链接的线程，要在execute之前开始
			new Thread(ab).start();
			response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() != 200) {
				//System.out.println("-----------------responce :"
				//		+ response.getStatusLine().getStatusCode()
				//		+ "-----------------------");
				httpget.abort();

			}
			//完全获取返回的结果
			HttpEntity entity = response.getEntity();
			EntityUtils.consumeQuietly(entity);
			if (entity != null) {
				//System.out.println("-----------------link success-----------------------");
				return true;
			}
		} catch (IOException e) {
			//System.out.println("-----------------connect fail-----------------------");
		} finally {
			try {
				//伴随着正常的链接，终止中止线程
				ab.stop();
				//只关闭response
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 以本机的IP进行内容获取
	 * 
	 * @param client
	 *            请求客户端
	 * @param url
	 *            要获取的链接地址
	 * @return 返回网页的内容
	 */
	public static String getLinkNoProxy(CloseableHttpClient client, String url) {
		return getLink(client, "1", 1, url, false);
	}

	/**
	 * 以代理的方式进行内容获取
	 * 
	 * @param client
	 *            请求客户端
	 * @param ip
	 *            代理的地址
	 * @param port
	 *            代理的端口
	 * @param url
	 *            要获取的链接地址
	 * @return
	 */
	public static String getLinkWithProxy(CloseableHttpClient client,
			String ip, int port, String url) {
		return getLink(client, ip, port, url, true);
	}

	private static String getLink(CloseableHttpClient client, String ip,
			int port, String url, boolean withProxy) {
		HttpGet httpget = new HttpGet();
		Abortor ab=new Abortor(httpget,LINK_MAX_SECONDS);
		CloseableHttpResponse response = null;
		
		try {
//			System.out.println(ip+" "+port);
			HttpHost proxy = new HttpHost(ip, port);
			config(httpget, proxy, URI.create(url), withProxy);
			//System.out.println("Executing request " + httpget.getRequestLine());
			
			//用于中断链接的线程，要在execute之前开始
			new Thread(ab).start();
			response = client.execute(httpget);
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("-----------------responce :"
						+ response.getStatusLine().getStatusCode()
						+ "-----------------------");
				httpget.abort();

			}
			//完全获取返回的结果
			HttpEntity entity=response.getEntity();
			
			if (entity != null) {
				String html = EntityUtils.toString(entity, "gbk");
				return html;
			}
		} catch (IOException e) {
			//System.out.println("-----------------connect fail-----------------------");
		} finally {
			try {
				//伴随着正常的链接，终止中止线程
				ab.stop();
				//只关闭response
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// 108.921718--lng,经度,34.270138--lat,纬度
	public String getResponce(String lng, String lat) {

		return lng;

	}
}
