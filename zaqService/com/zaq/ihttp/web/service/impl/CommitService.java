package com.zaq.ihttp.web.service.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.ihttp.web.service.ICommitService;
/**
 * 事务提交hibernate接口
 * @author zaqzaq
 *
 */
public class CommitService extends HibernateDaoSupport implements ICommitService{
	public HttpServiceCommit save(HttpServiceCommit commit){
		commit.setId((Long)(getHibernateTemplate().save(commit)));
		return commit ;
	}
	public HttpServiceCommit[] save(HttpServiceCommit[] commits){
		for(int i=0;i<commits.length;i++){
			commits[i].setId((Long)(getHibernateTemplate().save(commits[i])));
		}
		
		return commits ;
	}
	public HttpServiceCommit reSendAdd(long id,long errorCode){
		HttpServiceCommit commit=(HttpServiceCommit) getSession().get(HttpServiceCommit.class, id);
		commit.setCountReSend(commit.getCountReSend()+1);
		commit.setErrorCode(commit.getErrorCode()+"|"+errorCode);
		getHibernateTemplate().update(commit);
		
		return commit;
	}
	
	public void del(HttpServiceCommit commit){
		getHibernateTemplate().delete(commit);
	}
	
	public List<HttpServiceCommit> getAll(){
		return getSession().createCriteria(HttpServiceCommit.class).list();
	}
}
