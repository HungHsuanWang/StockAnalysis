package com.ydd.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.cz.framework.JsonUtil;
import com.cz.framework.LogUtil;
import com.cz.framework.StringUtil;
import com.ydd.model.HttpResInfo;

@SuppressWarnings("deprecation")
public class HttpsUtil {
	private static int SocketTimeout = 30000;// 3秒
	private static int ConnectTimeout = 30000;// 3秒
	private static Boolean SetTimeOut = true;

	public static void setConnectTimeout(int second){
		HttpsUtil.SocketTimeout=second;
		HttpsUtil.ConnectTimeout=second;
	}

	private static CloseableHttpClient getHttpClient() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);
		// 指定信任密钥存储对象和连接套接字工厂
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			// 信任任何链接
			TrustStrategy anyTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			};
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy)
					.build();
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			registryBuilder.register("https", sslSF);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		// 设置连接管理器
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
		// connManager.setDefaultConnectionConfig(connConfig);
		// connManager.setDefaultSocketConfig(socketConfig);
		// 构建客户端
		return HttpClientBuilder.create().setConnectionManager(connManager).build();
	}

	/**
	 * get
	 * 
	 * @param url
	 *            请求的url
	 * @param queries
	 *            请求的参数，在浏览器？后面的数据，没有可以传null
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HttpResInfo get(String url, Map<String, String> queries) {
		HttpResInfo httpResInfo = new HttpResInfo();
		String responseBody = "";
		// CloseableHttpClient httpClient=HttpClients.createDefault();
		// 支持https
		CloseableHttpClient httpClient = getHttpClient();

		StringBuilder sb = new StringBuilder(url);

		if (queries != null && queries.keySet().size() > 0) {
			boolean firstFlag = true;
			Iterator iterator = queries.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry<String, String>) iterator.next();
				if (firstFlag) {
					sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
					firstFlag = false;
				} else {
					sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
				}
			}
		}

		HttpGet httpGet = new HttpGet(sb.toString());
		if (SetTimeOut) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SocketTimeout)
					.setConnectTimeout(ConnectTimeout).build();// 设置请求和传输超时时间
			httpGet.setConfig(requestConfig);
		}
		CloseableHttpResponse response = null;
		try {
//			System.out.println("Executing request " + httpGet.getRequestLine());
			// 请求数据
			response = httpClient.execute(httpGet);
//			System.out.println(response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();
			httpResInfo.setCode(status);
			if (status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				responseBody = EntityUtils.toString(entity);
				// EntityUtils.consume(entity);
			} else {
				LogUtil.warn("http return status error:" + status);
				responseBody = "http return status error:" + status;
			}
		} catch (Exception ex) {
			LogUtil.error("https get", ex);
			responseBody = ex.toString();
		} finally {
			try {
				if (response != null)
					response.close();
				if (httpClient != null) {
					httpClient.close();
				}
				
			} catch (IOException e) {
				LogUtil.error("https get close", e);
			}

		}
		httpResInfo.setRepMsg(responseBody);
		return httpResInfo;
	}

	/**
	 * post
	 * 
	 * @param url
	 *            请求的url
	 * @param queries
	 *            请求的参数，在浏览器？后面的数据，没有可以传null
	 * @param params
	 *            post form 提交的参数
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HttpResInfo post(String url, Map<String, String> queries, Map<String, String> params) {
		HttpResInfo httpResInfo = new HttpResInfo();
		String responseBody = "";
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		// 支持https
		CloseableHttpClient httpClient = getHttpClient();

		StringBuilder sb = new StringBuilder(url);

		if (queries != null && queries.keySet().size() > 0) {
			boolean firstFlag = true;
			Iterator iterator = queries.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry<String, String>) iterator.next();
				if (firstFlag) {
					sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
					firstFlag = false;
				} else {
					sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
				}
			}
		}

		// 指定url,和http方式
		HttpPost httpPost = new HttpPost(sb.toString());
		if (SetTimeOut) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SocketTimeout)
					.setConnectTimeout(ConnectTimeout).build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);
		}
		// 添加参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null && params.keySet().size() > 0) {
			Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
				nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		// 请求数据
		CloseableHttpResponse response = null;
		try {
			response=httpClient.execute(httpPost);
			httpResInfo.setCode(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				responseBody = EntityUtils.toString(entity);
				// EntityUtils.consume(entity);
			} else {
				responseBody = "http return status error:" + response.getStatusLine().getStatusCode();
				LogUtil.warn("http return status error:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			responseBody = e.toString();
			LogUtil.error("https post", e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LogUtil.error("https post close", e);
			}
		}
		httpResInfo.setRepMsg(responseBody);
		return httpResInfo;
	}
	
	
	public static HttpResInfo post(String url, String params, String token, HttpServletRequest request) {
		HttpResInfo httpResInfo = new HttpResInfo();
		String responseBody = "";
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		// 支持https
		CloseableHttpClient httpClient = getHttpClient();

		StringBuilder sb = new StringBuilder(url);

		// 指定url,和http方式
		HttpPost httpPost = new HttpPost(sb.toString());
		if (SetTimeOut) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SocketTimeout)
					.setConnectTimeout(ConnectTimeout).build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);
		}
		// 添加参数
		httpPost.setHeader("Content-Type","application/json");
		httpPost.setEntity(new StringEntity(params, Consts.UTF_8));
		if(StringUtil.isNotBlank(token)){
			httpPost.setHeader("X-Auth-Token",token);
		}
		// 请求数据
		CloseableHttpResponse response = null;
		try {
			response=httpClient.execute(httpPost);
			httpResInfo.setCode(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				responseBody = EntityUtils.toString(entity);
				// EntityUtils.consume(entity);
			} else {
				LogUtil.warn("http return status error:" + response.getStatusLine().getStatusCode());
				responseBody = "http return status error:" + response.getStatusLine().getStatusCode();
			}
		} catch (Exception e) {
			LogUtil.error("https post", e);
			responseBody = e.toString();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LogUtil.error("https post close", e);
			}
		}
		httpResInfo.setRepMsg(responseBody);
		return httpResInfo;
	}
	
	
	public static HttpResInfo post(String url, Map<String, String> params,String referer) {
		HttpResInfo httpResInfo = new HttpResInfo();
		String responseBody = "";
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		// 支持https
		CloseableHttpClient httpClient = getHttpClient();

		StringBuilder sb = new StringBuilder(url);

		// 指定url,和http方式
		HttpPost httpPost = new HttpPost(sb.toString());
		if (SetTimeOut) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SocketTimeout)
					.setConnectTimeout(ConnectTimeout).build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);
		}
		// 添加参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null && params.keySet().size() > 0) {
			Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
				nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
			}
		}
		httpPost.setHeader("Referer", referer);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		// 请求数据
		CloseableHttpResponse response = null;
		try {
			response=httpClient.execute(httpPost);
			httpResInfo.setCode(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				responseBody = EntityUtils.toString(entity);
				// EntityUtils.consume(entity);
			} else {
				LogUtil.warn("http return status error:" + response.getStatusLine().getStatusCode());
				responseBody = "http return status error:" + response.getStatusLine().getStatusCode();
			}
		} catch (Exception e) {
			LogUtil.error("https post", e);
			responseBody = e.toString();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LogUtil.error("https post close", e);
			}
		}
		httpResInfo.setRepMsg(responseBody);
		return httpResInfo;
	}
	
	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	
	public static byte[] xmHttpsPost(String url, String entity) {
		if (url == null || url.length() == 0) {
			System.out.println("httpPost, url is null");
			return null;
		}
		HttpClient httpClient = getNewHttpClient();
		// httpClient.getParams().setParameter(
		// CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);// 连接时间
		// httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
		// 10000);// 数据传输时间
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(10000).setConnectTimeout(10000).build();
		HttpPost httpPost = new HttpPost(url);

		try {
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(entity, "utf-8"));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.out.println("httpGet fail, status code = "
						+ resp.getStatusLine().getStatusCode());
				return null;
			}

			return EntityUtils.toByteArray(resp.getEntity());
		} catch (Exception e) {
			System.out.println("httpPost exception, e = " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static HttpResInfo postJson(String url, Map<String, String> params) {
		HttpResInfo httpResInfo = new HttpResInfo();
		String responseBody = "";
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		// 支持https
		CloseableHttpClient httpClient = getHttpClient();

		StringBuilder sb = new StringBuilder(url);

		// 指定url,和http方式
		// 请求数据
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(sb.toString());
			if (SetTimeOut) {
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SocketTimeout)
						.setConnectTimeout(ConnectTimeout).build();// 设置请求和传输超时时间
				httpPost.setConfig(requestConfig);
			}
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setEntity(new StringEntity(JsonUtil.toJson(params)));
			response=httpClient.execute(httpPost);
			httpResInfo.setCode(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				responseBody = EntityUtils.toString(entity);
				// EntityUtils.consume(entity);
			} else {
				LogUtil.warn("http return status error:" + response.getStatusLine().getStatusCode());
				responseBody = "http return status error:" + response.getStatusLine().getStatusCode();
			}
		} catch (Exception e) {
			LogUtil.error("https post", e);
			responseBody = e.toString();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LogUtil.error("https post close", e);
			}
		}
		httpResInfo.setRepMsg(responseBody);
		return httpResInfo;
	}
	
	private static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
}
