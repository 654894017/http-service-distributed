package com.zaq.ihttp.web;

import javax.servlet.http.HttpServletRequest;
/**
 * 【删除操作】
 * @author zaqzaq
 *
 * @param <T>
 */
public interface IHttpDelService<T> {
	/**
	 * 【删除操作】预处理接口
	 * @param delIds 需要删除的Ids
	 * @return 预处理后 事务提交的seqId序列  当返回值小于0是 为错误代码
	 */
	public long delPrepare(Long...delIds);
	/**
	 * 【删除操作】事务提交接口
	 * @param seqId 预处理返回的seqId序列
	 * @return 成功时为事务提交的seqId序列  当返回值小于0是 为错误代码
	 */
	public long del(long seqId);
}
