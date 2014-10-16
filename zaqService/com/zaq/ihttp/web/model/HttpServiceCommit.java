package com.zaq.ihttp.web.model;


import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.zaq.ihttp.util.HttpServiceConf;
import com.zaq.ihttp.util.HttpServiceUtil;
/**
 * 事务提交BEAN
 * @author zaqzaq
 *
 */
public class HttpServiceCommit implements Serializable {

	private static final long serialVersionUID = 1L;
	@Expose
    protected Long id;  
	@Expose
	protected String host;
	@Expose
	protected String packagez;
	@Expose
	protected String action;
	@Expose
	protected String method;
	@Expose
	protected String uri;
	@Expose
	protected Long seqId;
	@Expose
	protected java.util.Date timeCreate;
	@Expose
	protected Integer countReSend;
	@Expose
	protected String errorCode;
	@Expose
	protected String localObjClassName;
	@Expose
	protected String localObjJson;
	
	/**
	 * Default Empty Constructor for class HttpServiceCommit
	 */
	public HttpServiceCommit () {
		super();
	}
	
	public HttpServiceCommit(String host, String packagez, String action, String method, Long seqId) {
		super();
		this.host = host;
		this.packagez = packagez;
		this.action = action;
		this.method = method;
		this.seqId = seqId;
		this.timeCreate=new Date();
		this.countReSend=0;
		this.uri=HttpServiceUtil.packageUri(HttpServiceConf.getMyPro(host, "httpUri", ""), packagez, action, method);
	}
	public HttpServiceCommit(String host, String packagez, String action, String method, Long seqId,String localObjClassName,String localObjJson) {
		super();
		this.host = host;
		this.packagez = packagez;
		this.action = action;
		this.method = method;
		this.seqId = seqId;
		this.timeCreate=new Date();
		this.countReSend=0;
		this.uri=HttpServiceUtil.packageUri(HttpServiceConf.getMyPro(host, "httpUri", ""), packagez, action, method);
		
		this.localObjClassName=localObjClassName;
		this.localObjJson=localObjJson;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Default Key Fields Constructor for class HttpServiceCommit
	 */
	public HttpServiceCommit (
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
	
	public String getLocalObjClassName() {
		return localObjClassName;
	}

	public void setLocalObjClassName(String localObjClassName) {
		this.localObjClassName = localObjClassName;
	}


	public String getLocalObjJson() {
		return localObjJson;
	}
	public void setLocalObjJson(String localObjJson) {
		this.localObjJson = localObjJson;
	}
	/**
	 * Set the id
	 */	
	public void setId(Long aValue) {
		this.id = aValue;
	}	

	/**
	 * 应用	 * @return String
	 * @hibernate.property column="host" type="java.lang.String" length="128" not-null="false" unique="false"
	 */
	public String getHost() {
		return this.host;
	}
	
	/**
	 * Set the host
	 */	
	public void setHost(String aValue) {
		this.host = aValue;
	}	

	/**
	 * 模块	 * @return String
	 * @hibernate.property column="packagez" type="java.lang.String" length="128" not-null="false" unique="false"
	 */
	public String getPackagez() {
		return this.packagez;
	}
	
	/**
	 * Set the packagez
	 */	
	public void setPackagez(String aValue) {
		this.packagez = aValue;
	}	

	/**
	 * 功能	 * @return String
	 * @hibernate.property column="action" type="java.lang.String" length="128" not-null="false" unique="false"
	 */
	public String getAction() {
		return this.action;
	}
	
	/**
	 * Set the action
	 */	
	public void setAction(String aValue) {
		this.action = aValue;
	}	

	/**
	 * 方法	 * @return String
	 * @hibernate.property column="method" type="java.lang.String" length="128" not-null="false" unique="false"
	 */
	public String getMethod() {
		return this.method;
	}
	
	/**
	 * Set the method
	 */	
	public void setMethod(String aValue) {
		this.method = aValue;
	}	

	/**
	 * 请求uri	 * @return String
	 * @hibernate.property column="uri" type="java.lang.String" length="1024" not-null="false" unique="false"
	 */
	public String getUri() {
		return this.uri;
	}
	
	/**
	 * Set the uri
	 */	
	public void setUri(String aValue) {
		this.uri = aValue;
	}	

	/**
	 * 请求ID	 * @return Long
	 * @hibernate.property column="seqId" type="java.lang.Long" length="19" not-null="false" unique="false"
	 */
	public Long getSeqId() {
		return this.seqId;
	}
	
	/**
	 * Set the seqId
	 */	
	public void setSeqId(Long aValue) {
		this.seqId = aValue;
	}	

	/**
	 * 创建时间	 * @return java.util.Date
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

	/**
	 * 重新提交次数	 * @return Integer
	 * @hibernate.property column="countReSend" type="java.lang.Integer" length="3" not-null="false" unique="false"
	 */
	public Integer getCountReSend() {
		return this.countReSend;
	}
	
	/**
	 * Set the countReSend
	 */	
	public void setCountReSend(Integer aValue) {
		this.countReSend = aValue;
	}	

}
