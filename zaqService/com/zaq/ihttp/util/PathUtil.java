package com.zaq.ihttp.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author zhangyj
 *
 * 
 */
public class PathUtil {
	private PathUtil(){}
	private static PathUtil pathUtil=null;
	
	public static PathUtil instance(){
		if(null==pathUtil){
			pathUtil=new PathUtil();
		}
		
		return pathUtil;
	}
	/**
	 * 获取绝对路径
	 * @param fileName
	 * @return
	 */
	public String getRealPath(String fileName){
		
		String filePath=this.getClass().getClassLoader().getResource(fileName)
				.getFile().replaceAll("%20"," ");
		try {
			filePath = URLDecoder.decode(filePath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return filePath;
	}
	/**
	 * 获取本jar包中的文件流
	 * @param fileName
	 * @return
	 */
	public InputStream getThisJar(String fileName){
		
		return this.getClass().getResourceAsStream(fileName);
	}
}
