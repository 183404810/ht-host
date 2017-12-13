Ext.define('MyApp.model.Resource', {
    extend: 'Ext.data.Model',
    fields: [ 
        {name: 'nodeId',   type: 'int'},
        {name: 'parentNodeId',  type: 'string'},
        {name: 'resource',  type: 'string'},
        {name: 'path',  type: 'string'}
    ]
});