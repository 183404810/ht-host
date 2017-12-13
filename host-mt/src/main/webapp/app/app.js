/*
 * This file is generated and updated by Sencha Cmd. You can edit this file as
 * needed for your application, but these edits will have to be merged by
 * Sencha Cmd when upgrading.
 */
Ext.application({
    name: 'MyApp',
    extend: 'MyApp.Application',
    autoCreateViewport: 'MyApp.view.main.Main',
 
    //-------------------------------------------------------------------------
    // Most customizations should be made to MyApp.Application. If you need to
    // customize this file, doing so below this section reduces the likelihood
    // of merge conflicts when upgrading to new versions of Sencha Cmd.
    //-------------------------------------------------------------------------
    	
	/*init: function () {
        var me = this;

        me.callParent();

        if (Belle.moduleWidget()) return;

        var module = location.href.indexOf('#') > 0 ? location.href.split('#')[1] : 'bltemplate',
            className = Ext.ClassManager.getNameByAlias('widget.' + module);

        if(className) {
            me.autoCreateViewport = className;
        }else{
            Ext.Msg.alert('错误提示','URL出错,请检查是否已导入');
        }
    },*/
    
});