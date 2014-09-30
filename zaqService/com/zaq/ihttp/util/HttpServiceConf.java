package com.zaq.ihttp.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.zaq.ihttp.web.ZAQhttpException;


/**
 * httpService资源配置类
 * @author zaqzaq
 *
 */
public class HttpServiceConf{
	private static Logger logger=Logger.getLogger(HttpServiceConf.class);
	
	public static final String fileDefaultPath="/com/zaq/conf/http-service.properties";
	
	private static Properties properties=new Properties();
	public static void init(String filetPath){
		
		try {
			if(StringUtils.isEmpty(filetPath)){
				properties.load(PathUtil.instance().getThisJar(fileDefaultPath));
			}else{
				properties.load(new FileInputStream(PathUtil.instance().getRealPath(filetPath)));
			}
		} catch (Exception e) {
			throw new ZAQhttpException(logger,"初始化web.xml中httpServiceFilePath配置的文件加载异常", e);
		}
	}
	/**
	 * 获取配置资源
	 * @param key 
	 * @param defVal
	 * @return
	 */
	public static String getPro(String key,String defVal){
		
		return properties.getProperty(key,defVal);
	}
	/**
	 * 获取host下配置的资源
	 * @param host
	 * @param key
	 * @param defVal
	 * @return
	 */
	public static String getMyPro(String host,String key,String defVal){
		
		return getPro(host+"."+key, defVal);
	}
}
