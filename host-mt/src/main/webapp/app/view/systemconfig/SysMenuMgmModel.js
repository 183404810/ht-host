Ext.define('MyApp.view.systemconfig.SysMenuMgmModel',{
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.SysMenuMgmModel',
    requires: ['MyApp.model.systemconfig.MenuTreeModel'],  //动态加载MenuTreeModel类

    stores: {  //设置该视图模型的stores集合，可用于在视图组件中作为绑定数据源
        //mystore:{
        // model: 'Xxx.Model',
        // autoLoad: true,
        // proxy: {},
        // reader: {}
        //}
    }

});