/**
 * @author 
 * @createtime 
 * @class HttpServiceCommitForm
 * @extends Ext.Window
 * @description HttpServiceCommit表单
 */
HttpServiceCommitForm=Ext.extend(Ext.Window,{
	//内嵌FormPanel
	formPanel:null,
	//构造函数
	constructor:function(_cfg){
		Ext.applyIf(this,_cfg);
		//必须先初始化组件
		this.initUIComponents();
		HttpServiceCommitForm.superclass.constructor.call(this,{
			id:'HttpServiceCommitFormWin',
			layout:'fit',
			items:this.formPanel,
			modal:true,
			height:200,
			width:400,
			maximizable:true,
			title:'[HttpServiceCommit]详细信息',
			buttonAlign : 'center',
			buttons:this.buttons
		});
	},//end of the constructor
	//初始化组件
	initUIComponents:function(){
		this.formPanel=new Ext.FormPanel({
				layout : 'form',
				trackResetOnLoad:true,
				bodyStyle: 'padding:10px 10px 10px 10px',
				border:false,
				url : __ctxPath + '/test/saveHttpServiceCommit.do',
				id : 'HttpServiceCommitForm',
				defaults : {
					anchor : '98%,98%'
				},
				defaultType : 'textfield',
				items : [{
							name : 'httpServiceCommit.id',
							id : 'id',
							xtype:'hidden',
							value : this.id == null ? '' : this.id
						}
																																										,{
												fieldLabel : '应用',	
												name : 'httpServiceCommit.host',
						id : 'host'
							}
																																				,{
												fieldLabel : '模块',	
												name : 'httpServiceCommit.packagez',
						id : 'packagez'
							}
																																				,{
												fieldLabel : '功能',	
												name : 'httpServiceCommit.action',
						id : 'action'
							}
																																				,{
												fieldLabel : '方法',	
												name : 'httpServiceCommit.method',
						id : 'method'
							}
																																				,{
												fieldLabel : '请求uri',	
												name : 'httpServiceCommit.uri',
						id : 'uri'
							}
																																				,{
												fieldLabel : '请求ID',	
												name : 'httpServiceCommit.seqId',
						id : 'seqId'
							}
																																				,{
												fieldLabel : '创建时间',	
												name : 'httpServiceCommit.timeCreate',
						id : 'timeCreate'
							}
																																				,{
												fieldLabel : '重新提交次数',	
												name : 'httpServiceCommit.countReSend',
						id : 'countReSend'
							}
																								
												]
			});
		//加载表单对应的数据	
		this.initData();
		//初始化功能按钮
		this.buttons=[{
				text : '保存',
				iconCls : 'btn-save',
				hidden:!(isGranted("_HttpServiceCommitAdd")||isGranted("_HttpServiceCommitEdit")) ,
				handler :this.save.createCallback(this.formPanel,this)
			}, {
				text : '重置',
				iconCls : 'btn-reset',
				hidden:!(isGranted("_HttpServiceCommitAdd")||isGranted("_HttpServiceCommitEdit")) ,
				handler :this.reset.createCallback(this.formPanel)
			},{
				text : '取消',
				iconCls : 'btn-cancel',
				handler : this.cancel.createCallback(this)
			}];
	},//end of the initcomponents
	/**
	 * 初始化数据
	 * @param {} formPanel
	 */
	initData:function(){
		//加载表单对应的数据	
		if (this.id != null && this.id != 'undefined') {
			this.formPanel.getForm().load({
				deferredRender : false,
				url : __ctxPath + '/test/getHttpServiceCommit.do?id='+ this.id,
				waitMsg : '正在载入数据...',
				success : function(form, action) {
						var jsonData = Ext.util.JSON.decode(action.response.responseText);
						var res = jsonData.data;
						//Ext.getCmp("").originalValue=
				},
				failure : function(form, action) {
				}
			});
		}
	},
	/**
	 * 重置
	 * @param {} formPanel
	 */
	reset:function(formPanel){
		formPanel.getForm().reset();
	},
	/**
	 * 取消
	 * @param {} window
	 */
	cancel:function(window){
		window.close();
	},
	/**
	 * 保存记录
	 */
	save:function(formPanel,window){
		if (formPanel.getForm().isValid()) {
			formPanel.getForm().submit({
				method : 'POST',
				waitMsg : '正在提交数据...',
				success : function(fp, action) {
					Ext.ux.Toast.msg('操作信息', '成功保存信息！');
					var gridPanel=Ext.getCmp('HttpServiceCommitGrid');
					if(gridPanel!=null){
						gridPanel.getStore().reload();
					}
					window.close();
				},
				failure : function(fp, action) {
					Ext.MessageBox.show({
								title : '操作信息',
								msg : '信息保存出错，请联系管理员！',
								buttons : Ext.MessageBox.OK,
								icon : Ext.MessageBox.ERROR
							});
					//window.close();
				}
			});
		}
	}//end of save
	
});