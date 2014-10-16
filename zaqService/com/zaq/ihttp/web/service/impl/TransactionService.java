package com.zaq.ihttp.web.service.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.zaq.ihttp.util.HttpServiceUtil;
import com.zaq.ihttp.util.HttpUtil;
import com.zaq.ihttp.util.ThreadPool;
import com.zaq.ihttp.web.IcallBack;
import com.zaq.ihttp.web.OpeaterInTransaction;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.TransactionCommand;
import com.zaq.ihttp.web.ZAQhttpException;
import com.zaq.ihttp.web.client.HttpServiceCall;
import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.ihttp.web.service.ICommitService;
import com.zaq.ihttp.web.service.ITransactionService;

/**
 * 事务控制层中 实际操作类
 * @author zaqzaq
 *
 * @param <T>
 */
public class TransactionService<T> implements ITransactionService<T>{
	@Autowired
	private  ICommitService commitService;

	private Logger logger=Logger.getLogger(getClass());
	/**
	 * 多事务请求超时时间=httpClient从连接池中获取的超时时间+http连接的超时时间+http请求处理的超时时间
	 * 这样不会因为超时而引起程序通过后 再保存了远程调用的（脏）事务
	 */
	private static final long TIME_OUT_PREPARE_TRANSACTION=HttpUtil.TIME_OUT_GET_POOL+HttpUtil.TIME_OUT_HTTP_CONNECT+HttpUtil.TIME_OUT_HTTP_PROCESS;
	protected HttpServiceCall<T> call=new HttpServiceCall<T>();
	
	@Override
	public HttpServiceCommit[] prepareTransaction(TransactionCommand...commands){
		HttpServiceCommit[] commits=new HttpServiceCommit[commands.length];
		for(int i=0;i<commands.length;i++){
			commits[i]=commands[i].execute();
			commitService.save(commits[i]);
		}
		return commits;
	}

	@Override
	public HttpServiceCommit[] prepareTransactionWithThread(TransactionCommand localCommand,final TransactionCommand... commands) {
		if(null!=localCommand){
			localCommand.execute();
		}
//		final CyclicBarrier barrier=new CyclicBarrier(commands.length+1);
		final CountDownLatch latch=new CountDownLatch(commands.length);//快速释放线程
		final HttpServiceCommit[] commits=new HttpServiceCommit[commands.length];
		final AtomicInteger errorCount=new AtomicInteger(0);//远程调用错误次数
		final FutureTask[] futureTasks=new FutureTask[commands.length];
		for(int i=0;i<commands.length;i++){
			final int ii=i;
			ThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					FutureTask<HttpServiceCommit> future=new FutureTask<HttpServiceCommit>(
						new Callable<HttpServiceCommit>() {
							@Override
							public HttpServiceCommit call() throws Exception {
								return commands[ii].execute();
							}
						}
					);
					futureTasks[ii]=future;
					ThreadPool.execute(future);
					try {
						//提供ii ms的程序缓冲的执行时间
						commits[ii]=future.get(TIME_OUT_PREPARE_TRANSACTION+ii, TimeUnit.MILLISECONDS);
					}catch (ExecutionException e) {
						errorCount.addAndGet(1);
						logger.error("多线程事务预处理异常【重要的日志】", e);
						//当有一个错误时  终止所有其它线程
						for(FutureTask futureTask:futureTasks){
							if(null!=futureTask){//&&!futureTask.isDone()
								futureTask.cancel(true);
							}
						}
					}catch (Exception e) {
						logger.error("多线程事务预处理异常【不太重要的日志】", e);
					}finally{
						try {
//							barrier.await();
							latch.countDown();
						} catch (Exception e) {
							logger.error("多线程事务预处理异常【一点也不重要的日志】", e);
						}
					}
					
				}
			});
			
		}
		try {
			/*
			//提供ii+5ms的程序缓冲的执行时间  这样不会因为超时而引起程序通过后，再保存了远程调用的（脏）事务
			barrier.await(TIME_OUT_PREPARE_TRANSACTION+commands.length+5, TimeUnit.MILLISECONDS);
			*/
//			barrier.await();
			latch.await();
		} catch (Exception e) {
			throw new ZAQhttpException(logger, "多线程事务预处理异常", e);
		}
		 if(errorCount.get()>0){
			 throw new ZAQhttpException(logger, "多线程事务预处理异常");
		 }else{
			 commitService.save(commits);
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
	@Override
	public boolean saveReCall(HttpServiceCommit[] commits,OpeaterInTransaction opeaterInTransaction,IcallBack... calls){
		 boolean retBoo=true;
		 for(HttpServiceCommit commit:commits){
			 if(null!=commit){
				 if(!call.reCall(commit,opeaterInTransaction,calls)){
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
	public HttpServiceCommit prepareRemove(String host, String packagez, String action, Long... ids){
		BasicNameValuePair[] parms=new BasicNameValuePair[ids.length];
		for(int i=0;i<ids.length;i++){
			parms[i]=new BasicNameValuePair(HttpServiceUtil.HTTP_ARG_DELS, ids[i]+"");
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
