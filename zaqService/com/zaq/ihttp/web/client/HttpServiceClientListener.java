package com.zaq.ihttp.web.client;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;

import com.zaq.ihttp.util.HttpServiceConf;
/**
 * httpService客户端初始化监听器
 * @author zaqzaq
 * 在web.xml 中配置路径
 * 	<context-param>
        <param-name>httpServiceFilePath</param-name>
        <param-value>classpath:conf/http-service.properties</param-value>
   </context-param>
 */
public class HttpServiceClientListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String httpServiceFilePath= event.getServletContext().getInitParameter("httpServiceFilePath");
		HttpServiceConf.init(httpServiceFilePath);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("no zuo no dead");
	}
	
	
	
	
}
