Ext.define('MyApp.Application', {
    extend: 'Ext.app.Application',
 
    name: 'MyApp',
   // appFolder:'app',
    requires: [
        'MyApp.view.user.User'	 
   	],
   	
   	init: function() {
        var me = this;
        Ext.setGlyphFontFamily('FontAwesome');
        Ext.QuickTips.init();
    },
    
    launch: function () {
        // TODO - Launch the application
    }
});