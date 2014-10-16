package com.zaq.ihttp.web;
/**
 * 【保存或更新操作】
 * @author zaqzaq
 *
 * @param <T>
 */
public interface IHttpSaveOrUpdateService<T> {
	/**
	 * 【保存或更新操作】预处理接口
	 * @param obj 需要处理的对象
	 * @return 预处理后 事务提交的seqId序列  当返回值小于0是 为错误代码
	 */
	public long saveOrUpdatePrepare(T obj);
	/**
	 * 【保存或更新操作】事务提交接口
	 * @param seqId 预处理返回的seqId序列
	 * @return 成功时返回大于0的数  当返回值小于0是 为错误代码
	 */
	public long saveOrUpdate(long seqId);
}
