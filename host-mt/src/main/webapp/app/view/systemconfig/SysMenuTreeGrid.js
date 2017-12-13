Ext.define('MyApp.view.systemconfig.SysMenuTreeGrid',{
    extend: 'Ext.tree.Panel',  //集成自Ext.tree.Panel，是树结构表格的控件
    alias: 'widget.SysMenuTreeGrid',  //设置别名

    controller: 'SysMenuMgmController',  //设置该组件控制器
    viewModel: 'SysMenuMgmModel',  //设置该组件视图模型

    listeners: {  //注册事件响应函数
        itemclick: 'onItemClick',  //单击事件
        itemdblclick: 'onItemDblClick'  //双击事件
    },

    reserveScrollbar: true,  //预留滚动条位置大小
    border: false,
    useArrows: true,  //使用箭头符号
    rootVisible: false,  //设置树的根节点不可见
    multiSelect: true,  //设置可以多选行记录

    initComponent: function() {  //初始化函数
        Ext.apply(this, {  //Ext.apply函数
            store: new Ext.data.TreeStore({  //设置当前组件（树结构表格）的store属性，新建了一个Ext.data.TreeStore实例
                model: 'MyApp.model.systemconfig.MenuTreeModel',  //该store的model属性为MyApp.model.systemconfig.MenuTreeModel类的实例
                autoLoad:true,  //自动加载
                proxy: {  //store的代理
                    type: 'ajax',  //类型为ajax的代理
                    url: 'http://localhost:89/host-mt/login/sysmenu.json'  //数据来源的URL地址；此处为了测试方便读取的本地json文件，实际项目中一般是从后台的URL映射方法返回json格式的内容
                },
                reader: {  //设置store的阅读器reader属性
                    type: 'json',  //json格式的阅读器，将json格式数据转换成store可以识别的record对象
                    rootProperty: 'text'  //设置读取的根节点
                },
                folderSort: true  //自动预留的对叶节点的sorter
            }),

            viewConfig: {
                 plugins: {  //设置视图应用的插件
                     ptype: 'treeviewdragdrop',  //树结构视图拖放插件
                     containerScroll: true
                 }
             },

             tbar: [{  //顶部工具条
                 labelWidth: 40,
                 xtype: 'textfield',
                 fieldLabel: '搜索',
                 listeners: {  //配置事件监听函数
                     change: function() {  //change事件的处理函数
                         var tree = this.up('treepanel'),
                             v,
                             matches = 0;

                         try {
                             v = new RegExp(this.getValue().trim(), 'i');
                             Ext.suspendLayouts();
                             tree.store.filter({
                                 filterFn: function(node) {
                                     var children = node.childNodes,
                                         len = children && children.length,

                                         visible = node.isLeaf() ? v.test(node.get('funcName')) : false,
                                         i;

                                     for (i = 0; i < len && !(visible = children[i].get('visible')); i++);

                                     if (visible && node.isLeaf()) {
                                         matches++;
                                     }
                                     return visible;
                                 },
                                 id: 'titleFilter'
                             });
                             tree.down('#matches').setValue(matches);
                             Ext.resumeLayouts(true); 
                         } catch (e) {
                             this.markInvalid('Invalid regular expression');
                         }
                     },
                     buffer: 250
                 }
             }, {
                 xtype: 'displayfield',
                 itemId: 'matches',
                 labelWidth: 40,
                 fieldLabel: '匹配',

                 // Use shrinkwrap width for the label
                 listeners: {
                     beforerender: function() {
                         var me = this,
                             tree = me.up('treepanel'),
                             root = tree.getRootNode(),
                             leafCount = 0;

                         tree.store.on('fillcomplete', function(store, node) {
                             if (node === root) {
                                 root.visitPostOrder('', function(node) {
                                     if (node.isLeaf()) {
                                         leafCount++;
                                     }
                                 });
                 me.setValue(leafCount);
                             }
                         });
                     },
                     single: true
                 }
             }],

            columns: [{
                xtype: 'treecolumn', //treecolumn列会以树状结构显示
                text: '功能名称',
                flex: 2,
                sortable: true,  //该字段可排序
                dataIndex: 'funcName'  //设置该列绑定的字段名
            },{
                text: '功能编号',
                flex: 1,
                sortable: true,
                dataIndex: 'funcNum'
            },{
                text: '排序号',
                flex: 1,
                dataIndex: 'sortNum',
                sortable: true
            }, {
                text: '所属类别',
                flex: 1,
                sortable: true,
                dataIndex: 'funcType'
            }, {
                text: '视图名称',
                flex: 1,
                sortable: true,
                dataIndex: 'viewPath'
            }, {
                text: '功能描述',
                dataIndex: 'description',
                width: 120
            }, {
                text: '模块链接库',
                flex: 1,
                sortable: true,
                dataIndex: 'linkLib'
            }]
        });
        this.callParent();  //initComponent函数必须有的
    }
});