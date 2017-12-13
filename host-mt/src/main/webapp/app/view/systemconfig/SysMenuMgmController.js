Ext.define('MyApp.view.systemconfig.SysMenuMgmController',{
    extend: 'Ext.app.ViewController',
    alias: 'controller.SysMenuMgmController',

    onItemClick: function(grid, rowIndex){
         var store = Ext.getCmp('SysMenuTreeGrid').getStore(),
             selectedrecord = store.getAt(store.indexOf(rowIndex.getData()));

         Ext.getCmp('MenuModify').getForm().loadRecord(selectedrecord); 
    },
    onItemDblClick: function(grid, rowIndex){
        var store = Ext.getCmp('SysMenuTreeGrid').getStore(),
            selectedrecord = store.getAt(store.indexOf(rowIndex.getData())),
            win = Ext.create('MyApp.view.systemconfig.SysMenuDetailWin',{
                id: 'SysMenuDetailWin'
            }),
            form = Ext.getCmp('MenuDetail');

        form.loadRecord(selectedrecord);
        win.show();
    },
    onAddClick: function(grid, rowIndex, colIndex, actionItem, event, record, row) {
        var win = Ext.create('MyApp.view.systemconfig.SysMenuCreateWin',{
            id: 'SysMenuCreateWin'
        });
        win.show();
    },

    onDeleteClick: function(){
        var grid = Ext.getCmp('SysMenuTreeGrid'),
            selection = grid.selection,
            data = selection.data,
            store = grid.getStore(),
            selectedrecord = store.getAt(store.indexOf(data));

        Ext.MessageBox.confirm('操作确认', '您是否确定删除功能编号为：'+data.funcNum+ ' 的功能菜单？', function(btn){
            if(btn=='yes'){
                //Delete the data
                //isLeaf?
                if(data.isLeaf){
                    //Delete the leaf node
                    store.remove(selectedrecord);
                    Ext.getCmp('MenuModify').reset();

                    this.showToast('您已经删除了所选的数据！');
                }else{
                    //Confirm to Delete Parent Node and its childNotes?
                    Ext.MessageBox.confirm('操作确认', '您是否确定删除功能编号为：'+data.funcNum+ ' 的功能菜单，以及它的所有子菜单？', function(btn){
                        if(btn=='yes'){
                            //Delete All nodes
                            store.remove(selectedrecord);
                            Ext.getCmp('MenuModify').reset();

                            this.showToast('您已经保存了修改的数据！');
                        }else{
                            //if NO btn was chose

                            this.showToast('您已经取消了删除操作！');
                        }

                    }, this);
                }
            }else{
                //if NO btn was chose

                this.showToast('您已经取消了删除操作！');
            }
        }, this);

    },

    onSaveClick: function(){
        //1.Validate the data

        //2.Confirm the operation
        var form = Ext.getCmp('MenuModify');
        Ext.MessageBox.confirm('操作确认', '您是否确定修改功能编号为：'+form.down('textfield').lastValue+ ' 的功能菜单？', function(btn){
            if(btn=='yes'){
                //3.Save the data

                this.showToast('您已经保存了修改的数据！');
            }else{
                //if NO btn was chose

                this.showToast('您已经取消了保存操作！');
            }

        }, this);
    },

    onRefreshClick: function(){
        var grid = Ext.getCmp('SysMenuTreeGrid'),
            selection = grid.selection,
            data = selection.data,
            store = grid.getStore(),
            selectedrecord = store.getAt(store.indexOf(data));

        Ext.getCmp('MenuModify').getForm().loadRecord(selectedrecord);
    },

    onCreateSaveClick: function(){
        //1.Validate the data

        //2.Confirm the operation
        var form = Ext.getCmp('MenuForm'),
            win,
            grid = Ext.getCmp('SysMenuTreeGrid');
        Ext.MessageBox.confirm('操作确认', '您是否确定创建功能编号为：'+form.down('textfield').lastValue+ ' 的功能菜单？', function(btn){
            win = Ext.getCmp('SysMenuCreateWin');
            if(btn=='yes'){
                //3.Save the data

                this.showToast('您已经保存了新建数据！');
                //4.Close the window
                win.close();  //Close to prevent duplicated ID of this window

                //5.Reload the TreeGrid
                grid.getStore().reload();
            }else{
                //if NO btn was chose

                this.showToast('您已经取消了保存操作！');
            }

        }, this);

    },

    onWinRefreshClick: function(){
        var form = Ext.getCmp('MenuForm');
        form.reset();
    },

    onCreateCancelClick: function(){
        var win = Ext.getCmp('SysMenuCreateWin');
        win.close();
    },

    onDetailWinCloseClick: function(){
        var win = Ext.getCmp('SysMenuDetailWin');
        win.close();
    },

    showToast: function(s, title) {
        Ext.toast({
            html: s,
            closable: false,
            align: 't',
            slideInDuration: 200,
            minWidth: 400
        });
    }
});