package com.xpsoft.oa.action.system;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jim2.rmi.service.Jim2Client;
import com.xpsoft.core.Constants;
import com.xpsoft.core.command.FieldCommandImpl;
import com.xpsoft.core.command.QueryFilter;
import com.xpsoft.core.log.Action;
import com.xpsoft.core.model.OnlineUser;
import com.xpsoft.core.util.AppUtil;
import com.xpsoft.core.util.BeanUtil;
import com.xpsoft.core.util.ContextUtil;
import com.xpsoft.core.util.ExportUtil;
import com.xpsoft.core.util.JsonUtil;
import com.xpsoft.core.util.PositionComparator;
import com.xpsoft.core.util.StringUtil;
import com.xpsoft.core.web.paging.PagingBean;
import com.xpsoft.oa.model.hrm.EmpProfile;
import com.xpsoft.oa.model.statistics.SalesProject;
import com.xpsoft.oa.model.system.AppRole;
import com.xpsoft.oa.model.system.AppUser;
import com.xpsoft.oa.model.system.AppUserMapping;
import com.xpsoft.oa.model.system.AppUserMove;
import com.xpsoft.oa.model.system.AppUserType;
import com.xpsoft.oa.model.system.Department;
import com.xpsoft.oa.model.system.IndexDisplay;
import com.xpsoft.oa.model.system.PanelItem;
import com.xpsoft.oa.model.system.RosterDTO;
import com.xpsoft.oa.model.system.UserFlowconfig;
import com.xpsoft.oa.service.hrm.EmpProfileService;
import com.xpsoft.oa.service.statistics.SalesProjectService;
import com.xpsoft.oa.service.system.AppRoleService;
import com.xpsoft.oa.service.system.AppUserMappingService;
import com.xpsoft.oa.service.system.AppUserMoveService;
import com.xpsoft.oa.service.system.AppUserService;
import com.xpsoft.oa.service.system.AppUserTypeService;
import com.xpsoft.oa.service.system.AppUserUpdateService;
import com.xpsoft.oa.service.system.DepartmentService;
import com.xpsoft.oa.service.system.IndexDisplayService;
import com.xpsoft.oa.service.system.UserFlowconfigService;
import com.xpsoft.oa.service.system.UserSubService;
import com.zaq.ihttp.util.CallUtil;
import com.zaq.ihttp.web.OpeaterInTransaction;
import com.zaq.ihttp.web.RetObj;
import com.zaq.ihttp.web.TransactionCommand;
import com.zaq.ihttp.web.client.SaveOpeaterInTransaction;
import com.zaq.ihttp.web.client.SimpleCallBack;
import com.zaq.ihttp.web.model.HttpServiceCommit;
import com.zaq.oa.OaBaseAction;

import flexjson.DateTransformer;
import flexjson.JSONSerializer;

public class AppUserAction  extends OaBaseAction<AppUser>{
	private static Long SUPPER_MANAGER_ID = Long.valueOf(-1L);
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private AppUserService appUserService;
	@Resource
	private AppUserTypeService appUserTypeService;
	@Resource
	private DepartmentService departmentService;
	@Resource
	private AppUserMappingService appUserMappingService;
	@Resource
	private AppRoleService appRoleService;

	@Resource
	private UserSubService userSubService;
	@Resource
	private EmpProfileService empProfileService;
	@Resource
	private IndexDisplayService indexDisplayService;
	@Resource
	private UserFlowconfigService userFlowconfigService;
	@Resource
	private AppUserUpdateService appUserUpdateService;
	@Resource
	private AppUserMoveService appUserMoveService;
	@Resource
	private SalesProjectService salesProjectService;
	private AppUser appUser;
	private Long userId;
	private Long depId;
	private Long roleId;

	public Long getDepId() {
		return this.depId;
	}

	public void setDepId(Long depId) {
		this.depId = depId;
	}

	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AppUser getAppUser() {
		return this.appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public String getCurrent() {
		AppUser currentUser = ContextUtil.getCurrentUser();
		Department curDep = currentUser.getDepartment();
		if (curDep == null) {
			curDep = new Department();
			curDep.setDepId(Long.valueOf(0L));
			curDep.setDepName(AppUtil.getCompanyName());
		}
		Iterator publicIds = AppUtil.getPublicMenuIds().iterator();
		StringBuffer publicIdSb = new StringBuffer();

		while (publicIds.hasNext()) {
			publicIdSb.append(",").append((String) publicIds.next());
		}
		List<IndexDisplay> list = this.indexDisplayService
				.findByUser(currentUser.getUserId());
		List items = new ArrayList();
		for (IndexDisplay id : list) {
			PanelItem pi = new PanelItem();
			pi.setPanelId(id.getPortalId());
			pi.setColumn(id.getColNum().intValue());
			pi.setRow(id.getRowNum().intValue());
			items.add(pi);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("{success:true,user:{userId:'").append(
				currentUser.getUserId()).append("',fullname:'").append(
				currentUser.getFullname()).append("',depId:'").append(
				curDep.getDepId()).append("',depName:'").append(
				curDep.getDepName()).append("',rights:'");
		sb.append(currentUser.getRights().toString().replace("[", "").replace(
				"]", ""));
		if ((!"".equals(currentUser.getRights())) && (publicIdSb.length() > 0)) {
			sb.append(publicIdSb.toString());
		}
		Gson gson = new Gson();
		sb.append("',items:").append(gson.toJson(items).toString());
		sb.append("}}");
		setJsonString(sb.toString());
		return "success";
	}

	public String listHasCardNo() {

		QueryFilter filter = new QueryFilter(getRequest());
		filter.addFilter("Q_appUser.delFlag_SN_EQ", Constants.FLAG_UNDELETED
				.toString());
		List list = this.empProfileService.getAll(filter);
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(filter.getPagingBean().getTotalItems()).append(
						",result:");
		JSONSerializer serializer = new JSONSerializer();
		serializer.transform(new DateTransformer("yyyy-MM-dd"),
				new String[] { "appUser.accessionTime" });
		buff.append(serializer.include(new String[] { "appUser", "idCard" })
				.serialize(list));
		buff.append("}");
		this.jsonString = buff.toString();

		return "success";
	}

	public String listHasEMP() {

		QueryFilter filter = new QueryFilter(getRequest());
		filter.addFilter("Q_u.delFlag_SN_EQ", Constants.FLAG_UNDELETED
				.toString());
		List<Object[]> list = this.appUserService.getAllHasEMP(filter);
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(filter.getPagingBean().getTotalItems()).append(
						",result:[");
		StringBuffer buffTmp = new StringBuffer("");

		for (int i = 0; i < list.size(); i++) {
			buffTmp.append(",{");

			buffTmp.append("'userId': '" + list.get(i)[0] + "'");
			buffTmp.append(",'username': '" + list.get(i)[1] + "'");
			buffTmp.append(",'fullname': '" + list.get(i)[2] + "'");
			buffTmp.append(",'staffNo': '" + list.get(i)[3] + "'");
			buffTmp.append(",'email': '" + list.get(i)[4] + "'");
			buffTmp.append(",'department': '" + list.get(i)[5] + "'");
			buffTmp.append(",'position': '" + list.get(i)[6] + "'");
			buffTmp.append(",'accessionTime': '" + list.get(i)[7] + "'");
			buffTmp.append(",'status': '" + list.get(i)[8] + "'");
			buffTmp.append(",'profileId': '" + list.get(i)[9] + "'");

			buffTmp.append("}");

		}
		if(list.size()>0){
			buffTmp.deleteCharAt(0);
		}
		

		buff.append(buffTmp);

		buff.append("]}");

		jsonString = buff.toString();

		return SUCCESS;
	}

	public String list() {
		QueryFilter filter = new QueryFilter(getRequest());
		filter
				.addFilter("Q_delFlag_SN_EQ", Constants.FLAG_UNDELETED
						.toString());
		List list = this.appUserService.getAll(filter);
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(filter.getPagingBean().getTotalItems()).append(
						",result:");
		JSONSerializer serializer = new JSONSerializer();
		serializer.transform(new DateTransformer("yyyy-MM-dd"),
				new String[] { "accessionTime" });
		buff.append(serializer.exclude(new String[] { "password" }).serialize(
				list));
		buff.append("}");
		this.jsonString = buff.toString();
		return "success";
	}
	public String listPhones() {
		QueryFilter filter = new QueryFilter(getRequest());
		filter
				.addFilter("Q_delFlag_SN_EQ", Constants.FLAG_UNDELETED
						.toString());
		String username=getRequest().getParameter("Q_fullname_S_EQ");
		FieldCommandImpl fieldCommand = new FieldCommandImpl("fullname", username, "EQ", filter);
		filter.getCommands().add(fieldCommand);
		List list = this.appUserService.getAll(filter);
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(filter.getPagingBean().getTotalItems()).append(
						",result:");
		JSONSerializer serializer = new JSONSerializer();
		serializer.transform(new DateTransformer("yyyy-MM-dd"),
				new String[] { "accessionTime" });
		buff.append(serializer.exclude(new String[] { "password" }).serialize(
				list));
		buff.append("}");
		this.jsonString = buff.toString();
		return "success";
	}
	public String findByFullnames() {
		String fullnames = getRequest().getParameter("fullnames");
		List list = this.appUserService.findByFullnames(fullnames);
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append("25").append(",result:");
		if (list != null && list.size() > 0) {
			JSONSerializer serializer = new JSONSerializer();
			serializer.transform(new DateTransformer("yyyy-MM-dd"),
					new String[] { "accessionTime" });
			buff.append(serializer.exclude(new String[] { "password" })
					.serialize(list));
		} else {
			buff.append("''");
		}
		buff.append("}");
		this.jsonString = buff.toString();
		return "success";
	}

	public String select() {
		PagingBean pb = getInitPagingBean();
		String strDepId = getRequest().getParameter("depId");
		
		String path = "0.";
		this.appUser = ContextUtil.getCurrentUser();
		if (StringUtils.isNotEmpty(strDepId)) {
			Long depId = Long.valueOf(Long.parseLong(strDepId));
			Department dep = (Department) this.departmentService.get(depId);
			if (dep != null)
				path = dep.getPath();
		} else {
			Department dep = this.appUser.getDepartment();
			if (dep != null) {
				path = dep.getPath();
			}
		}
		List list = this.appUserService.findByDepartment(path, pb);
		List listNew=new ArrayList();
		
		String projectid=getRequest().getParameter("projectid");
		if(StringUtils.isNotEmpty(projectid)){//过滤下用户
			SalesProject salesProject=salesProjectService.get(Long.parseLong(projectid));
			for (int i = 0; i < list.size(); i++) {
				AppUser au=(AppUser) list.get(i);
				if(salesProject.getBusinessUsers().contains(au)){
					continue;
				}
				listNew.add(au);
			}
		}else{
			listNew=list;
		}
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(pb.getTotalItems()).append(",result:");
		JSONSerializer serializer = new JSONSerializer();
		serializer.transform(new DateTransformer("yyyy-MM-dd"),
				new String[] { "accessionTime" });
		buff.append(serializer.exclude(new String[] { "password" }).serialize(
				listNew));
		buff.append("}");

		this.jsonString = buff.toString();
		return "success";
	}

	public String selectUserRole() {
		PagingBean pb = getInitPagingBean();
		String strDepId = getRequest().getParameter("depId");

		String path = "0.";
		this.appUser = ContextUtil.getCurrentUser();
		if (StringUtils.isNotEmpty(strDepId)) {
			Long depId = Long.valueOf(Long.parseLong(strDepId));
			Department dep = (Department) this.departmentService.get(depId);
			if (dep != null)
				path = dep.getPath();
		} else {
			Department dep = this.appUser.getDepartment();
			if (dep != null) {
				path = dep.getPath();
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		String fullname = getRequest().getParameter("fullname");
		String rolename = getRequest().getParameter("rolename");
		List list = this.appUserService.findUserRoleByDepartment(fullname,
				rolename, path, pb);
		StringBuffer stringBuffer = new StringBuffer();
		for (Object object : list) {
			AppUser appUser = (AppUser) object;
			String appUserId = appUser.getId();
			String appUserName = appUser.getFullname();
			String appUserDeptName = appUser.getDepartment().getDepName();
			String key = appUserId + "," + appUserName + "," + appUserDeptName;
			stringBuffer.setLength(0);
			Set<AppRole> set = appUser.getRoles();
			for (AppRole appRole : set) {
				stringBuffer.append(" , ");
				stringBuffer.append(appRole.getRoleName());
			}
			map.put(key, stringBuffer.toString().replaceFirst(" , ", ""));
		}

		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(pb.getTotalItems()).append(",result:[");
		int c = 0;
		for (Entry<String, String> entry : map.entrySet()) {
			if (c != 0) {
				buff.append(",");
			}
			c++;
			buff.append("{");
			String key = entry.getKey();
			String value = entry.getValue();
			String[] keys = key.split(",");
			buff.append("userId:'" + keys[0] + "',");
			buff.append("fullname:'" + keys[1] + "',");
			buff.append("department:'" + keys[2] + "',");
			buff.append("haveRole:'" + value + "'");
			buff.append("}");
		}
		buff.append("]}");
		System.out.println(buff.toString());
		this.jsonString = buff.toString();
		return "success";
	}

	public String online() {
		Map onlineUsers = new HashMap();
		Map onlineUsersByDep = new HashMap();
		Map onlineUsersByRole = new HashMap();

		onlineUsers = AppUtil.getOnlineUsers();

		if (this.depId != null) {
			Set<String> onlineUsersSet = onlineUsers.keySet();
			for (String sessionId : onlineUsersSet) {
				OnlineUser onlineUser = new OnlineUser();
				onlineUser = (OnlineUser) onlineUsers.get(sessionId);

				String path = "";
				if (!onlineUser.getUserId().equals(AppUser.SUPER_USER)) {
					path = onlineUser.getDepPath();
				}
				if (!this.depId.equals(new Long(0L))) {
					if (Pattern.compile("." + this.depId + ".").matcher(path)
							.find())
						onlineUsersByDep.put(sessionId, onlineUser);
				} else {
					onlineUsersByDep.put(sessionId, onlineUser);
				}
			}

		}

		if (this.roleId != null) {
			Set<String> onlineUsersSet = onlineUsers.keySet();
			for (String sessionId : onlineUsersSet) {
				OnlineUser onlineUser = new OnlineUser();
				onlineUser = (OnlineUser) onlineUsers.get(sessionId);

				if (Pattern.compile("," + this.roleId + ",").matcher(
						onlineUser.getRoleIds()).find()) {
					onlineUsersByRole.put(sessionId, onlineUser);
				}
			}
		}

		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(onlineUsers.size()).append(",result:");

		Gson gson = new Gson();
		Type type = new TypeToken<Collection<OnlineUser>>() {
		}.getType();
		if (this.depId != null)
			buff.append(gson.toJson(onlineUsersByDep.values(), type));
		else if (this.roleId != null)
			buff.append(gson.toJson(onlineUsersByRole.values(), type));
		else {
			buff.append(gson.toJson(onlineUsers.values(), type));
		}

		buff.append("}");
		this.jsonString = buff.toString();
		return "success";
	}

	public String find() {
		String strRoleId = getRequest().getParameter("roleId");
		PagingBean pb = getInitPagingBean();
		if (StringUtils.isNotEmpty(strRoleId)) {
			List<AppUser> userList = this.appUserService.findByRole(Long
					.valueOf(Long.parseLong(strRoleId)), pb);
			Type type = new TypeToken<List<AppUser>>() {
			}.getType();

			StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
					.append(pb.getTotalItems()).append(",result:");
			Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();
			buff.append(gson.toJson(userList, type));
			buff.append("}");

			this.jsonString = buff.toString();
		} else {
			this.jsonString = "{success:false}";
		}
		return "success";
	}

	public String multiDel() {
		String[] ids = getRequest().getParameterValues("ids");
		StringBuffer buff = new StringBuffer("{success:true");
		if (ids != null) {
			buff.append(",msg:'");
			for (String id : ids) {
				AppUser delUser = (AppUser) this.appUserService
						.get(new Long(id));
				AppRole superManager = (AppRole) this.appRoleService
						.get(SUPPER_MANAGER_ID);
				if (delUser.getRoles().contains(superManager)) {
					buff.append("员工:").append(delUser.getUsername()).append(
							"是超级管理员,不能删除!<br><br/>");
				} else if (delUser.getUserId().equals(
						ContextUtil.getCurrentUserId())) {
					buff.append("不能删除自己!<br></br>");
				} else {
					delUser.setStatus(Constants.FLAG_DISABLE);
					delUser.setDelFlag(Constants.FLAG_DELETED);
					delUser.setUsername("__" + delUser.getUsername());
					this.appUserService.saveAppUser(delUser);
					QueryFilter queryfilter=new QueryFilter(getRequest());
					queryfilter.addFilter("Q_appUser.userId_L_EQ", id+"");
					List<EmpProfile> l=empProfileService.getAll(queryfilter);
					
					if(!l.isEmpty()){
						EmpProfile empProfile=empProfileService.getAll(queryfilter).get(0);
						empProfile.setDelFlag(Constants.FLAG_DELETED);
						empProfileService.save(empProfile);
					}
					AppUserMapping aum=appUserMappingService.getByUserId(new Long(id));
					if(aum!=null){
						appUserMappingService.remove(aum);
					}
				}
			}
			buff.append("'");
		}
		buff.append("}");
		setJsonString(buff.toString());
		return "success";
	}

	public String get() {
		AppUser appUser = null;
		JSONSerializer json = JsonUtil
				.getJSONSerializer(new String[] { "accessionTime" });
		if (this.userId != null) {
			appUser = (AppUser) this.appUserService.get(this.userId);
		} else {
			json.exclude(new String[] { "accessionTime", "department",
					"password", "status", "position" });
			appUser = ContextUtil.getCurrentUser();
		}

		StringBuffer sb = new StringBuffer("{\"success\":\"true\",\"totalCounts\":\"1\",\"data\":[");
		sb.append(JsonUtil.getJSONSerializer(
				new String[] { "accessionTime", "departureTime" }).serialize(
				appUser));
		
		AppUserType appUserType=appUserTypeService.get(appUser.getUserId());
		if(appUserType!=null){
			sb.append("],\"type\":");
			sb.append(JsonUtil.getJSONSerializer().serialize(
					appUserType));
			sb.append("}");
		}else{
			sb.append("]}");
		}
		
		setJsonString(sb.toString());
System.out.println(sb.toString());
		return "success";
	}

	public String getCurrentByMobile() {
//		AppUser appUser = ContextUtil.getCurrentUser();
		AppUser appUser = (AppUser) this.appUserService.get(ContextUtil.getCurrentUserId());

//		StringBuffer sb = new StringBuffer();
//		sb.append(JsonUtil.getJSONSerializer(
//				new String[] { "accessionTime", "departureTime" }).
//								exclude("accountNonExpired","accountNonLocked","credentialsNonExpired",
//										"enabled","id","givenName","familyName","businessEmail").serialize(
//				appUser));
//		setJsonString(sb.toString());
		StringBuffer buff = new StringBuffer();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd").create();
		 
	    buff.append(gson.toJson(appUser));
	    setJsonString(buff.toString());

		return "success";
	}
	public String getByUsername() {
		AppUser appUser = null;
		JSONSerializer json = JsonUtil
				.getJSONSerializer(new String[] { "accessionTime" });

		String username = getRequest().getParameter("username");

		List<AppUser> list = this.appUserService.getAll(new QueryFilter(
				getRequest()));

		if (!list.isEmpty()) {
			appUser = list.get(0);
		} else {
			setJsonString("{failure:true}");
			return "success";
		}

		StringBuffer sb = new StringBuffer("{success:true,data:");
		sb.append(JsonUtil.getJSONSerializer(
				new String[] { "accessionTime", "departureTime" }).exclude(
				new String[] { "password" }).serialize(appUser));
		sb.append("}");
		setJsonString(sb.toString());

		return "success";
	}

	@Action(description = "添加或保存用户信息")
	public String save() {
		Boolean save=Boolean.parseBoolean(getRequest().getParameter("save"));
		String rolesIds = getRequest().getParameter("AppUserRoles");
		String[] ids = rolesIds.split(",");
		Set roles = new HashSet();
		for (String id : ids) {
			if (!"".equals(id)) {
				AppRole role = (AppRole) this.appRoleService.get(new Long(id));
				roles.add(role);
			}
		}
		this.appUser.setRoles(roles);
		
		if (this.appUser.getUserId() != null) {
			AppUser old = (AppUser) this.appUserService.get(this.appUser
					.getUserId());
			this.appUser.setDelFlag(old.getDelFlag());
			this.appUser.setPassword(old.getPassword());
			this.appUserService.merge(this.appUser);
			appUserUpdateService.saveAppUserUpdate(this.appUser);
			UserFlowconfig userFlowconfig = userFlowconfigService
					.getByUserId(this.appUser.getUserId());
			if (this.appUser.getStatus().intValue() == 3) {// 集团用户
				if (userFlowconfig == null) {
					userFlowconfig = new UserFlowconfig();
					userFlowconfig.setAppUser(appUser);
					userFlowconfig.setIsAgent(false);
					userFlowconfig.setIsEmail(false);
					userFlowconfig.setIsMsg(false);
					userFlowconfig.setIsToERP(true);
				} else {
					userFlowconfig.setIsToERP(true);
				}
				userFlowconfigService.save(userFlowconfig);
			} else {
				if (userFlowconfig != null) {
					userFlowconfig.setIsToERP(false);
					userFlowconfigService.save(userFlowconfig);
				}
			}
			setJsonString("{success:true}");
		} else if (this.appUserService.findByUserName(this.appUser
				.getUsername()) == null) {
			this.appUser.setDelFlag(Constants.FLAG_UNDELETED);
			this.appUser.setPassword(StringUtil.encryptSha256(this.appUser
					.getPassword()));
			this.appUser = this.appUserService.save(this.appUser);
			appUserUpdateService.saveAppUserUpdate(this.appUser);
			if (this.appUser.getStatus().intValue() == 3) {// 集团用户
				UserFlowconfig userFlowconfig = new UserFlowconfig();
				userFlowconfig.setAppUser(this.appUser);
				userFlowconfig.setIsAgent(false);
				userFlowconfig.setIsEmail(false);
				userFlowconfig.setIsMsg(false);
				userFlowconfig.setIsToERP(true);
				userFlowconfigService.save(userFlowconfig);
			}
			setJsonString("{success:true}");
		} else {
			setJsonString("{success:false,msg:'用户登录账号:"
					+ this.appUser.getUsername() + "已存在,请重新输入账号.'}");
		}
		AppUserType appUserType=appUserTypeService.get(appUser.getUserId());
		if(appUserType==null){
			appUserType=new AppUserType();
			appUserType.setAppUser(appUser);
		}
		String userType=getRequest().getParameter("userType");
		if(StringUtils.isNotEmpty(userType)){
			appUserType.setType(Long.parseLong(userType));
			appUserTypeService.save(appUserType);
		}
		//调整部门
		if(getJsonString().contains("{success:true}")&&save){
			
			Long newdeptId=Long.parseLong(getRequest().getParameter("newdeptId"));
			Long olddeptId=Long.parseLong(getRequest().getParameter("olddeptId"));
			AppUserMove appUserMove=new AppUserMove();
			appUserMove.setMovetime(new Date());
			appUserMove.setNewdept(departmentService.get(newdeptId));
			appUserMove.setOlddept(departmentService.get(olddeptId));
			appUserMove.setOper(ContextUtil.getCurrentUser());
			appUserMove.setUser(appUser);
			appUserMove.setType(new Long(1));
			appUserMoveService.save(appUserMove);
		}
		if(appUser.getDelFlag()==Constants.FLAG_DELETED){
			AppUserMapping aum=appUserMappingService.getByUserId(appUser.getUserId());
			if(aum!=null){
				appUserMappingService.remove(aum);
			}
		}else if(appUser.getDelFlag()==Constants.FLAG_UNDELETED){
			AppUserMapping aum=new AppUserMapping();
			aum.setDep(appUser.getDepartment());
			aum.setOper(ContextUtil.getCurrentUser());
			aum.setOpertime(new Date());
			aum.setUser(appUser);
			AppUserMapping appUserMapping=appUserMappingService.getByUserId(appUser.getUserId());
			if(appUserMapping==null){
				appUserMappingService.save(aum);
			}
		}
		return "success";
	}

	public String selectedRoles() {
		if (this.userId != null) {
			setAppUser((AppUser) this.appUserService.get(this.userId));
			Set<AppRole> roles = this.appUser.getRoles();
			StringBuffer sb = new StringBuffer("[");
			for (AppRole role : roles) {
				sb.append("['" + role.getRoleId() + "','" + role.getRoleName()
						+ "'],");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			setJsonString(sb.toString());
		}
		return "success";
	}

	public String chooseRoles() {
		List<AppRole> chooseRoles = this.appRoleService.getAll();

		if (this.userId != null) {
			setAppUser((AppUser) this.appUserService.get(this.userId));
			Set<AppRole> selectedRoles = this.appUser.getRoles();
			for (AppRole role : selectedRoles) {
				chooseRoles.remove(role);
			}
		}
		StringBuffer sb = new StringBuffer("[");
		for (AppRole role : chooseRoles) {
			if (role.getStatus().shortValue() != 0) {
				sb.append("['" + role.getRoleId() + "','" + role.getRoleName()
						+ "'],");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		setJsonString(sb.toString());
		return "success";
	}

	@Action(description = "修改密码")
	public String resetPassword() {
		String userId = getRequest().getParameter("appUserUserId");
		String oldPassword = StringUtil.encryptSha256(getRequest()
				.getParameter("oldPassword"));
		String newPassword = getRequest().getParameter("newPassword");
		String againPassword = getRequest().getParameter("againPassword");
		if (StringUtils.isNotEmpty(userId))
			setAppUser((AppUser) this.appUserService.get(new Long(userId)));
		else {
			setAppUser(ContextUtil.getCurrentUser());
		}
		StringBuffer msg = new StringBuffer("{msg:'");
		boolean pass = false;

		if (!newPassword.matches("^(?![0-9]+$)(?![a-zA-Z]+$).+$")) {
			msg.append("密码中必须含有字母和数字.'");
			pass = false;
		}else{
			if (oldPassword.equals(this.appUser.getPassword())) {
				if (newPassword.equals(againPassword))
					pass = true;
				else
					msg.append("两次输入不一致.'");
			} else
				msg.append("旧密码输入不正确.'");
		}

		if (pass) {
			this.appUser.setPassword(StringUtil.encryptSha256(newPassword));
			this.appUserService.save(this.appUser);
			setJsonString("{success:true}");
			getSession().setAttribute("modifyPWD", false);
		} else {
			msg.append(",failure:true}");
			setJsonString(msg.toString());
		}
		return "success";
	}

	public String photo() {
		setAppUser((AppUser) this.appUserService.get(getUserId()));
		this.appUser.setPhoto("");
		this.appUserService.save(this.appUser);
		return "success";
	}

	public String subAdepartment() {
		PagingBean pb = getInitPagingBean();
		String strDepId = getRequest().getParameter("depId");
		String path = "0.";
		AppUser user = ContextUtil.getCurrentUser();
		if (StringUtils.isNotEmpty(strDepId)) {
			Long depId = Long.valueOf(Long.parseLong(strDepId));
			Department dep = (Department) this.departmentService.get(depId);
			if (dep != null)
				path = dep.getPath();
		} else {
			Department dep = user.getDepartment();
			if (dep != null) {
				path = dep.getPath();
			}
		}
		if ("0.".equals(path)) {
			path = null;
		}
		Long uId = user.getUserId();
		Set userIds = this.userSubService.findAllUpUser(uId);
		List<Long> userIdsL = this.userSubService.subUsers(uId);
		userIds.add(uId);
		for (Long l : userIdsL) {
			userIds.add(l);
		}
		List<AppUser> list = this.appUserService.findSubAppUser(path, userIds,
				pb);
		Type type = new TypeToken<List<AppUser>>() {

		}.getType();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(pb.getTotalItems()).append(",result:");
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		buff.append(gson.toJson(list, type));
		buff.append("}");
		this.jsonString = buff.toString();
		return "success";
	}

	public String subArole() {
		String strRoleId = getRequest().getParameter("roleId");
		PagingBean pb = getInitPagingBean();
		AppUser user = ContextUtil.getCurrentUser();
		if (StringUtils.isNotEmpty(strRoleId)) {
			Long uId = user.getUserId();
			Set userIds = this.userSubService.findAllUpUser(uId);
			List<Long> userIdsL = this.userSubService.subUsers(uId);
			userIds.add(uId);
			for (Long l : userIdsL) {
				userIds.add(l);
			}
			List<AppUser> userList = this.appUserService.findSubAppUserByRole(
					new Long(strRoleId), userIds, pb);

			Type type = new TypeToken<List<AppUser>>() {

			}.getType();
			StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
					.append(pb.getTotalItems()).append(",result:");
			Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();
			buff.append(gson.toJson(userList, type));
			buff.append("}");
			this.jsonString = buff.toString();
		} else {
			this.jsonString = "{success:false}";
		}
		return "success";
	}

	public String onlineAsub() {
		Map onlineUsers = new HashMap();
		Map onlineUsersBySub = new HashMap();
		onlineUsers = AppUtil.getOnlineUsers();

		AppUser user = ContextUtil.getCurrentUser();
		Long uId = user.getUserId();
		Set userIds = this.userSubService.findAllUpUser(uId);
		userIds.add(uId);
		List<Long> userIdsL = this.userSubService.subUsers(uId);
		for (Long l : userIdsL) {
			userIds.add(l);
		}
		Set<String> online = onlineUsers.keySet();
		for (String sessionId : online) {
			OnlineUser onlineUser = new OnlineUser();
			onlineUser = (OnlineUser) onlineUsers.get(sessionId);
			if (!userIds.contains(onlineUser.getUserId())) {
				onlineUsersBySub.put(sessionId, onlineUser);
			}
		}
		Type type = new TypeToken<Collection<OnlineUser>>() {
		}.getType();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(onlineUsers.size()).append(",result:");
		Gson gson = new Gson();
		buff.append(gson.toJson(onlineUsersBySub.values(), type));
		buff.append("}");
		this.jsonString = buff.toString();
		return "success";
	}

	public String upUser() {
		List<Long> ids = this.userSubService.upUser(ContextUtil
				.getCurrentUserId());
		List<AppUser> list = new ArrayList();
		for (Long l : ids) {
			list.add((AppUser) this.appUserService.get(l));
		}
		StringBuffer buff = new StringBuffer("[");
		for (AppUser user : list) {
			buff.append("['" + user.getUserId().toString() + "','"
					+ user.getFullname() + "'],");
		}
		if (list.size() > 0) {
			buff.deleteCharAt(buff.length() - 1);
		}
		buff.append("]");
		setJsonString(buff.toString());
		return "success";
	}

	@Action(description = "修改个人资料")
	public String profile() {
		AppUser old = ContextUtil.getCurrentUser();
		try {
			BeanUtil.copyNotNullProperties(old, this.appUser);
		} catch (Exception e) {
			this.logger.info(e);
		}
		this.appUserService.saveAppUser(old);
		this.jsonString = "{success:true}";
		return "success";
	}
	
	public String updateRole() {
		AppUser old = appUserService.get(appUser.getUserId());
		String rolesIds = getRequest().getParameter("AppUserRoles");
		String[] ids = rolesIds.split(",");
		Set roles = new HashSet();
		for (String id : ids) {
			if (!"".equals(id)) {
				AppRole role = (AppRole) this.appRoleService.get(new Long(id));
				roles.add(role);
			}
		}
		this.appUser.setRoles(roles);
		try {
			BeanUtil.copyNotNullProperties(old, this.appUser);
		} catch (Exception e) {
			this.logger.info(e);
		}
		this.appUserService.save(old);
		this.jsonString = "{success:true}";
		return "success";
	}
	
	public String geIdCard(){
		String userId=getRequest().getParameter("userId");
		QueryFilter filter = new QueryFilter(getRequest());
		filter.addFilter("Q_appUser.delFlag_SN_EQ", Constants.FLAG_UNDELETED
				.toString());
		
		filter.addFilter("Q_appUser.userId_L_EQ", userId);
		
		
		List<EmpProfile> list=empProfileService.getAll(filter);
		
		if(null==list||list.isEmpty()){
			this.jsonString = null;
		}else {
			EmpProfile empProfile=list.get(0);
			
			this.jsonString = empProfile.getIdCard();
		}
		
		return SUCCESS;
	}

	/**
	 * 花名册
	 * @return
	 */
	public String listRoster() {
		QueryFilter filter = new QueryFilter(getRequest());
		filter.addFilter("Q_u.delFlag_SN_EQ", Constants.FLAG_UNDELETED
				.toString());
		
		List<RosterDTO> list = this.appUserService.getAllRoster(filter);

		Type type = new TypeToken<List<RosterDTO>>() {
		}.getType();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':").append(filter.getPagingBean().getTotalItems()).append(",result:");

		Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_YMD).create();
		buff.append(gson.toJson(list, type));
		buff.append("}");

		jsonString = buff.toString();

		return SUCCESS;
	}
	public String exportRoster() throws Exception {
		String format = getRequest().getParameter("format");
		String jasperName= getRequest().getParameter("jasperName");
		String fileName="在职花名册";
		
		QueryFilter filter=new QueryFilter(getRequest());
		
		filter.addFilter("Q_u.staffNo_S_LK", URLDecoder.decode(getRequest().getParameter("staffNo"),"utf-8"));
		filter.addFilter("Q_u.fullname_S_LK", URLDecoder.decode(getRequest().getParameter("fullname"),"utf-8"));
		filter.addFilter("Q_u.department.depName_S_LK", URLDecoder.decode(getRequest().getParameter("depName"),"utf-8"));
		filter.addFilter("Q_u.position_S_LK", URLDecoder.decode(getRequest().getParameter("position"),"utf-8"));

		List<RosterDTO> list = this.appUserService.getAllRoster(filter);
		Map<Object,Object> parmsMap = new HashMap<Object,Object>();
		parmsMap.put(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);//白色背景透明
		parmsMap.put(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, true);//忽略单元格背景
		parmsMap.put(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);//移除空白
        ExportUtil.export(list, jasperName,fileName, format,parmsMap);
		return null;
	}
	
	
	
	/**
	 * 手机端获取部门内全部人员信息
	 * @return
	 */
	public String getAppDep(){
		String depId=getRequest().getParameter("depId");//部门ID
		
		QueryFilter filter=new QueryFilter(getRequest());
		filter.addFilter("Q_delFlag_SN_EQ", Constants.FLAG_UNDELETED+"");
		filter.addFilter("Q_status_SN_EQ", Constants.FLAG_ACTIVATION+"");
		filter.addFilter("Q_department.depId_L_EQ", depId);
		filter.addFilter("Q_userId_L_NEQ", ContextUtil.getCurrentUserId().toString());//不要包括自己
		filter.addFilter("Q_userId_L_NEQ", "1");
		filter.getPagingBean().setPageSize(Integer.MAX_VALUE);
		List<AppUser> list= appUserService.getAll(filter);
		
		
		List<AppUser> managerList = new ArrayList<AppUser>();
		List<AppUser> normalList = new ArrayList<AppUser>();
		for(AppUser appUser:list){
			if((appUser.getPosition().contains("经理")||appUser.getPosition().contains("董事长")||appUser.getPosition().contains("院长"))&&
					!appUser.getPosition().equals("销售经理")&&
					!appUser.getPosition().equals("产品经理")){
				managerList.add(appUser);
			}else{
				normalList.add(appUser);
			}
		}
		list.clear();
		Collections.sort(managerList,new Comparator<AppUser>() {
			@Override
			public int compare(AppUser user1, AppUser user2) {
				
				String user1Position = user1.getPosition().replace("副总经理", "").replace("规划设计中心分管副总","");
				String user2Position = user2.getPosition().replace("副总经理", "").replace("规划设计中心分管副总","");
				if(user1Position.contains("副")&&user2Position.contains("副")){
					return 0;
				}else if(user1Position.contains("副")){
					return 1;
				}else if(user2Position.contains("副")){
					return -1;
				}
				return 0;
			}
		});
		list.addAll(managerList);
		list.addAll(normalList);
		Collections.sort(list, new PositionComparator());
		
		jsonString=getJsonAppUserList(list);
		
		return SUCCESS;
	}
	/**
	 * 手机端获取人的详细信息
	 * @return
	 */
	public String getApp(){
		String userId=getRequest().getParameter("userId");//人员userId
		
		AppUser user= appUserService.findByUserId(Long.valueOf(userId));
		
		Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(Constants.DATE_FORMAT_YMD).create();
		
		jsonString=gson.toJson(user);
		
		return SUCCESS;
	}
	
	/**
	 * 获取appuserList 的json 手机端用
	 * @param list
	 * @return
	 */
	public static String getJsonAppUserList(List<AppUser> list){
		JSONSerializer serializer=new JSONSerializer();
		List<String> includes=new ArrayList<String>();
		
		includes.add("userId");
		includes.add("fullname");
		includes.add("photo");
		
		
		BeanInfo beanInfo=null;
		try {
			beanInfo = Introspector.getBeanInfo(AppUser.class);
		} catch (IntrospectionException e) {
			e.printStackTrace();
			return "[]";
		} // 从一个类的字节码中以 JavaBean 的方式读取类信息  
        PropertyDescriptor[] getPropertyDescriptors = beanInfo  
                .getPropertyDescriptors();
       
        String[] excludeString=new String[getPropertyDescriptors.length-includes.size()];
        int k=0;
        
        for(PropertyDescriptor propertyDescriptor:getPropertyDescriptors){
        	if(includes.contains(propertyDescriptor.getName())){
        		continue;
        	}
        	excludeString[k++]=propertyDescriptor.getName();
        }
		serializer.exclude(excludeString);
//		System.out.println(serializer.serialize(list));
		return serializer.serialize(list);
	}
	/**
	 * 同步用户资料到jim2
	 * @return
	 */
	public String synToJim2(){
		String[] uids=getRequest().getParameterValues("ids");
		String userName=getRequest().getParameter("userName");
		String retVal=null;
		if(StringUtils.isNotEmpty(userName)){
			appUser=appUserService.findByUserName(userName);
			retVal=Jim2Client.userUpdate(appUser);
			if(!Jim2Client.SUCCESS.equals(retVal)){
				retVal="用户【"+appUser.getFullname()+"】同步失败----原因："+retVal;
			}
		}else{
			for(String uid:uids){
				appUser=appUserService.get(Long.parseLong(uid));
				retVal=Jim2Client.userUpdate(appUser);
				if(!Jim2Client.SUCCESS.equals(retVal)){
					retVal="用户【"+appUser.getFullname()+"】同步失败----原因："+retVal;
					break;
				}
			}
		}
		setJsonString(retVal);
		return SUCCESS;
	}
	/**
	 * 注销Jim2帐户
	 * @return
	 */
	public String delToJim2(){
		String[] userNames=getRequest().getParameterValues("userNames");
		String retVal=Jim2Client.userDel(userNames);
		if(!Jim2Client.SUCCESS.equals(retVal)){
			retVal="帐户注销失败----原因："+retVal;
		}
	
		setJsonString(retVal);
		return SUCCESS;
	}

	
	@Override
	public long delPrepare(Long... delIds) {
		System.out.println(delIds);
		return 1;
	}

	@Override
	public long del(long seqId) {
		return 1;
	}

	@Override
	public RetObj<AppUser> query(HttpServletRequest request) {
		
		QueryFilter filter=new QueryFilter(request);
		
		List<AppUser> appUsers=appUserService.getAll(filter);
		
		
		return new RetObj<AppUser>(appUsers);
	}

	@Override
	public long saveOrUpdatePrepare(AppUser obj) {
		
		System.out.println(obj.getUsername());
		System.out.println(obj.getFullname());
		return 1;
	}

	@Override
	public long saveOrUpdate(long seqId) {
		// TODO Auto-generated method stub
		return 1;
	}
	
	public String callTest(){
		final   AppUser au = new AppUser();
       
       au.setTitle(Short.valueOf((short) 1));
       au.setUsername("user" );
       au.setPassword("1");
       au.setFullname("李海" );
       au.setAddress("testAddress");
       au.setEducation("test");
       au.setEmail("user"  + "@xpsoft.com");
       au.setAccessionTime(new Date());
       au.setPhoto("photo");
       au.setZip("00003");
       au.setStatus(Short.valueOf((short) 1));
       au.setFax("020-003034034");
       au.setPosition("UserManager");
       au.setDelFlag(Constants.FLAG_UNDELETED);
       
      final  String host="ljt";
      final   String packagez="system";
      final   String action="appUser";
		TransactionCommand command0=new TransactionCommand() {
			@Override
			public HttpServiceCommit execute() {
				appUserService.save(au);
				return null;
			}
		};
		TransactionCommand command1=new TransactionCommand() {
			@Override
			public HttpServiceCommit execute() {
				return appUserService.prepareSaveOrUpdate(host, packagez, action, au);
			}
		};       
		TransactionCommand command2=new TransactionCommand() {
			@Override
			public HttpServiceCommit execute() {
				return appUserService.prepareRemove(host, packagez, action, 1l,2L);
			}
		}; 
	   HttpServiceCommit[] commits= appUserService.prepareTransactionWithThread(command0,command1,command2);
//	   HttpServiceCommit[] commits= appUserService.saveWithLocal(host, packagez, action, au);

//		boolean retBoo=appUserService.callCommon(command0,command1);
//		HttpServiceCommit[] commits=appUserService.saveWithLocal(host, packagez, action, au);
//	   retBoo=appUserService.saveReCall(commits,new SimpleCallBack());
	   OpeaterInTransaction<AppUser> opeaterInTransaction=new SaveOpeaterInTransaction<AppUser>() {

			@Override
			public void save(AppUser obj, long retId) {
				System.out.println("xxxxxxx----->retId:"+retId+"---appUserName"+obj.getUsername());
				obj.setPassword("1");
				appUserService.merge(obj);
				
			}
		
		};
		
	   boolean retBoo= appUserService.saveReCall(commits, opeaterInTransaction,new SimpleCallBack());
	   boolean retBoo3=appUserService.callCommonWithThread(opeaterInTransaction,command0,command1);
	   
	   System.out.println(retBoo);
	   setJsonString(retBoo3+"");
	   
	   RetObj<AppUser> retAu=appUserService.query(host, packagez, action, new BasicNameValuePair("Q_username_S_EQ","admin"));
	   
	   System.out.println("userId======"+retAu.getObjs().get(0).getUserId());
	   
	   return SUCCESS;
			   
	}
}
