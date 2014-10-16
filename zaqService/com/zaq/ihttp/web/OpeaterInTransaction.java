package com.zaq.ihttp.web;

import com.zaq.ihttp.web.model.HttpServiceCommit;

/**
 * 远程事务提交成功后的业务接口
 * @author zaqzaq
 * 2014年10月16日
 *
 */
public interface OpeaterInTransaction<T> {
	/**
	 * 远程事务提交成功后的业务操作
	 * @param commit 远程事务提交后返回的Id
	 * @param retId 远程事务提交后返回的Id
	 */
	public void execute(HttpServiceCommit commit,long retId);
}
