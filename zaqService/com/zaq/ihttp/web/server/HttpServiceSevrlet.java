package com.zaq.ihttp.web.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.zaq.ihttp.util.HttpServiceUtil;
import com.zaq.ihttp.util.HttpUtil;
import com.zaq.ihttp.web.model.HttpServiceFirewall;
import com.zaq.ihttp.web.service.IFireWallService;
import com.zaq.ihttp.web.service.impl.FireWallService;

/**
 * httpService Sevrlet总接口
 * @author zaqzaq
 */
public class HttpServiceSevrlet extends HttpServlet{
	private Logger logger=Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	public String execute(HttpServletRequest req){
		String uri=req.getRequestURL().toString();
		System.out.println(uri);
		uri=uri.replaceAll(".do", "");
		
		return HttpServiceUtil.httpProcess(req,uri);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//设置全局HttpServletRequest对象
		HttpServiceUtil.setRequest(req);
		resp.setContentType("text/html;charset=utf-8");
		if(!fireWall(req)){
			resp.getWriter().write(HttpUtil.ACCESSDENIED);
//			resp.sendError(403);
		}else{
			
			resp.getWriter().write(execute(req));	
		}
	}
	/**防火墙*/
	public boolean fireWall(HttpServletRequest request){
		 String remoteIP = null;
		 if ((request.getHeader("x-forwarded-for") == null) && (request.getHeader("x-real-ip") == null)) {
		   remoteIP = request.getRemoteAddr();
		 } else {
		   remoteIP = request.getHeader("x-forwarded-for");
		   if (StringUtils.isBlank(remoteIP)) {
		     remoteIP = request.getHeader("x-real-ip");
		   }
		 }
		 System.out.println("request-from:" + remoteIP);
		 
		 IFireWallService fireWallService=(IFireWallService) HttpServiceUtil.getBean("fireWallService");
		 
		 boolean retBoo=true;
		 
		 String userName=null;
		 String pwd=null;
		 //从cookie中获取用户密码信息
		 if(null!=request.getCookies()){
			 for(Cookie c:request.getCookies()){
					if(HttpUtil.COOKIE_USERNAME.equals(c.getName())){
						userName=c.getValue();
						continue;
					}
					if(HttpUtil.COOKIE_PWD.equals(c.getName())){
						pwd=c.getValue();
						continue;
					}
					if(StringUtils.isNotEmpty(userName)&&StringUtils.isNotEmpty(pwd)){
						break;
					}
				 }
		 }

		 
		if(StringUtils.isEmpty(userName)||StringUtils.isEmpty(pwd)){
			retBoo=false;
		}else{
			 HttpServiceFirewall firewall= fireWallService.getByIp(remoteIP,userName);
			 if(null==firewall||firewall.getStatus().shortValue()!=HttpServiceFirewall.STATUS_USE){
				 retBoo=false;
			 }else{
				 if(!firewall.getIp().equals(remoteIP)){
					 retBoo=false;
				 }else{
					 //验证密码
					 retBoo=pwd.equals(firewall.getPwd());
				 }
			 }
		}
		
		if(!retBoo){
			logger.info("拦截请求ip"+remoteIP+"用户："+userName);
		}
		
		return retBoo;
		 
	}
}
