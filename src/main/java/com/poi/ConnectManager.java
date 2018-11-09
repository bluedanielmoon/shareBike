package com.poi;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

/**
 * http连接的链接池
 * 
 * @author daniel
 * @date 2018.10.13
 *
 */
public class ConnectManager {
	public static PoolingHttpClientConnectionManager manager;
	private static HttpRequestRetryHandler retry;
	private static RequestConfig config;
	private static SSLConnectionSocketFactory sslsf;
	private static final int RETRY_TIMES = 2;
	private static final int REQUEST_TIME_SECONDS = 2000;// get a link from link														// pool
	private static final int CONNECT_TIME_OUT = 5000;// build connect time
	private static final int SOCKET_TIME_OUT = 5000;// data tranfer time
	static {

		retry = new HttpRequestRetryHandler() {

			@Override
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= RETRY_TIMES) {// 如果已经重试了2次，就放弃
					return false;
				}

				if (exception instanceof org.apache.http.conn.HttpHostConnectException) {// 如果服务器丢掉了连接，那么就重试
					return true;
				}

				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					return false;
				}

				if (exception instanceof InterruptedIOException) {// 超时
					return false;
				}

				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					return false;
				}

				if (exception instanceof org.apache.http.conn.ConnectTimeoutException) {// 连接被拒绝
					return false;
				}

				if (exception instanceof SSLException) {// ssl握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext
						.adapt(context);
				HttpRequest request = clientContext.getRequest();

				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		config = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT)
				.setSocketTimeout(SOCKET_TIME_OUT)
				.setConnectionRequestTimeout(REQUEST_TIME_SECONDS)
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		SSLContext ctx = null;
		try {
			ctx =SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {

				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[]{};
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		// Allow TLSv1 protocol only

		sslsf = new SSLConnectionSocketFactory(ctx,NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslsf).build();

		manager = new PoolingHttpClientConnectionManager(sfr);
		manager.setMaxTotal(200);
		// Increase default max connection per route to 20
		manager.setDefaultMaxPerRoute(20);
		// Increase max connections for localhost:80 to 50
	}

	/**
	 * 得到一个连接客户端
	 * 
	 * @return
	 */
	public static CloseableHttpClient getClient() {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(manager).setRetryHandler(retry)
				.setDefaultRequestConfig(config).build();
		return httpClient;
	}
	/**
	 * 获取默认的链接
	 * @return
	 */
	public static CloseableHttpClient getDefaultClient() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient;
	}
	/**
	 * 获取ssl的链接
	 * @return
	 */
	public static CloseableHttpClient getSSLClient() {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(manager).setRetryHandler(retry)
				.setSSLSocketFactory(sslsf).setDefaultRequestConfig(config)
				.build();
		return httpClient;
	}
}
