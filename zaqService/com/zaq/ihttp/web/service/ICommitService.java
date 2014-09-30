package com.zaq.ihttp.web.service;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.zaq.ihttp.web.model.HttpServiceCommit;
/**
 * 事务提交hibernate接口
 * @author zaqzaq
 *
 */
public interface ICommitService{
	/**
	 * 请求的预处理成功后 保存事务
	 * @param commit 事务对象
	 * @return
	 */
	public HttpServiceCommit save(HttpServiceCommit commit);
	/**
	 * 事务提交失败后，更新事务对象
	 * @param id 提交事务ID
	 * @param errorCode 处理失败返回的错误码
	 * @return
	 */
	public HttpServiceCommit reSendAdd(long id,long errorCode);
	/**
	 * 事务提交成功后，清除该事务
	 * @param commit
	 */
	public void del(HttpServiceCommit commit);
	/**
	 * 获取所有未能成功提交的事务
	 * @return
	 */
	public List<HttpServiceCommit> getAll();
}
