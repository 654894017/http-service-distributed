package com.zaq.ihttp.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * http服务接口调用入口
 * @author zaqzaq
 *
 */
public class HttpUtil {
	private static Logger logger=Logger.getLogger(HttpUtil.class);
	public static final String UTF8="utf-8";
	public static final String ERROR="error";
	public static final long CODE_ERROR=-444;
	public static final String COOKIE_USERNAME="s";
	public static final String COOKIE_PWD="p";
	/**
	 * 未授权的访问
	 */
	public static final long CODE_ACCESSDENIED=-403;
	public static final String ACCESSDENIED="未授权的访问";
	/* 每个路由最大连接数  默认2个-_-! */  
    private static int MAX_ROUTE_CONNECTIONS = 100; 
    /* 从连接池中取连接的超时时间 */
    public static int TIME_OUT_GET_POOL = 3000; 
    /* http连接超时 */
    public static int TIME_OUT_HTTP_CONNECT = 3000; 
    /* http处理超时 */
    public static int TIME_OUT_HTTP_PROCESS = 4000; 
	private static String User_Agent="Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36";

	private static Map<String, HttpClient> httpClientMap=new HashMap<String, HttpClient>();
	private static Map<String, String> serviceCookie=new HashMap<String, String>();
	
	static{
		MAX_ROUTE_CONNECTIONS=Integer.valueOf(HttpServiceConf.getPro("MAX_ROUTE_CONNECTIONS", MAX_ROUTE_CONNECTIONS+""));
		TIME_OUT_GET_POOL=Integer.valueOf(HttpServiceConf.getPro("TIME_OUT_GET_POOL", TIME_OUT_GET_POOL+""));
		TIME_OUT_HTTP_CONNECT=Integer.valueOf(HttpServiceConf.getPro("TIME_OUT_HTTP_CONNECT", TIME_OUT_HTTP_CONNECT+""));
		TIME_OUT_HTTP_PROCESS=Integer.valueOf(HttpServiceConf.getPro("TIME_OUT_HTTP_PROCESS", TIME_OUT_HTTP_PROCESS+""));
	}
	
	/**
	 * http 之 post方式请求
	 * @param host
	 * @param postUrl
	 * @param parm
	 * @return
	 */
	public static String httpPost(String host,String postUrl,NameValuePair... parms){
		HttpClient httpClient=getClient(host);
		HttpPost httpPost=new HttpPost(postUrl);
		
		httpPost.addHeader("Cookie",serviceCookie.get(host));
//		httpPost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		String retVal="";
		HttpResponse response;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(parms),UTF8));
			response=httpClient.execute(httpPost);
			 if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				 logger.error(postUrl+"请求异常：statusCode="+response.getStatusLine().getStatusCode());
				 retVal= response.getStatusLine().getStatusCode()*-1+"";
			 }else{
				 retVal=(response.getEntity() == null) ? "" : EntityUtils.toString(response.getEntity(),UTF8); 
				 logger.info(postUrl+"请求post返回："+retVal);
			 }
		} catch (Exception e) {
			logger.error(postUrl+"请求异常",e);
			retVal= ERROR;
		}finally{
			httpPost.abort();
		}
		return retVal;
	}
	/**
	 * http 之 get方式请求
	 * @param host
	 * @param getUrl
	 * @param nameValuePairs
	 * @return
	 */
	public static String httpGet(String host,String getUrl,NameValuePair... nameValuePairs){
		HttpClient httpClient=getClient(host);
		StringBuilder sb = new StringBuilder();
		sb.append(getUrl);
		if (nameValuePairs != null && nameValuePairs.length > 0) {
			sb.append("?");
			for (int i = 0; i < nameValuePairs.length; i++) {
				if (i > 0) {
					sb.append("&");
				}
				sb.append(String.format("%s=%s",
						nameValuePairs[i].getName(),
						nameValuePairs[i].getValue()));
			}
		}
		
		// HttpGet连接对象
		HttpGet httpget=new HttpGet(sb.toString());
		httpget.addHeader("Cookie",serviceCookie.get(host));
		String retVal="";
		HttpResponse response;
		try {
			response=httpClient.execute(httpget);
			 if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				 logger.error(sb.toString()+"请求异常：statusCode="+response.getStatusLine().getStatusCode());
				 retVal= response.getStatusLine().getStatusCode()*-1+"";
			 }else{
				 retVal=(response.getEntity() == null) ? "" : EntityUtils.toString(response.getEntity(),UTF8); 
				 logger.info(sb.toString()+"请求get返回："+retVal);
			 } 
		} catch (Exception e) {
			logger.error(sb.toString()+"请求异常",e);
			retVal= ERROR;
		}finally{
			httpget.abort();
		}
		return retVal;
	}
	
	/**
	 * 获取httpClient
	 * @param host 接口应用host
	 * @return
	 */
	private static HttpClient getClient(String host){
		HttpClient customerHttpClient=httpClientMap.get(host);
		if(null==customerHttpClient){
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, UTF8);
			HttpProtocolParams.setUseExpectContinue(params, false);
			HttpProtocolParams.setUserAgent(params,User_Agent);
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, TIME_OUT_GET_POOL);
			ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);  
	        ConnManagerParams.setMaxConnectionsPerRoute(params,connPerRoute);  
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, TIME_OUT_HTTP_CONNECT);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, TIME_OUT_HTTP_PROCESS);
			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
//			schReg.register(new Scheme("https", SSLSocketFactory
//					.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			customerHttpClient = new DefaultHttpClient(conMgr, params);
			
			httpClientMap.put(host, customerHttpClient);//为每一个host分配一个httpClient
			serviceCookie.put(host, COOKIE_USERNAME+"="+HttpServiceConf.getMyPro(host, "userName", "")+
									";"+COOKIE_PWD+"="+HttpServiceConf.getMyPro(host, "password", ""));//每host的每个请求放置密码帐户信息
		}
		
		((DefaultHttpClient)customerHttpClient).setCookieStore(null);//清除cookie，启用手动加入cookie
		
		return customerHttpClient;
		
	}
}
