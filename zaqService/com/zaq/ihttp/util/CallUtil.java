package com.zaq.ihttp.util;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.zaq.ihttp.web.HttpServiceMethod;
import com.zaq.ihttp.web.IcallBack;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.ZAQhttpException;
import com.zaq.ihttp.web.client.HttpServiceCall;
import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.ihttp.web.service.ICommitService;
import com.zaq.ihttp.web.service.impl.CommitService;

/**
 * httpService客户端调用工具类
 * 
 * @author zaqzaq
 * 
 */
public class CallUtil {
	protected static Logger logger = Logger.getLogger(CallUtil.class);
	protected static ICommitService commitService = (ICommitService) HttpServiceUtil.getBean("commitService");

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
		/*
		String uri = HttpServiceUtil.packageUri(HttpServiceConf.getMyPro(host, "httpUri", ""), packagez, action, HttpServiceMethod.delPrepare.name());
		String retSeqId = HttpUtil.httpPost(host, uri, parms);
		long retCode = retCode(retSeqId);
		if (retCode > 0) {
			return commitService.save(new HttpServiceCommit(host, packagez, action, HttpServiceMethod.del.name(), retCode));

		} else {
			throw new ZAQhttpException(logger, "接口请求update失败，失败代码" + retCode);
		}
	*/
		return call(host, packagez, action, HttpServiceMethod.delPrepare, parms);

	}

	/**
	 * 预处理公共类
	 * @param host
	 * @param packagez
	 * @param action
	 * @param methodPrepare
	 * @param parms
	 * @return
	 */
	public static HttpServiceCommit call(String host, String packagez, String action,HttpServiceMethod methodPrepare, NameValuePair... parms) {
		String uri = HttpServiceUtil.packageUri(HttpServiceConf.getMyPro(host, "httpUri", ""), packagez, action, methodPrepare.name());
		String retSeqId = HttpUtil.httpPost(host, uri, parms);
		long retCode = retCode(retSeqId);
		if (retCode > 0) {
//			if(null==commitService){
//				commitService = (CommitService) HttpServiceUtil.getBean("commitService");
//			}
/**
 * 将预处理的事务对象 转至TransactionService中持久化（多线程Spring事务无法传播）			
 */
//			return commitService.save(new HttpServiceCommit(host, packagez, action, HttpServiceMethod.get(methodPrepare.ordinal()+1).name(), retCode));
			return new HttpServiceCommit(host, packagez, action, HttpServiceMethod.get(methodPrepare.ordinal()+1).name(), retCode);
		} else {
			throw new ZAQhttpException(logger, "接口请求预处理事务 失败，失败代码" + retCode);
		}

	}
	
	/**
	 * 提交分布式事务
	 * 
	 * @param commit
	 *            提交内容序列
	 * @param calls    超过【TRIGGER_EVENT_TIMES】次触发
	 *            回调方法
	 * @return 返回提交事务是否成功
	 */
	public static boolean reCall(HttpServiceCommit commit, IcallBack... calls) {
		boolean retBoo = false;
		try {
			String retSeqId = HttpUtil.httpPost(commit.getHost(), commit.getUri(), new BasicNameValuePair(HttpServiceUtil.HTTP_ARG_SEQID, commit.getSeqId() + ""));
			long retCode = retCode(retSeqId);
//			if(null==commitService){
//				commitService = (CommitService) HttpServiceUtil.getBean("commitService");
//			}
			
			if (retCode > 0 && retCode == commit.getSeqId()) {
				// 清除本地信息 操作成功返回true 失败记录log日志
				commitService.del(commit);
				retBoo = true;
			} else {
				// 更新本条业务数据的重调次数+1 并追加返回的错误码 超过最大错误限制将触发回调事件
				commit = commitService.reSendAdd(commit.getId(),retCode);
				if (Integer.parseInt(HttpServiceConf.getPro("TRIGGER_EVENT_TIMES", 3 + "")) < commit.getCountReSend()) {
					// 线程池中多线程执行
					for (final IcallBack callBack : calls) {
						ThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								callBack.call();
							}
						});
					}
				}
			}
		} catch (Exception e) {
			logger.error("【FIXME 极为重要的日志】回调业务处理失败", e);//FIXME 极为重要的日志
		}
		return retBoo;
	};
	
	/**
	 * 开启分布式提交所有操作的事务
	 * @param commits 所有操作
	 * @param calls 失败超过最大限制TRIGGER_EVENT_TIMES的回调方法
	 * @return 有一个不成功则返回false
	 */
	public static boolean saveReCall(HttpServiceCommit[] commits,IcallBack... calls){
		 boolean retBoo=true;
		 for(HttpServiceCommit commit:commits){
			 if(null!=commit){
				 if(reCall(commit,calls)){
					 retBoo=false;
				 }
			 }
		 }
		 return retBoo;
	}

	protected static long retCode(String retSeqId) {
		// 请求失败
		if (HttpUtil.ERROR.equals(retSeqId)) {
			return HttpUtil.CODE_ERROR;
		}
		// 无权限
		if (HttpUtil.ACCESSDENIED.equals(retSeqId)) {
			return HttpUtil.CODE_ACCESSDENIED;
		}

		long retLong = Long.parseLong(retSeqId);
//		if (retLong < 0) {
//			throw new ZAQhttpException("接口返回异常代码：" + retSeqId);
//		}
		return retLong;
	};
	
	
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
	public static RetObj query(String host, String packagez, String action,Type type, NameValuePair... parms) {
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
		RetObj obj;
		try {
			obj = gson.fromJson(retJsonStr,type);
		} catch (Exception e) {
			obj = new RetObj("接口对接失败");
		}
		return obj;
	}
	
}
