package com.zaq.ihttp.web.service;


import org.apache.http.NameValuePair;

import com.zaq.ihttp.web.IcallBack;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.TransactionCommand;
import com.zaq.ihttp.web.model.HttpServiceCommit;
/**
 * 分布式事务包装处理接口
 * @author zaqzaq
 *
 * @param <T>
 */
public interface ITransactionService<T>{

	/**
	 * 打开一个事务，用以组装成一个事务 按顺序执行
	 * @param commands 需要处理的多个事务命令 
	 * 
	 * @return 返回每个业务预处理后的提交事务  本地业务 请返回空
	 */
	public HttpServiceCommit[] prepareTransaction(TransactionCommand...commands);
	/**
	 * 分布式业务一个远程预处理【保存或更新】
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param obj
	 *            要保存的对象
	 * @return 返回一个远程调用的预处理
	 */
	public HttpServiceCommit prepareSaveOrUpdate(final String host,final String packagez,final String action,final T obj);
	
	/**
	 * 分布式业务一个远程预处理【删除】  。。。。些方法未开启事务
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param ids 
	 *            要保存的对象
	 * @return  返回一个远程调用的预处理
	 */
	public HttpServiceCommit prepareRemove(final String host,final String packagez,final String action,final Long... ids)throws Exception;
	
	/**
	 * 开启分布式提交所有操作的事务
	 * @param commits 所有操作
	 * @param calls 失败超过最大限制TRIGGER_EVENT_TIMES的回调方法
	 * @return 返回分布式业务处理流程是否成功  false 表示预处理成功，提交失败，可以手动再提交
	 */
	public boolean saveReCall(HttpServiceCommit[] commits,IcallBack... calls);
	/**
	 * 查询接口
	 * @param host
	 *            应用名
	 * @param packagez
	 *            模块
	 * @param action
	 *            功能
	 * @param parms 参数
	 * @return
	 */
	public RetObj<T> query(String host,String packagez,String action,NameValuePair... parms);


	/**
	 * 通用的分布式业务处理
	 * @param commands 需要处理的多个事务命令 
	 * @return 返回分布式业务处理流程是否成功  false 表示预处理成功，提交失败，可以手动再提交
	 */
	public boolean callCommon(TransactionCommand... commands);
}
