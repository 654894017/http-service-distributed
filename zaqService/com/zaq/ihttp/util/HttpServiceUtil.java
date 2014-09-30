package com.zaq.ihttp.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaq.ihttp.web.HttpServiceMethod;
import com.zaq.ihttp.web.ZAQhttpException;
import com.zaq.ihttp.web.server.HttpServiceBaseAction;

/**
 * httpService 集成于Spring 工具类
 * @author zaqzaq
 *
 */
public class HttpServiceUtil  implements ApplicationContextAware {
	public final static String[] METHODS=HttpServiceMethod.toStrArray();
	public final static String HTTP_ARG_SEQID="seqId";
	public final static String HTTP_ARG_JSONOBJ="jsonObj";
	private static Logger logger=Logger.getLogger(HttpServiceUtil.class);
	/*匹配httpService接口前缀*/
	public static final String HTTP_SERVICE_PREFIX="^http://.*?/httpService/";
	/*匹配httpService接口*/
	public static final String HTTP_SERVICE_REX="^http://.*?/httpService/.*?/.*?/.*?$";
	private static ApplicationContext springContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		springContext = applicationContext;
	}
	public static Object getBean(String beanName){
		return springContext.getBean(beanName);
	}
	/**
	 * 将url组装成restful风格
	 * @param baseUri
	 * @param packagez
	 * @param action
	 * @param method
	 * @return httpServicePreFix+"/"+packagez+"/"+action+"/"+method
	 */
	public static String packageUri(String httpServicePreFix,String packagez,String action ,String method){
		if(StringUtils.isEmpty(httpServicePreFix)||StringUtils.isEmpty(packagez)||StringUtils.isEmpty(action)||StringUtils.isEmpty(method)){
			throw new ZAQhttpException(logger,"参数不能为空");
		}
		if(!httpServicePreFix.matches(HTTP_SERVICE_PREFIX)){
			throw new ZAQhttpException(logger,"httpService前缀格式错误 regex:"+HTTP_SERVICE_PREFIX);
		}
		return httpServicePreFix+packagez+"/"+action+"/"+method;
	}
	/**
	 * 转换成stuts2的.do的请求
	 * @param uri
	 * @return
	 */
	@Deprecated
	public static String getWebTODO(String uri){
		checkHttpServiceUri(uri);
		uri=uri.replaceAll(HTTP_SERVICE_PREFIX, "");
		String[] args=uri.split("/");
		return args[0]+"/"+args[2]+StringZAQUtil.upFirstChar(args[1]);
	}
	/**
	 * 处理http接口服务请求分发控制中心
	 * @param uri
	 * @return
	 */
	public static String httpProcess(HttpServletRequest req,String uri){
		checkHttpServiceUri(uri);
		uri=uri.replaceAll(HTTP_SERVICE_PREFIX, "");
		String[] args=uri.split("/");
		
		String actionBeanName=StringZAQUtil.upFirstChar(args[1])+"Action";
		Object actionObj=getBean(actionBeanName);
		
		if(null==actionObj){
			throw new ZAQhttpException(logger,"spring容器中未配置"+actionBeanName);
		}
		
		/*反射降低性能
		Method method=null;
		try {
			method=actionObj.getClass().getMethod(args[2]);
		} catch (NoSuchMethodException e) {
			throw new ZAQhttpException(logger,actionBeanName+"没有"+args[2]+"方法",e);
		} catch (SecurityException e) {
			throw new ZAQhttpException(logger,actionBeanName+"反射获取"+args[2]+"方法异常",e);
		}
		try {
			retStr=method.invoke(actionObj).toString();
		} catch (Exception e) {
			logger.error(actionBeanName+"反射"+args[2]+"方法调用异常",e);
			retStr=uri+"方法调用异常,请检查参数是否正确";
		}
		*/
		int i=0;
		for(;i<METHODS.length;i++){
			if((METHODS[i]).equals(args[2])){
				break;
			}
		}
		Long retSeqId = null;
		String retQuery = null;
		try {
			HttpServiceBaseAction httpService=(HttpServiceBaseAction) actionObj;
			switch(i){
				case 0:
					if(StringUtils.isEmpty( req.getParameter(HTTP_ARG_JSONOBJ))){
						throw new ZAQhttpException(logger,HTTP_ARG_JSONOBJ+"参数不能为空！");
					}
					retSeqId=httpService.saveOrUpdate( req.getParameter(HTTP_ARG_JSONOBJ));
					break;
				case 1:
					retSeqId=httpService.saveOrUpdate(Long.parseLong(req.getParameter(HTTP_ARG_SEQID)));
					break;
				
				case 2:
					retSeqId=httpService.delPrepare(req);
					break;

				case 3:
					retSeqId=httpService.del( Long.parseLong(req.getParameter(HTTP_ARG_SEQID)));
					break;
				
				case 4:
					Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					retQuery=gson.toJson(httpService.query(req),actionObj.getClass().getMethod("query", HttpServletRequest.class).getGenericReturnType());
					System.out.println("请求："+uri+"返回："+retQuery);
					break;
			
				default: throw new ZAQhttpException(logger,actionBeanName+"不支持"+args[2]+"方法only:"+"【"+StringZAQUtil.stringsToStr(METHODS, "|")+"】");
			}
		}catch (NumberFormatException e) {
			throw new ZAQhttpException(logger,HTTP_ARG_SEQID+"参数序列错误，为long型",e);
		}catch(ZAQhttpException zaQhttpException){
			throw zaQhttpException;
		}catch (Exception e) {
			throw new ZAQhttpException(logger,"服务器响应异常：请联系服务提供者",e);
		}
		
		if(!StringUtils.isEmpty(retQuery)){
			return retQuery;
		}else{
			return retSeqId.toString();
		}
		
	}
	/**
	 * 检测是否为接口的uri
	 * @param uri
	 */
	public static void checkHttpServiceUri(String uri) {
		if(StringUtils.isEmpty(uri)){
			throw new ZAQhttpException(logger,"参数不能为空");
		}
		if(!uri.matches(HTTP_SERVICE_REX)){
			throw new ZAQhttpException(logger,"httpService格式错误 regex:"+HTTP_SERVICE_REX);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("http://sadad/httpService/p/a/m/".matches(HTTP_SERVICE_REX));
		System.out.println(getWebTODO("http://sadad/httpService/p/a/m/"));
		System.out.println("http://sadad/httpService/".matches(HTTP_SERVICE_PREFIX));
		System.out.println("http://sadasd/httpService/ssss".replaceAll(HTTP_SERVICE_PREFIX,""));
	}

}
