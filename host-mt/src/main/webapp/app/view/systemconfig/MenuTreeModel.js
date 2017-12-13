Ext.define('MyApp.model.systemconfig.MenuTreeModel', {
    extend: 'MyApp.model.BaseModel',
    fields: [{
        //功能名称
        name: 'funcName',
        type: 'string'
    }, {
        //功能编号
        name: 'funcNum',
        type: 'int'
    }, {
        //排序号
        name: 'sortNum',
        type: 'int'
    }, {
        //所属类别
        name: 'funcType',
        type: 'string'
    }, {
        //视图名称
        name: 'viewPath',
        type: 'string'
    }, {
        //功能描述
        name: 'description',
        type: 'string'
    }, {
        //模块链接库
        name: 'linkLib',
        type: 'string'
    }] });