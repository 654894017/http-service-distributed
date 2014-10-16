package com.zaq.ihttp.web.client;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaq.ihttp.util.CallUtil;
import com.zaq.ihttp.util.HttpServiceConf;
import com.zaq.ihttp.util.HttpServiceUtil;
import com.zaq.ihttp.util.HttpUtil;
import com.zaq.ihttp.web.HttpServiceMethod;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.model.HttpServiceCommit;

/**
 * httpService客户端调用基类
 * 
 * @author zaqzaq
 * 
 * @param <T>
 */
public class HttpServiceCall<T> extends CallUtil{
	
	/**
	 * query操作 统一查询操作
	 * 
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param parms
	 *            查询参数
	 * @return
	 */
	public RetObj<T> query(String host, String packagez, String action, NameValuePair... parms) {
		String retJsonStr = HttpUtil.httpPost(host, HttpServiceUtil.packageUri(HttpServiceConf.getMyPro(host, "httpUri", ""), packagez, action, HttpServiceMethod.query.name()), parms);
		// 请求失败
		if (HttpUtil.ERROR.equals(retJsonStr)) {
			return new RetObj("接口调用失败");
		}
		// 无权限
		if (HttpUtil.ACCESSDENIED.equals(retJsonStr)) {
			return new RetObj(HttpUtil.ACCESSDENIED);
		}

		// 无权限
		if (StringUtils.isNumeric(retJsonStr) && (Long.parseLong(retJsonStr)) < 0) {
			return new RetObj("接口返回错误代码：" + retJsonStr);
		}

		Gson gson = new Gson();
		RetObj<T> obj;
		try {
			obj = gson.fromJson(retJsonStr, this.getClass().getMethod("query", HttpServiceCall.class).getGenericReturnType());
		} catch (Exception e) {
			logger.error("接口对接失败",e);
			obj = new RetObj("接口对接失败");
		}
		return obj;
	}
	
	/**
	 * save OR update操作：预处理 保存调用信息 与本地业务数据保存在一个事务中
	 * 
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param obj
	 *            要更新的对象
	 * @return
	 */
	public HttpServiceCommit callSaveOrUpdatePrepare(String host, String packagez, String action, T obj) {
		Gson gsonRemote = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Gson gsonLocal = new Gson();
		return call(host, packagez, action, HttpServiceMethod.saveOrUpdatePrepare,obj.getClass().getName(),gsonLocal.toJson(obj), new BasicNameValuePair(HttpServiceUtil.HTTP_ARG_JSONOBJ, gsonRemote.toJson(obj)));
	}
	/**
	 * del操作：预处理 保存调用信息 与本地业务数据保存在一个事务中
	 * 
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param parms
	 *            要删除的对象的Id
	 * @return
	 */
	public static HttpServiceCommit callDelPrepare(String host, String packagez, String action, NameValuePair... parms) {
		return call(host, packagez, action, HttpServiceMethod.delPrepare, parms);

	}
}
