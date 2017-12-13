Ext.define('MyApp.view.main.Main', {
    extend : 'Ext.container.Viewport',  //继承自Ext.container.Viewport
    requires: ['MyApp.view.main.MainController',
               'MyApp.view.main.MainModel',
               'MyApp.model.Resource',
    ],  //动态加载所需类

    controller: 'main',  //设置该视图的Controller
    viewModel: 'main',  //设置该视图的ViewModel
    layout: 'border',  //设置视图的布局方式为border
    items: [{  //配置该视图的子组件
        xtype: 'container',  //该子组件类型为Ext.container.Container
        id : 'app-header',
        region : 'north',  //border布局中的north region
        height : 52,
        layout : {
            type : 'hbox',  //该组件的布局方式为hbox（水平布局）
            align : 'middle'  //对其方式为居中
        },

        items : [ {
            xtype : 'component',  //该组件类型为Ext.Component
            id : 'app-header-logo'  //在已经加载的组件中（内存中）寻找id为app-header-logo的组件
        }, {
            xtype : 'component',
            cls : 'app-header-text',  //设置该组件样式，在已加载的css文件中寻找该样式
            bind : '管理系统',  //绑定的字符串
            flex : 1  //设置大小的权重
        }, {
            xtype : 'component',
            id : 'app-header-username',
            cls : 'app-header-text',
            bind : '欢迎' ,//+ current_user_name,
            margin : '0 10 0 0'  //组件边缘空白设置
        }, {
            xtype : 'button',
            id : 'app-header-logout',
            text : '退出',
            margin : '0 10 0 0',
            href : 'logout',  //设置超链接的URL地址
            hrefTarget : '_self'  //设置超链接属性
        } ]
    },{
        xtype: 'panel',
        region : 'west',
        id: 'navigator',
        title : '导航栏',
        width : 250,
        layout : 'accordion',
        split : true,  //是子组件可以重新设置布局大小，该属性不能在center region中使用
        collapsible : true,  //该区域是可以收缩的
        listeners : {  //设置事件的监听
            afterrender : 'requestTreeData'  //添加afterrender事件的监听处理函数'requestTreeData',该函数在Controller中定义
        }
    },{
        region : 'center',  //border布局中的center region，不可缺失
        xtype: 'container',
        id: 'app-content',
        layout: 'fit',  //fit布局只会以合适的大小显示第一个子组件
        items : [{
            xtype: 'tabpanel',  //Ext.tab.Panel组件
            flex : 1,
            reference : 'main',  //设置该组件的引用名称，该名称'main'必须在该组件所在的视图View和该组件所配置的Controller中命名唯一
            items: [{
                //xtype: 'app-dashboard',  //该xtype是自定义的，在MyApp.view.dashboard.Dashboard类中定义
                title: '首页'
            }]
        }]
    }]
});