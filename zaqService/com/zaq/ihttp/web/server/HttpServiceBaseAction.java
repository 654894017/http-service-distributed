package com.zaq.ihttp.web.server;

import com.google.gson.Gson;
import com.zaq.ihttp.util.TypeUtil;
import com.zaq.ihttp.web.IHttpDelService;
import com.zaq.ihttp.web.IHttpQueryService;
import com.zaq.ihttp.web.IHttpSaveOrUpdateService;
/**
 * http接口基类
 * @author zaqzaq
 *
 * @param <T>
 */
public abstract class HttpServiceBaseAction<T> implements IHttpDelService<T>,IHttpQueryService<T>,IHttpSaveOrUpdateService<T>{
	/**
	 * 注意线程安全问题，struts2 的action Bean请设置成原型模式
	 */
	public long saveOrUpdate(String jsonObj){
		Gson gson=new Gson();
		T obj=gson.fromJson(jsonObj, TypeUtil.getSuperclassTypeParameter(getClass()));
		return saveOrUpdatePrepare(obj);
	}
	
//	public long update(String jsonObj){
//		Gson gson=new Gson();
//		T obj=gson.fromJson(jsonObj, TypeUtil.getSuperclassTypeParameter(getClass()));
//		return updatePrepare(obj);
//	}
}
