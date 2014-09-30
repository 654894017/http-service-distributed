package com.zaq.ihttp.web;

import com.zaq.ihttp.web.model.HttpServiceCommit;
/**
 * 事务处理的命令接口 ，处理一个事务时可创建其实现类
 * @author zaqzaq
 *
 */
public interface TransactionCommand {
	public HttpServiceCommit execute();
}
