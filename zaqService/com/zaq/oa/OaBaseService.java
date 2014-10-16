package com.zaq.oa;


import com.xpsoft.core.service.BaseService;
import com.zaq.ihttp.web.TransactionCommand;
import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.ihttp.web.service.ITransactionService;

public interface OaBaseService<T> extends BaseService<T>,ITransactionService<T>{
	/**
	 * 快捷的分布式业务本地和一个远程预处理【保存或更新】
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param obj
	 *            要保存的对象
	 * @return 返回本地及一个远程调用的预处理
	 */
	public HttpServiceCommit[] saveWithLocal(final String host,final String packagez,final String action,final T obj);
	
	/**
	 * 快捷的分布式业务处理包含本地的【删除】  。。。。些方法未开启事务
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param ids 
	 *            要保存的对象
	 * @return 返回分布式业务处理流程是否成功  false 表示预处理成功，提交失败，可以手动再提交
	 */
	public HttpServiceCommit removeWithLocal(final String host,final String packagez,final String action,final Long... ids)throws Exception;
	
	/**
	 * 通用的分布式业务处理 按顺序依次执行
	 * @param commands 需要处理的多个事务命令 
	 * @return 返回分布式业务处理流程是否成功  false 表示预处理成功，提交失败，可以手动再提交
	 */
	public boolean callCommon(TransactionCommand... commands);
	/**
	 * 通用的分布式业务处理 按多线程方式执行(提高执行效率)
	 * @param localCommand 需要处理的本地事务命令
	 * @param commands 需要处理的多个事务命令 
	 * @return 返回分布式业务处理流程是否成功  false 表示预处理成功，提交失败，可以手动再提交
	 */
	public boolean callCommonWithThread(TransactionCommand localCommand,TransactionCommand...commands);
}
