package com.zaq.ihttp.web.service.impl;

import java.util.HashMap;
import java.util.Map;

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
	/**
	 * 防火墙缓存
	 */
	private static Map<String, HttpServiceFirewall> cache=new HashMap<String, HttpServiceFirewall>();
	@Override
	public HttpServiceFirewall getByIp(String ip,String userName){
		String key=ip+userName;
		if(!cache.containsKey(key)){
			Criteria criteria=getSession().createCriteria(HttpServiceFirewall.class).add(Restrictions.eq("ip", ip))
					.add(Restrictions.eq("userName", userName));

			cache.put(key, (HttpServiceFirewall) criteria.uniqueResult());
		}
		
		return cache.get(key);
	}
	@Override
	public void save(HttpServiceFirewall firewall){
		getHibernateTemplate().save(firewall);
		cache.put(firewall.getIp()+firewall.getUserName(), firewall);
	}

	@Override
	public void clearCache() {
		cache.clear();
	}
	
}
