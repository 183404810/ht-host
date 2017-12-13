Ext.define('MyApp.view.systemconfig.SysMenuMgmView',{
    extend: 'Ext.Container',
    alias: 'widget.SysMenuMgmView',
    requires: ['MyApp.view.systemconfig.SysMenuMgmController',
               'MyApp.view.systemconfig.SysMenuMgmModel',
               'MyApp.view.systemconfig.SysMenuTreeGrid'],  //动态加载的类

    controller: 'SysMenuMgmController',  //设置视图控制器
    viewModel: 'SysMenuMgmModel',  //设置视图模型

    bodyStyle: 'background-color:#fff',  //设置该视图背景颜色（白色）
    layout: 'border',  //设置布局
    items:[{  //设置该视图子组件
        region: 'center',
        xtype: 'container',
        layout: 'vbox',
        items: [{
            xtype: 'panel',
            flex: 6,  //设置布局大小权重
            width: '100%',
            heigth: 450,
            padding: '0,0,5,0',
            layout: 'fit',
            items: [{
                xtype: 'SysMenuTreeGrid',  //别名为SysMenuTreeGrid的组件
                id: 'SysMenuTreeGrid'  //设置id属性，可以用id检索，但是注意不能存在重名id
            }]
        },{
            xtype: 'panel',
            border: false,  //取消边框
            flex: 3,
            width: '100%',
            padding: '0,10,0,0',
            layout: 'fit',
            items: [{
                xtype: 'fieldset',  //Ext.form.FieldSet
                title: '修改记录',
                layout: 'fit',
                items:[{
                    xtype:'form',  //Ext.form.Panel
                    id: 'MenuModify',
                    border: false,
                    buttonAlign: 'center',
                    layout: 'vbox',  //vbox垂直布局
                    items: [{
                        xtype: 'panel',
                        border: false,
                        width: '100%',
                        layout: 'column', 
                        columns: 3,
                        items: [{
                            xtype: 'textfield',  //Ext.form.field.TextField
                            fieldLabel: '功能编号',
                            labelWidth: 65,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'funcNum'  //设置组件name属性
                        },{
                            xtype: 'textfield',
                            fieldLabel: '排序号',
                            labelWidth: 65,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'sortNum'
                        },{
                            xtype: 'textfield',
                            fieldLabel: '功能名称',
                            labelWidth: 65,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'funcName'
                        }]
                    },{
                        xtype: 'panel',
                        border: false,
                        width: '100%',
                        layout: 'column',  //column列布局
                        columns: 3,
                        items: [{
                            xtype: 'textfield',
                            fieldLabel: '功能描述',
                            labelWidth: 65,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'description'
                        },{
                            xtype: 'textfield',
                            fieldLabel: '模块文件名',
                            labelWidth: 75,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'linkLib'
                        },{
                            xtype: 'textfield',
                            fieldLabel: '类型名',
                            labelWidth: 65,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'funcType'
                        }]
                    },{
                        xtype: 'panel',
                        border: false,
                        width: '100%',
                        layout: 'column',
                        columns: 3,
                        items: [{
                            xtype: 'textfield',
                            fieldLabel: '视图名称',
                            labelWidth: 65,
                            columnWidth: .33,
                            margin: '5,5,5,5',
                            name: 'viewPath'
                        }]
                    },{
                        xtype: 'panel',
                        border: false,
                        width: '100%',
                        layout: 'hbox',
                        items: [{
                            xtype: 'label',
                            text: '访问快捷键：',
                            width: 90,
                            margin: '5,5,5,5'
                        },{
                             xtype: 'checkboxgroup',  //Ext.form.CheckboxGroup
                             labelWidth: 60,
                             width: 200,
                             margin: '5,5,5,5',
                             fieldLabel: '修饰符：',
                             labelSeparator: '',
                             items: [
                                 { boxLabel: 'Ctrl', name: 'modifier', inputValue: '1' }, 
                                 { boxLabel: 'Shift', name: 'modifier', inputValue: '2', checked: true },
                                 { boxLabel: 'Alt', name: 'modifier', inputValue: '3' },
                             ]
                        },{
                            xtype: 'combobox',  //Ext.form.field.ComboBox
                            labelWidth: 30,
                            margin: '5,5,5,5',
                            fieldLabel: '键：',
                            width: 150
                        }]
                    }]
                }]
            }]
        },{
            xtype: 'panel',
            border: false,
            flex: 1,
            width: '100%',
            padding: '0,0,5,0',
            layout: {
                 align: 'middle',
                 pack: 'center',
                 type: 'hbox'
            },
            items: [{
                xtype: 'button',  //Ext.button.Button
                text: '新增',
                scale: 'small',
                margin : '5 0 5 5',
                iconCls: 'fa fa-plus-circle',  //设置样式
                iconAlign: 'left',
                handler: 'onAddClick'  //设置button的handler处理函数
            },{
                xtype: 'button',
                text: '保存',
                scale: 'small',
                margin : '5 0 5 5',
                iconCls: 'fa fa-save',
                iconAlign: 'left',
                handler: 'onSaveClick'
            },{
                xtype: 'button',
                text: '删除',
                scale: 'small',
                margin : '5 0 5 5',
                iconCls: 'fa fa-remove',
                iconAlign: 'left',
                handler: 'onDeleteClick'
            },{
                xtype: 'button',
                text: '复位',
                scale: 'small',
                margin : '5 0 5 5',
                iconCls: 'fa fa-recycle',
                iconAlign: 'left',
                handler: 'onRefreshClick'
            }]
        }]
    }]
});