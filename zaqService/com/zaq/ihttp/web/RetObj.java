package com.zaq.ihttp.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;

/**
 * 对象数据包
 * @author zaqzaq
 *
 * @param <T>
 */
public class RetObj <T>{
	@Expose
	private boolean success=false;
	@Expose
	private String remark;
	@Expose
	private T obj;
	@Expose
	private List<T> objs;
	@Expose
	private Integer hasTotal;
	@Expose
	private String jsonObjs;

	public RetObj(){}
	/**
	 * success=false
	 * @param remark 备注
	 */
	public RetObj(String remark){
		this.remark=remark;
	}
	/**
	 * jsonObjs 如果不为空 success=true
	 * @param remark
	 * @param jsonObjs 如果不为空 success=true
	 */
	public RetObj(String remark,String jsonObjs){
		this.remark=remark;
		this.jsonObjs=jsonObjs;
		if(!StringUtils.isEmpty(jsonObjs)){
			this.success=true;
		}
	}
	public RetObj(T obj){
		this.success=true;
		this.obj=obj;
	}
	public RetObj(List<T> objs){
		this.success=true;
		this.objs=objs;
	}
	public RetObj(List<T> objs,int hasTotal){
		this.success=true;
		this.objs=objs;
		this.hasTotal=hasTotal;
	}
	
	
	
	public String getJsonObjs() {
		return jsonObjs;
	}
	public boolean isSuccess() {
		return success;
	}
	public String getRemark() {
		return remark;
	}
	public T getObj() {
		return obj;
	}
	public List<T> getObjs() {
		return objs;
	}
	public Integer getHasTotal() {
		return hasTotal;
	}
}
