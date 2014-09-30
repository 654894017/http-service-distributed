package com.zaq.oa;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.xpsoft.core.dao.GenericDao;
import com.xpsoft.core.service.impl.BaseServiceImpl;
import com.zaq.ihttp.util.CallUtil;
import com.zaq.ihttp.util.HttpServiceUtil;
import com.zaq.ihttp.web.IcallBack;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.TransactionCommand;
import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.ihttp.web.service.ITransactionService;

/**
 * 提供给OA系统中serviceimpl层继层的父类【分布式事务代理对象】
 * @author zaqzaq
 *
 * @param <T>
 */
public abstract class OaBaseServiceImpl<T> extends BaseServiceImpl<T> implements OaBaseService<T>{
	protected ITransactionService<T> call= (ITransactionService<T>) HttpServiceUtil.getBean("transactionService");
	
	public OaBaseServiceImpl(GenericDao dao) {
		super(dao);
	}
	
	@Override
	public HttpServiceCommit[] prepareTransaction(TransactionCommand...commands){
		return call.prepareTransaction(commands);
	}
	
	/**
	 * 组装【保存或更新】操作事务 与本地业务数据保存在一个事务中
	 * @param host
	 * @param packagez
	 * @param action
	 * @param obj
	 */
	@Override
	public HttpServiceCommit[] saveWithLocal(final String host,final String packagez,final String action,final T obj){
		TransactionCommand command0=new TransactionCommand() {
			@Override
			public HttpServiceCommit execute() {
				save(obj);
				return null;
			}
		};
		TransactionCommand command1=new TransactionCommand() {
			@Override
			public HttpServiceCommit execute() {
				return prepareSaveOrUpdate(host, packagez, action, obj);
			}
		};
		
		return prepareTransaction(command0,command1);
	}
	
	@Override
	public HttpServiceCommit prepareSaveOrUpdate(final String host,final String packagez,final String action,final T obj){
				return call.prepareSaveOrUpdate(host, packagez, action, obj);
	}
	
	/**
	 * 组装【删除】操作事务
	 * @param host
	 * @param packagez
	 * @param action
	 * @param obj
	 */
	/*
	protected HttpServiceCommit remove(String host,String packagez,String action,Long... ids)throws Exception{
		BasicNameValuePair[] parms=new BasicNameValuePair[ids.length];
		for(int i=0;i<ids.length;i++){
			remove(ids[i]);
			parms[i]=new BasicNameValuePair(HttpServiceUtil.HTTP_ARG_SEQID, ids[i]+"");
		}
		return call.callDelPrepare(host, packagez, action, parms);
	}*/
	@Override
	public HttpServiceCommit removeWithLocal(final String host,final String packagez,final String action,final Long... ids)throws Exception{
				for(int i=0;i<ids.length;i++){
					remove(ids[i]);
				}
				return call.prepareRemove(host, packagez, action, ids);
	}
	@Override
	public HttpServiceCommit prepareRemove(String host, String packagez, String action, Long... ids) throws Exception {
		return call.prepareRemove(host, packagez, action, ids);
	}
	/**
	 * 开启分布式提交所有操作的事务
	 * @param commits 所有操作
	 * @return 有一个不成功则返回false
	 */
	public boolean saveReCall(HttpServiceCommit[] commits,IcallBack... calls){
		 return call.saveReCall(commits,calls);
	}
	/**
	 * 分布式业务处理【删除】  。。。。些方法未开启事务
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
	/*
	*/
	@Override
	public boolean callCommon(TransactionCommand... commands){
			return saveReCall(prepareTransaction(commands));
	}
	
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
	@Override
	public RetObj<T> query(String host,String packagez,String action,NameValuePair... parms){
		try {
			System.out.println(getClass().getMethod("query").getGenericReturnType());
			return CallUtil.query(host, packagez, action,this.getClass().getDeclaredMethod("query").getGenericReturnType(), parms);
		} catch (Exception e) {
			logger.error("接口对接失败",e);
			return new RetObj("接口对接失败");
		}
	}
	/**
	 * 只为获取getClass().getMethod("query").getGenericReturnType();
	 * @return 返回空实现
	 */
	public abstract RetObj<T> query();
}
