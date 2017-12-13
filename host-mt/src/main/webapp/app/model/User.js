Ext.define('MyApp.model.User', {
    extend: 'Ext.data.Model',
    fields: [ 
        {name: 'userId',   type: 'int'},
        {name: 'loginCode',  type: 'string'},
        {name: 'userName',  type: 'string'},
        {name: 'password',  type: 'string'},
        {name: 'cardId',  type: 'string'},
        {name: 'email',  type: 'string'},
        {name: 'telephone',  type: 'string'},
        {name: 'address',  type: 'string'},
        {name: 'creator',  type: 'string'},
        {name: 'createTime',  type: 'string'},
        {name: 'remarks',  type: 'string'}
    ]
});