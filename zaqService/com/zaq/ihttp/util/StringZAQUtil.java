package com.zaq.ihttp.util;

import org.apache.commons.lang.StringUtils;

/**
 *字符串工具类
 * @author zaqzaq
 *
 */
public class StringZAQUtil {
	/**
	 * 字符首字母大写
	 * @param str
	 * @return
	 */
    public static String upFirstChar(String str) {
    	if(null==str||"".equals(str.trim())){
    		return "";
    	}
        char[] cs=str.toCharArray();
        if(!Character.isUpperCase(cs[0])){
        	cs[0]-=32;
        	return String.valueOf(cs);
        }else{
        	return str;
        }
    }
    /**
     * 将字符数组转成一行字符
     * @param strings
     * @param spiltChar 分隔符 默认为,
     * @return
     */
    public static String stringsToStr(String[] strings,String spiltChar){
    	String retVal=null;
    	if(null==strings||strings.length<1){
    		retVal= "";
    	}else{
    		String spc=",";
    		if(StringUtils.isNotEmpty(spiltChar)){
    			spc=spiltChar;
    		}
    		for(int i=0;i<strings.length;i++){
    			if(i==0){
    				retVal=strings[i];
    			}else{
    				retVal+=spc+strings[i];
    			}
        	}
    	}
    	return retVal;
    	
    }
    
    public static void main(String[] args) {
		System.out.println(stringsToStr(HttpServiceUtil.METHODS, null));
	}
}
