package com.zaq.ihttp.web;

import javax.servlet.http.HttpServletRequest;
/**
 * 【查询操作】
 * @author zaqzaq
 *
 * @param <T>
 */
public interface IHttpQueryService<T> {
	/**
	 * 【查询操作】
	 * @param request 放置有查询参数
	 * @return 返回查询到的数据包装对象
	 */
	public RetObj<T> query(HttpServletRequest request);
}
