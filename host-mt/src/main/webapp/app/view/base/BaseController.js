Ext.define('MyApp.view.base.BaseController', {
    extend : 'Ext.app.ViewController',
    alias : 'controller.base',
 // 权限值
	viewRight : 1,	//读取按钮权限
	editRight : 2,	//编辑
	addRight : 4,	//添加
	deleteRight : 8,	//删除
	RFRight : 16,	//扫描	
	printRight : 32,	//打印
	exportRight : 64,	//导出
	printSetupRight : 128,	//打印设置
	auditRight : 256,	//审核1
	auditRight_1 :512,	//审核2
	
	otherRight1 : 1024,
	otherRight2 : 2048,
	otherRight3 : 4096,
	otherRight4 : 8192,
	otherRight5 : 16384,

	setupRight	:1024,	//权限设置
	giveRight : 2048,	//权限传递

	// 当前正在编辑的控件
	editingList : null,

	// 当前的活动控件
	workObject : null,

	// 是否保存成功之后， 用于重新绑定数据
	_isAfterSave : false,

	// 保存之后，回写ID,用于grid定位到编辑的行
	_idValue : '',

	// 页面进入编辑状态
	pageEditing : false,

	init : function() {
		var me = this;
		me.editingList = [];
		me._isAfterSave = false;
		me._idValue = '';

		me.getRightKey();
	    var objList = me.getObjList();

		me.callParent(arguments);

		try {

			if (!objList)
				return;
			var grid = objList['mastergrid'];

			if (!me.workObject && grid) {
				me.workObject = grid;
			}
			if (grid) {

				grid.on("beforeselect", me.onGridBeforeSelect, me);
				grid.on("selectionchange", me.onGridSelectionChange, me);

				grid.store.on('beforeload', me.onGridBeforeLoad, me);
				grid.store.on('load', me.onGridLoaded, me);

				grid.on('beforeedit', me.onGridBeforeEdit, me);
				grid.on('edit', me.onGridAfterEdit, me);

				grid.store.on('update', me.onGridDataChanged, me);
				grid.store.on('datachanged', me.onGridDataChanged, me);

				grid.on('rowdblclick', me.onGridRowDblClick, me);

				grid.on('celldblclick', me.onGridCellDblClick, me);
				
				if (me.canAdd() && me.canDelete() && !grid.isReadOnly)
					grid.view.getRowClass = me.initRowClass;
			}

			me.initToolbar(objList);
			me.setLabelCls();
			me.initGridCls(objList);
			me.bindData();
			
			//单独打开模块时默认选定当前模块
			window.main.selectPanel = me.view;
			
		} catch (e) {
		}
		// me.view.on('beforedestroy','onBeforeDestroy',me);
	},

	// region 权限处理开始

	/** 获取权限值 */
	getRightKey : function() {

	},

	/** 检查权限 */
	hasRight : function(rightCode) {
		return ((this.view.moduleRight & rightCode) == rightCode) && ((this.view.userRight & rightCode) == rightCode);
	},

	/** 查看权限 */
	canView : function() {
		return this.hasRight(this.viewRight);
	},

	/** 新增权限 */
	canAdd : function() {
		return this.hasRight(this.addRight);
	},

	/** 编辑权限 */
	canEdit : function() {
		return this.hasRight(this.editRight);
	},

	/** 删除权限 */
	canDelete : function() {
		return this.hasRight(this.deleteRight);
	},

	/** 打印权限 */
	canPrint : function() {
		return this.hasRight(this.printRight);
	},

	/** 导出权限 */
	canExport : function() {
		return this.hasRight(this.exportRight);
	},

	/** 审批权限 */
	canAudit : function() {
		return this.hasRight(this.auditRight);
	},

	canAudit2:function(){
		return this.hasRight(this.auditRight_1);
	},

	canOther1:function(){
		return this.hasRight(this.otherRight1);
	},
	canOther2:function(){
		return this.hasRight(this.otherRight2);
	},
	canOther3:function(){
		return this.hasRight(this.otherRight3);
	},

	canOther4:function(){
		return this.hasRight(this.otherRight4);
	},

	canOther5:function(){
		return this.hasRight(this.otherRight5);
	},


	/** 赋权权限 */
	canGiveRight : function() {
		return this.hasRight(this.giveRight);
	},

	getUserCode : function() {
		return this.view.userCode;
	},

	getUserName : function() {
		return this.view.userName;
	},

	// endregion 权限处理结束

	// region 统一与后端交互入口开始

	/* 实现 Ext.Ajax.request 方法 */
	callServer : function(options) {
		options.url = this.setUrlModuleInfo(options.url);
		if(options.url.indexOf('noFilterData=')<0) {
			options.url += '&noFilterData=TRUE';
		}
		Ext.Ajax.request(options);
	},

	/*
	 * 通用保存数据 options 属性说明 srcObj 源对象，必须指定, 如 tree, grid data 数据对象, 必须指定 url 或
	 * srcObj.batchUrl 必须指定,后端的服务地址 btn 触发保存事件的按钮,可选对象 isJson 当等于 false 时，用param
	 * 方式传递，否则用 jsonData方式传递
	 */
	saveData : function(options) {
		var me = this;

		if (!options.srcObj) {
			Belle.alert('没有指定源对象');
			return;
		}
		var url = options.url || options.srcObj.batchUrl;
		if (!url) {
			Belle.alert('没的指定后端服务URL');
			return;
		}
		if (!options.data) {
			Belle.alert('无数据需保存');
			return;
		}
		if (options.btn) {
			options.btn.setDisabled(true);
		}

		var param = {
			url: url,
			method: 'POST',
			timeout: options.timeout || 180000,
			success: function (response) {
				if (me.view.isMasked()) {
					me.view.unmask();
				}
				try {
					var result = JSON.parse(response.responseText);
					if (result.result.resultCode == '0') {
						me.editingList = [];
						me._isAfterSave = true;
						if (result.masterId) {
							me._idValue = result.masterId;
						}
						me.pageEditing = false;
						me.changePageEdit();
					}
					me.afterSave(result, options, response.responseText);
				} catch (e) {
					Belle.show({
						title: '错误提示',
						msg: e,
						height: 300,
						width: 500
					});
				}
			},
			failure: function (response) {
				if (me.view.isMasked()) {
					me.view.unmask();
				}
				try {
					var d = JSON.parse(response.responseText);
					var msg = d.result.msg;
					msg += ' <a onclick="Belle.alert(\'' + Belle.strEscape(d.result.retData || response.responseText) + '\');" href="javascript:void(0)"> 查看详情</a>';
					Belle.alert(msg);
				} catch (e) {
					Belle.alert('系统异常，请联系管理员！<a onclick="Belle.alert(\'' + Belle.strEscape(response.responseText) + '\');" href="javascript:void(0)"> 查看详情</a>');
				}
				if (options.btn) {
					options.btn.setDisabled(false);
				}
			}
		};
		//dwh 添加提交数据遮罩状态
		if(options&&options.isMask!=false){
			me.view.mask("数据提交中,请稍后...");
		}
		if (options.isJson === false) {
			param.params = JSON.stringify(options.data);
		} else {
			param.jsonData = JSON.stringify(options.data);
		}
		
		me.callServer(param);
	},

	setUrlModuleInfo : function(url) {
		return Belle.setUrlModuleInfo(this.view, url);
	},

	// endregion 统一与后端交互入口结束

	// region 获取需要保存的更改数据及数据验证开始

	/** 获取网格中被更改的记录数据 */
	getGridDirtyData : function(obj) {
		var param = {}, store = obj.store, items = store.getModifiedRecords(), tabName = obj.modelName
				.substr(obj.modelName.lastIndexOf('.') + 1), flag = '', addItem = [], updateItem = [], delItem = [];
		Ext.Array.each(items, function(item) {
					flag = item.get('_flag');
			        var rowval={};
			        for(var col in item.data) {
						rowval[col] = (item.data[col] && item.data[col].join) ? item.data[col].join(',') : item.data[col];
					}
					if (flag == 'A') {
						addItem.push(rowval);
					} else if (flag == 'D') {
						delItem.push(rowval);
					} else {
						updateItem.push(rowval);
					}
				});
		param.customerName = tabName;
		if (obj.mSizeIdx > -1) {
			param.isSizeHorizontal = 1;
			param.qtyProperty = obj.mSizeQtyField
		}

		if (obj.convertToSize == 1) {
			param.type = 1;
		}
		if (addItem.length > 0)
			param.insertList = addItem;
		if (delItem.length > 0)
			param.deleteList = delItem;
		if (updateItem.length > 0)
			param.updateList = updateItem;
		return param;
	},

	/** 获取需要保存的数据 */
	getDataToSave : function(obj, isFormDel) {
		var me = this, param = {}, subGrid = obj.subGrid || [];

		if (!me.validData(obj))
			return false;

		// 单表模式获取数据
		if (subGrid.length == 0) {
			if (obj.is('form')) {
				var record = obj.getRecord();
				param.customerName = obj.modelName.substr(obj.modelName
						.lastIndexOf('.')
						+ 1);
				if (isFormDel) {
					param.deleteList = [record.data];
				} else {
					if (record.phantom) {
						param.insertList = [record.data];
					} else {
						param.updateList = [record.data];
					}
				}
			} else {
				param = me.getGridDirtyData(obj);
			}
			return param;
		}

		// 多表模式获取数据
		var idField = obj.primaryKey, customerListData = [], objlist = me
				.getObjList();

		// 处理主表
		param.idFieldName = idField;
		if (obj.is('form')) {
			var record = obj.getRecord();
			if (isFormDel) {
				param.operateType = "deleted";
			} else {
				if (!obj.isDirty()) {
					param.operateType = 'nochanged';
				} else {
					if (record.phantom) {
						param.operateType = 'inserted';
					} else {
						param.operateType = 'updated';
					}
				}
			}
			param.masterJson = record.data;
		} else {
			var store = obj.store, items = store.getModifiedRecords();
			if (items.length < 1) {
				param.operateType = 'nochanged';
				items = obj.getSelection();
				if (items.length < 1) {
					Belle.alert('操作异常!');
					return false;
				}
				param.masterJson = items[0].data;
			} else {
				var item = items[0];
				var flag = item.get('_flag');
				if (flag == 'A') {
					param.operateType = 'inserted';
				} else if (flag == 'D') {
					param.operateType = 'deleted';
				} else {
					param.operateType = 'updated';
				}
				param.masterJson = item.data;
			}

		}

		// 处理从表
		for (var i = 0; i < subGrid.length; i++) {

			var gridobj = objlist[subGrid[i]];
			if (!gridobj)
				continue;

			if (param.operateType == 'deleted') {
				var delobj = {};
				delobj[idField] = param.masterJson[idField];
				if(me.view.billMyCatField) {
					delobj[me.view.billMyCatField] = param.masterJson[me.view.billMyCatField];
				}
				customerListData.push({
							customerName : gridobj.modelName
									.substr(gridobj.modelName.lastIndexOf('.')
											+ 1),
							deleteList : [delobj]
						});
			} else {
				if (gridobj.isUpdating) {
					customerListData.push(me.getGridDirtyData(gridobj));
				}
			}
		}
		if (customerListData.length > 0) {
			param.customerListData = customerListData;
		}

		if (param.operateType == "updated" || param.operateType == 'nochanged') {
			me._idValue = param.masterJson[idField];
		}

		return param;
	},

	/** 主键录入重复时处理 */
	keyValueError : function(e) {
		var error = function() {
			
//			if(e.record.previousValues){
//				for(item in e.record.previousValues){
//                	e.record.set(item,e.record.previousValues[item]);
//            	}
//			}
//			else{
				e.record.set(e.field, e.originalValue);
//			}
			
            
			if (e.grid.editModel == 'cell') {
				e.grid.editingPlugin.startEditByPosition({
							row : e.rowIdx,
							column : e.colIdx
						});
			} else {
				e.grid.editingPlugin.startEdit(e.rowIdx, e.colIdx);
			}
		};
		if (e.field == e.grid.primaryKey) {
			Belle.alert('新输入的主键【' + e.value + '】已存在', function() {
						error();
					});
		} else {
			var val = '', ukey = e.grid.unionKey.split(',');
			Ext.each(ukey, function(k) {
						val += ' 【' + (e.record.get(k) || '') + '】 ';
					});

			Belle.alert('唯一索引值' + val + '已存在', function() {
						error();
					});
		}
	},

	/** 检查主键是否重复,优先检查本地录入，再检查服务器 */
	checkKeyValue : function(e) {

		var me = this, isPass = true, // 是否验证通过
			ukey = (e.grid.unionKey || '').split(','), belleFilter = e.column.belleFilter;

		if ((e.field == e.grid.primaryKey || ukey.indexOf(e.field) >= 0)
			&& e.value != e.originalValue) {

			var idx = 0, param, store = e.grid.store;

			var checkdata = function (p) {
				//新的验证url,需要common-core升级到0.3.8 才可用。 通过queryCondition传参数
				var url = store.proxy.url,
				   dbtablename= e.grid.dbTableName||'';

				url=url.substr(0,url.indexOf('.json'));
				url = me.setUrlModuleInfo(url.substr(0, url.lastIndexOf('/') + 1) + 'queryBaseCount.json?tableName='+dbtablename);
				me.callServer({
					url: url,
					method: 'POST',
					async: false,
					params: {
						queryCondition: JSON.stringify(p)
					},
					success: function (response) {
						var result = JSON.parse(response.responseText);
						if (result.count > 0) {
							me.keyValueError(e);
							isPass = false;
						}
					},
					failure: function () { //没有启用 0.3.8 的工程，通过原来list.json,listvo.json去验证
						//改为key：value传值方式 0902
						var formp = {};
						var for16 = [];
						for (var i = 0, len = p.length; i < len; i++) {
							if (p[i].operator == 10) {
								formp[p[i].property] = p[i].value
							} else {
								for16.push({
									property:me.view.queryDataIndex+ p[i].property,
									value:p[i].value,
									operator:p[i].operator
								});
							}
						}
						if (for16.length > 0) {
							formp.queryCondition = JSON.stringify(for16);
						}
						formp.checkData = 'TRUE';
						me.callServer({
							url: store.proxy.url,
							method: 'POST',
							async: false,
							params: formp,
							success: function (response) {
								var result = JSON.parse(response.responseText);
								if (result.list && result.list.length > 0) {
									me.keyValueError(e);
									isPass = false;
								}
							},
							failure: function () {
								isPass = false;
							}
						})
					}
				});
			};

			if (e.field == e.grid.primaryKey) {
				idx = store.findBy(function (item) {
					return item.get(e.field) == e.value
						&& item != e.record;
				});
				if (idx > -1) {
					me.keyValueError(e);
				} else {
					param = [{
						property: e.field,
						value: e.value,
						operator: 10
					}];
					checkdata(param)
				}
			} else {

				// 如果维一索引中字段的数据为空，不检查，即所有字段都录入完成数据后才去检查
				var needCheck = true;
				for (var i = 0; i < ukey.length; i++) {
					if (Ext.isEmpty(e.record.get(ukey[i])) && ukey[i]!='billNo') {
						needCheck = false;
						break;
					}
				}

				if (!needCheck)
					return isPass;

				//前端检查
				var itemval, recordval;
				idx = store.findBy(function (item) {
					itemval = [];
					recordval = [];
					Ext.each(ukey, function (k) {
						itemval.push(item.get(k));
						recordval.push(e.record.get(k));
					});
					return Ext.Array.equals(itemval, recordval)
						&& item != e.record;
				});

				if (idx > -1) {
					isPass = false;
					me.keyValueError(e);
				} else {
					//如果是单据明细且是新增数据，不需后端检查
					if(ukey.indexOf('billNo')>-1 && Ext.isEmpty(e.record.get('billNo')))
					    return isPass;
					if(me.view.pageType=='billDetail' && Ext.isEmpty( me.view.billNo)) return isPass;


					//后端检查
					param = [];
					if(me.view.pageType=='billDetail' && ukey.indexOf('billNo')<0){
						param.push({
							property: 'billNo',
							value: me.view.billNo,
							operator: 10
						});
					}
					Ext.each(ukey, function (k) {
						param.push({
							property: k,
							value: e.record.get(k),
							operator: 10
						});
					});
					param.push({
						property: e.grid.primaryKey,
						value: e.record.get(e.grid.primaryKey),
						operator: 16
					});
					checkdata(param)
				}
			}
		}
		return isPass;
	},

	/** grid网格数据 */
	gridValidData : function(grid) {
		var columns = grid.columns, editor, i, isPass = true, list = grid.store
				.getModifiedRecords();

		if (Ext.isEmpty(list))
			return true;
		for (i = 0; i < columns.length; i++) {
			editor = columns[i].getEditor && columns[i].getEditor();
			if (!editor)
				continue;
			if (editor.allowBlank === false) {
				if (Ext.Array.some(list, function(item) {
							return Ext.isEmpty(item.get(columns[i].dataIndex))
									&& item.get('_flag') != 'D';
						})) {
					isPass = false;
					Belle.alert('【' + columns[i].text + '】列数据不能为空');
					break;
				}
			}
		}
		return isPass;
	},

	/** 验证数据是否通过 */
	validData : function(obj) {
		var me = this, i, isPass = true, subobj, subGrid = obj.subGrid || [];

		if (Ext.isString(obj)) {
			obj = me.getObj(obj);
		}
		if (obj.is('form')) {
			isPass = obj.isValid();
		} else if (obj.is('grid')) {
			isPass = me.gridValidData(obj);
		}
		if (isPass && subGrid.length > 0) {
			for (i = 0; i < subGrid.length; i++) {
				subobj = me.getObj(subGrid[i]);
				if (!subobj || !subobj.is('grid'))
					continue;
				isPass = me.gridValidData(subobj);
				if (!isPass)
					break;
			}
		}
		return isPass;
	},

	// endregion 获取需要保存的更改数据及数据验证结束

	// region 网格辅助控制开始

	/** 网格选择中时，控制按钮可用状态、控制从表加载、给viewModel绑定数据 */
	gridSelectionChange : function(sender, e) {
		var me = this, item = e[0], objList = me.getObjList(), grid = sender.view.grid, gridname = grid.reference;

		// 戴文辉，删除按钮不受编辑状态控制
		if (me.canDelete() // && me.pageEditing
				&& grid.isCanDelete && !grid.isReadOnly) {
			objList.btnDelete.setDisabled(e.length == 0);
		}
		if (me.canPrint()) {
			objList.btnPrint.setDisabled(e.length == 0)
		}

		if (me.canAudit() && !grid.isUpdating && grid.auditField) {
			objList.btnAudit.setDisabled(e.length == 0)
		}

		me.bindSubGrid(sender.view.grid);
		if (gridname == "mastergrid") {
			gridname = 'grid';
		}

		if (!item) {
			item = Ext.create(sender.view.grid.modelName);
		}
		me.getViewModel().set(gridname + 'Row', item);
	},

	/** 网格数据更新事件，控制按钮可用状态、更新网络编辑状态、更新页面编辑对象列表 */
	gridDataChanged : function(store, grid) {

		var me = this, objList = me.getObjList();
		if (typeof grid == 'string') {
			grid = objList[grid];
		}

		if (grid.isReadOnly)
			return;

		var isDirty = me.getDirtyIndex(store) > -1;

		grid.isUpdating = isDirty;
		if (isDirty) {
			me._isAfterSave = false;
			// dwh 处理新增数据时出现的空白区域
			//grid.view.refresh();
			// 没有找到替代方案，暂时放开refresh();
		}

		if (isDirty) {
			Ext.Array.include(me.editingList, grid.reference);
		} else {
			Ext.Array.remove(me.editingList, grid.reference);
		}

		objList.btnSave.setDisabled(me.editingList.length == 0);
		objList.btnCancel.setDisabled(me.editingList.length == 0);
		objList.btnUndo.setDisabled(me.editingList.length == 0);
		if (isDirty) {
			objList.btnAudit.setDisabled(true)
		}
		if (me.editingList.length == 0) {
			me.pageEditing = false;
			me.changePageEdit(objList);
		}
	},

	/** 网格中控制不能更改主键 */
	gridCannotEditKeyField : function(e) {
		// dwh 新增数据不受编辑按钮控制
		if (!this.pageEditing && "A" != e.record.data._flag)
			return false;
		if (!this.canAdd() && !this.canEdit())
			return false;
		if (e.grid.isReadOnly == true)
			return false;
		if (e.field == e.grid.primaryKey && !e.record.phantom) {
			return false;
		}
		if (!e.grid.isCanEdit && !e.record.phantom)
			return false;

		if(e.column.xtype=='bllookupedit' && e.column.multiSelect && !e.originalValue.join && e.originalValue.indexOf(',')>-1) {
			//e.originalValue = e.originalValue.split(',');
			e.value =e.originalValue.split(',');
		}

	},

	/** 绑定从表 */
	bindSubGrid : function(obj) {
		if (typeof obj == 'string') {
			obj = this.getObj(obj);
		}
		var subGrid = obj.subGrid || [];
		if (subGrid.length == 0)
			return

		var item, idValue = '', objs = this.getObjList(), idField = obj.primaryKey;
		if (obj.is('form')) {
			item = obj.getRecord();
		} else {
			item = obj.getSelection()[0];
		}
		if (item && !item.phantom) {
			idValue = item.get(idField);
		}
		for (var i = 0; i < subGrid.length; i++) {
			var grid =objs[subGrid[i]],
				store = grid.store;
			store.removeAll();
			store.commitChanges();
			if (idValue) {
				store.proxy.extraParams[idField] = idValue;
				store.loadPage(1);
			}
		}
	},

	/** 检查是否可以加载数据 */
	gridIsCanLoad : function(store, obj) {

		if (this._isAfterSave) {
			return true;
		}

		if (typeof obj == 'string') {
			obj = this.getObj(obj);
		}
		if (this.getDirtyIndex(store) > -1
				|| (obj.isMaster && this.editingList.length > 0)) {
			Belle.alert('您正在编辑数据,请先保存或取消后再进行此操作');
			return false;
		}
		var supGrid = obj.supGrid;
		if (supGrid) {
			var supObj = this.getObj(supGrid), idField = supObj.primaryKey, item = supObj
					.getSelection()[0];
			if (!item || item.phantom)
				return false;
			store.proxy.extraParams[idField] = item.get(idField);
		}

		// 提交查询之前先清除原有数据,处理查询超时或出错无返回数据时，页面还显示旧数据，用户分不清是新查询结果还是旧记录。
		//store.removeAll();
		//store.commitChanges();

	},

	/**
	 * 设置网格的表头样式
	 */
	gridHeadCls : function(grid) {
		var columns = grid.vcolumn, headers = grid.headerCt, c, editor, headitem, field;
		for (c = 0; c < columns.length; c++) {
			editor = columns[c].editor;
			field = columns[c].dataIndex;
			if (!editor || !field)
				continue;
			headitem = Ext.Array.findBy(headers.items.items, function(item) {
						return item.dataIndex == field
					});
			if (!headitem)
				continue;
			if (editor.allowBlank === false) {
				headitem.addCls('notnull-field');
			} else {
				headitem.addCls('cannull-field');
			}

		}
	},

	// endregion 网格辅助控制结束

	// region 处理尺码横排，所有 grid 统一调用 开始

	/** 在绑定数据时处理尺码横排 */
	setSizeColsOnLoad : function(grid, store, options) {
		var me = this;
		if (grid.mSizeIdx == -1)
			return;
		try {
			var result = JSON.parse(options.getResponse().responseText), _head = result['headlist']
					|| [], _use = result['usedlist'] || [];
			me.setGridHeadList(grid, _head, _use);
			me.setGridSizeCols(grid, store);
		} catch (e) {
			console.info("Error:", e);
		}
	},

	/** 在录入物料编码时处理尺码横排 */
	setSizeColsOnEdit : function(grid, e) {

		return;
		// 尺码横排只用于展示，不用于录入

		if (e.field !== 'materialNo' || grid.mSizeIdx == -1)
			return;
		if (e.value == e.originalValue)
			return;

		if (grid.store.findBy(function(item) {
					return item.get('materialNo') == e.value
							&& item != e.record
				}) > -1) {
			Belle.alert('物料号【' + e.value + '】已存在', function() {
						e.record.set('materialNo', '');
					});
			return;
		}

		var me = this;
		if (me.setSizeFillFields(grid, e)) {
			me.setGridSizeCols(grid, grid.store);
			return;
		}

		me.callServer({
			url : grid.mSizeUrl,
			params : {
				'materialNo' : e.value
			},
			method : 'POST',
			success : function(d) {
				try {
					var result = JSON.parse(d.responseText), _head = result.headlist
							|| [], _use = result.usedlist || [];

					if (Ext.isEmpty(_head)) {
						Belle.alert('无法获取物料【' + e.value + '】的尺码信息', function() {
									e.record.set('materialNo', '');
								});
						return;
					}
					me.setGridHeadList(grid, _head, _use);
					me.setSizeFillFields(grid, e);
					me.setGridSizeCols(grid, grid.store);
				} catch (err) {
					console.info('通过物料读取尺码信息时出错:', err);
					Belle.alert('获取物料【' + e.value + '】的尺码信息出错', function() {
								e.record.set('materialNo', '');
							});
				}

			},
			failure : function(d) {
				console.info('通过物料读取尺码信息访问后端出错:', d.responseText);
				Belle.alert('读取物料尺码出错! 错误信息:' + d.responseText, function() {
							e.record.set('materialNo', '');
						});
			}
		});
	},

	/** 尺码横排处理grid表头 */
	setGridSizeCols : function(grid, store) {

		var sIdx = grid.mSizeIdx;
		if (sIdx == -1)
			return;



		var me = this, head = Belle.clone(grid._headlist), gc = Belle
				.clone(grid.vcolumn), i, j, tmpHead, field;

		if(Ext.Array.findBy(gc,function(item) {return item.xtype=='rownumberer'})) {
			sIdx++;
		}

		if (!head)
			return;

		for (i = 1; i < 21; i++) {
			field = 'f' + i;
			if (!Ext.Array.findBy(head, function(item) {
						return item[field] != '0'
					})) {
				for (j = 0; j < head.length; j++) {
					tmpHead = head[j];
					delete tmpHead[field];
				}
			}
		}
		if (head.length == 0) {
			grid.reconfigure(store, gc);
			me.gridHeadCls(grid);
			return;
		}

		try {
			var sizeCols = [], mSizeCol = gc[sIdx], uselist = grid._uselist
					|| [], sizeInpuTtype = 'numberfield',
				isSummary=grid.mSummary;
			if (grid.sizeInputType != 'number') {
				sizeInpuTtype = 'textfield';
			}

			var getcol = function(_field, _text, _column) {
				_text = _text == '0' ? '&nbsp;' : (_text || '&nbsp;');
				if (_column) {
					return {
						text : _text,
						columns : [_column]
					}
				}
				var col= {
					dataIndex : _field,
					text : _text,
					width : 50,
					belleFilter:{isOpen:false},
					align : 'center'

					// 尺码横排只用于展示，不用于录入
					/*
					 * , editor: { xtype: sizeInpuTtype }
					 */
					,
					renderer : function(val, obj, record) {
						// if (Ext.Array.findBy(uselist, function (item) {
						// return item.materialNo == record.get('materialNo') &&
						// (item[_field] != '0')
						// })) {
						// obj.tdCls = 'x-grid-input-cell';
						// }
						return val == 0 ? '' : val;
					}
				}
				if(isSummary) {
					col.summaryType = "sum";
					col.summaryRenderer = function (value) {
						return value == 0 ? '' : value;
					}
				}
				return col;
			};

			if (head.length == 1) {
				tmpHead = head[0];
				var tmpcol={
					text : tmpHead['sizeTypeNo'],
					dataIndex : 'sizeTypeNo',
					align : 'center',
					belleFilter:{isOpen:false},
					width : '70'
				};
				if(isSummary) {
					tmpcol.summaryType = "count";
					tmpcol.summaryRenderer = function (value) {
						return '合计';
					}
				}
				sizeCols.push(tmpcol);
				for (i = 1; i < 21; i++) {
					field = 'f' + i;
					if (!tmpHead[field])
						continue;
					sizeCols.push(getcol(field, tmpHead[field]));
				}
			} else {
				var tmp, tmpType = {};
				for (i = 1; i < 21; i++) {
					tmp = {};
					field = 'f' + i;
					for (j = 0; j < head.length; j++) {
						tmpHead = head[j];
						if (i == 1) {
							if (j == 0) {
								tmpType = {
									text : tmpHead['sizeTypeNo'],
									dataIndex : 'sizeTypeNo',
									align : 'center',
									belleFilter:{isOpen:false},
									width : '70'
								}
								if(isSummary) {
									tmpType.summaryType = "count";
									tmpType.summaryRenderer = function (value) {
										return '合计';
									}
								}
							} else {
								tmpType = {
									text : tmpHead['sizeTypeNo'],
									columns : [tmpType]
								}
							}
						}

						if (!tmpHead[field])
							continue;
						if (j == 0) {
							tmp = getcol(field, tmpHead[field]);
						} else {
							tmp = getcol(field, tmpHead[field], tmp);
						}
					}
					if (i == 1)
						sizeCols.push(tmpType);
					if (Ext.Object.isEmpty(tmp))
						continue;
					sizeCols.push(tmp);
				}
			}
			Ext.Array.insert(gc, sIdx, sizeCols);
			Ext.Array.remove(gc, mSizeCol);
			grid.reconfigure(store, gc);
			me.gridHeadCls(grid);
		} catch (e) {
			console.info('创建物料的尺码表头时出错:', e);
			Belle.alert('创建物料的尺码表头时出错');
		}
	},

	/** 设置物料只能编辑可用的尺码 */
	sizeFieldBeforeEdit : function(grid, e) {

		return;
		// 尺码横排只用于展示，不用于录入

		var uselist = grid._uselist;

		if (grid.mSizeIdx == -1)
			return;
		var fields = [];
		for (var i = 1; i < 21; i++) {
			fields.push('f' + i);
		}
		if (fields.indexOf(e.field) == -1)
			return;

		if (Ext.isEmpty(uselist))
			return false;

		var materialNo = e.record.get('materialNo');
		if (Ext.isEmpty(materialNo))
			return false;

		var usesize = Ext.Array.findBy(uselist, function(item) {
					return item.materialNo == materialNo;
				});
		if (!usesize || !usesize[e.field] || usesize[e.field] == "0")
			return false;
	},

	/** 把取到的尺码信息存在grid属性中 */
	setGridHeadList : function(grid, newHeadList, newUseList) {
		var i = 0, tmp, headlist =[], //Belle.clone(grid._headlist || []),
		       usedlist = Belle	.clone(grid._uselist || []);
		if (!Ext.isEmpty(newHeadList)) {
			for (i = 0; i < newHeadList.length; i++) {
				tmp = newHeadList[i];
				if (!tmp || !tmp['sizeTypeNo'])
					continue;
				if (Ext.Array.findBy(headlist, function(item) {
							return item['sizeTypeNo'] == tmp['sizeTypeNo'];
						}))
					continue;
				headlist.push(tmp);
			}
			grid._headlist = headlist;
		}

		if (!Ext.isEmpty(newUseList)) {
			for (i = 0; i < newUseList.length; i++) {
				tmp = newUseList[i];
				if (!tmp || !tmp['materialNo'])
					continue;
				if (Ext.Array.findBy(usedlist, function(item) {
							return item['materialNo'] == tmp['materialNo'];
						}))
					continue;
				usedlist.push(tmp);
			}
			grid._uselist = usedlist;
		}
	},

	/** 输入物料时，带出尺码信息顺便填充其它列值 */
	setSizeFillFields : function(grid, e) {
		var headlist = grid._headlist || [], usedlist = grid._uselist || [], fields = grid.mSizeFillFields
				.split(','), useInfo = Ext.Array.findBy(usedlist,
				function(item) {
					return item.materialNo == e.value
				});

		if (useInfo && Ext.Array.findBy(headlist, function(item) {
					return item['sizeTypeNo'] == useInfo['sizeTypeNo']
				})) {
			var i = 0, fvalue = '';
			for (; i < fields.length; i++) {
				fvalue = useInfo[fields[i]];
				if (fvalue == null)
					fvalue = '';
				e.record.set(fields[i], fvalue)
			}
			return true;
		}
		return false;
	},

	// endregion 处理尺码横排，所有 grid 统一调用 结束

	// region其它辅助方法开始

	/** 返回页面所有定义Reference属性的对象 */
	getObjList : function() {
		return this.getReferences();
	},

	/**
	 * 通过reference返回对象
	 */
	getObj : function(RefKey) {
		return this.lookupReference(RefKey);
	},

	/** 返回当前模块的高度百分比 */
	getBodyHeight : function(per) {
		var bodyh = window.innerHeight, appmain = this.view.up('app-main');
		if (appmain) {
			try {
				var toph = appmain.down().getHeight();
				bodyh = bodyh - toph - 25;
			} catch (e) {
				bodyh = bodyh - 85;
			}
		}
		per = per || 1;
		return bodyh * per;
	},

	/** 初始化网格行类样式 */
	initRowClass : function(record, index, rowParams, store) {
		var flag = record.get('_flag');
		if (flag == 'A')
			return 'x-grid-rows-add';
		if (flag == 'D')
			return 'x-grid-rows-delete';
		if (record.dirty && !flag)
			return 'x-grid-rows-edit';
		return ''
	},

	/** 自动获取面板的子对象 */
	getFormItems : function(columns, rowname) {
		var formItems = [], item, obj, i;
		if (columns.length == 0)
			return formItems;
		for (i = 0; i < columns.length; i++) {
			obj = columns[i];
			item = {
				name : obj.dataIndex,
				fieldLabel : obj.text || obj.header,
				xtype : 'textfield',
				bind : {
					value : '{' + rowname + "." + obj.dataIndex + "}"
				}
			};
			if (Ext.isObject(obj.editor)) {
				Ext.apply(item, columns[i].editor);
			}
			if (!obj.editor) {
				item.disabled = true;
			}
			formItems.push(item);
		}
		return formItems;
	},

	/** 返回第一条被更改记录的index */
	getDirtyIndex : function(store) {
		return store.findBy(function(a) {
					return a.dirty || a.phantom
				});
	},

	/**
	 * 在主页面的tabpanel中打开模块 object中参数如下： xtype 模块别名 title tab中显示的文本 moduleRight
	 * 模块权限 userRight 用户权限
	 */
	showInTab : function(object, isRefresh) {

		if (!Ext.isObject(object)) {
			Belle.alert('请正确传入参数,只接收对象参数!');
			return;
		}
		var widgetName = object.xtype, tabPanel = Ext.getCmp('maintabpanel');
		if (!Ext.ClassManager.getNameByAlias('widget.' + widgetName)) {
			Belle.alert('模块名【' + widgetName + '】不存在!');
			return;
		}

		if (!tabPanel) {
			var title = object.title;
			delete object.title;
			Ext.apply(object, {
						height : Ext.getBody().getHeight() * 0.8,
						width : Ext.getBody().getWidth() * 0.8
					})
			this.showInWin(object, {
						title : title,
						winName : object.itemId,
						showBbar : false
					});
			return;
		}

		var tabitem = tabPanel.getComponent(object.itemId);
		if (!tabitem) {
			var tab = {
				closable : true,
				reorderable : true,
				loadMask : '加载中...'
			};
			Ext.apply(tab, object)
			tabitem = tabPanel.add(tab);
		} else {
			Ext.apply(tabitem, object);
			if (isRefresh && tabitem.controller) {
				tabitem.controller.init();
			}
		}
		tabPanel.setActiveTab(tabitem);
	},

	/**
	 * 在弹出框中显示 object 对象 options window参数
	 */
	showInWin : function(object, options) {
		if (!Ext.isObject(object)) {
			Belle.alert('请正确传入参数,第一个参数只接收对象!');
			return;
		}
		var widgetName = object.xtype, me = this;
		if (!Ext.ClassManager.getNameByAlias('widget.' + widgetName)) {
			Belle.alert('类名【' + widgetName + '】不存在!');
			return;
		}

		var winoptions = {
			closeAction : 'destroy',
			modal : true,
			//constrainHeader : true,
			items : [object],
			autoShow : true,
			bbar : ['->', {
						xtype : 'button',
						text : options.confirmText || '确认',
						handler : me.onWinConfirmClick,
						scope : me,
						glyph : Belle.Icon.btnSave
					}, {
						xtype : 'button',
						text : options.cancelText || '取消',
						handler : me.onWinClose,
						scope : me,
						glyph : Belle.Icon.btnCancel
					}]
		};
		Ext.apply(winoptions, options);
		if (options.showBbar === false) {
			delete winoptions.bbar;
		}
		var win = Ext.widget('window', winoptions);
		return win;
	},

	changePageEdit : function(objList) {
		var me = this;
		objList = objList || me.getObjList();

		var len = me.workObject && me.workObject.getSelection().length;
		objList.btnDelete.setDisabled(len === 0);

		if (objList.btnEdit.isHidden()) {
			me.pageEditing = true;
			return;
		}

		objList.btnEdit.setDisabled(me.pageEditing);
		// objList.btnAdd.setDisabled(!me.pageEditing);
		// objList.btnDelete.setDisabled(true);
		// if(me.pageEditing) {
		// var len = me.workObject && me.workObject.getSelection().length;
		// objList.btnDelete.setDisabled(len === 0);
		// }
	},

	/** 弹出框中的确认按钮事件 */
	onWinConfirmClick : function(btn) {
		btn.up('window').close();
	},

	/** 弹出框中取消按钮事件 */
	onWinClose : function(btn) {
		btn.up('window').close();
	},

	// 是否字段
	renderYesNo : function(val, metaData, model, row, col, store, gridview) {
		return val ? "是" : "否";
	},
	// 启用状态
	renderUseFlag : function(val, metaData, model, row, col, store, gridview) {
		return val ? "启用" : "禁用";
	},

	// endregion其它辅助方法结束

	onBeforeDestroy : function() {
		if (this.editingList.length > 0) {
			if (!confirm('正在编辑数据,是否退出？')) {
				return false;
			}
		}
	},

	gridCellDblClick:function(objview, td, cellIndex, record, tr, rowIndex,e){
		//objview.grid.inEditStatus=true;
	},

	gridAfterEdit: function(editor, record) {
		editor.grid.inEditStatus=false;
	},
	bindData:function(){
		var me = this,searchPanel = me.getObj('commonsearch');;
		if(me.view.showType!="linkcolumn") return;
		
		//绑定传递值
		if(me.view.params){
			searchPanel.getForm().setValues(me.view.params);
			
		}
		if(me.showDtlModule&&!Ext.isEmpty(me.view.billNo)){
			me.showDtlModule(me.view.billNo.split(',')[0],me.view.params);
		}
		
	},
	afterRender:function(){
        var me=this,objList=me.getObjList(),btnSearch;
        if(!objList) return;
        btnSearch =objList.btnSearch;
        
        if(me.view && me.view.showType!="linkcolumn") return;
        //通过linkColumn跳转到的模块时处理查询事件
        if(btnSearch&&btnSearch.btnEl&&btnSearch.btnEl.dom&&btnSearch.btnEl.dom.click)
        me.getObjList().btnSearch.btnEl.dom.click();
    },
	onBeforeSort : function(store, sorts) {
		var me = this, grid = me.workObject, store = grid.store, proxy = store
				.getProxy(), sortText, property, direction;

		if (Ext.isEmpty(sorts))
			return;

		Ext.each(grid.getColumnManager().columns, function(col) {

					if (col.dataIndex != sorts[0]._property)
						return true;
					if (Ext.isEmpty(col.belleFilter)) {
						property = col.dataIndex;
					} else {
						property = col.belleFilter.dataIndex || col.dataIndex;
					}
				});

		if (sorts[0]._direction == "ASC") {
			direction = "asc";
		} else {
			direction = "desc";
		}

		if(!property) return true;
		
		sortText = property + " " + direction;

		if (Ext.isEmpty(proxy.extraParams))
			return true;

		proxy.extraParams.sort = sortText;
		store.setProxy(proxy);

	},
	// region 工具条按钮事件开始
	/* 查询按钮 */
	onBtnSearchClick : function(btn) {
		var me = this, store = me.workObject.store, isDtlCondition = false, searchPanel = me
				.getObj('commonsearch'),otherFilter=[];

		if (searchPanel) {
			var formValue = searchPanel.getValues();

			for (var field in formValue) {
				var txt = Belle.getField(searchPanel, field);
				if (formValue[field] !== '') {
					if (txt.isDtlCondition) {
						isDtlCondition = true;
					}
					
					if(txt.xtype==="bellefilter"){
						
						var val=formValue[field];
						if(field==="billNo" && formValue[field].indexOf(",")>=0){
							val="'"+formValue[field].replace(/\,/g,"','")+"'";
						}
						otherFilter.push({
							value : val,
							operator : txt.operator,
							property : txt.dataIndex||txt.name
						});
					}
					else{
						//dwh
						if(formValue[field].join){
							if(txt.autoSplit){
								store.proxy.extraParams[field] = "'" + formValue[field].join("','") + "'";
							}else {
								store.proxy.extraParams[field] = formValue[field].join(',');
							}
						}
						else{
							store.proxy.extraParams[field] = formValue[field];
						}
					}
					
					
				} else {
					delete store.proxy.extraParams[field]
				}
			}
			if (isDtlCondition) {
				store.proxy.extraParams.isDtlCondition = 'TRUE'
			} else {
				delete store.proxy.extraParams.isDtlCondition;
			}
		}
		
		//dwh
		 me.workObject.otherfilter=otherFilter;
		if(me.workObject.initExtraParams){
			 me.workObject.initExtraParams();
		}
		
		if (me.beforeSearch(store) === false)
			return;
		store.currentPage = 1;
		store.loadPage(1);
	},

	keyToSearch : function(obj, e) {
		var me = this;
		if (obj.isExpanded!=true && e.keyCode == e.ENTER) {
			me.onBtnSearchClick();
		} else if (e.keyCode == e.ESC) {
			me.onBtnResetClick();
		}
	},

	/* 查询数据之前处理事项 */
	beforeSearch : function(store) {
		var me = this, fields = me.view.searchNotNullField;
		if (!fields)
			return true;

		var form = me.getObj('commonsearch'), flag = false, fArray = fields
				.split(','), txt, label = [];
		fArray.forEach(function(item) {
					txt = Belle.getField(form, item);
					if (txt) {
						if (!Ext.isEmpty(txt.getValue()))
							flag = true;
						label.push('【' + txt.fieldLabel + '】');
					}
				});
		if (!flag) {
			Belle.alert('查询条件' + label.join('') + ',必须输入一组值')
		}
		return flag;
	},

	/* 重置按钮 */
	onBtnResetClick : function(btn) {
		var me = this, form = me.getObj('commonsearch');
		if (form) {
			form.reset();
		}
	},

	/** 进入编辑状态 */
	onBtnEditClick : function(btn) {
		var me = this;
		me.pageEditing = true;
		me.changePageEdit()
	},

	/* 新增按钮 */
	onBtnAddClick : function(btn) {
		var me = this, grid = me.workObject, store = grid.store, model = store.model,  cellIndex = -1, rowIndex = store
				.getCount(),records = store.getData().items;

		if (!grid.isCanAdd || grid.isReadOnly) {
			Belle.alert("此网络不可新增记录");
			return;
		}
		var newObj = model.create({
					_flag : 'A'
				});
		delete newObj.data.id;
		if (me.initAddData(newObj) === false)
			return;
		
		//dwh 修改
		store.loadData([newObj],true);
		//store.loadData([{_flag : 'A'}],true);
		//模拟滚动到底部，这样才能定位到最后一行
		grid.view.scrollTo(grid.view.getScrollX(),grid.view.getScrollY() + 21);
		//grid.view.refresh();
		
		grid.getSelectionModel().select(rowIndex);

		if (grid.editModel != "cell" && grid.editModel != 'row')
			return;

		var columns = grid.getColumnManager().columns;
		for (var i = 0; i < columns.length; i++) {
			if (columns[i].getEditor && columns[i].getEditor()) {
				cellIndex = i;
				break;
			}
		}
		if (cellIndex == -1)
			return;
		if (grid.editModel == 'cell') {
			grid.editingPlugin.startEditByPosition({
						row : rowIndex,
						column : cellIndex
					});
		} else {
			grid.editingPlugin.startEdit(rowIndex, cellIndex);
		}
	},
	/* 新增数据时，初始化数据对象 */
	initAddData : function(newObj) {

	},

	/** 批量导入按钮 */
	// 弹出导入选择文件框
	onBtnImportClick : function(btn) {
		var me = this, importErrorMsg = '', grid = me.workObject, supGrid = grid.supGrid, objJson = ''; // 从表导入，设值主表主键值

		if (!grid.isCanAdd || grid.isReadOnly) {
			Belle.alert("此网络不可新增记录");
			return;
		}

		if (grid.isMaster) {
			Belle.alert('当前网格为主表不能批量导入');
			return;
		}

		// else if(supGrid) {
		// var mainGrid = me.getObj(supGrid),
		// mainGridprimaryKey = mainGrid.primaryKey,
		// mainGridprimaryValue =
		// mainGrid.getSelection()[0].get[mainGridprimaryKey];
		// objJson = '{' + '"' + mainGridprimaryKey + '"' + ':' +
		// mainGridprimaryValue + '}';
		// }

		if (me.getDirtyIndex(grid.getStore()) > -1
				|| (grid.isMaster && grid.editingList.length > 0)) {
			Belle.alert('您正在编辑数据,请先保存或取消后再进行此操作');
			return false;
		}

		if (!grid.importUrl || grid.importUrl.indexOf('.json')==-1) {
			var url = grid.store.getProxy().url.split('?')[0];
			grid.importUrl = url.substr(0,url.lastIndexOf('/'))+'/importExcel.json';;
		}
		
		var win = new Belle_Common.ux.BelleImport({
					ImportUrl : Belle.setUrlModuleInfo(me.view,
							me.workObject.importUrl),
					workObject : me.workObject
				});

		win.show();
		
		win.on("beforeImprort",me.onBeforeImprort,me);
	},
	onBeforeImprort:function(form,win){
		var me=this;
		return true;
		
	},
	// 处理导入事件
	onWinConfirmClick : function(btn) {
		var win = btn.up('window');
		if (win.winName == "importwin") {
			var form = win.down('form'), formobj = form.getForm();
			if (!formobj.isValid())
				return;
			formobj.submit({
						url : form.grid.importUrl,
						waitMsg : '正在导入...',
						success : function(obj, result) {
							Belle.alert(result.result
									? result.result.msg
									: '导入成功！');
							form.grid.store.reload();
							win.close();
						},
						failure : function(formobj, result) {
							Ext.Msg.alert('导入提示', result.result.result.msg);
							form.grid.store.reload();
							if (result.result.result.msg == '导入成功') {
								win.close();
							}
						}
					});
		}
	},

	/** 复制记录按钮 (复制选中的行) */
	onBtnCopyClick : function(btn) {
		var me = this, grid = me.workObject, store = me.workObject.store, idField = me.workObject.primaryKey, unionField = (me.workObject.unionKey || '')
				.split(','), selection = me.workObject.getSelection(), newObj, isGetMaxOrderNo = false// ,
		// billNo = me.view.billPanel.store.data.items[0].data.billNo;
		if (selection.length < 1)
			return;
		if (!grid.isCanAdd || grid.isReadOnly) {
			Belle.alert('此网格不允许新增数据');
			return;
		}
		if (grid.isMaster && this.editingList.length > 0) {
			Belle.alert('您有一笔数据正在处理，不能复制主表记录');
			return;
		}

		Ext.each(selection, function(item) {
					newObj = Ext.create(store.model);
					Ext.apply(newObj.data, item.data);
					newObj.set(idField, '');
					for (var i = 0; i < unionField.length; i++) {
						newObj.set(unionField[i], '');
					}
					newObj.set('_flag', 'A');

					if (me.beforeCopy(newObj) === false)
						return true;

					store.add(newObj);
				});
	},

	beforeCopy : function(newObj) {

	},

	/** 删除 如果是新增还没有保存的数据，直接删除，如果是已保存的数据，打上删除标识 */
	onBtnDeleteClick : function(btn) {
		var me = this, obj = this.workObject, store = obj.getStore(), auditField = obj.auditField
				|| 'isLock', items = obj.getSelection();

		if (items.length < 1)
			return;
		if (obj.isReadOnly || !obj.isCanDelete)
			return;
		if (this.checkDelete(items) === false)
			return;
		//删除之前，取消网格当前的编辑状态，行中不要有输入框
		if(obj.editingPlugin && obj.editingPlugin.cancelEdit)
			obj.editingPlugin.cancelEdit();

		Ext.each(items, function(record) {
			var _flag = record.get('_flag'), auditflag = record.get(auditField);
			//dwh 获取当前行位置
			var index = store.indexOf(record),selectRowIndex = index;
			
			//获取焦点
			if(index>=store.getCount()-1){
				index--;
			}
//			var row = obj.view.getRow(index)
//			if(row && row.cells[0] && row.cells[0].focus){
//				row.cells[0].focus();
//			}
				
			if (_flag == 'A') {
				
				//删除
				store.remove(record);
				
				if(selectRowIndex > store.getCount()-1){
					selectRowIndex = store.getCount()-1;
				}
				//删除行时重新设置焦点
				obj.getSelectionModel().select(selectRowIndex);
				row = obj.view.getRow(selectRowIndex)
				if(row && row.cells[0] && row.cells[0].focus){
					row.cells[0].focus();
				}
				
			} else {
				if (auditflag == null || auditflag == 0) {
					record.set('_flag', 'D');
				}
				
				//obj.view.getRow(index).focus();
			}
			//obj.view.refresh();
			//获取焦点在网格上，方便键盘操作
			//获取当前行
			
		});
	},

	/* 检查数据是否能删除,如果不能删除，返回false */
	checkDelete : function(items) {

	},

	/** 还原（还原选中行的所有操作） */
	onBtnUndoClick : function(btn) {
		var grid = this.workObject, store = grid.store, items = grid
				.getSelectionModel().getSelection();

		//dwh 撤销编辑状态 未撤出编辑状态时会出现数据行已经删除，但编辑框还在
		if(grid.editingPlugin && grid.editingPlugin.cancelEdit){
			grid.editingPlugin.cancelEdit();
		}

		//dwh 修改还原
		Ext.each(items, function(record,i) {
					if(!record) return true;
					if (record.phantom) {
						store.remove(record);
						i--;
					} else {
						record.reject();
					}
		});
		// dwh 刷新网格，解决单据明细还原时无法更新状态bug
		//grid.view.refresh();
	},

	/** 取消当前所有操作 */
	onBtnCancelClick : function(btn) {
		var grid = this.workObject, store = grid.getStore(),
		records = store.getData().items;
		
		//dwh 撤销编辑状态 未撤出编辑状态时会出现数据行已经删除，但编辑框还在
		if(grid.editingPlugin && grid.editingPlugin.cancelEdit)
			grid.editingPlugin.cancelEdit();
		
			
		//戴文辉 修复删除新增数据逻辑	
		for(var i=0;i<records.length;i++){
			var record = records[i];
			if(record){
				if(record.phantom){
					store.remove(record);
					i--;
				}
				else{
					record.reject();
				}
			}
		}
		
//		Ext.each(store.getData().items, function(record,i) {
//					if(!record) return true;
//					if (record.phantom) {
//						store.remove(record);
//						i--;
//					} else {
//						record.reject();
//					}
//		});
		//grid.view.refresh();
	},

	/** 提交保存 (批量处理) */
	onBtnSaveClick : function(btn) {
		var me = this, obj = me.workObject;

		if (!obj.isUpdating)
			return;

		var param = me.getDataToSave(obj);

		if (!param)
			return;

		if (me.beforeSave(param) === false)
			return;
		me.saveData({
					srcObj : obj,
					data : param,
					btn : btn
				});
	},

	/* 保存之前特殊处理 返回false 阻止保存 */
	beforeSave : function(data) {

	},
	 
	/* 从写保存之后方法（保存完后，如果成功重新加载数据，失败则提示错误消息） */
	afterSave : function(result, options) {
		var me = this;
		if (options.btn) {
			options.btn.setDisabled(false);
		}
		if (result.result.resultCode == "0") {
			if (options.srcObj.is('grid')) {
				options.srcObj.store.reload();
			} else {
				me.afterSaveResetForm(result, options);
			}
		} else {
			var msgmore = result.result.msg;
			msgmore += ' <a onclick="Belle.alert(\'' + Belle.strEscape(result.result.retData || JSON.stringify(result)) + '\');" href="javascript:void(0)"> 查看详情</a>';
			Belle.alert(msgmore);
		}
	},

	afterSaveResetForm : function(result, options) {

	},

	/* 导出当前页数据 */
	onBtnExportPageClick : function(btn) {
		var me = this, exportErrorMsg = '', grid = me.workObject, objs = me
				.getObjList(), exportUrl = grid.exportUrl, subgridExport = '', searchPanel = objs['commonsearch'];

		var win = new Belle_Common.ux.BelleExport({
					gridColumns : me.workObject.columns,
					grid : me.workObject,
					objs : me.getObjList(),
					exportUrl : Belle.setUrlModuleInfo(me.view, grid.exportUrl),
					searchPanel : objs.commonsearch,
					subgridExport : null//,
					//exportType : "page"

				});
		win.show();
		return;
	},

	/* 导出所有数据 */
//	onBtnExportAllClick : function(btn) {
//		var me = this, exportErrorMsg = '', grid = me.workObject, objs = me
//				.getObjList(), exportUrl = Belle.setUrlModuleInfo(me.view,
//				grid.exportUrl), searchPanel = objs.commonsearch, subgridExport = '';
//
//		var win = new Belle_Common.ux.BelleExport({
//					gridColumns : me.workObject.columns,
//					grid : me.workObject,
//					objs : me.getObjList(),
//					exportUrl : exportUrl,
//					searchPanel : objs.commonsearch,
//					subgridExport : null,
//					exportType : "all"
//
//				});
//		win.show();
//		return false;
//	},

	/* 打印（选中的记录） */
	onBtnPrintClick : function(btn) {
		// 通过打印控件生成报表页面
	},

	/* 查看日志 */
	onBtnViewLog : function(btn) {
		// 查看记录的操作日志
		var me = this, view = me.view, showmodel = view.showModel, dtlname = view.dtlName, itemid = dtlname
				+ "logView", grid = me.workObject || {};

		// if(!grid.hasLog) {
		// Belle.alert("本模块未设置查看日志功能！");
		// return;
		// }

		panel = new Belle_Common.ux.Log({
					masterGrid : grid,
					parentView : view,
					width : 1000,
					height : 500,
					renderUseFlag : me.renderUseFlag,
					renderYesNo : me.renderYesNo
				});

		Ext.create("Ext.window.Window", {
			border : false,
			layout : 'fit',
			items : [panel],
			title : me.view.title + "【日志查看】",
			modal : true, // 模态窗口
			//constrainHeader : true, // 约束在显示范围内
			closeAction : 'destroy',
			autoShow : true
				// 默认显示,
			});
	},

	/** 审批前接口 */
	beforeAudit : function(list) {

	},

	/** 审批功能 */
	onBtnAuditClick : function(btn) {

		var me = this, grid = me.getObj('mastergrid'), items = grid
				.getSelection();

		// 编辑状态不能审核
		if (grid.isUpdating || items.length < 1)
			return;

		var auditItems = [], itemdata, auditFlag, param = {};

		Ext.Array.each(items, function(item) {
					auditFlag = item.get(grid.auditField);
					if (auditFlag == 0) {
						itemdata = Belle.clone(item.data);
						itemdata[grid.auditField] = 1;
						auditItems.push(itemdata);
					}
				});

		if (auditItems.length == 0 && auditFlag == null) {
			console.info('当前网格没有审核字段(isLock),请检查数据表');
			Belle.alert('当前网格没有审核字段(isLock),请检查数据表');
			return;
		}

		if ((grid.subGrid || []).length > 0) {
			param.idFieldName = grid.primaryKey;
			param.masterJson = auditItems[0];
			param.operateType = 'updated';
		} else {
			param.customerName = grid.modelName.substr(grid.modelName
					.lastIndexOf('.')
					+ 1);
			param.updateList = auditItems;
		}

		if (me.beforeAudit(auditItems) === false)
			return;

		Belle.confirm('有' + auditItems.length + '条记录可审批,确认审批？', function(
						btnflag) {
					if (btnflag != 'yes')
						return;
					me.saveData({
								srcObj : grid,
								data : param,
								btn : btn,
								url : grid.auditUrl
							});
				});
	},

	onBtnBatchModifyClick : function(btn) {
		var me = this, grid = me.workObject, combodata = [], formpanel,gridColumns=[];

		if (grid.isReadOnly || !grid.isCanEdit || !me.canEdit()
				|| grid.store.getCount() == 0)
			return;
		//获取允许更改的数据列
		Ext.each(grid.columns, function(col) {
					if (col.getEditor && col.getEditor()
							&& col.dataIndex != grid.primaryKey) {
						
						var editor=col.config.editor;	
						
						if(editor===true||Ext.isEmpty(editor.xtype)){
							
							editor={
									xtype:col.config.xtype||"textfield"
									};
									
							if(editor.xtype==="gridcolumn"){
								editor.xtype="textfield";
							}
						}
						
						if(editor.xtype==="bllookupedit"||editor.xtype==="extcombox"){
							editor.xtype="extcombox";
							if(col.valuemember){
								editor.valuemember = col.config.valuemember;
							}
							if(col.displayvalue){
								editor.displayvalue = col.config.displayvalue;
							}
							if(col.displaymember){
								editor.displaymember = col.config.displaymember;
							}
							
							if(col.estore){
								editor.store = col.estore;
								if(col.estore.getProxy()&&col.estore.getProxy().url){
									editor.displayvalue = col.estore.getProxy().url;
								}
								
							}
							
							if(col.gstore){
								editor.store = col.gstore;
								if(col.estore.getProxy()&&col.estore.getProxy().url){
									editor.displayvalue = col.estore.getProxy().url;
								}
							}
						}
						else if(editor.xtype==="blgriddate"){
							editor.xtype="datefield";
							editor.format="Y-m-d";
						}
						
						if(col.config.renderer){
							
							if(typeof col.config.renderer==="function"){
								editor.renderer=col.config.renderer;
								
							}
							else{
								editor.renderer=me[col.config.renderer];
							}
							
						}
						
						
						editor.name=col.dataIndex;
						
						var obj = {
							'code' : col.dataIndex,
							'name' : col.text,
							'editor':editor
						};
						combodata.push(obj);
						
					}
		});
		//创建表单
		var form=me._createChangeGridValueForm(combodata);
		me._createChangeGridValueWin(form);
		
	},
	_createChangeGridValueForm:function(combodata){
		return {
				xtype : 'form',
				bodyPadding : '20 10 10 10',
				baseCls : 'x-plain',
				items : [{
							xtype:"grid",
							layout:"fit",
							itemId:"changeGridPanel",
							columns:[{
								text:"更改列",
								dataIndex:"name",
								width:160
							},
							{
								text:"更改值",
								dataIndex:"fieldValue",
								editor:{},
								width:100,
								renderer: function(val,met,rec,rowIndex,colIndex,store,view){
									if(Ext.isEmpty(val)) return '';
									
									var editor =rec.get("editor"),
									editorObj = met.column.getEditor();
									//处理日期格式显示值
									if(editor.xtype==="datefield"&&editor.format){
										val = Ext.Date.format(val,editor.format);
									}
									else if(editor.xtype==="extcombox"||editor.xtype==="combo"||editor.xtype==='combobox'){
										//处理下拉框显示值
										
										var store=editorObj.store,
										data = store.data.items;
										
										Ext.each(data,function(item){
											if(item.data[editorObj.valueField]===val){
												val= item.data[editorObj.displayField];
												return false;
											}
										});
										
									}
									
									if(editor.renderer){
										val=editor.renderer(val,met,rec,rowIndex,colIndex,store,view);
									}
									
									return val;
								}
							}],
							plugins : {
								ptype : 'cellediting',
								clicksToEdit : 1,
								listeners:{
									beforeedit:function(editor, context){
										
										var editorConfig = context.record.data.editor,
										field;
										//editorObj = editor.getEditor(context.record,context.column);
										
										field = Ext.widget(editorConfig);
										
										context.column.setEditor(field);
									}
								}
							},
							store:new Ext.data.Store({
								fields:['code', 'name','fieldValue'],
								data:combodata
							}),
							height:200,
							width:300
						},{
							xtype : 'radiogroup',
							columns : 2,
							vertical : false,
							items : [{
										boxLabel : '更改选中行',
										name : 'changeType',
										inputValue : '1',
										checked : true,
										width : 140
									}, {
										boxLabel : '更改所有行',
										name : 'changeType',
										inputValue : '2'
									}]
						}]
			};
	},
	_createChangeGridValueWin:function(formpanel){
		var me =this;
		Ext.widget('window', {
			modal:true,
			resizable : false,
			title : '批量更改',
			//constrainHeader : true,
			closeAction : 'destroy',
			autoShow : true,
			items : [formpanel],
			bbar : ['->', {
						xtype : 'button',
						text : '确认',
						glyph : Belle.Icon.btnSave,
						handler : me._saveChangeGridValue,
						scope : me
					}, {
						xtype : 'button',
						text : '退出',
						glyph : Belle.Icon.btnCancel,
						handler : function(b) {
							b.up('window').close();
						}
					}]
		});
	},
	_saveChangeGridValue:function(b){
		var me = this,
		win = b.up('window'), 
		form = win.down('form'), 
		val = form.getValues(), 
		records,
		grid = me.workObject,
		fieldGrid=form.down('[itemId="changeGridPanel"]');
		
		if (!form.isValid())
			return;
		
		if (!val.changeType) {
			return;
		} else if (val.changeType == 1) {
			records = grid.getSelection();
		} else {
			records = grid.store.data.items;
		}
		
		
		if (records.length < 1) {
			Belle.alert('没有需要更改的行');
			return;
		}
		
		Belle.confirm('确认批量更改此列的值？', function(flag) {
			if (flag != 'yes') return;
			//循环需要修改的列
			Ext.each(fieldGrid.store.data.items,function(fieldItem){
				//循环需要修改的行
				//不更改则不操作
				if(Ext.isEmpty(fieldItem.get("fieldValue")) )return true;
				
				Ext.each(records, function(record) {
							if (grid.auditField == null|| record.get(grid.auditField) == null) {
								record.set(fieldItem.get("code"), fieldItem.get("fieldValue"));
								me.changeGridRowValue(record,fieldItem.get("code"));
							} 
							else if (grid.auditField&& record.get(grid.auditField) == 0) {
								record.set(fieldItem.get("code"), fieldItem.get("fieldValue"));
								me.changeGridRowValue(record,fieldItem.get("code"));
							}
				});
			});
			
		});
	},
	//批量更改时促发事件 当前更改行数据 更改列
	changeGridRowValue:function(record,colFieldName){
		
	},
	// dwh 切换过滤按钮显示
	onFilterClose : function(btn) {
		var me = this, grid = me.workObject, objList = me.getObjList(), chGrid = objList.grid1, chIndex = 1, i = 0;

		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterStatus(false);
		}
		// 遍历从表
		while (chGrid) {
			i++;
			if (typeof(chGrid.setFilterStatus) == "function") {
				chGrid.setFilterStatus(false);
			}
			chGrid = objList["grid" + i];
		}

		me._setBtnFilterText(btn.up("[itemId='btnFilter']"));
	},
	// dwh 当前页
	onSetFilterLocal : function(btn) {
		var me = this, grid = me.workObject;
		objList = me.getObjList(), chGrid = objList.grid1, chIndex = 1, i = 0;
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterStatus(true);
		}

		if (typeof(grid.setFilterLocal) == "function") {
			grid.setFilterLocal(true);
		}

		// 遍历从表
		while (chGrid) {
			i++;
			if (typeof(chGrid.setFilterStatus) == "function") {
				chGrid.setFilterStatus(true);

			}
			if (typeof(chGrid.setFilterLocal) == "function") {
				chGrid.setFilterLocal(true);
			}

			chGrid = objList["grid" + i];
		}

		me._setBtnFilterText(btn.up("[itemId='btnFilter']"));
	},
	// 所有
	onSetFilterServer : function(btn) {
		var me = this, grid = me.workObject, objList = me.getObjList(), chGrid = objList.grid1, chIndex = 1
		i = 0;

		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterStatus(true);
		}

		if (typeof(grid.setFilterLocal) == "function") {
			grid.setFilterLocal(false);
		}

		// 遍历从表
		while (chGrid) {
			i++;
			if (typeof(chGrid.setFilterStatus) == "function") {
				chGrid.setFilterStatus(true);
			}
			if (typeof(chGrid.setFilterLocal) == "function") {
				chGrid.setFilterLocal(false);
			}

			chGrid = objList["grid" + i];
		}

		me._setBtnFilterText(btn.up("[itemId='btnFilter']"));
	},
	// 显示文本
	_setBtnFilterText : function(btn) {
		var me = this, grid = me.workObject;
		if (typeof(grid.setFilterStatus) != "function") {
			btn.setText("请加入filter插件");
			return;
		}

		if (grid.getFilterStatus()) {
			text = "过滤[" + (grid.isLocal ? "本页" : "所有") + "]";
		} else {
			text = "过滤";
		}
		btn.setText(text);
	},

	onBtnOther1 : function(btn) {

	},
	onBtnOther2 : function(btn) {

	},

	onBtnOther3 : function(btn) {

	},

	onBtnOther4 : function(btn) {

	},

	onBtnOther5 : function(btn) {

	},

	// endregion 工具栏按钮事件结束

	// region网格事件控制开始

	/* 选择行之前 */
	onGridBeforeSelect : function(sender, e, index, eOpts) {

	},

	/* 选择变化之后,重新绑定从表记录及更改按钮状态 */
	onGridSelectionChange : function(sender, e, index, eOpts) {
		this.gridSelectionChange(sender, e);
	},

	/* 编辑之前，控制主键不可更改 */
	onGridBeforeEdit : function(sender, e) {
		var me = this;
		if (me.gridCannotEditKeyField(e) === false)
			return false;
		if (me.sizeFieldBeforeEdit(me.getObj('mastergrid'), e) === false)
			return false;
	},

	/* 编辑之后 */
	onGridAfterEdit : function(editor, e) {
		var me = this;
		me.gridAfterEdit(editor,e);
		// me.setSizeColsOnEdit(me.getObj('mastergrid'), e);
		return me.checkKeyValue(e);
	},

	/* 数据更改之后 */
	onGridDataChanged : function(store) {
		this.gridDataChanged(store, 'mastergrid');
	},

	/* 加载数据之前 判断是否可以加载数据，当有在编辑时，不能加载数据 */
	onGridBeforeLoad : function(store, opts) {
		var me = this, searchform = this.getObj('commonsearch'),
		objs = me.getObjList(),btnSearch = objs.btnSearch;
		// 戴文辉
		if (store.sorters && store.sorters.items
				&& store.sorters.items.length > 0) {
			me.onBeforeSort(store, store.sorters.items)
		}

		if (searchform && !searchform.isValid())
			return false;
		if (me.gridIsCanLoad(store, 'mastergrid') === false)
			return false;
			
		if(btnSearch){
			if(btnSearch.isDisabled()) return false; 
			btnSearch.disable();
		}
	},

	// 加载数据之后(重新绑定从表)
	onGridLoaded : function(store, records, isOk, options) {
		var me = this, idx, idValue = me._idValue, grid = me
				.getObj('mastergrid'), keyfield = grid.primaryKey,
				objs = me.getObjList(),btnSearch = objs.btnSearch;

		if(btnSearch){
			btnSearch.enable();
		}
		
		me.setSizeColsOnLoad(grid, store, options);

		if (idValue) {
			idx = Ext.Array.findBy(records, function(item) {
						return item.get(keyfield) == idValue;
					});
			me._idValue = '';
		}
		if (idx) {
			grid.getSelectionModel().select(idx);
		} else {
			me.bindSubGrid(grid);
		}
	},

	onGridRowDblClick : function(view, record, tr, rowIndex, e) {

	},

	onGridCellDblClick : function(view, td, cellIndex, record, tr, rowIndex, e){
		this.gridCellDblClick(view, td, cellIndex, record, tr, rowIndex, e);
	},
	// 设置当前对象为活动对象
	onGirdActivate : function(view, e) {
		this.workObject = view.grid;
	},
	onPageKeyDown : function(view,e){
		//用户自定义键盘事件,用户自定义时返回true
		return false;
	},
	/* 初始化网格样式 */
	initGridCls : function(objlist) {
		var me = this, g, tmp = '';

		objlist = objlist || me.getObjList();

		for (g = 0; g < 10; g++) {
			if (g == 0) {
				tmp = 'mastergrid';
			} else {
				tmp = 'grid' + g;
			}
			if (!objlist[tmp])
				continue;
			me.gridHeadCls(objlist[tmp]);
		}
	},

	// endregion网格事件控制结束

	/** 初始化按钮 */
	initToolbar : function(objList) {
		var me = this, isShowFilter = true;

		objList.btnAdd.setVisible(me.canAdd());
		objList.btnPrint.setVisible(me.canPrint());
		objList.btnExport.setVisible(me.canExport());
		objList.btnDelete.setVisible(me.canDelete());
		objList.btnAudit.setVisible(me.canAudit());
		objList.btnReset.setVisible(!!objList.commonsearch);
		objList.btnFilter.setVisible(isShowFilter);

		objList.btnOther1.setVisible(me.canOther1());
		objList.btnOther2.setVisible(me.canOther2());
		objList.btnOther3.setVisible(me.canOther3());
		objList.btnOther4.setVisible(me.canOther4());
		objList.btnOther5.setVisible(me.canOther5());

		// objList.btnViewLog.setDisabled(!objList.mastergrid.hasLog);
		if (!me.canAdd() && !me.canEdit() && !me.canDelete()) {
			objList.btnUndo.setVisible(false);
			objList.btnCancel.setVisible(false);
			objList.btnSave.setVisible(false);
			objList.btnEdit.setVisible(false);
		}

		me.setToolspVisible(objList);

		if (objList.commonsearch) {
			var list = objList.commonsearch
					.query('textfield,numberfield,datefield,combo');
			Ext.each(list, function(txt) {
						txt.on('specialkey', me.keyToSearch, me);
				////戴文辉 已在override实现
						//txt.on('afterrender', function() {
						//			if (txt.labelEl) {
						//				txt.labelEl.on('dblclick', function() {
						//							txt.setValue('');
						//						});
						//			}
						//		});
					});
		}
		// 关闭网格过滤
		me.workObject.setFilterStatus(false);

		// 初始化过滤文本
		me._setBtnFilterText(objList.btnFilter);

		me.changePageEdit(objList);
	},

	setToolspVisible:function(objList){
		objList.commontoolsp1.setHidden(objList.btnFilter.isHidden());
		objList.commontoolsp2.setHidden(objList.btnAdd.isHidden()
		&& objList.btnDelete.isHidden());
		objList.commontoolsp3.setHidden(objList.btnCancel.isHidden()
		&& objList.btnUndo.isHidden());
		objList.commontoolsp4.setHidden(objList.btnSave.isHidden());
		objList.commontoolsp5.setHidden(objList.btnExport.isHidden());
		objList.commontoolsp6.setHidden(objList.btnPrint.isHidden());
	},

	/** 设置label样式，且双击清除数据 */
	setLabelCls : function() {
		var list = this.view.query('textfield,numberfield,datefield,combo');
		Ext.each(list, function(txt) {
			if (txt.fieldLabel) {
				txt.on('afterrender', function() {
							if (txt.labelEl) {
								var form = this.up("form");

								if (txt.allowBlank === false) {
									txt.labelEl.addCls('notnull-field');
								}
								// 判断是否为单据
								else if (form
										&& form.reference === "commonbill"
										&& txt.canInput != false) {

									txt.labelEl.addCls('cannull-field');
								} else if (!form
										&& txt.canInput != false
										&& txt.readOnly != true) {
									txt.labelEl.addCls('cannull-field');
								}
							}
						});
			}
		});
	}
});