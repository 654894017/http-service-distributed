package com.zaq.ihttp.web.client;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaq.ihttp.util.TypeUtil;
import com.zaq.ihttp.web.OpeaterInTransaction;
import com.zaq.ihttp.web.model.HttpServiceCommit;

/**
 * 实现事务提交时可以实现此方法
 * 更快捷的转换
 * @author zaqzaq
 * 2014年10月16日
 * 
 * @param <T>
 */
public abstract class SaveOpeaterInTransaction<T> implements OpeaterInTransaction<T>{

	@Override
	final public void execute(HttpServiceCommit commit, long retId) {
		if(StringUtils.isNotEmpty(commit.getLocalObjClassName())&&StringUtils.isNotEmpty(commit.getLocalObjJson())){
			
			Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			T obj= gson.fromJson(commit.getLocalObjJson(), TypeUtil.getSuperclassTypeParameter(getClass()));
			save(obj, retId);
		}
//		else{
//			throw new ZAQhttpException("此操作非远程save操作");
//		}
	}
	/**
	 * 注意：反序列出来的对象用的Gson对象为new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
	 * save前还是从调用一下get(long id)方法吧，除非bean中每个字段都加了@Expose注解
	 * @param obj 反序列化出的对象
	 * @param retId 事务提交后返回的ID
	 */
	public abstract void save(T obj,long retId);
}
