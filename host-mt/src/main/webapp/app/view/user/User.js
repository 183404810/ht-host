Ext.define('MyApp.view.user.User', {
    extend : 'MyApp.view.base.Base',  //继承自Ext.container.Viewport
    alias: 'widget.user',
    requires: [
       'MyApp.model.User',
       'MyApp.view.user.UserController',
       'MyApp.view.user.UserModel',
    ],  //动态加载所需类
   
	controller: 'user',
	viewModel: {
		type: 'user'
	},
	initComponent: function() {
        var me = this;
        Ext.apply(me, {
            searchItems: [
             {xtype : 'textfield',fieldLabel : '用户',name : 'loginUser', reference: 'loginUser',},
             {xtype : 'textfield',fieldLabel : '电话',name : 'telephone', reference: 'telephone',}
             ],
			
            gridModel: 'MyApp.model.User',
            gridColumns: [
              {
            	  xtype:'gridcolumn',
            	  dataIndex: 'userId',   text: '用户',
            	  editor: {
	                    allowBlank: false,
	                    maxLength: 19,
	                    enforceMaxLength: true
	                }
              },
	          {
            	  xtype:'gridcolumn',
            	  dataIndex: 'loginCode',  text: '登录账号',
            	  editor: {
	                    allowBlank: false,
	                    maxLength: 19,
	                    enforceMaxLength: true
	                }
	          },
	          {
	        	  xtype:'gridcolumn',
	        	  dataIndex: 'userName',  text: '用户姓名',
	        	  editor: {
	                    allowBlank: false,
	                    maxLength: 19,
	                    enforceMaxLength: true
	                }  
	          },
	          {
	        	  xtype:'gridcolumn',
	        	  dataIndex: 'password',  text: '密码',
	        	  editor: {
	                    allowBlank: false,
	                    maxLength: 19,
	                    enforceMaxLength: true
	                }  
	          },
	          {
	        	  xtype:'gridcolumn',
	        	  dataIndex: 'cardId',  text: '身份ID',
	        	  editor: {
	                    allowBlank: false,
	                    maxLength: 19,
	                    enforceMaxLength: true
	                }  
	          },
	          {dataIndex: 'email',  text: '邮件'},
	          {dataIndex: 'telephone',  text: '电话'},
	          {dataIndex: 'address',  text: '地址'},
	          {dataIndex: 'creator',  text: '创建人'},
	          {dataIndex: 'createTime',  text: '创建时间'},
	          {dataIndex: 'remarks',  text: '备注'}
            ],
            gridPrimaryKey: 'lineId',
            gridLoadUrl: mt.mcPath + 'scuser/list.json',
            gridSaveUrl: mt.mcPath + 'scuser/batchOperate.json',
            gridExportUrl: mt.mcPath + 'scuser/do_export.json',
         	gridHasMark:false,
		   	gridHasCreator:false,
		   	gridHasModifier:false,
		   	gridHasAuditor:false,
		   	gridHasBillNo:false,					
        });
		me.callParent();
	}
});