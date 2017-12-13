Ext.define('MyApp.view.base.Base', {
    extend : 'Ext.container.Container',  
    requires: ['MyApp.view.base.BaseController',
               'MyApp.view.base.BaseModel',
    ],  //动态加载所需类
    
	controller : 'base',
	viewModel : {
		type : 'base'
	},
	
	referenceHolder : true,

	moduleId : '',
	moduleName : '',
	moduleUrl : '',
	isReadOnly : false,
	pageSize : 25,
	isAutoLoad : false,

	editStatus : 0,
	auditStatus : 20,

	userCode : '',
	userName : '',
	pageType : '',

	moduleRight : 511,
	userRight : 511,
	
	canCustomLayout:true,
	layout: 'border',
	canQueryCondition:false,		//查询面板是否开启query参数
	queryDataIndex:"",		//基类默认字段别名
	// 自定义布局 默认为系统自动布局
	customLayout: false,
	gridSaveGridLayout : true,
	// 其它对象清单，用于扩充
	otherItems: null,

	// form 中默认的列宽
	fieldWidth: '100%',
	labelWidth: 80,
	labelAlign: 'right',
	gridDbTableName:'',
	gridBuffer:true,

	// region定义工具条开始
	toolbarRegion: 'north',
	//canQueryCodition:true,
	toolbar: {
		xtype: 'toolbar',
		reference: 'commontoolbar',
		region: '',
		plugins: new Ext.ux.ToolbarKeyMap(),
		items: [{
			text: '查询',
			itemId: 'btnSearch',
			reference: 'btnSearch',
			handler: 'onBtnSearchClick',
			glyph: Belle.Icon.btnSearch,
			tooltip:"查询(Ctrl+Enter)"
		}, {
			text: '重置',
			itemId: 'btnReset',
			reference: 'btnReset',
			handler: 'onBtnResetClick',
			glyph: Belle.Icon.btnReset,
			tooltip:"重置(Ctrl+R)"

		}, {
			text: '过滤',
			icon: './static/js/extjs/packages/ext-theme-classic/images/grid/filters/find.png',
			itemId: 'btnFilter',
			reference: 'btnFilter',
			xtype: 'splitbutton',
			menu: [{
				text: '本页',
				itemId: 'btnFilterLocal',
				reference: 'btnFilterLocal',
				handler: 'onSetFilterLocal'
			}, {
				text: '所有',
				itemId: 'btnServer',
				reference: 'btnFilterServer',
				handler: 'onSetFilterServer'
			}, {
				text: '关闭',
				itemId: 'btnFilterClose',
				reference: 'btnFilterClose',
				handler: 'onFilterClose'
			}],
			handler: function (e) {
				e.maybeShowMenu();
			}
		}, {
			xtype: 'tbseparator',
			reference: 'commontoolsp1'
		}, {
			xtype: 'splitbutton',
			text: '新增',
			itemId: 'btnAdd',
			reference: 'btnAdd',
			handler: 'onBtnAddClick',
			glyph: Belle.Icon.btnAdd,
			menu: [{
				text: '批量导入',
				itemId: 'btnImport',
				reference: 'btnImport',
				handler: 'onBtnImportClick',
				glyph: Belle.Icon.btnImport
			}, {
				text: '复制记录',
				itemId: 'btnCopy',
				reference: 'btnCopy',
				handler: 'onBtnCopyClick',
				glyph: Belle.Icon.btnCopy
			}],
			tooltip:"新增(Ctrl+Insert)"
		}, {
			text: '编辑',
			itemId: 'btnEdit',
			reference: 'btnEdit',
			handler: 'onBtnEditClick',
			glyph: Belle.Icon.btnEdit,
			tooltip:"编辑(Ctrl+E)"
		}, {
			text: '删除',
			itemId: 'btnDelete',
			reference: 'btnDelete',
			handler: 'onBtnDeleteClick',
			glyph: Belle.Icon.btnDelete,
			disabled: true,
			tooltip:"删除(Ctrl+Delete)"
		}, {
			xtype: 'tbseparator',
			reference: 'commontoolsp2'
		}, {
			text: '还原',
			itemId: 'btnUndo',
			reference: 'btnUndo',
			handler: 'onBtnUndoClick',
			glyph: Belle.Icon.btnUndo,
			disabled: true,
			tooltip:"还原(Ctrl+U)"
		}, {
			text: '取消',
			itemId: 'btnCancel',
			reference: 'btnCancel',
			handler: 'onBtnCancelClick',
			glyph: Belle.Icon.btnCancel,
			disabled: true,
			tooltip:"取消(alt+C)"
		}, {
			xtype: 'tbseparator',
			reference: 'commontoolsp3'
		}, {
			text: '保存',
			itemId: 'btnSave',
			reference: 'btnSave',
			handler: 'onBtnSaveClick',
			glyph: Belle.Icon.btnSave,
			disabled: true,
			tooltip:"保存(Ctrl+S)"
		}, {
			text: '审核',
			itemId: 'btnAudit',
			reference: 'btnAudit',
			handler: 'onBtnAuditClick',
			glyph: Belle.Icon.btnAudit,
			disabled: true
		}, {
			xtype: 'tbseparator',
			reference: 'commontoolsp4'
		}, {
			//xtype: 'splitbutton',
			text: '导出',
			itemId: 'btnExport',
			reference: 'btnExport',
			handler: 'onBtnExportPageClick',
			glyph: Belle.Icon.btnExport//,
//			menu: [{
//				text: '导出当前页',
//				itemId: 'btnExportPage',
//				reference: 'btnExportPage',
//				handler: 'onBtnExportPageClick'
//			}, {
//				text: '导出全部',
//				itemId: 'btnExportAll',
//				reference: 'btnExportAll',
//				handler: 'onBtnExportAllClick'
//			}]
		}, {
			xtype: 'tbseparator',
			reference: 'commontoolsp5'
		}, {
			text: '打印',
			itemId: 'btnPrint',
			reference: 'btnPrint',
			handler: 'onBtnPrintClick',
			glyph: Belle.Icon.btnPrint,
			disabled: true
		}, {
			xtype: 'tbseparator',
			reference: 'commontoolsp6'
		}, {
			text: '更多',
			itemId: 'btnOther',
			reference: 'btnOther',
			xtype: 'splitbutton',
			glyph: Belle.Icon.btnOther,
			menu: [{
				text: '查看日志',
				itemId: 'btnViewLog',
				reference: 'btnViewLog',
				handler: 'onBtnViewLog'
			}, {
				text: '批量更改列值(M)',
				itemId: 'btnBatchModify',
				reference: 'btnBatchModify',
				handler: 'onBtnBatchModifyClick',
				keyBinding: {
					key: 'm',
					ctrl: true
				}
			}],
			handler: function (e) {
				e.maybeShowMenu();
			}
		}, {
			text: '功能1',
			itemId: 'btnOther1',
			reference: 'btnOther1',
			handler: 'onBtnOther1',
			hidden: true
		}, {
			text: '功能2',
			itemId: 'btnOther2',
			reference: 'btnOther2',
			handler: 'onBtnOther2',
			hidden: true
		}, {
			text: '功能3',
			itemId: 'btnOther3',
			reference: 'btnOther3',
			handler: 'onBtnOther3',
			hidden: true
		}, {
			text: '功能4',
			itemId: 'btnOther4',
			reference: 'btnOther4',
			handler: 'onBtnOther4',
			hidden: true
		}, {
			text: '功能5',
			itemId: 'btnOther5',
			reference: 'btnOther5',
			handler: 'onBtnOther5',
			hidden: true
		}]
	},

	// endregion定义工具条结束

	billStatusUrl: '',
	billStatusKey: '',
	billStatusData: null,
	billNoText: '单据编号',
	billStatusText: '单据状态',
	gridHasOrderNo: true,
	gridTableNames: "",
	// region定义通用查询面板开始

	searchItems: [],
	searchColumn: 4,
	searchNotNullField: '', // 多个查询条件中必须输入一个条件的验证
	searchPanel: {
		xtype: 'form',
		region: 'north',
		reference: 'commonsearch',
		collapsible: true,
		collapseMode: 'undefined',
		title: '查询面板',
		layout: {
			type: 'table'
		},
		header: {
			height: 20,
			padding: 0
		},
		defaults: {},
		defaultType: 'textfield',
		bodyPadding: 3,
		autoScroll: true,
		items: []
	},

	// endregion定义通用查询面板结束

	// region 定义网格属性开始
	gridIsCandNewRow: true, // 控制网格是否允许回车加行
	gridModel: '',
	gridColumns: [],
	gridAuditField: 'isLock',
	gridEditModel: 'cell', // row 行编辑，cell 单元格编辑，window 在弹出框中编辑, none不在网络中编辑
	gridCanDrag: false,
	gridCanEdit: true,
	gridCanAdd: true,
	gridCanDelete: true,
	gridReadOnly: false,
	gridPrimaryKey: '',
	gridUnionKey: '',
	gridRegion: 'center',
	gridIsMaster: false,
	gridSupGrid: '',
	gridSubGrid: [],

	gridAuditUrl: '',

	// dwh 控制当前网格过滤状态
	gridFilterStatus: true, // 是否启用过滤
	gridFilterType: true, // 类型 true 本页, false 所有
	gridPlugins: [],
	// "SINGLE"/"SIMPLE"/"MULTI"/"checkboxmodel"
	gridSelModel: 'checkboxmodel',
	gridCanDeSelect: true,
	isGridSummary: false, // 是否启用网格统计
	isServerGridSummary:false,
	_sumfeature: {
		ftype: 'summary',
		dock:'bottom'
	},
	_serversumfeature:{
		ftype: 'serversummary',
		dock:'bottom'
	},
	gridLoadUrl: '',
	gridSaveUrl: '',
	gridExportUrl: '',
	gridImportUrl: '',
	gridTitle: '',

	// 物料尺码
	gridMSizeIdx: -1,
	gridMSizeUrl: Belle.sdsPath
	+ 'bl_co_dtl/listvo.json?selectVoName=SelectListByVoBlCoMaterial',
	gridMSizeQtyField: 'sizeQty',
	gridMSizeFillField: 'materialName,sizeTypeNo',
	gridSizeInputType: 'number',
	gridMConvertSize: 0,
	gridMSummary:false,

	// 固定列开关设置
	gridHasMark: true,
	gridHasCreator: true,
	gridHasModifier: true,
	gridHasAuditor: false,

	grid: {
		xtype: 'grid',
		reference: 'mastergrid',
		columnLines: true,
		columns: [],
		region: '',
		// layout: 'box',
		bbar: {
			xtype: 'pagingtoolbar',
			displayInfo: true
		},
		viewConfig: {
			enableTextSelection: true
		},
		// enableLocking:true, //启用初始化冻结列面板
		selModel: {},
		features: []
	},

	// 导入服务传输字段定义
	gridcolNames: '',
	gridmustArray: '',
	gridisValidateAll: '',
	gridmainKey: '',
	gridvalidationConditions: '',

	// 导出服务
	gridfileName: 'grid.xls',
	gridfileType: '.xls',
	gridexportColumns: '',

	// endregion定义网格属性结束

	// region 定义编辑面板结束 当编辑模式改为 'window', editPanelColumn 默认等于 2
	gridEditColumn: 0,
	gridEditItems: [],
	gridEditHeight: -1,
	gridEditLayout: 'table',

	editPanel: {
		xtype: 'form',
		region: 'south',
		split: true,
		reference: 'commonedit',
		border: false,
		autoScroll: true,
		defaults: {},
		layout: {},
		bodyPadding: 3,
		items: []
	},

	pageType: 'simple',
	// endregion定义编辑面板结束

	/** 查看日志属性* */
	gridLogWinTitle: '', // 日志弹窗标题
	gridLogLoadUrl: '', // 日志网格加载url
	gridModelText: '',
	gridHasPage:true,
	gridHasIndex:true,
	initComponent: function () {
		var me = this,queryDataIndex = me.queryDataIndex?me.queryDataIndex+".":"";
		if (!me.gridModel) {
			Belle.alert('必须此定网格对应的数据模型gridModel属性');
			me.callParent();
			return;
		}

		me.queryDataIndex=queryDataIndex;
		
		me.gridModelText = me.gridModel.substr(me.gridModel.lastIndexOf('.')
		+ 1)

		me.toolbar.region = me.toolbarRegion;

		// region处理grid开始
		me.grid.region = me.gridRegion;

		if (me.gridReadOnly) {
			me.gridCanAdd = false;
			me.gridCanDelete = false;
			me.gridCanEdit = false;
		} else if (!me.gridCanAdd && !me.gridCanDelete && !me.gridCanEdit) {
			me.gridReadOnly = true;
		}
		me.pageSize =  me.gridHasPage? me.pageSize:20000;

		// 定义数据源store
		var store = Ext.create('MyApp.store.BaseStore', {
			model: me.gridModel,
			autoLoad: me.isAutoLoad,
			pageSize: me.pageSize,
			module: me,
			proxy: {
				url: me.gridLoadUrl
			}
		});

		me.grid.store = store;

		me.grid.bbar={
			xtype: 'pagingtoolbar',
			displayInfo: true
		};
		me.grid.bbar.store = store;

		me.grid.bbar.plugins = Ext.create('Ext.ux.ComboPageSize', {
				defaultSize: me.pageSize
		});

		if(me.gridHasPage===false){
			delete me.grid.bbar;
		}

		// 处理固定列
		var gCols = mt.clone(me.gridColumns);

		if (me.pageType == 'billList') {
			gCols = [{
				belleFilter:{
					xtype:"combo",
					filterType:"=",
					emptyText:"请选择",
					valueField:"itemCode",
					displayField:"itemName",
					//editable:false,
					store:new Ext.data.Store(),
					dataIndex:queryDataIndex+"billStatus"
				},
				canQueryCondition:true,
				text: me.billStatusText,
				dataIndex: 'billStatus',
				width: 60,
				renderer: function (val,mat,rec) {
					if (Ext.isEmpty(val))
						return val;
					var ddl = me.lookupReference('ddlBillStatus');
					if (!ddl)
						return val;
						
					var obj=mat.column.bellefilterObj;
					//dwh
					if(obj.store.getCount()<=0){
						
						
						var fields = [],
							dfields=ddl.store.proxy.reader.getFields();

							obj.store.fields=dfields;
							obj.store.model=ddl.store.getModel();
							
							obj.store.loadRecords(ddl.store.getData().items);
					}
						
						
					var idx = ddl.store.findExact(ddl.valueField, val
						.toString());
					return idx > -1 ? ddl.store.getAt(idx)
						.get(ddl.displayField) : val;
				}
			}, {
				text: me.billNoText,
				dataIndex: 'billNo',
				width: 155,
				belleFilter:{
					dataIndex:queryDataIndex+"billNo"
				},
				canQueryCondition:true
			}].concat(gCols);
		} else if (me.pageType == 'billDetail' && me.gridHasOrderNo) {
			gCols = [{
				text: '序号',
				dataIndex: 'orderNo',
				width: 50,
				belleFilter:{
					xtype:"numberfield",
					filterType:">",
					minValue:0,
					dataIndex:queryDataIndex+"orderNo"
				},
				
				canQueryCondition:true
			}].concat(gCols);
		}

		if (me.gridHasMark) {
			var mCols = {
				text: '备注',
				dataIndex: 'remarks',
				belleFilter:{
					dataIndex:queryDataIndex+"remarks"
				},
				canQueryCondition:true
			};
			if (!me.gridReadOnly)
				mCols.editor = true;
			gCols.push(mCols)
		}

		if (me.gridHasCreator) {
			gCols = gCols.concat([{
				text: '创建人',
				dataIndex: 'creator',
				width: 80,
				belleFilter:{
					dataIndex:queryDataIndex+"creator"
				},
				canQueryCondition:true
			}, {
				text: '创建时间',
				dataIndex: 'createTime',
				width: 140,
				belleFilter:{
					xtype:"datefield",
					filterType:">",
					dataIndex:queryDataIndex+"createTime"
				},
				canQueryCondition:true
			}]);
		}

		if (me.gridHasModifier) {
			gCols = gCols.concat([{
				text: '修改人',
				dataIndex: 'modifier',
				width: 80,
				belleFilter:{
					dataIndex:queryDataIndex+"modifier"
				},
				canQueryCondition:true
			}, {
				text: '修改时间',
				dataIndex: 'modifyTime',
				width: 140,
				belleFilter:{
					xtype:"datefield",
					filterType:">",
					dataIndex:queryDataIndex+"modifyTime"
				},
				canQueryCondition:true
			}]);
		}

		if (me.gridHasAuditor) {
			gCols = gCols.concat([{
				dataIndex: 'auditor',
				text: '审核人',
				width: 80,
				belleFilter:{
					dataIndex:queryDataIndex+"auditor"
				},
				canQueryCondition:true
			}, {
				dataIndex: 'auditTime',
				text: '审核时间',
				width: 140,
				belleFilter:{
					xtype:"datefield",
					filterType:">",
					dataIndex:queryDataIndex+"auditTime"
				},
				canQueryCondition:true
			}]);
		}

		// 处理尺码预留列
		if (me.gridMSizeIdx > -1) {
			Ext.Array.insert(gCols, me.gridMSizeIdx, [{
				text: '物料尺码信息',
				mSizeCol: true,
				width:100
			}]);
		}

		if(me.gridHasIndex && Ext.Array.findBy(gCols,function(item) {return item.xtype=='rownumberer'})==null){ //添加序号
			Ext.Array.insert(gCols,0,  [{xtype:'rownumberer',width: 40,resizable:true}]);
		}

		// 单表审核的服务跟保存服务是同一个，即只要更入状态即可。
		if (Ext.isEmpty(me.gridAuditUrl)) {
			me.gridAuditUrl = me.gridSaveUrl;
		}

		
		
		Ext.apply(me.grid, {
			reserveScrollbar:true,
			bufferedRenderer:me.gridBuffer, //设置bufferedRenderer:false 后渲染比较慢，差一点的电脑会卡死
			isCandNewRow: me.gridIsCandNewRow,
			editModel: me.gridEditModel,
			isCanAdd: me.gridCanAdd,
			isCanEdit: me.gridCanEdit,
			isCanDelete: me.gridCanDelete,
			isMaster: me.gridIsMaster,
			isReadOnly: me.gridReadOnly,
			auditField: me.gridAuditField,
			batchUrl: me.gridSaveUrl,
			exportUrl: me.gridExportUrl,
			importUrl: me.gridImportUrl,
			primaryKey: me.gridPrimaryKey,
			unionKey: me.gridUnionKey,
			//columns: gCols,
			//vcolumn: gCols,
			modelName: me.gridModel,
			dbTableName:me.gridDbTableName,
			modelText: me.gridModelText,
			supGrid: me.gridSupGrid,
			subGrid: me.gridSubGrid,
			hasOrderNo: me.gridHasOrderNo,
			mSizeIdx: me.gridMSizeIdx,
			mSizeUrl: me.gridMSizeUrl,
			mSummary:me.gridMSummary,
			mSizeQtyField: me.gridMSizeQtyField,
			mSizeFillFields: me.gridMSizeFillField,
			sizeInputType: me.gridSizeInputType,
			convertToSize: me.gridMConvertSize,
			customerObjs: me.gridTableNames,
			canSaveGridLayout:me.gridSaveGridLayout,
			// 导入服务传输字段定义
			colNames: me.gridcolNames,
			mustArray: me.gridmustArray,
			isValidateAll: me.gridisValidateAll,
			mainKey: me.gridmainKey,
			validationConditions: me.gridvalidationConditions,

			auditUrl: me.gridAuditUrl,

			// 过滤
			isFilter: me.gridFilterStatus,
			isLocal: me.gridFilterType,

			// 导出服务
			fileName: me.gridfileName,
			fileType: me.gridfileType,
			exportColumns: me.gridexportColumns,

			/** 查看日志属性* */
			logWinTitle: me.gridLogWinTitle,
			logLoadUrl: me.gridLogLoadUrl
		});

		if(me.exportMaxRows){
			me.grid.exportMaxRows = me.exportMaxRows;
		}

		if (me.gridTitle) {
			me.grid.title = me.gridTitle;
		} else {
			delete me.grid.title;
		}

		if (me.gridEditModel === "cell") {
			me.grid.plugins = [{
				ptype: 'cellediting',
				clicksToEdit: 1
			}];
		} else if (me.gridEditModel === 'row') {
			me.grid.plugins = [{
				ptype: 'rowediting',
				clicksToEdit: 2
			}];
		} else {
			delete me.grid.plugins;
		}

		if (me.gridCanDrag) {
			me.grid.viewConfig.plugins = [{
				ptype: 'gridviewdragdrop',
				ddGroup: 'dd_commongrid',
				enableDrop: true
			}];
		}
		if (me.gridSelModel == 'checkboxmodel') {
			me.grid.selModel.selType = 'checkboxmodel';
			me.grid.selModel.mode = 'MULTI';
			me.grid.selModel.allowDeselect = true;
		} else {
			me.grid.selModel.selType = 'rowmodel';
			me.grid.selModel.mode = me.gridSelModel;
			me.grid.selModel.allowDeselect = me.gridCanDeSelect;
		}

		if(me.isGridSummary == true){
			for(var i= 0,len=gCols.length;i<len;i++){
				if(gCols[i].summaryType=='sum' && gCols[i].isServerSummary!==false){
					gCols[i].isServerSummary = true;
					me.isServerGridSummary=true;
				}
			}
		}

		//加入后端总计行
		if (me.isServerGridSummary == true) {
			if (me.grid.features.indexOf(me._serversumfeature) < 0) {
				me.grid.features.push(me._serversumfeature);
			}

		} else {
			if (!Ext.isEmpty(me.grid.features)) {
				var fIndex = me.grid.features.indexOf(me._serversumfeature);
				if (fIndex >= 0) {
					me.grid.features.splice(fIndex, 1);
				}
			}

		}

		//加入合计
		if (me.isGridSummary == true) {
			if (me.grid.features.indexOf(me._sumfeature) < 0) {
				me.grid.features.push(me._sumfeature);
			}
		} else {
			if (!Ext.isEmpty(me.grid.features)) {
				var fIndex = me.grid.features.indexOf(me._sumfeature);
				if (fIndex >= 0) {
					me.grid.features.splice(fIndex, 1);
				}
			}

		}


		// endregion处理grid结束

		// region 处理查询面板开始

		var sitem = mt.clone(me.searchItems);
		
		if (me.pageType == 'billList') {

			me.billStatusUrl = me.billStatusUrl || '';
			if (!me.billStatusUrl && me.billStatusKey) {
				me.billStatusUrl = Belle.mdmPath
				+ 'bas_dict/getbasdictcombo.json?dictCode='
				+ me.billStatusKey;
			}
			if (Ext.isEmpty(me.billStatusUrl) && Ext.isEmpty(me.billStatusData)) {
				me.billStatusUrl = Belle.mdmPath
				+ 'bas_dict/getbasdictcombo.json?dictCode=bill_status';
			}
			var tday = new Date();

			sitem = [{
				fieldLabel: me.billStatusText,
				name: 'billStatus',
				reference: 'ddlBillStatus',
				xtype: 'ddlfield',
				editable: false,
				valueField: 'itemCode',
				displayField: 'itemName',
				async: true,
				localData: me.billStatusData,
				url: me.billStatusUrl
			}, {
				fieldLabel: me.billNoText,
				name: 'billNo',
				dataIndex:queryDataIndex+"billNo"
			}, {
				fieldLabel: '创建人',
				name: 'creator',
				dataIndex:queryDataIndex+"creator"
			}, {
				fieldLabel: '审核人',
				name: 'auditor',
				dataIndex:queryDataIndex+"auditor"
			}, {
				fieldLabel: '创建时间',
				xtype: 'datefield',
				name: 'createTime1',
				allowBlank: false,
				value: Ext.Date.format(Ext.Date.add(tday, 'd', -31),
					'Y-m-d')
			}, {
				fieldLabel: '至',
				xtype: 'datefield',
				name: 'createTime2',
				allowBlank: false,
				value: Ext.Date.format(tday, 'Y-m-d'),
				vtype: 'compareTo',
				compareTo: 'createTime1',
				compareType: '>=',
				compareError: '创建时间开始不能大于结束'
			}, {
				fieldLabel: '审核时间',
				xtype: 'datefield',
				name: 'auditTime1'
			}, {
				fieldLabel: '至',
				xtype: 'datefield',
				name: 'auditTime2',
				vtype: 'compareTo',
				compareTo: 'auditTime1',
				compareType: '>=',
				compareError: '审核时间开始不能大于结束'
			}].concat(sitem);
		}

		
		if(me.canQueryCondition){
			Ext.each(sitem,function(item){
				if((Ext.isEmpty(item.xtype) || item.xtype=="textfield")&&item.canQueryCondition!=false){
					item.xtype="bellefilter";
				}
			});
		}
		
		me.searchPanel.layout.columns = me.searchColumn;
		me.searchPanel.defaults.width = me.fieldWidth;
		me.searchPanel.defaults.labelAlign = me.labelAlign;
		me.searchPanel.defaults.labelWidth = me.labelWidth;
		if (sitem.length > 3) {
			me.searchPanel.layout.type = 'table';
		} else if (sitem.length > 0) {
			me.searchPanel.layout.type = 'column';
			Ext.each(sitem, function (item) {
				item.columnWidth = item.columnWidth || "0.25";
			})
		}
		me.searchPanel.items = sitem;
		// endregion 处理查询面板结束

		// region 处理编辑面板开始
		me.editPanel.layout.type = me.gridEditLayout;

		me.editPanel.height = me.gridEditHeight == -1 ? me.controller
			.getBodyHeight(0.4) : me.gridEditHeight;

		if ((me.gridEditColumn > 0 && me.gridEditItems.length == 0)
			|| me.gridEditModel == "window") {
			me.editPanel.items = me.controller.getFormItems(me.grid.columns,
				'gridRow');
			if (me.gridEditColumn == 0) {
				me.gridEditColumn = 4;
			}
		} else {
			me.editPanel.items = me.gridEditItems;
		}
		if (me.gridEditLayout == 'table') {
			me.editPanel.layout.columns = me.gridEditColumn;
			me.editPanel.defaults.width = me.fieldWidth;
			me.editPanel.defaults.labelAlign = me.labelAlign;
			me.editPanel.defaults.labelWidth = me.labelWidth;
		}

		// endregion 处理编辑面板结束

		// region系统自动布局开始
		if (!me.customLayout) {
			me.items = [me.toolbar];
			if (me.searchPanel.items.length > 0) {
				me.items.push(me.searchPanel);
			}
			me.items.push(me.grid);
			if (me.editPanel.items.length > 0 && me.gridEditModel != 'window') {
				me.items.push(me.editPanel);
			}
			me.otherItems = me.otherItems || [];
			me.items = me.items.concat(me.otherItems);
		}
		// endregion系统自动布局结束

		try {
			//开启用户自定义布局
			if(me.userGridCols && !Ext.isEmpty(me.userGridCols)){
				
				var gridCols;
				Ext.each(me.userGridCols,function(item){
					if(item.gridId==="mastergrid"){
						gridCols=Ext.decode(item.layoutJson);
						return false;
					}
				});
				if(gridCols){
					me._initGridColumns(me.grid,gridCols,gCols);
				}
				else{
					me.grid.columns=gCols;
					me.grid.vcolumn=gCols;
				}
			}
			else{
				me.grid.columns=gCols;
				me.grid.vcolumn=gCols;
				
			}	
			
			me.callParent();
			
		} catch (e) {
			Belle.alert(e);
		}
	},
	_applyGridCulumns:function(gCols,userCols){
		var me = this ,gridColumns=[],rownumbererCel;
		
//		if(!Ext.isEmpty(gCols) && gCols[0] && gCols[0].xtype=="rownumberer"){
//			rownumbererCel=gCols[0];
//			gCols.splice(0,1);
//		}
		
		for(var uIndex=0;uIndex<userCols.length;uIndex++){
			
			var uCol=userCols[uIndex];
			for(var gIndex=0;gIndex<gCols.length;gIndex++){
				
				var gCol = gCols[gIndex];
				
				if(gCol.columns && gCol.columns.length>0){
					me._applyGridCulumns(gCol.columns,userCols);
				}
				else if(gCol.dataIndex && gCol.text && gCol.dataIndex==uCol.dataIndex && gCol.text==uCol.text){
					//附加用户自定义属性
					Ext.apply(gCol,uCol);
					if(uIndex>gIndex){
						//移动到新位置
						gCols.splice(uIndex,0,gCol);
						//删除历史位置
						gCols.splice(gIndex,1);
					}
					else{
						gCols.splice(gIndex,1);
						gCols.splice(uIndex,0,gCol);
					}
				}
				else if(!gCol.dataIndex && gCol.text && gCol.text==uCol.text){
					//附加用户自定义属性
					Ext.apply(gCol,uCol);
					if(uIndex>gIndex){
						//移动到新位置
						gCols.splice(uIndex,0,gCol);
						//删除历史位置
						gCols.splice(gIndex,1);
					}
					else{
						gCols.splice(gIndex,1);
						gCols.splice(uIndex,0,gCol);
					}
				}
				else if(gCol.dataIndex && !gCol.text && gCol.dataIndex==uCol.dataIndex){
					//附加用户自定义属性
					Ext.apply(gCol,uCol);
					if(uIndex>gIndex){
						//移动到新位置
						gCols.splice(uIndex,0,gCol);
						//删除历史位置
						gCols.splice(gIndex,1);
					}
					else{
						gCols.splice(gIndex,1);
						gCols.splice(uIndex,0,gCol);
					}
				}
			}
		}
		
		//遍历自定义列
//		Ext.each(userCols,function(uCol,uIndex){
//			if(!uCol) return true;
//			//遍历所有列
//			Ext.each(gCols,function(gCol,gIndex){
//				
//				if(!gCol) return true;
//				
//				if(gCol.columns && gCol.columns.length>0){
//					me._applyGridCulumns(gCol.columns,userCols);
//				}
//				else if((gCol.dataIndex==uCol.dataIndex) || (gCol.text==uCol.text) || (gCol.header==uCol.text)){
//					Ext.apply(gCol,uCol);
//
//					if(uIndex>gIndex){
//						//移动到新位置
//						gCols.splice(uIndex,0,gCol);
//						//删除历史位置
//						gCols.splice(gIndex,1);
//					}
//					else{
//						gCols.splice(gIndex,1);
//						gCols.splice(uIndex,0,gCol);
//					}
//				}
//			});
//		});
//		gCols.splice(0,0,rownumbererCel);
		return gCols;
		
	},
	_initGridColumns:function (grid,userCols,gCols){
		var me = this;
		if(grid.mSizeIdx==-1) {
			gCols=me._applyGridCulumns(gCols, userCols);
		}
		grid.columns=gCols;
		grid.vcolumn=gCols;
	}
});