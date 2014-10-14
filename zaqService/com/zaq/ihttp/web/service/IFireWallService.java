package com.zaq.ihttp.web.service;

import com.zaq.ihttp.web.model.HttpServiceFirewall;
/**
 * 防火墙hibernate接口
 * @author zaqzaq
 *
 */
public interface IFireWallService{
	/**
	 * 按IP和用户名获取
	 * @param ip
	 * @param userName
	 * @return
	 */
	public HttpServiceFirewall getByIp(String ip,String userName);
	/**
	 * 保存一个防火墙信息
	 * @param firewall
	 */
	public void save(HttpServiceFirewall firewall);
	/**
	 * 清除缓存
	 */
	public void clearCache();
}
