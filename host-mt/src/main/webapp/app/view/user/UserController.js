Ext.define('MyApp.view.user.UserController', {
    extend : 'MyApp.view.base.BaseController',
    alias : 'controller.user',
    init:function(){            
    	var me = this;
    	objList = me.getObjList();
    	me.callParent(arguments);
    	/*var items = me.lookupReference('commontoolbar').items;
    	for(var i=0;i<items.length;i++){
    		var item = items.getAt(i);
    		item.setVisible(item.itemId=='btnSearch'||item.itemId=='btnReset'
    			||item.itemId=='btnFilter'||item.itemId=='btnAdd');
    	}*/
    }
});