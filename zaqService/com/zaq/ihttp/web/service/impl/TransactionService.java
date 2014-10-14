package com.zaq.ihttp.web.service.impl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zaq.ihttp.util.HttpServiceUtil;
import com.zaq.ihttp.web.IcallBack;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.TransactionCommand;
import com.zaq.ihttp.web.client.HttpServiceCall;
import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.ihttp.web.service.ITransactionService;

/**
 * 事务控制层中 实际操作类
 * @author zaqzaq
 *
 * @param <T>
 */
public class TransactionService<T> implements ITransactionService<T>{
	protected HttpServiceCall<T> call=new HttpServiceCall<T>();
	
	@Override
	public HttpServiceCommit[] prepareTransaction(TransactionCommand...commands){
		HttpServiceCommit[] commits=new HttpServiceCommit[commands.length];
		for(int i=0;i<commands.length;i++){
			commits[i]=commands[i].execute();
		}
		return commits;
	}
	
	
	@Override
	public HttpServiceCommit prepareSaveOrUpdate(final String host,final String packagez,final String action,final T obj){
		return call.callSaveOrUpdatePrepare(host, packagez, action, obj);
	}
	/**
	 * 开启分布式提交所有操作的事务
	 * @param commits 所有操作
	 * @return 有一个不成功则返回false
	 */
	@Override
	public boolean saveReCall(HttpServiceCommit[] commits,IcallBack... calls){
		 boolean retBoo=true;
		 for(HttpServiceCommit commit:commits){
			 if(null!=commit){
				 if(!call.reCall(commit,calls)){
					 retBoo=false;
				 }
			 }
		 }
		 return retBoo;
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
		
		return call.query(host, packagez, action, parms);
	}


	@Override
	public HttpServiceCommit prepareRemove(String host, String packagez, String action, Long... ids) throws Exception {
		BasicNameValuePair[] parms=new BasicNameValuePair[ids.length];
		for(int i=0;i<ids.length;i++){
			parms[i]=new BasicNameValuePair(HttpServiceUtil.HTTP_ARG_SEQID, ids[i]+"");
		}
		return call.callDelPrepare(host, packagez, action, parms);
	}
	
	/**
	 * 分布式业务处理  。。。。此方法未开启事务------供外层无事务的类使用。。。eg:com.zaq.oa.OaBaseServiceImpl.callCommon()
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
	@Deprecated
	private boolean callCommon(TransactionCommand... commands){
			return saveReCall(prepareTransaction(commands));
	}
}
