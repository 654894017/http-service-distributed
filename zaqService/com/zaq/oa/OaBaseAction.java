package com.zaq.oa;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.xpsoft.core.web.paging.PagingBean;
import com.zaq.ihttp.web.server.HttpServiceBaseAction;
/**
 * 提供给 公司OA调用的action的基类，用于嵌入http接口
 * @author zaqzaq
 *
 * @param <T> 需要处理的对象
 */
public abstract class OaBaseAction<T> extends HttpServiceBaseAction<T>{
	
	  public static final String SUCCESS = "success";
	  public static final String INPUT = "input";
	  private String successResultValue;
	  public static final String JSON_SUCCESS = "{success:true}";
	  protected String dir;
	  protected String sort;
	  protected Integer limit = Integer.valueOf(25);
	  protected Integer start = Integer.valueOf(0);
	  protected String jsonString;
	  private static final long serialVersionUID = 1L;
	  protected final transient Logger logger = Logger.getLogger(super.getClass());
	  public final String CANCEL = "cancel";
	  public final String VIEW = "view";

	  public String getSuccessResultValue()
	  {
	    return this.successResultValue;
	  }

	  public void setSuccessResultValue(String successResultValue) {
	    this.successResultValue = successResultValue;
	  }

	  public void setJsonString(String jsonString)
	  {
	    this.jsonString = jsonString;
	  }

	  public String getJsonString() {
	    return this.jsonString;
	  }

	  public OaBaseAction() {
	    setSuccessResultValue("/jsonString.jsp");
	  }

	  protected HttpServletRequest getRequest()
	  {
	    return ServletActionContext.getRequest();
	  }

	  protected HttpServletResponse getResponse()
	  {
	    return ServletActionContext.getResponse();
	  }

	  protected HttpSession getSession()
	  {
	    return getRequest().getSession();
	  }

	  protected PagingBean getInitPagingBean()
	  {
	    PagingBean pb = new PagingBean(this.start.intValue(), this.limit.intValue());
	    return pb;
	  }

	  public String list() {
	    return "success";
	  }

	  public String edit() {
	    return "input";
	  }

	  public String save() {
	    return "input";
	  }

	  public String delete() {
	    return "success";
	  }

	  public String multiDelete() {
	    return "success";
	  }

	  public String multiSave() {
	    return "success";
	  }

	  public String getDir() {
	    return this.dir;
	  }

	  public void setDir(String dir) {
	    this.dir = dir;
	  }

	  public String getSort() {
	    return this.sort;
	  }

	  public void setSort(String sort) {
	    this.sort = sort;
	  }

	  public Integer getLimit() {
	    return this.limit;
	  }

	  public void setLimit(Integer limit) {
	    this.limit = limit;
	  }

	  public Integer getStart() {
	    return this.start;
	  }

	  public void setStart(Integer start) {
	    this.start = start;
	  }

}