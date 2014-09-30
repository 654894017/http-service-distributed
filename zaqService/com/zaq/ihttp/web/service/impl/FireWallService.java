package com.zaq.ihttp.web.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.zaq.ihttp.web.model.HttpServiceFirewall;
import com.zaq.ihttp.web.service.IFireWallService;
/**
 * 防火墙hibernate接口
 * @author zaqzaq
 *
 */
public class FireWallService extends HibernateDaoSupport implements IFireWallService{

	
	public HttpServiceFirewall getByIp(String ip,String userName){
		Criteria criteria=getSession().createCriteria(HttpServiceFirewall.class).add(Restrictions.eq("ip", ip))
							.add(Restrictions.eq("userName", userName));
		return (HttpServiceFirewall) criteria.uniqueResult();
	}
	
	public void save(HttpServiceFirewall firewall){
		getHibernateTemplate().save(firewall);
	}
	
}
