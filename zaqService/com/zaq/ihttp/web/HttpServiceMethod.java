package com.zaq.ihttp.web;
/**
 * 可操作的methdo类型
 * @author zaqzaq
 *
 */
public enum HttpServiceMethod {
	saveOrUpdatePrepare,saveOrUpdate,delPrepare,del,query;
	
	public static String[] toStrArray(){
		HttpServiceMethod[] arr=HttpServiceMethod.values();
		String[] retArr=new String[arr.length];
		for(int i=0;i<arr.length;i++){
			retArr[i]=arr[i].name();
		}
		return retArr;
	}
	
	public static HttpServiceMethod get(int ordinal){
		return HttpServiceMethod.values()[ordinal];
	}
}
