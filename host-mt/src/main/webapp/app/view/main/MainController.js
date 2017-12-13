Ext.define('MyApp.view.main.MainController', {
    extend : 'Ext.app.ViewController',
    alias : 'controller.main',
    NodeClick: function (rec,view, id) {
        var panel = Ext.getCmp(id);
        if (!panel) {
            panel = {
                id: id,
                closable: true,
                title: rec.data.text,
                //iconCls: record.data.iconCls,
                autoScroll: true,
                border: false,
                layout: 'fit',
                autoLoad: {//采用autoload方式
                    url: 'MyApp.view.user.User',
                    scope: this,
                    scripts: true,
                    text: '页面加载中，请稍候....'
                }
            };
            this.openTab(panel, id);
        } else {
            var main = Ext.getCmp("ViewPortCoreTab");
            main.setActiveTab(panel);
        }
    },
    openTab: function (panel, id) {
        var o = (typeof panel == "string" ? panel : id || panel.id);
        var main = Ext.getCmp("ViewPortCoreTab");
        var tab = main.getComponent(o);
        if (tab) {
            main.setActiveTab(tab);
        } else if (typeof panel != "string") {
            panel.id = o;
            //var p = main.add(panel);
            var p = main.add({
            	id:id,
            	title:''
            	
            });
            main.setActiveTab(p);
        }
    },
    createTab : function(rec) {
        var tabs = this.lookupReference('main'), id = 'tab_'
                + rec.getId(), tab = tabs.items.getByKey(id), 
                viewtab, viewpath,pathArray,viewname;
        //this.NodeClick(rec,'MyApp.view.user.User',id);
        if (!tab) {
            viewpath = rec.get('view');  //获取record记录中的view字段内容，该字段存储视图相对路径名称（相对于view目录的）
            //Ext.require('user');  //动态加载请求该视图的类
            Ext.onReady(function(){  //增加一个监听器，当document都加载就绪（在onload之前，以及images加载之前）时执行函数体中的内容
                pathArray = viewpath.split('.');
                viewname = pathArray[pathArray.length-1];  //拆分视图相对路径，得到视图别名（约定别名与类名相同）
                tab = tabs.add({
                    id: id,
                    title: rec.get('text'),
                    layout: 'border',  //设置视图的布局方式为border
                    closable: true,
                    xtype:'user'  //该tab的类型为该视图（viewname包含的别名所代表的视图）
                });
                tabs.setActiveTab(tab);  //设置该tab为当前活动的tab
            });
        } 
    },
    onClickLeaf : function(view, record, item, index, e) {
        var leaf = record.get('leaf');
        if (leaf) {
            this.createTab(record);  //调用当前对象的createTab函数
            //...
        }
    },
    buildTree : function(json) {
    	 var store = Ext.create('Ext.data.TreeStore',{root:json.data});
		 var tree = new Ext.tree.TreePanel({  
	         region: 'center',  
	         //True表示为面板是可收缩的，并自动渲染一个展开/收缩的轮换按钮在头部工具条  
	         collapsible: true,  
	         title: '资源',//标题文本  
	         width: 200,  
	         border : false,//表框  
	         autoScroll: true,//自动滚动条  
	         animate : true,//动画效果  
	         rootVisible: true,//根节点是否可见  
	         split: true,  
	         store: store,  
	         listeners: {
	        	 'itemdblclick':'onClickLeaf',
	             //'selectionchange': 'onTreeSelectionChange',
	             //'afteritemexpand': 'onAfterItemExpand'
	         }
	     });
		 
	     return tree;
    },
    requestTreeData : function(navigatePanel) {  //加载Main视图时配置的监听处理函数，此函数逻辑是在左侧创建导航栏的内容
        var me = this;
        Ext.Ajax.request({
            url : 'http://localhost:89/host-mt/login/sysmenu.json',  //Ajax请求的URL地址
            success : function(response) {  //Ajax请求成功调用的函数
                var json = Ext.JSON.decode(response.responseText)
                navigatePanel.add(me.buildTree(json));
            },
            failure : function(request) {  //Ajax请求失败调用的函数
                Ext.MessageBox.show({
                    title : '操作提示',
                    msg : "获取菜单数据失败",
                    buttons : Ext.MessageBox.OK,
                    icon : Ext.MessageBox.ERROR
                });
            },
            method : 'post'
        });
    },
    //...
});