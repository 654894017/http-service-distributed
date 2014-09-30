package com.zaq.ihttp.web.model;


import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
/**
 * 防火墙BEAN
 * @author zaqzaq
 *
 */
public class HttpServiceFirewall implements Serializable {

	private static final long serialVersionUID = 1L;
	@Expose
    protected Long id;  
	@Expose
	protected String ip;
	@Expose
	protected String userName;
	@Expose
	protected String pwd;
	@Expose
	protected Short status;
	@Expose
	protected java.util.Date timeCreate;

	/**
	 * 在用
	 */
	public final static short STATUS_USE=1;
	/**
	 * 禁用
	 */
	public final static short STATUS_NOTUSE=2;

	/**
	 * Default Empty Constructor for class HttpServiceFirewall
	 */
	public HttpServiceFirewall () {
		super();
	}
	
	public HttpServiceFirewall(String ip, String userName, String pwd) {
		super();
		this.ip = ip;
		this.userName = userName;
		this.pwd = pwd;
		this.timeCreate=new Date();
		this.status=STATUS_USE;
	}

	/**
	 * Default Key Fields Constructor for class HttpServiceFirewall
	 */
	public HttpServiceFirewall (
		 Long in_id
        ) {
		this.setId(in_id);
    }

    

	/**
	 * 	 * @return Long
     * @hibernate.id column="id" type="java.lang.Long" generator-class="native"
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * Set the id
	 */	
	public void setId(Long aValue) {
		this.id = aValue;
	}	

	/**
	 * IP	 * @return String
	 * @hibernate.property column="ip" type="java.lang.String" length="64" not-null="false" unique="false"
	 */
	public String getIp() {
		return this.ip;
	}
	
	/**
	 * Set the ip
	 */	
	public void setIp(String aValue) {
		this.ip = aValue;
	}	

	/**
	 * 用户名	 * @return String
	 * @hibernate.property column="userName" type="java.lang.String" length="128" not-null="false" unique="false"
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * Set the userName
	 */	
	public void setUserName(String aValue) {
		this.userName = aValue;
	}	

	/**
	 * 密码	 * @return String
	 * @hibernate.property column="pwd" type="java.lang.String" length="128" not-null="false" unique="false"
	 */
	public String getPwd() {
		return this.pwd;
	}
	
	/**
	 * Set the pwd
	 */	
	public void setPwd(String aValue) {
		this.pwd = aValue;
	}	

	/**
	 * 状态	 * @return Short
	 * @hibernate.property column="status" type="java.lang.Short" length="5" not-null="false" unique="false"
	 */
	public Short getStatus() {
		return this.status;
	}
	
	/**
	 * Set the status
	 */	
	public void setStatus(Short aValue) {
		this.status = aValue;
	}	

	/**
	 * 	 * @return java.util.Date
	 * @hibernate.property column="timeCreate" type="java.util.Date" length="19" not-null="false" unique="false"
	 */
	public java.util.Date getTimeCreate() {
		return this.timeCreate;
	}
	
	/**
	 * Set the timeCreate
	 */	
	public void setTimeCreate(java.util.Date aValue) {
		this.timeCreate = aValue;
	}	

}
