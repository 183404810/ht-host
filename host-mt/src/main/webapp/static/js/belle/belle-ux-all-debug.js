/**
 * Description: 数据精灵--窗口选择器 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: daiwenhui Createdate:
 * 2015/6/4
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */
Ext.define('Belle_Common.ux.SearchWin', {
	extend : 'Ext.window.Window',
	xtype : 'selectionwin',
	
	canQueryCondition:false,
	queryDataIndex:"a",
	// 指定弹出窗中的 grid 列
	gridColumns : null,
	searchNotNullField:'',
	// 请求地址
	url : "",
	// 指定弹出窗中的查询条件
	searchItems : null,
	searchColumn : 4,
	fieldWidth : '100%',
	title : '窗口选择器',
	width : 700,
	height : 500,
	isAutoLoad : true,
	isMultiSelect : false,
	// 默认过滤条件 例如：[{property: "id",value: "100",operator:"10"}]
	filters : [],
	otherParams : {},// 请求参数
	modal : true, // 模态窗口
	//constrainHeader : true, // 约束在显示范围内
	layout : 'border',
	closeAction : 'destroy',
	autoShow : true, // 默认显示
	formAllowBlank : true, // 是否允许表单为空
	isQueryParams : true, // 表单查询条件传参类型,默认传给queryCondition
	initComponent : function() {
		var me = this, items = [];

		if(window.innerHeight && window.innerHeight < me.height) {
			me.height = window.innerHeight;
		}

		if(window.innerWidth && window.innerWidth < me.width) {
			me.width = window.innerWidth;
		}

		// 创建查询面板
		if (!Ext.isEmpty(me.searchColumn)) {
			items.push(me.createForm());
		}
		// 创建网格
		if (Ext.isEmpty(me.gridColumns)) {
			Belle.alert("网格未指定任何列！");
			return;
		}

		items.push(me.createGrid());

		me.items = items;
		// 加入工具条
		me.dockedItems = [{
			reference : 'winbtnToolbar',
			xtype : 'toolbar',
			dock : 'top',
			items : [{
						xtype : 'button',
						text : '查询',
						width : 60,
						margin : '0 0 0 5',
				        reference:'winsearchbtn',
						glyph : Belle.Icon.btnSearch,
						handler : function(btn) {
							me.onSearch(btn)
						}
					}, {
						text : '过滤',
						icon : './resources/static/js/extjs/packages/ext-theme-classic/images/grid/filters/find.png',
						itemId : me.id + 'btnFilter',
						reference : 'winbtnFilter',
						xtype : 'splitbutton',
						menu : [{
									text : '本页',
									itemId : me.id + 'btnFilterLocal',
									reference : 'winbtnFilterLocal',
									handler : function(btn) {
										me.onSetFilterLocal(btn);
									}
								}, {
									text : '所有',
									itemId : me.id + 'btnServer',
									reference : 'winbtnFilterServer',
									handler : function(btn) {
										me.onSetFilterServer(btn);
									}
								}, {
									text : '关闭',
									itemId : me.id + 'btnFilterClose',
									reference : 'winbtnFilterClose',
									handler : function(btn) {
										me.onFilterClose(btn);
									}
								}]
					}, {
						itemId : me.id + 'btnSave',
						reference : 'winbtnSave',
						xtype : 'button',
						text : '确认',
						glyph : Belle.Icon.btnSave,
						handler : function(btn) {
							me.onSave(btn)
						}
					}, {
						xtype : 'button',
						text : '取消',
						glyph : Belle.Icon.btnCancel,
						handler : function(btn) {
							btn.up('window').close();
						}
					}]
		}];
		me.callParent(arguments);
	},
	initEvents : function() {
		
		var me = this, form = me.formPanel, fields = form
				&& form.query('textfield,combo,datefield,numberfield');
		if (fields) {
			Ext.each(fields, function(txt) {
						txt.on('specialkey', function(obj, e) {
									if (obj.isExpanded!=true && e.getKey() === e.ENTER) {
										me.onSearch(me.lookupReference('winsearchbtn')||txt);
									}
								});
				//戴文辉 已在override实现
//						txt.labelEl.on('dblclick', function(obj, e) {
//									if (txt.readOnly || txt.canInput == false
//											|| txt.isDisabled() == true) {
//										return;
//									}
//									txt.setValue("");
//								});
					});
		}
	},
	onSearch : function(btn) {
		// 查询
		var me = this, params = me.getParams(), grid = me.gridPanel, form = me.formPanel, store = grid
				.getStore(), queryCondition = Belle
				.clone(params.queryCondition);

		if (!me.beforeSearch())
			return;

		delete params.queryCondition;

		store.getProxy().extraParams = params;

		btn.setDisabled(true);

		if (!form) {
			if (grid && grid.setOtherFilters) {
				grid.setOtherFilters(queryCondition);
			}

			store.loadPage(1, {
						callback : function() {
							btn.setDisabled(false);
						}
					});
			return;
		}

		if (!form.isValid()) {
			btn.setDisabled(false);
			return;
		}

		// 验证必填一项
		if (me.formAllowBlank == false) {
			var values = form.getValues(), isNull = true;
			for (var val in values) {
				if (!Ext.isEmpty(values[val])) {
					isNull = false;
				}
			}

			if (isNull) {
				Belle.alert("至少输入一个查询条件");
				btn.setDisabled(false);
				return;
			}
		}

		if (grid && grid.setOtherFilters) {
			grid.setOtherFilters(queryCondition);
		}

		store.loadPage(1, {
					callback : function() {
						btn.setDisabled(false);
					}
				});
	},
	onSave : function() {
		// 保存
		var me = this, grid = me.gridPanel;

		var selModels = grid.getSelection();

		if (me.fireEvent("onBeforeReturnData", selModels, grid, me) == false)
			return;

		me.fireEvent("onReturnData", selModels, grid, me);

		me.close();

	},
	createForm : function() {
		// 创建查询窗口
		var me = this;

		if (Ext.isEmpty(me.searchItems)) {
			return;
		}
		
		if(me.canQueryCondition){
			Ext.each(me.searchItems,function(item){
				//替换成过滤样式
				if(item.canQueryCondition!=false && (Ext.isEmpty(item.xtype) ||item.xtype=="textfield")){
					item.xtype="bellefilter";
				}
			});
		}

		var form = new Ext.form.Panel({
					border : false,
					region : 'north',
					bodyPadding : 3,
					items : [{
								border : false,
								defaultType : 'textfield',
								layout : {
									type : 'table',
									columns : me.searchColumn
								},
								defaults : {
									labelAlign : 'right',
									labelWidth : 80,
									width : me.fieldWidth
								},
								itemId : "searchfields",
								items : me.searchItems
							}]
				});

		me.formPanel = form;
		return form;
	},
	/** 弹出选择框 */
	createGrid : function() {
		// 创建网格
		var me = this, selMode, params = me.getParams(), queryCondition = Belle
				.clone(params.queryCondition);
		delete params.queryCondition

		if (!me.gridColumns || !me.url)
			return false;
		var fields = [];
		Ext.each(me.gridColumns, function(column) {
			
			if(Ext.isEmpty(column.dataIndex)) return true;
			
			fields.push(column.dataIndex);

				// //判断是否查询表单已存在
				// Ext.each(me.searchItems, function (scolumn) {
				// if(column.dataIndex==scolumn.name){
				// if(!column.belleFilter){
				// column.belleFilter={};
				// }
				// //关闭当前过滤字段
				// column.belleFilter.isOpen=true;
				// }
				// });

			});
		var store = Ext.create('Belle_Common.store.Base', {
					fields : fields,
					autoLoad : false,
					proxy : {
						extraParams : params,
						url : me.url
					}

				});

		// 获取过滤条件

		// 支持多选
		if (me.isMultiSelect) {
			selModel = Ext.create('Ext.selection.CheckboxModel', {
						mode : "SIMPLE"
					});
		} else {
			selModel = {
				mode : 'MULTI'
			};
		}
		
		var grid = new Ext.grid.Panel({
					border : false,
					region : 'center',
					columns : me.gridColumns,
					plugins : ["belleheaderfilter"],
					columnLines : true,
					selModel : selModel,
					store : store,
					bbar : {
						xtype : 'pagingtoolbar',
						plugins : Ext.create('Ext.ux.ComboPageSize'),
						displayInfo : true,
						store : store
					},
					viewConfig : {
						enableTextSelection : true
					}
				});

		// dwh
		if (!Ext.isEmpty(queryCondition)) {
			// 设置grid过滤参数
			if (grid.setOtherFilters) {
				grid.setOtherFilters(queryCondition);
			}
		}
		// 是否默认加载
		if (me.isAutoLoad) {
			store.load();
		}

		// 关闭网格过滤
		if (grid.setFilterStatus) {
			grid.setFilterStatus(false);
		}

		if (me.isMultiSelect == false) {
			grid.on("itemdblclick", function(val, rec) {
				// 保存
				var selModels = [];
				selModels.push(rec);

				if (me.fireEvent("onBeforeReturnData", selModels, grid, me) == false)
					return false;

				me.fireEvent("onReturnData", selModels, grid, me);

				me.close();
			});
		}

		me.gridPanel = grid;
		return grid;
	},
	// dwh 切换过滤按钮显示
	onFilterClose : function(btn) {
		var me = this, grid = me.gridPanel;
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterStatus(false);

			me._setBtnFilterText(btn.up("[itemId='" + me.id + "btnFilter']"));
		}
	},
	// dwh 当前页
	onSetFilterLocal : function(btn) {
		var me = this, grid = me.gridPanel;
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterLocal(true);
		}
		me._setBtnFilterText(btn.up("[itemId='" + me.id + "btnFilter']"));
	},
	// 所有
	onSetFilterServer : function(btn) {
		var me = this, grid = me.gridPanel;
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterLocal(false);
		}
		me._setBtnFilterText(btn.up("[itemId='" + me.id + "btnFilter']"));
	},
	// 显示文本
	_setBtnFilterText : function(btn) {
		var me = this, grid = me.gridPanel;

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
	getParams : function() {
		var me = this, form = me.formPanel, values = form
				? form.getValues()
				: {}, parms = Belle.clone(me.otherParams) || {}, queryCondition = [];

		if(!form || form.isValid() == false) return parms;
		//
		for (var field in values) {
				
			var txt = Belle.getField(form, field),
			val = values[field];
			
			if(val.join){
				val = "'"+values[field].join('\',\'')+"'";
			}
			
			if (!Ext.isEmpty(val)) {
					if(txt.xtype=="bellefilter"){
						queryCondition.push({
								property :txt.dataIndex|| field,
								value : val,
								operator : txt.operator||15
							});
					}
					else{
						parms[field] = val;
						
					}
					
			}
				
		}

		if (!Ext.isEmpty(me.filters)) {
			queryCondition = queryCondition.concat(me.filters);
		}
		if (!Ext.isEmpty(queryCondition)) {
			parms.queryCondition = queryCondition;

//			// 改为key:value传值方式 0902
//			Ext.each(queryCondition,function(item){
//				parms[item.property] = item.value;
//			})
			
		}

		return parms;
	},
	beforeSearch : function() {
		var me = this, fields = me.searchNotNullField;
		if (!fields)
			return true;

		var form = me.formPanel, flag = false, fArray = fields.split(','), txt, label = [];
		if (!form)
			return true;

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
	}
});
/**
 * Description: 数据精灵 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: wudefeng Createdate:
 * 2015/4/10 0010
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */
Ext.define('Belle_Common.ux.SearchHelpField', {
	extend : 'Ext.form.field.Text',
	xtype : 'searchhelpfield',
	//selectionWinConfig:{},
	enableKeyEvents : true,
	searchNotNullField:'',
	// 指定后端取数据的URL
	url : '',
	formAllowBlank : true, // 是否允许表单为空
	// 指定弹出窗中的 grid 列
	gridColumns : null,
	canQueryCondition: false,
	queryDataIndex:"a",
	// 指定弹出窗中的查询条件
	searchItems : null,
	searchColumn : 4,
	fieldWidth : '100%',

	winTitle : '选择器',
	winHeight : 500,
	winWidth : 700,
	isAutoLoad : true,
	isMultiSelect : false,
	// 返回值写入其它的列（如有多个，用逗号分隔）
	otherFields : '',

	// 通过哪个列的值去过滤(如有多个，用逗号分隔)
	fromFields : '',

	// 字段映射对照，即当页面的字段名跟精灵中的字段名不同时，做一个对照表，如("id=dictId,text=dictName")
	fieldMap : '',
	// 默认过滤条件 例如：[{property: "id",value: "100"}]
	filters : [],
	otherParams : {}, // 请求参数
	listeners : {
		keydown : 'onKeyDown',
		blur : 'onTxtBlur',
		focus: 'onTxtFocus',
		afterrender : 'onrendered',
		change : 'onValChange',
		scope : 'this'
	},
	tabIndex:1,
	triggers : {
		search : {
			cls : 'x-form-search-trigger',
			weight : 1,
			handler : 'showSelectWin',
			scope : 'this'
		}
	},
	isCanInput:true, //控制精灵窗口是否允许输入
	needCall : false,
	checkValue : true,
	oldValue : '',
	errorText:null,
	initComponent : function() {
		var me = this;
		me.needCall = false;
		//me.enableKeyEvents = true;
		me.callParent(arguments);
		me.oldValue = '';

		if (me.column && me.column.up()) {
			me.url = Belle.setUrlModuleInfo(me.column.up().grid, me.url)
		} else {
			me.url = Belle.setUrlModuleInfo(me, me.url);
		}
		if (!me.gridColumns) {
			me.getTrigger('search').hide();
		}

		if(me.isCanInput==false){ me.checkValue=false;}
	},
	afterRender:function(){
		var me=this;
		me.callParent(arguments);

		if(me.isCanInput==false&&me.inputEl&&me.inputEl.dom){
			me.inputEl.dom.readOnly=true;
		}	
	},
	onrendered : function() {
		var me = this;
		if (me.inputEl) {
			me.inputEl.on('dblclick', function() {
						me.showSelectWin();
					}, me);
		}
	},

	onValChange : function() {
		var me = this;		
		me.needCall = true;
	},
	/** 弹出选择框 */
	showSelectWin : function() {
		var me = this;

		if (!me.fireEvent("onBeforeShowWin", me)) {
			return;
		}

		if (!me.gridColumns || !me.url || me.readOnly || me.disabled)
			return;

		
		Ext.each(me.gridColumns, function(column) {
					// 判断是否查询表单已存在
					Ext.each(me.searchItems, function(scolumn) {
								if (column.dataIndex == scolumn.name) {
									if (!column.belleFilter) {
										column.belleFilter = {};
									}
									// 用户未定义时,默认关闭远程过滤功能  、//关闭当前过滤字段
									column.belleFilter.isOpen = column.belleFilter.isOpen||false;
								}
							});

			});

		var query = me.getFromFieldsVal(),params={};
		
		Ext.each(query,function(item){
			params[item.property] = item.value;
		});

		Ext.applyIf(params,me.otherParams);
		
		var config={
				otherParams:params,
				gridColumns : me.gridColumns,
				url : me.url,
				searchItems : me.searchItems,
				title : me.winTitle,
				isAutoLoad : me.isAutoLoad,
				isMultiSelect : me.isMultiSelect,
				//filters : query,
				//otherParams : me.otherParams,
				isQueryParams : me.isQueryParams || false,
				height : me.winHeight,
				width : me.winWidth,
				searchNotNullField: me.searchNotNullField,
				autoShow : true,
				formAllowBlank: me.formAllowBlank,
				canQueryCondition:me.canQueryCondition,
				queryDataIndex:me.queryDataIndex
		};
		//Ext.applyIf(config,me.selectionWinConfig);
		
		var win = new Belle_Common.ux.SearchWin(config);
		win.on("onBeforeReturnData", function(selRecord, grid, win) {
					// 验证
					me.checkFun(selRecord, function(type) {
								if (type === true) {
									me.onReturnValue(win.down("[itemId='"+ win.id + "btnSave']"));
									win.close();
								}
							});
					return false;
				});
		var grid = win.gridPanel;

		me.fireEvent("onAfterShowWin", me, win, grid);
		me.selectionWin = win;
	},
	getSearchItems : function() {

	},
	// dwh 切换过滤按钮显示
	onFilterClose : function(btn) {
		var me = this, win = btn.up('window'), grid = win.down('grid');
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterStatus(false);

			me._setBtnFilterText(btn.up("[itemId='" + win.id + "btnFilter']"));
		}
	},
	// dwh 当前页
	onSetFilterLocal : function(btn) {
		var me = this, win = btn.up('window'), grid = win.down('grid');
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterLocal(true);
		}
		me._setBtnFilterText(btn.up("[itemId='" + win.id + "btnFilter']"));
	},
	// 所有
	onSetFilterServer : function(btn) {
		var me = this, win = btn.up('window'), grid = win.down('grid');
		if (typeof(grid.setFilterStatus) == "function") {
			grid.setFilterLocal(false);
		}
		me._setBtnFilterText(btn.up("[itemId='" + win.id + "btnFilter']"));
	},
	// 显示文本
	_setBtnFilterText : function(btn) {
		var text, win = btn.up('window'), grid = win.down('grid');

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
	/** 弹出框返回值 */
	onReturnValue : function(btn) {
		var me = this, win = btn.up('window'), grid = win.gridPanel, items = grid
				.getSelection();
		if (items.length < 1) {
			Belle.alert('必须选择一条记录');
			return;
		}
		me.needCall = false;
		me.setOtherFieldsVal(items[0].data,items);
		win.close();
	},
	
	onTxtFocus:function(){
		var me = this,txtVal = me.getValue();
		if(!Ext.isEmpty(txtVal)){
			me.oldValue = txtVal;
		}else{
			me.oldValue='';
		}
	},

	/** 数据发生变化时 */
	onTxtBlur : function() {
		var me = this;

		if(!me.needCall) return;	

		if (Ext.isEmpty(me.getValue())) {
			me.needCall = false;
			me.oldValue = '';
			me.clearOtherFieldsVal();
			return;
		} 		
		me.sendToServer();		
	},

	/** 按下回车键时 */
	onKeyDown : function(e) {
		var me = this;
		if (e.getKey() === e.ENTER) {
			if(Ext.isEmpty(me.getValue())){
				me.oldValue = '';
				me.clearOtherFieldsVal();
			}else {
				me.sendToServer();
			}
		} else if (e.getKey() === e.F4) {
			me.showSelectWin();
		}
	},

	getFieldMap : function() {
		var map = [], me = this;
		if (!me.fieldMap)
			return map;
		var list = me.fieldMap.split(',');
		Ext.each(list, function(item) {
					var keys = item.split('=');
					if (keys.length == 2) {
						var obj = {
							s : keys[0],
							t : keys[1]
						};
						map.push(obj)
					}
				});
		return map;
	},

	/** 获取过滤条件 */
	getFromFieldsVal : function() {

		var me = this, params = [];

		// dwh 添加过滤条件
		if (!Ext.isEmpty(me.filters)) {
			Ext.each(me.filters, function(item) {

						if (!Ext.isEmpty(item.property)) {
							params.push({
										property : item.property,
										value : item.value,
										operator : item.operator || 10
									});
						}
					});
		}

		if (!me.fromFields)
			return params;

		var fields = me.fromFields.split(','), editor = me.up(), context, val, form = me
				.up('form'), fieldmap = me.getFieldMap(), dataIndex = "",grid=me.up('grid');

		if (editor) {
			context = editor.context;
			if(!context && grid && grid.editingPlugin){
				context=grid.editingPlugin.context;
			}
		}

		if (context || form) {
			Ext.each(fields, function(f) {
						val = '';
						dataIndex = f;
						if (context) {
							val = context.record.get(f);

							//不需要处理别名
							//Ext.each(context.grid.columns, function(col) {
							//			if (col.dataIndex === f) {
							//				dataIndex = col.belleFilter.dataIndex
							//						? col.belleFilter.dataIndex
							//						: f;
							//				return false;
							//			}
							//		});
						}
						if (!val && form) {
							var txt = Belle.getField(form, f);
							if (txt) {
								val = txt.getValue();
							}
							//不需要处理别名
							//dataIndex = txt.dataIndex ? txt.dataIndex : f
						}
						var map = Ext.Array.findBy(fieldmap, function(fm) {
									return fm.s == f;
								});

						if(val && val.join){
							val=val.join(',')
						}
						params.push({
									property : (map && map.t) || dataIndex,
									value : val || '',
									operator : 10
								});
					});
		}

		return params;
	},
	checkFun : function(val, callback) {
		var me = this;
		callback(true);
	},
	/** 提交后端，返回对应的记录 */
	sendToServer : function() {
		var me = this,
			isInGrid = me.up('grid')?true:false;
		if (!me.needCall || !me.checkValue || me.oldValue==me.getValue())
			return;
		me.needCall = false;

		if (!me.url || Ext.isEmpty(me.getValue())) {
			me.clearOtherFieldsVal();
			return;
		}
		// 验证数据
		me.checkFun(me.getValue(), function(type) {
			if (type === true) {
				var params = me.getFromFieldsVal(), val = me.getValue(), map = Ext.Array
						.findBy(me.getFieldMap(), function(fm) {
									return fm.s == me.name;
								}), fname = map && map.t || me.property
						|| me.name;

				params.push({
							property : fname,
							value : val,
							operator : 10
						});

				//改为key:value传值方式 0902
				var formp={};
				//formp.queryCondition = JSON.stringify(params);
				formp.isSearchHelpInput='TRUE';
				Ext.each(params,function(item){
					formp[item.property] = item.value;
				});

				var options = {
					url : me.url,
					async: !isInGrid, //如果是在网格中，通过同步处理
					params:formp,
					//params : {
					//	queryCondition : JSON.stringify(params),
					//	isSearchHelpInput : 'TRUE'
					//},
					method : 'POST',
					success : function(d) {
						try {
							var result = JSON.parse(d.responseText);
							if (!result.list || result.list.length == 0) {
								//me.startEditingCell();
								me.clearOtherFieldsVal();
								Belle.alert(me.errorText ||'输入【' + val + '】是无效的值', function() {
									//me.clearOtherFieldsVal();
										});

							} else {
								me.setOtherFieldsVal(result.list[0]);
							}
						} catch (e) {
							//me.startEditingCell();
							Belle.alert('输入值【' + val + '】后端验证失败', function() {
										me.clearOtherFieldsVal();
									});

						}
					},
					failure : function() {
						//me.startEditingCell();
						Belle.alert('数据精灵验证失败，请联系管理员', function() {
									me.clearOtherFieldsVal();
								});
					}
				};
				Belle.callServer(options);
			} else {
				me.clearOtherFieldsVal();
			}
		});

	},

	/** 设置相关控件的值 */
	setOtherFieldsVal : function(itemInfo,list) {

		var me = this, form = me.up('form'), editor = me.up(), context, record, fieldmap = me
			.getFieldMap(), grid = me.up('grid'), plugins;

		if (grid) {
			plugins = grid.getPlugins()
		}


		if (grid && !context) {
			Ext.each(plugins, function (plu) {
				if (plu.ptype === "cellediting") {
					editor = plu;
					return false;
				}
			});
		}

		list = list || [];
		me.fireEvent("onReturnData", itemInfo, list);
		itemInfo = itemInfo || {};


		if (editor) {
			context = editor.context;
		}

		if (context) {
			record = context.record;

		} else if (form) {
			record = form.getRecord();
		}
		me.startEditingCell();

		if (me.afterCall(me, itemInfo, record, context, list) === false)
			return;

		var map = Ext.Array.findBy(fieldmap, function (fm) {
			return fm.s == me.name;
		}), fname = map && map.t || me.property || me.name, selfValue = itemInfo[fname] == null
			? (me.getValue()||me.oldValue)
			: itemInfo[fname];
			
		if (context && context.column && !me.inputEl) {
			me = context.column.field;
		}
		//戴文辉 添加多选功能
		if (me.isMultiSelect && list && list.length > 0) {

			var vals = [];
			Ext.each(list, function (item) {
				vals.push(item.data[fname]);
			});
			me.setValue(vals.join(','));
		}
		else  {			
			me.setValue(selfValue);
		}

		if (context) {
			record.set(me.name, selfValue);
		}

		me.oldValue = me.getValue();
		me.needCall = false;

		if (!me.otherFields)
			return;

		var fields = me.otherFields.split(',');
		Ext.each(fields, function (field) {
			map = Ext.Array.findBy(fieldmap, function (fm) {
				return fm.s == field;
			});
			fname = map && map.t || field;

			var fvalue = itemInfo[fname] == null ? '' : itemInfo[fname];

			if (context) {
				record.set(field, fvalue);
			} else if (form) {
				var txt = Belle.getField(form, field);
				if (txt) {
					txt.setValue(fvalue);
				}
				if (record) {
					record.set(field, fvalue);
				}
			}
		});
	},

	/** 清空相关控件的值 */
	clearOtherFieldsVal : function() {
		var me = this, form = me.up('form'), editor = me.up(), context, record,
		oldValue,grid = me.up('grid'),plugins;

		if(grid){
			plugins = grid.getPlugins()
		}
		
		
		if(grid&&!context){
			Ext.each(plugins,function(plu){
				if(plu.ptype==="cellediting"){
					editor = plu;
					return false;
				}
			});
		}
		
		if (editor) {
			context = editor.context;
		}

		if (context) {
			record = context.record;
		} else if (form) {
			record = form.getRecord();
		}

		me.startEditingCell();
		
		oldValue=me.oldValue;
		//获取当前编辑器容器的输入控件
		if(context&&context.column&&!me.inputEl){
			me = context.column.field;
			context.column.field.setValue(oldValue);
		}
		else{
			me.setValue(oldValue);
		}
		
		
		if (context) {
			record.set(me.name, oldValue);
		}

		me.needCall = false;
		me.onValueError();

		if (oldValue)
			return;

		if (!me.otherFields)
			return;
		var fields = me.otherFields.split(',');
		Ext.each(fields, function(field) {
					if (context) {
						record.set(field, '');
					} else if (form) {
						var txt = Belle.getField(form, field);
						if (txt) {
							txt.setValue('');
						}
						if (record) {
							record.set(field, '');
						}
					}
				});

	},

	onValueError:function(){

	},

	startEditingCell : function() {
		var me = this, grid = me.up('grid'), editor = me.up(), context,
		plugins;

		if(grid){
			plugins = grid.getPlugins()
		}
		
		
		if(grid&&!context){
			Ext.each(plugins,function(plu){
				if(plu.ptype==="cellediting"){
					editor = plu;
					return false;
				}
			});
		}
		
		if (editor) {
			context = editor.context;
		}
		
		
		if (!grid || !context)
			return;
		grid.editingPlugin.startEdit(context.record, context.column);
	},

	/**
	 * 返回值之后处理接口，由开发人员处理 txtobj ， 控件本身 newdata, 返回的记录值 record, 原记录值即 form，或 grid
	 * 绑定的行 context, 网格中编辑事件对应的 context
	 */
	afterCall : function(txtobj, newdata, record, context) {
	}
});

Ext.define('Belle_Common.ux.BelleExport', {
	extend: 'Ext.window.Window',
	alias: 'widget.belleexport',

	constructor: function (config) {
		var me = this;
		Ext.apply(me, config);
		me.callParent(arguments);
	},
	title:"文件导出",
	exportType:"page",
	maxCount:20000,
	initComponent: function () {
		var me = this;
		me.fileType = me.fileType || "xls"; // 文件类型
		me.fieldType = me.fieldType || "all"; // 字段


		if(me.grid.exportMaxRows) {
			me.maxCount = me.grid.exportMaxRows;
		}

		me.items = [me._createForm()];
		me.bbar = ['->', {
			glyph: Belle.Icon.btnSave,
			text: "确定",
			itemId: me.id + "_exportOk",
			handler: me._exportOk
		}, {
			glyph: Belle.Icon.btnCancel,
			text: "退出",
			itemId: me.id + "_exportCanel",
			handler: me._exportCanel
		}];

		me.callParent(arguments);
	},
	title: "数据导出",
	modal: true,
	resizable: false,
	//constrainHeader: true,
	ATTRS: {
		btns: {},
		fields: {}
	},
	initEvents: function () {
		// 注册事件
		var me = this;

		me.ATTRS.btns.btnAllField.on("click", function () {
			me.fieldType = "all";
			me._allfield();
		});
		me.ATTRS.btns.btnCustomField.on("click", function () {
			me.fieldType = "custom";
			me._customField();
		});

		me.ATTRS.btns.btnSelectAll.on("click", function () {
			me._selectAll();
		});
		me.ATTRS.btns.btnUnSelectAll.on("click", function () {
			me._unSelectAll();
		});
		
		me.ATTRS.btns.exportAll.on("click",function(btn){
			me.exportType="all";
			me._setExportTypeBtnCls(btn);
		});
		me.ATTRS.btns.exportPage.on("click",function(btn){
			me.exportType="page";
			me._setExportTypeBtnCls(btn);
		});
		me.ATTRS.btns.exportPageSize.on("click",function(btn){
			me.exportType="pageSize";
			me._setExportTypeBtnCls(btn);
		});

		Ext.each(me.ATTRS.btns.btnFileTypes.items.items, function (item) {
			item.on("click", function () {
				me._selectFileType(this.value);
			});
		});
	},
	_createForm: function () {
		// 创建表单
		var me = this, grid = me.grid, store = grid.getStore(), exportFiels = grid.columnManager.getColumns()||[], 
		form, fields = [];
		
		me.gridColumns=exportFiels;

		// 生成导出列
		Ext.each(exportFiels, function (item) {

			if (Ext.isEmpty(item.dataIndex))
				return true;
			if(item.isHidden()==true) return true;
			
			var dataIndex=item.dataIndex;
			//读取别名
			if(item.belleFilter && item.belleFilter.dataIndex){
					dataIndex=item.belleFilter.dataIndex;
			}

			var field = {
				tooltipType: "title",
				title: item.text,
				tooltip: item.text,
				text: item.text,
				value: item.dataIndex,//字段名称
				dataIndex:dataIndex,//字段别名
				name:item.dataIndex,
				handler: me._addOrDelFields,
				isSelect: false
			};
			fields.push(field);
		});

		form = new Ext.form.Panel({
			border: false,
			defaults: {
				style: 'margin:5px 10px 5px 5px;'
			},
			items: [{
				xtype: 'fieldcontainer',
				fieldLabel: '导出文件',
				labelWidth: 70,
				layout: 'hbox',
				defaults: {
					xtype: "button",
					style: 'margin:5px 10px 5px 0px;',
					cls: "belle-export"
				},
				itemId: me.id + "_fileTypes",
				items: [{
					text: 'Excel(.xls)',
					itemId: me.id + '_xls',
					iconCls: me.fileType == "xls" ? "belle-export-select" : "",
					value: "xls"
				}, {
					text: 'Excel(.xlsx)',
					itemId: me.id + '_xlsx',
					iconCls: me.fileType == "xlsx"
						? "belle-export-select"
						: "",
					value: "xlsx"
				}]
			}, {
				xtype: 'fieldcontainer',
				fieldLabel: '文件名称',
				layout: 'vbox',
				labelWidth: 70,
				defaults: {
					style: 'margin:5px 10px 5px 0px;'
				},
				items: [{
					xtype: "textfield",
					emptyText: "请输入导出文件名称",
					value: me.fileName || "export",
					allowBlank: false,
					itemId: me.id + "_exportFileName"
				}]
			}, {
				xtype: 'fieldcontainer',
				fieldLabel: '导出字段',
				layout: 'vbox',
				labelWidth: 70,
				items: [{
					border: false,
					layout: 'hbox',
					defaults: {
						xtype: "button",
						style: 'margin:5px 10px 5px 0px;',
						cls: "belle-export"
					},
					items: [{
						text: '所有',
						itemId: me.id + '_allfield',
						iconCls: me.fieldType == "all"
							? "belle-export-select"
							: ""
					}, {
						iconCls: me.fieldType != "all"
							? "belle-export-select"
							: "",
						text: '自定义',
						itemId: me.id + '_Customfield'
					}]
				}, {
					border: false,
					hidden: true,
					itemId: me.id + "_btnExport",
					layout: 'hbox',
					defaults: {
						style: 'margin:5px 10px 5px 0px;',
						xtype: "button",
						cls: "belle-export"
					},
					items: [{
						text: '全选',
						itemId: me.id + '_selectAll'

					}, {
						text: '反选',
						itemId: me.id + '_unselectAll'
					}]
				}, {
					border: false,
					layout: 'column',
					labelWidth: 70,
					maxHeight:300,
					width: 600,
					autoScroll:true,
					hidden: true,
					itemId: me.id + "_exportFiels",
					defaults: {
						style: 'margin:5px 5px 5px 0px;',
						xtype: "button",
						cls: "belle-export",
						columnWidth: 0.25

					},
					items: fields
				}]
			},
			{
				xtype: 'fieldcontainer',
				fieldLabel: '导出方式',
				layout: 'hbox',
				labelWidth: 70,
				defaults: {
					xtype: "button",
					style: 'margin:5px 10px 5px 0px;',
					cls: "belle-export"
				},
				items: [{
						text: '导出所有',
						itemId: me.id + 'exportAll',
						iconCls: me.exportType == "all"
							? "belle-export-select"
							: ""
					}, {
						text: '导出当前页',
						itemId: me.id + 'exportPage',
						iconCls: me.exportType == "page"
							? "belle-export-select"
							: ""
					},{
						text: '导出指定页数(最大'+me.maxCount+')行',
						itemId: me.id + 'exportPageSize',
						iconCls: me.exportType == "pageSize"
							? "belle-export-select"
							: ""
					}]
			},
			{
				hidden:true,
				itemId:me.id+"exportPageInput",
				xtype: 'fieldcontainer',
				layout: 'column',
				defaults: {
					labelWidth: 70,
					style: 'margin:5px 10px 5px 0px;'
				},
				items:[{
						minValue:1,
						xtype:"numberfield",
						fieldLabel: '开始页',
						itemId: me.id + 'exportPageStar',
						columnWidth:0.45,
						value:1//,
//						validator:function(val){
//							if(me.exportType==="pageSize"){
//								if(val > me.ATTRS.fields.exportPageStar.getValue()){
//									return "此项必须小于或等于结束页";
//								}
//								else{
//									return true;
//								}
//							}
//							else{
//								return true;
//							}
//						}
					},
					{
						value:1,
						minValue:1,
						xtype:"numberfield",
						fieldLabel: '结束页',
						itemId: me.id + 'exportPageStop',
						columnWidth:0.45,
						validator:function(val){
							if(me.exportType==="pageSize"){
								if(val < me.ATTRS.fields.exportPageStar.getValue()){
									return "此项必须大于或等于开始页";
								}
								else{
									return true;
								}
							}
							else{
								return true;
							}
						}
					}]
			}
			]
		});

		// 获取控件

		me.ATTRS.fields.exportFiels = form.down('[itemId=' + me.id
		+ '_exportFiels]');
		me.ATTRS.fields.exportFileName = form.down('[itemId=' + me.id
		+ '_exportFileName]');
		me.ATTRS.fields.btnExport = form.down('[itemId=' + me.id
		+ '_btnExport]');

		me.ATTRS.fields.exportPageInput =  form.down('[itemId=' + me.id
		+ 'exportPageInput]');
		
		me.ATTRS.fields.exportPageStar =  form.down('[itemId=' + me.id
		+ 'exportPageStar]');
		
		me.ATTRS.fields.exportPageStop =  form.down('[itemId=' + me.id
		+ 'exportPageStop]');
		
		// 文件类型
		me.ATTRS.btns.btnFileTypes = form.down('[itemId=' + me.id
		+ '_fileTypes]');

		me.ATTRS.btns.btnSelectAll = form.down('[itemId=' + me.id
		+ '_selectAll]');
		me.ATTRS.btns.btnUnSelectAll = form.down('[itemId=' + me.id
		+ '_unselectAll]');

		me.ATTRS.btns.btnAllField = form
			.down('[itemId=' + me.id + '_allfield]');
		me.ATTRS.btns.btnCustomField = form.down('[itemId=' + me.id
		+ '_Customfield]');

		me.ATTRS.btns.exportAll = form.down('[itemId=' + me.id
		+ 'exportAll]');
		
		me.ATTRS.btns.exportPage = form.down('[itemId=' + me.id
		+ 'exportPage]');
		
		me.ATTRS.btns.exportPageSize = form.down('[itemId=' + me.id
		+ 'exportPageSize]');
		
		return form;
	},
	// 全选
	_selectAll: function () {
		var me = this, exportFiels = me.ATTRS.fields.exportFiels.items.items;

		me.ATTRS.btns.btnSelectAll.setIconCls("belle-export-select");
		me.ATTRS.btns.btnUnSelectAll.setIconCls("");

		Ext.each(exportFiels, function (item) {
			item.isSelect = true;
			item.setIconCls("belle-export-select");
		});

	},
	// 反选
	_unSelectAll: function () {
		var me = this, exportFiels = me.ATTRS.fields.exportFiels.items.items;

		me.ATTRS.btns.btnSelectAll.setIconCls("");
		me.ATTRS.btns.btnUnSelectAll.setIconCls("belle-export-select");

		Ext.each(exportFiels, function (item) {
			item.isSelect = !item.isSelect;
			if (item.isSelect) {
				item.setIconCls("belle-export-select");
			} else {
				item.setIconCls("");
			}
		});
	},
	_selectFileType: function (type) {
		var me = this,
			files = me.ATTRS.btns.btnFileTypes.items.items;
		Ext.each(files, function (item) {
			item.setIconCls(item.value == type
				? "belle-export-select"
				: "");
		});

		me.fileType = type;
	},
	// 点击选择或取消
	_addOrDelFields: function () {
		var me = this;
		me.isSelect = !me.isSelect;

		if (me.isSelect) {
			me.setIconCls("belle-export-select");
		} else {
			me.setIconCls("");
		}
	},
	// 导出所有字段
	_allfield: function () {
		var me = this, oddWidth = me.getWidth(), oddHeight = me.getHeight(), newWidth, newHeight, winXY = me
			.getXY();

		me.ATTRS.btns.btnAllField.setIconCls("belle-export-select");
		me.ATTRS.btns.btnCustomField.setIconCls("");

		me.ATTRS.fields.exportFiels.setVisible(false);
		me.ATTRS.fields.btnExport.setVisible(false);

		newWidth = me.getWidth();
		newHeight = me.getHeight();

		winXY[0] -= (newWidth - oddWidth) * 0.5;
		winXY[1] -= (newHeight - oddHeight) * 0.5;
		me.setXY(winXY);
	},
	_setExportTypeBtnCls :function(btn){
		var me = this;
		me.ATTRS.btns.exportAll.setIconCls("");
		me.ATTRS.btns.exportPage.setIconCls("");
		me.ATTRS.btns.exportPageSize.setIconCls("");
		
		btn.setIconCls("belle-export-select");
		
		me.ATTRS.fields.exportPageInput.setVisible(me.exportType==="pageSize");
	},
	// 自定义
	_customField: function () {
		var me = this, oddWidth = me.getWidth(), oddHeight = me.getHeight(), newWidth, newHeight, winXY = me
			.getXY();

		me.ATTRS.btns.btnAllField.setIconCls("");
		me.ATTRS.btns.btnCustomField.setIconCls("belle-export-select");

		me.ATTRS.fields.exportFiels.setVisible(true);
		me.ATTRS.fields.btnExport.setVisible(true);

		newWidth = me.getWidth();
		newHeight = me.getHeight();

		winXY[0] -= (newWidth - oddWidth) * 0.5;
		winXY[1] -= (newHeight - oddHeight) * 0.5;
		me.setXY(winXY);

	},
	// 点击确定
	_exportOk: function () {
		var me = this, winPanel = me.up("window"), form = winPanel.down("form");
		if (!form.isValid())
			return;

		if (winPanel._export() == true) {
			winPanel.close();
		}

	},
	// 导出
	_export: function () {
		var me = this, exportErrorMsg = '', grid = me.grid, objs = me.objs, exportUrl = me.exportUrl
			|| grid.exportUrl, searchPanel = objs.commonsearch, fileName = me.ATTRS.fields.exportFileName
			.getValue(), fileType = me.fileType, exportColumns = [],sortParam = [];
		
		if(searchPanel && !searchPanel.isValid()) return;
		//dwh 改用form提交方式。get方式提交时url出现特殊符号会导致异常
		var form = document.createElement("form");
		document.body.appendChild(form);
		form.method = "post";
		form.action = exportUrl;
		form.hidden =true;
			// 获取导出字段
		if (me.fieldType === "all") {
			Ext.each(me.gridColumns, function (item) {
				
				if(item.isHidden()==true) return;
				
				if (item.dataIndex) {
					exportColumns.push({
						field: item.dataIndex,
						title: item.text.replace('&nbsp;','').replace(/\%/g, '百分比') //dwh 特殊字符处理
					});
				}
			});

		} else {
			var exportFiels = me.ATTRS.fields.exportFiels.items.items;
			Ext.each(exportFiels, function (item) {
				if (item.isSelect === true) {
					exportColumns.push({
						field: item.value,
						title: item.text.replace(/\%/g, '百分比')  //dwh 特殊字符处理
					});
				}
			});
		}

		if (!exportUrl) {
			Belle.alert('此网格没有提供导出功能');
			return true;
		}
		if (exportColumns.length <= 0) {
			exportErrorMsg += '请选择导出的列';
		}

		if (exportErrorMsg != '') {
			Ext.Msg.alert('导出提示', exportErrorMsg);
			return false;
		}
		if (grid.supGrid) {
			var mainGrid = objs[grid.supGrid], mainGridprimaryKey = mainGrid.primaryKey, mainGridprimaryValue = mainGrid
				.getSelection()[0].data[mainGridprimaryKey];
			
			var subGridInput = document.createElement("input");
			subGridInput.value = mainGridprimaryValue;
			subGridInput.name = mainGridprimaryKey;
			form.appendChild(subGridInput);
		}
		fileName=fileName||"export";

		var queryCondition=[];

		if(searchPanel) {
			var fields=searchPanel.getForm().getFields().items,
				values=searchPanel.getValues();
			Ext.each(fields, function (field) {
				var name = field.dataIndex||field.name,
					value = values[field.name];

				if (Ext.isEmpty(value)) return true;
				if(value.join) value="'"+value.join("','")+"'";
				if (field.xtype === "bellefilter") {
					queryCondition.push({
						value: value,
						operator: field.operator,
						property: name
					});
				}
				else {
					var svalue = Ext.encode(value);
					if(svalue.length>0){
						svalue=svalue.substring(1,svalue.length-1);
					}
					var input = document.createElement("input");
					input.value = svalue;
					input.name = name;
					form.appendChild(input);
				}
			});
		}

		//获取网格的过滤条件
		queryCondition=queryCondition.concat(grid.getFilters());
		
		
		//排序
		var gridSort = grid.store.getSorters();
		if (gridSort && gridSort.items.length > 0) {
			
			Ext.Array.each(gridSort.items, function (item) {
				var dataIndex=item.getProperty();
				
				//获取别名
				Ext.each(me.gridColumns, function (item) {
					//判断当前列是否已设置别名
					if(item.dataIndex===dataIndex){
						//读取别名
						if(item.belleFilter && item.belleFilter.dataIndex){
							dataIndex=item.belleFilter.dataIndex;
						}
						return false;
					}
					
				});
				
				sortParam.push( dataIndex+ ' '
				+ item.getDirection().toLowerCase());
			});
		}

		var exportColumnsInput = document.createElement("input");
		exportColumnsInput.value = Ext.encode(exportColumns);
		exportColumnsInput.name = "exportColumns";
		form.appendChild(exportColumnsInput);
		
		var fileNameInput = document.createElement("input");
		fileNameInput.value = Ext.encode(fileName).substring(1,Ext.encode(fileName).length-1);
		fileNameInput.name = "fileName";
		form.appendChild(fileNameInput);
		
		var fileTypeInput = document.createElement("input");
		fileTypeInput.value = fileType;
		fileTypeInput.name = "fileType";
		form.appendChild(fileTypeInput);
		
		var sortInput = document.createElement("input");
		sortInput.value = sortParam.join(',');
		sortInput.name = "sort";
		form.appendChild(sortInput);
		
		var queryConditionInput = document.createElement("input");
		queryConditionInput.value = Ext.encode(queryCondition);
		queryConditionInput.name = "queryCondition";
		form.appendChild(queryConditionInput);
		
		
		
		// 导出所有
		if (me.exportType == "all") {
			
		} else if(me.exportType == "page") {
			form.action = exportUrl
			+ (exportUrl.indexOf('?') > 0 ? '&' : '?')+'pageNum=' + grid.store.currentPage
			+ '&pageSize=' + grid.store.pageSize
		}
		else{
			var pageStar= me.ATTRS.fields.exportPageStar.getValue(),
			pageStop = me.ATTRS.fields.exportPageStop.getValue(),
			pageSize = grid.store.pageSize;

			//实际导出列数小于
			if(((pageStop-pageStar+1) * pageSize) > me.maxCount ){
				Belle.alert("导出行数不能大于"+me.maxCount);
				return false;
			}
			
			form.action = exportUrl
			+ (exportUrl.indexOf('?') > 0 ? '&' : '?')+'pageNum=' + (pageStar)+'&pageNumTo='+pageStop
			+ '&pageSize=' + pageSize ;
		}
		
		form.submit();
		return true;
	},
	_exportCanel: function () {
		var me = this;
		Belle.confirm("是否关闭？", function (flag) {
			if (flag != "yes") {
				return;
			}
			me.up("window").close();
		});
	}
});
Ext.define('Belle_Common.ux.BelleFilter', {
	extend : 'Ext.form.field.Text',
	alias : 'widget.bellefilter',
	requires : ['Ext.menu.Menu'],
	emptyText : "请输入查找内容...",
	_filterCls : "belle-common-filter",
	filterType : "like",
	initComponent : function() {
		var me = this;

		me.callParent(arguments);
		me.createMenu();
		// 设置默认选中类型
		me.setFilterType(me.filterType);
		// 隐藏
		me.getTrigger('bellefilter').hide();

		if (me.menu) {
			me.menu.on("mouseover", function() {
						me._menuMouseMove();
					});
			me.menu.on("mouseleave", function() {
						me._menuMouseLeave();
					});
		}
	},
	initEvents : function() {
		var me = this;
		me.callParent(arguments);

		me.on("change", me._change);
		me.on("blur", me._blur);
		me.on("focus", me._focus);
		me.el.on("keyup", me._keyup);
	},
	triggers : {
		bellefilter : {
			cls : "belle-common-triggers-filter",
			handler : function(txtfield, filter, page) {
				var me = this, x = me.el.dom.offsetWidth - 61;
				me.menu.showBy(me, "tl-bl?", [x, 0]);

			}
		}
	},
	createMenu : function() {
		//
		var me = this;
		if (!me.menu) {
			me.menu = new Ext.menu.Menu({
				scope : me,
				maxWidth : 100,
				minWidth : 50,
				// activeItem:0,
				items : [{
					icon : "./resources/static/js/extjs/packages/ext-theme-classic/images/grid/filters/find.png",
					text : "包含",
					value : "like",
					cssName:"like",
					operator : "15",
					pressed : true,
					handler : function(item) {
						// 设置选定的类型
						me.setFilterType(this.value);
						// 传递参数给事件
						me.fireEvent("onSelectFilter", me.getValue(),
								me.filterType, item.operator, me);
					}
				}, {
					icon : "./resources/static/js/extjs/packages/ext-theme-classic/images/grid/dd-insert-arrow-right.gif",
					text : "开头包含",
					value : "likeleft",
					cssName:"likeleft",
					operator : "19",
					pressed : true,
					handler : function(item) {
						// 设置选定的类型
						me.setFilterType(this.value);
						// 传递参数给事件
						me.fireEvent("onSelectFilter", me.getValue(),
								me.filterType, item.operator, me);
					}
				},{
					icon : "./resources/static/js/extjs/packages/ext-theme-classic/images/grid/dd-insert-arrow-left.gif",
					text : "结尾包含",
					value : "likeright",
					cssName:"likeright",
					operator : "20",
					pressed : true,
					handler : function(item) {
						// 设置选定的类型
						me.setFilterType(this.value);
						// 传递参数给事件
						me.fireEvent("onSelectFilter", me.getValue(),
								me.filterType, item.operator, me);
					}
				},{
					icon : "./resources/static/js/extjs/packages/ext-theme-classic/images/grid/filters/equals.png",
					text : "等于",
					value : "=",
					operator : "10",
					cssName:"equal",
					handler : function(item) {
						me.setFilterType(this.value);
						me.fireEvent("onSelectFilter", me.getValue(),
								me.filterType, item.operator, me);
					}
				}, {
					icon : "./resources/static/js/extjs/packages/ext-theme-classic/images/grid/filters/greater_than.png",
					text : "大于",
					value : ">",
					operator : "11",
					cssName:"more",
					handler : function(item) {
						me.setFilterType(this.value);
						me.fireEvent("onSelectFilter", me.getValue(),
								me.filterType, item.operator, me);
					}
				}, {
					icon : "./resources/static/js/extjs/packages/ext-theme-classic/images/grid/filters/less_than.png",
					text : "小于",
					value : "<",
					operator : "12",
					cssName:"less",
					handler : function(item) {
						me.setFilterType(this.value);
						me.fireEvent("onSelectFilter", me.getValue(),
								me.filterType, item.operator, me);
					}
				}]
			});
		}
		return me.menu;
	},
	// 获取当前选中的过滤类型
	getFilterType : function() {
		return this.filterType;
	},
	// 设置过滤类型 index,value,text,operator
	setFilterType : function(val) {
		var me = this, items = me.menu.items.items, item;
		Ext.each(items, function(it, index) {
					if (val === index) {
						item = it;
						return false;
					}
					if (it.value === val || it.operator === val
							|| it.text === val) {
						item = it;
						return false;
					}
				});

		if (!item)
			return;

		// 切换css
		me._setFilterCls(me._oddCls || item.cssName, item.cssName);
		me.filterType = val;
		me.operator = item.operator;
	},
	_menuMouseLeave : function() {
		var me = this;
		me.isSelect = false;
	},
	_menuMouseMove : function() {
		var me = this;
		me.isSelect = true;
		me.getTrigger("bellefilter").show();
	},
	_keyup : function(event) {
		var me = this, field = me.component;
		field.fireEvent("onBelleFilterKeyup", field.getValue(),
				field.filterType, field.operator, field, event);
	},
	_change : function(field, event) {
		field.fireEvent("onBelleFilterChange", field.getValue(),
				field.filterType, field.operator, field, event);

	},
	_focus : function(field, event) {
		field.getTrigger('bellefilter').show();
	},
	_blur : function(field, event) {
		var me = this;
		if (!field.getValue()) {
			field.getTrigger('bellefilter').hide();
		}

		if (me.menu && !me.isSelect) {

			me.menu.hide();
		}
	},
	_setFilterCls : function(oddCls, newCls) {
		var me = this;
		if (!oddCls) {
			oddCls = newCls;
		}
		me._oddCls = newCls;
		me.addCls(me._filterCls + '-' + newCls);

		if (oddCls != newCls) {
			me.removeCls(me._filterCls + '-' + oddCls);
		}
	}
});
Ext.define('Belle_Common.ux.BelleHeaderFilter', {
	extend : 'Ext.plugin.Abstract',
	requires : ['Ext.grid.column.Column', 'Ext.form.Text', 'Ext.menu.Menu',
			'Belle_Common.ux.BelleFilter'],
	childEls : ['titleEl', 'triggerEl', 'textEl', 'bellefilterEl'],
	headerTpl : [
			'<div id="{id}-titleEl" data-ref="titleEl" {tipMarkup}class="',
			Ext.baseCSSPrefix,
			'column-header-inner',
			'<tpl if="empty"> ',
			Ext.baseCSSPrefix,
			'column-header-inner-empty</tpl>">',
			'<span id="{id}-textEl" data-ref="textEl" class="',
			Ext.baseCSSPrefix,
			'column-header-text',
			'{childElCls}">',
			'{text}',
			'</span>',
			'<tpl if="!menuDisabled">',
			'<div id="{id}-triggerEl" data-ref="triggerEl" role="presentation" class="',
			Ext.baseCSSPrefix,
			'column-header-trigger',
			'{childElCls}" style="{triggerStyle}"></div>',
			'</tpl>',
			'</div>',
			'<div id="{id}-bellefilterEl" data-ref="bellefilterEl" class="bellefilter-common-body"></div>',
			'{%this.renderContainer(out,values)%}'],
	alias : 'plugin.belleheaderfilter',
	constructor : function(config) {
		var me = this;
		me.callParent(arguments);
	},
	init : function(grid) {
		var me = this, columns = grid.columns;
		me.grid = grid;

		Ext.each(columns, function(column) {
					column.renderTpl = me.headerTpl;
					var els = column.getChildEls();

					els.bellefilterEl = {
						itemId : "bellefilterEl",
						name : "bellefilterEl"
					};
					column.setChildEls(els);

					column.on("afterrender", function(col) {
								me.createHeaderEl(this);
							});

				});
		// 添加方法
		me.initGridMethods(grid);

		// 监听事件
		me.initStoreEvents(grid);

		me.callParent(arguments);

		if (me.grid.isFilter) {
			grid.removeCls("grid-filter-hide");
		} else {
			grid.addCls("grid-filter-hide");
		}
	},
	initStoreEvents : function(grid) {
		var me = this;
		var store = grid.getStore();

		store.on("load", function() {
					// 数据加载后缓存当前数据
					store.oddData = store.getData().items.belleCopy();
				});

		// 初始化请求参数
		store.on("beforeload", function() {
					// 设置请求参数
					// me.initExtraParams();
				});

	},
	createHeaderEl : function(col) {
		var me = this, grid = me.grid, operator;
		// 判断是否为字段
		if (!col.dataIndex)
			return;
		// 2015-4-28 dwh
		// 获取过滤组件
		var bellefilterEl = col.bellefilterEl;
		if (bellefilterEl && !col.bellefilterObj) {
			// var property=col.sortField||col.belleFilter.sortField

			// 默认属性
			var config = {
				width : "100%",
				renderTo : bellefilterEl.dom
			};

			if (!col.belleFilter) {
				// 默认显示扩展控件
				col.belleFilter = {
					xtype : 'bellefilter'
				};
			} else if (!col.belleFilter.xtype) {
				col.belleFilter.xtype = "bellefilter";
			}
			// 默认类型
			if (!col.belleFilter.filterType) {
				col.belleFilter.filterType = "like";
			}

			config.property = col.dataIndex;
			config.propertyName = col.belleFilter.dataIndex || col.dataIndex; // 别名

			switch (col.belleFilter.filterType) {
				case "like" :
					operator = "15";
					break;
				case "=" :
					operator = "10";
					break;
				case ">" :
					operator = "11";
					break;
				case "<" :
					operator = "12";
					break;
				default :
					operator = "10";
					break;
			}
			col.belleFilter.operator = operator;
			col.belleFilter.hidden = col.belleFilter.isOpen == false
					? true
					: false;

			Ext.applyIf(config, col.belleFilter);
			col.bellefilterObj = Ext.widget(config);
			col.bellefilterObj.grid=grid;
			// 判断是否为扩展控件
			if (col.belleFilter.xtype === "bellefilter") {

				col.bellefilterObj.on("onSelectFilter", function(val, type,
								operator, el) {
							me._changeValueFilter();

						});
				// 过滤所有
				col.bellefilterObj.on("onBelleFilterKeyup", function(val, type,
								operator, el, event) {
							var keyCode = event.keyCode;

							me._keyFilter(keyCode);

						});
				// 过滤本地数据
				col.bellefilterObj.on("onBelleFilterChange", function(val,
								type, operator, el, event) {
							// var isLocal=grid.getFilterLocal(),
							// isFilterSarver=false;
							//					
							// me._changeValueFilter();
						});
			} else {
				col.bellefilterObj.el.on("keyup", function(field, event) {
							var keyCode = event.keyCode;

							me._keyFilter(keyCode);

						});

				col.bellefilterObj.on("change", function(field, event) {
					var isLocal = grid.getFilterLocal(), isFilterSarver = field.inputEl.dom.readOnly;

					me._changeValueFilter();
				});
			}

		}
	},
	// 按下回车时过滤方式
	_keyFilter : function(keyCode) {
		var me = this, grid = me.grid, isLocal = grid.getFilterLocal();

		if (!isLocal) {
			if (keyCode != 13)
				return;
			me._filterServer();
		} else {
			me._filterLocal();
		}
	},
	// 输入数据时过滤方式
	_changeValueFilter : function() {
		var me = this, grid = me.grid, isLocal = grid.getFilterLocal();

		if (!isLocal) {

			me._filterServer();
		} else {
			me._filterLocal();
		}
	},
	initExtraParams : function() {
		var me = this, grid = me.grid, store = grid.getStore(), extraParams = store
				.getProxy().extraParams, filters = me._getFilters() || [];

		if(!extraParams) return;
		
		if (grid.otherfilter) {
			filters = filters.concat(grid.otherfilter);
		}

		// 开启过滤所有状态时设置请求参数
		if (grid.getFilterStatus() == true && !grid.getFilterLocal()) {

			extraParams.queryCondition = Ext.encode(filters);
		} else {
			if (!Ext.isEmpty(grid.otherfilter)) {
				extraParams.queryCondition = Ext.encode(grid.otherfilter);
			} else {
				delete extraParams.queryCondition;
			}
		}

	},
	_getFilters : function() {
		var me = this, grid = me.grid, columns = grid.columns, filters = [], isLocal = grid
				.getFilterLocal();

		if (!grid.isFilter)
			return [];

		Ext.each(columns, function(item) {
					var obj = item.bellefilterObj, filter = {}, val;
					if (obj && obj.inputEl && obj.inputEl.dom && !obj.hidden) {
						if (obj.config.xtype == "combo"
								|| obj.config.xtype == "combobox"||obj.config.xtype=="extcombox") {
							val = obj.getValue();
						} else {
							val = obj.getRawValue();
						}

						if (!Ext.isEmpty(val)) {
							filter = {
								value : val,
								operator : obj.operator,
								property : isLocal
										? obj.property
										: obj.propertyName
							};
							filters.push(filter);
						}
					}
				});
		return filters;
	},
	_filterLocal : function() {
		var me = this, grid = me.grid, store = grid.getStore(), belleFilters = me
				._getFilters();

		if (!store.oddData)
			return;
		var filterData = [];
		filterData = store.oddData.filter(function(item) {
			var isFilter = true;
			Ext.each(belleFilters, function(filter) {
						var property = item.data[filter.property];
						if (Ext.isEmpty(property)) {

							property = "";
						}
						if (Ext.isEmpty(filter.value)) {
							filter.value = "";
						}
						// 其中一个条件不满足时跳出
						switch (filter.operator) {
							// like
							case '15' :
								// 支持不区分大小写
								if (typeof(property) != "string") {
									isFilter = property.toString()
											.toLowerCase().indexOf(filter.value
													.toLowerCase()) >= 0;
								} else {
									isFilter = property
											.toLowerCase()
											.indexOf(filter.value.toLowerCase()) >= 0;
								}

								break;
							// ==
							case '10' :
								isFilter = property == filter.value;
								break;
							case '12' :
								var valType = typeof(property);
								switch (valType) {
									case "number" :
										isFilter = property < parseInt(filter.value);
										break;
									case "string" :
										isFilter = me._comparisonDateValue(
												property, filter.value);

										break;
									default :
										isFilter = false;
										break;
								}
								break;
							case '11' :
								var valType = typeof(property);
								switch (valType) {
									case "number" :
										isFilter = property > parseInt(filter.value);
										break;
									case "string" :
										isFilter = !me._comparisonDateValue(
												property, filter.value);

										break;
									default :
										isFilter = false;
										break;
								}
								break;
						}
						// 不满足当前条件时退出筛选
						if (!isFilter) {
							return false;
						}
					});
			return isFilter;
		});
		me.initExtraParams();
		store.loadData(filterData);
	},
	// 判断val1是否大于val2
	_comparisonDateValue : function(val1, val2) {
		var v1 = Ext.Date.parse(val1, "Y-m-d H:i:s")
				|| Ext.Date.parse(val1, "Y-m-d");
		var v2 = Ext.Date.parse(val2, "Y-m-d H:i:s")
				|| Ext.Date.parse(val2, "Y-m-d");

		if (v1 && v2) {
			return v1.getTime() < v2.getTime();
		} else {
			return false;
		}
	},
	_filterServer : function() {
		var me = this, grid = me.grid, store = grid.getStore();
		if (!grid.isFilter)
			return;

		me.initExtraParams();

		store.loadPage(1);
	},
	// 过滤数据
	initGridMethods : function(grid) {
		var me = this;
		grid.getFilterStatus = function() {
			return grid.isFilter;
		};
		grid.setFilterStatus = function(status) {

			var store = grid.getStore();
			if (status) {
				grid.isFilter = true;
				grid.removeCls("grid-filter-hide");
			} else {
				grid.isFilter = false;
				grid.addCls("grid-filter-hide");
				//关闭时清空过滤值
				me.clearFilterValue();
			}
			grid.fireEvent("onFilterStatusChange", grid.isLocal, grid.isFilter,
					grid);
			me.initExtraParams();
			//if(grid.view)
			//grid.view.refresh();
		};

		grid.getFilterLocal = function() {
			return grid.isLocal;
		};

		grid.setFilterLocal = function(val) {
			grid.setFilterStatus(true);
			grid.isLocal = val;
			me.initExtraParams();
			grid.fireEvent("onFilterLocalChange", me.isLocal, me.isFilter, me);

			if (val) {
				Ext.each(grid.columns, function(item) {
							if (item.bellefilterObj) {
								item.bellefilterObj.show();
							}

						});
			} else {
				Ext.each(grid.columns, function(item) {
							if (item.bellefilterObj
									&& item.bellefilterObj.isOpen == false) {
								item.bellefilterObj.hide();
							}

						});
			}
			//if(grid.view)
			//grid.view.refresh();
		};
		//
		grid.setOtherFilters = function(filters) {
			grid.otherfilter = filters;
			me.initExtraParams();

		};

		grid.getFilters = function() {
			return me._getFilters();
		};
	},
	clearFilterValue :function(){
		var me = this, grid = me.grid, columns = grid.columns, store= grid.getStore();

		Ext.each(columns, function(item) {
			var obj = item.bellefilterObj, filter = {}, val;
			if (obj && obj.inputEl && obj.inputEl.dom && !obj.hidden) {
				obj.setValue("");
			}
		});
		
		if(grid.getFilterLocal()&&!Ext.isEmpty(store.oddData)){
			store.load(store.oddData)
		}
	}
});

Ext.define('Belle_Common.ux.BelleImport', {
	extend : 'Ext.window.Window',
	alias : 'widget.belleimport',
	constructor : function(config) {
		var me = this;

		Ext.apply(me, config);

		Ext.apply(me, {
					ImportUrl : me.workObject.importUrl,
					modelName : me.workObject.modelName
							.substr(me.workObject.modelName.lastIndexOf('.')
									+ 1)
				});

		var initData = me.initGridData();

		Ext.apply(me, {
					initData : initData
				});

		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.items = [me._createHeader(), me._createForm()];

		me.bbar = ['->', {
					glyph : Belle.Icon.btnSave,
					text : "确定",
					itemId : me.id + "_ok",
					handler : me._ok
				}, {
					glyph : Belle.Icon.btnCancel,
					text : "退出",
					itemId : me.id + "_canel",
					handler : me._canel
				}];

		me.callParent(arguments);
	},
	initData : {
		leftGridData : [],
		rightGridData : []
	},
	layout : "vbox",
	title : "数据导入",
	modal : true,
	resizable : false,
	//constrainHeader : true, // 约束在显示范围内
	_gridHeight : 300,
	formWidth : 750,
	ATTRS : {
		btns : {},
		fields : {},
		grids : {}
	},
	initEvents : function() {
		// 注册事件
		var me = this;
		me.ATTRS.grids.leftGrid.on("celldblclick", function(gridView, td,
						cellIndex, record, tr, rowIndex, e, eOpts) {
					if (record.get("isReadOnly")) {
						return;
					}

					var store = me.ATTRS.grids.rightGrid.getStore();
					store.add(record.data);

					gridView.getStore().remove(record);

					gridView.refresh();
					me.ATTRS.grids.rightGrid.getView().refresh();

				});
		me.ATTRS.grids.rightGrid.on("celldblclick", function(gridView, td,
						cellIndex, record, tr, rowIndex, e, eOpts) {

					if (record.get("allowBlank") == false)
						return;

					var store = me.ATTRS.grids.leftGrid.getStore();
					store.add(record.data);

					gridView.getStore().remove(record);

					gridView.refresh();
					me.ATTRS.grids.leftGrid.getView().refresh();
				});

		me.ATTRS.btns.btnAdd.on("click", function() {
					me._add();
				});
		me.ATTRS.btns.btnRemove.on("click", function() {
					me._remove();
				});
		me.ATTRS.btns.btnUp.on("click", function() {
					me._up();
				});
		me.ATTRS.btns.btnDown.on("click", function() {
					me._down();
				});

	},
	_fields : ['dataIndex', 'header', 'isReadOnly', 'allowBlank', 'mainKey',
			'unique', 'isexist', 'modelName'],
	_createHeader : function() {
		var me = this;
		var desc = me.title||"数据导入(文件不能大于5M)";
		var header = Ext.create('Ext.Component', {
					itemId : me.id + "_header",
					html : "<div class='belle-import-des' style='width:"
							+ me.formWidth + "px;'>" + desc + "</div>"
				});
		return header;
	},
	initGridData : function() {
		var me = this, columns = me.workObject.columns || [], grid, store, leftGridData = [], rightGridData = [];

		// 生成行数据
		Ext.each(columns, function(item) {

			var header = item.text, allowBlank = true, isReadOnly = false, editor = item.config.editor;

			// if(item.hidden) return;
			// 默认允许编辑的列都可以导入
			if (editor) {
				isReadOnly = false;
				if (editor.allowBlank == false) {
					allowBlank = false;
				} else {
					allowBlank = true;
				}
			} else {
				isReadOnly = true;
			}
			// 自定义属性，判断是否为导入字段
			if (item.isImport == true) {
				isReadOnly = false;
			} else if (item.isImport === false) {
				isReadOnly = true;
				allowBlank = true;
			}
			// 导入列必填
			if (item.isRequired == true) {
				isReadOnly = false;
				allowBlank = false;
			} else if (item.isRequired == false) {
				allowBlank = true
			}

			var dataItem = {
				header : header,
				dataIndex : item.dataIndex,
				isReadOnly : isReadOnly,
				allowBlank : allowBlank,
				mainKey : item.mainKey ? true : false,
				isexist : item.isexist ? true : false,
				unique : item.unique ? true : false,
				modelName : item.modelName || me.modelName
			};
			// 必填字段默认选中
			if (dataItem.allowBlank == false) {
				rightGridData.push(dataItem);
			} else {
				leftGridData.push(dataItem);
			}

		});

		return {
			leftGridData : leftGridData,
			rightGridData : rightGridData
		};
	},
	_createLeftGrid : function() {
		// 创建表单
		var me = this, columns = me.workObject.columns || [], grid, store, data = [];

		store = new Ext.data.Store({
					fields : me._fields,
					data : me.initData.leftGridData
					,

				});

		grid = new Ext.create('Ext.grid.Panel', {
					columnWidth : 0.3,

					height : me._gridHeight,
					selModel : Ext.create('Ext.selection.RowModel', {
								mode : "SIMPLE"
							}),// 支持多选
					// hideHeaders:true,
					columns : [{
						header : "数据列",
						dataIndex : "isReadOnly",
						width : "100%",
						renderer : function(val, mate, rec, rowIndex, celindex,
								store, grid) {
							var text = rec.get("header");

							if (rec.get("allowBlank") == false) {
								mate.style = "color:blue;";
							} else if (rec.get("isReadOnly") == false) {
								mate.style = "color:green !important;";
							}

							if (rec.get("isReadOnly")) {

								text += "<span style='color:red;'>[只读]</span>";
							}

							return text;
						}
					}],
					store : store
				});

		store.sort({
					property : "isReadOnly",
					direction : 'ASC'
				});

		me.ATTRS.grids.leftGrid = grid;
		return grid;

	},
	_createRightGrid : function() {
		// 创建表单
		var me = this, grid, store;

		store = new Ext.data.Store({
					fields : me._fields,
					data : me.initData.rightGridData
				});

		grid = new Ext.create('Ext.grid.Panel', {
					columnWidth : 0.5,
					height : me._gridHeight,
					selModel : Ext.create('Ext.selection.RowModel', {
								mode : "SIMPLE"
							}),// 支持多选
					viewConfig : {
						plugins : {
							ptype : 'gridviewdragdrop',
							dragText : '拖动行排序'
						}
					},
					// hideHeaders:true,
					columns : [{
								header : "Excel列",
								dataIndex : "header",
								width : "27.5%",
								renderer : function(val, mate, rec) {
									if (rec.get("allowBlank") == false) {
										mate.style = "color:blue;";
									} else if (rec.get("isReadOnly") == false) {
										mate.style = "color:green !important;";
									}
									return val;
								}
							}, {
								header : "字段类型",
								dataIndex : "allowBlank",
								width : "18%",
								renderer : function(val, mate, rec) {

									if (rec.get("allowBlank") == false) {
										mate.style = "color:blue;";
									} else if (rec.get("isReadOnly") == false) {
										mate.style = "color:green !important;";
									}

									if (!val) {
										return '必填';
									} else {
										return '选填';
									}
								}
							}, {
								header : "是否唯一",
								dataIndex : "unique",
								width : "18%",
								renderer : function(val, mate, rec) {
									if (rec.get("allowBlank") == false) {
										mate.style = "color:blue;";
									} else if (rec.get("isReadOnly") == false) {
										mate.style = "color:green !important;";
									}

									return val ? "是" : "否";
								}
							}, {
								header : "是否存在",
								dataIndex : "isexist",
								width : "18%",
								renderer : function(val, mate, rec) {
									if (rec.get("allowBlank") == false) {
										mate.style = "color:blue;";
									} else if (rec.get("isReadOnly") == false) {
										mate.style = "color:green !important;";
									}

									return val ? "是" : "否";
								}
							}, {
								header : "验证方式",
								dataIndex : "mainKey",
								width : "18%",
								renderer : function(val, mate, rec) {
									if (rec.get("allowBlank") == false) {
										mate.style = "color:blue;";
									} else if (rec.get("isReadOnly") == false) {
										mate.style = "color:green !important;";
									}

									if (val) {
										return '不允许重复';
									} else {
										return '允许重复';
									}
								}
							}],
					plugins : {
						ptype : 'cellediting',
						clicksToEdit : 1
					},
					store : store
				});

		me.ATTRS.grids.rightGrid = grid;
		return grid;

	},
	_createForm : function() {
		// 创建表单
		var me = this, form;

		form = new Ext.form.Panel({
			baseParams:{},
			method : "POST",
			timeout : "120",
			url : me.ImportUrl,
			border : false,
			width : me.formWidth,
			defaults : {
				style : 'margin:5px 0px 5px 5px;'
				,
			},
			items : [{
				border : false,
				layout : "column",
				items : [me._createLeftGrid(), {
							border : true,
							height : me._gridHeight,
							columnWidth : 0.2,
							layout : {
								align : 'middle',
								pack : 'center',
								type : 'vbox'
							},
							defaults : {
								xtype : "button",
								width : 80,
								height : 30,
								cls : "belle-export"
							},
							items : [{
										text : "添加",
										iconCls : "belle-btn-icon-right",
										style : "margin-bottom:20px;",
										itemId : me.id + "_add"
									}, {
										text : "删除",
										iconCls : "belle-btn-icon-left",
										style : "margin-bottom:20px;",
										itemId : me.id + "_remove"
									}, {
										text : "上",
										iconCls : "belle-btn-icon-up",
										style : "margin-bottom:20px;margin-top:20px;",
										itemId : me.id + "_up"
									}, {
										text : "下",
										iconCls : "belle-btn-icon-down",
										itemId : me.id + "_down"
									}]
						}, me._createRightGrid()]
			}, {
				layout : "column",
				xtype : 'fieldset',
				title : "高级设置",
				collapsible : true,
				defaults : {
					labelWidth : 180,
					columnWidth : 1
				},
				items : [{
					itemId : me.id + "_importfile",
					style : "margin-top:5px;",
					fieldLabel : "导入文件(.xls,.xlsx)",
					xtype : "filefield",
					name : 'importFileValue',
					msgTarget : 'side',

					allowBlank : false,
					buttonText : '选择...',
					validator : function(val) {
						if (Ext.isEmpty(val)) {
							return;
						}
						// 验证文件
						var lasIndex = val.lastIndexOf('\\'), filebame = val
								.substring(lasIndex + 1), ext = filebame
								.substring(filebame.lastIndexOf('.')), valiText;

						if (ext != ".xls" && ext != ".xlsx") {
							valiText = "文件格式错误，只支持.xls、.xlsx";
						} else {
							valiText = true;
						}
						var fileSize = this.fileInputEl.dom.files[0].size||1;
						if(fileSize>5124880){
							valiText="上传的文件太大(导入文件不能大于5M)";
						}
						return valiText;
					}
				}, {
					itemId : me.id + "_isValidData",
					name : "isValidateAll",
					fieldLabel : "是否全部验证通过才导入",
					layout : 'hbox',
					style : "margin-top:5px;",
					xtype : "radiogroup",
					defaults : {
						style : "margin-left:10px;"
					},
					items : [{
								checked : true,
								inputValue : "Y",
								name : "isValidateAll",
								boxLabel : '是(Y)',
								itemId : me.id + 'checkboxY'
							}, {
								inputValue : "N",
								name : "isValidateAll",
								boxLabel : '否(N)',
								itemId : me.id + 'checkboxN'
							}]

				}]
			}]
		});

		// 获取控件
		me.ATTRS.btns.btnAdd = form.down('[itemId=' + me.id + '_add]');
		me.ATTRS.btns.btnRemove = form.down('[itemId=' + me.id + '_remove]');
		me.ATTRS.btns.btnUp = form.down('[itemId=' + me.id + '_up]');
		me.ATTRS.btns.btnDown = form.down('[itemId=' + me.id + '_down]');

		me.ATTRS.fields.isValidData = form.down('[itemId=' + me.id
				+ '_isValidData]');
		me.form = form;
		return form;
	},
	_add : function() {
		var me = this, grid = me.ATTRS.grids.leftGrid, store = grid.getStore(), sel = grid
				.getSelection();

		if (!sel || sel.length <= 0) {
			Belle.alert("请选择左边数据列");
			return;
		}

		Ext.each(sel, function(item) {
					if (!item.data.isReadOnly) {
						me.ATTRS.grids.rightGrid.getStore().add(item.data);
						store.remove(item);
					}

				});
		grid.getView().refresh();
		me.ATTRS.grids.rightGrid.getView().refresh();

	},
	_remove : function() {
		var me = this, grid = me.ATTRS.grids.rightGrid, store = grid.getStore(), sel = grid
				.getSelection();

		if (!sel || sel.length <= 0) {
			Belle.alert("请选择右边Excel列");
			return;
		}

		Ext.each(sel, function(item) {

					if (item.get("allowBlank") == false)
						return;

					me.ATTRS.grids.leftGrid.getStore().add(item.data);
					store.remove(item);
				});

		grid.getView().refresh();
		me.ATTRS.grids.leftGrid.getView().refresh();
	},
	_up : function() {
		var me = this, grid = me.ATTRS.grids.rightGrid, store = grid.getStore(), sel = grid
				.getSelection();

		Ext.each(sel, function(rec) {

					var index = store.indexOf(rec);
					if (index > 0) {
						store.remove(rec);
						store.insert(index - 1, rec);
					}
				});

		grid.setSelection(sel);
		grid.getView().refresh();
	},
	_down : function() {
		var me = this, grid = me.ATTRS.grids.rightGrid, store = grid.getStore(), sel = grid
				.getSelection();

		Ext.each(sel, function(rec) {

					var index = store.indexOf(rec);
					if (index < store.getCount() - 1) {
						store.remove(rec);
						store.insert(index + 1, rec);

					}
				});

		grid.setSelection(sel);
		grid.getView().refresh();
	},
	// 点击确定
	_ok : function() {
		var me = this, winPanel = me.up("window"), form = winPanel.down("form");
		if (!form.isValid())
			return;

		winPanel._export();
	},

	_canel : function() {
		var me = this;
		Belle.confirm("是否关闭？", function(flag) {
					if (flag != "yes") {
						return;
					}
					me.up("window").close();
				});
	},
	_export : function() {
		var me = this, form = me.form, colNames = [], mustArray = [], mainKey = [], store = me.ATTRS.grids.rightGrid
				.getStore(),

		uniqueConditions = {}, validationConditions = [];

		if (store.count() <= 0) {

			Belle.alert("请选择导入的列");
			return;
		}

		Belle.confirm("是否确定导入数据？", function(flag) {
			if (flag != "yes") {
				return;
			}

			Ext.each(store.getData().items, function(rec) {
				var unique = rec.get("unique"), isexist = rec.get("isexist"), modelName = rec
						.get("modelName");

				if (isexist) {
					validationConditions.push({
								validationType : "isexist",
								conditionValue : rec.get("dataIndex"),
								validationModel : modelName
							});
				}

				if (unique) {
					if (Ext.isEmpty(uniqueConditions[me.modelName])) {
						uniqueConditions[me.modelName] = {
							validationType : "unique",
							conditionValue : rec.get("dataIndex"),
							validationModel : me.modelName
						}
					} else {
						uniqueConditions[me.modelName].conditionValue += ","
								+ rec.get("dataIndex");
					}

				}

				colNames.push(rec.get("dataIndex"));
				mustArray.push(!rec.get("allowBlank"));
				if (rec.get("mainKey")) {
					mainKey.push(rec.get("dataIndex"));
				}

			});

			for (var key in uniqueConditions) {
				validationConditions.push(uniqueConditions[key]);
			}
			form.getForm().baseParams={};
			if(me.fireEvent("beforeImprort",form,me)===false) return;
			
			if(!form.getForm().baseParams){
				form.getForm().baseParams={};
			}
			
			form.getForm().baseParams.colNames=colNames.join(',');
			form.getForm().baseParams.mustArray=mustArray.join(',');
			form.getForm().baseParams.mainKey=mainKey.join(',');
			form.getForm().baseParams.validationConditions=Ext.encode(validationConditions);
			
			form.getForm().submit({
						success : function(form, action) {
							Belle.alert('导入成功！');
							 me.workObject.store.loadPage(1);
						},
						failure : function(form, action) {
							me.workObject.store.loadPage(1);
							try {
								Belle.alert(action.result.result.msg);
							} catch (e) {
								Belle.alert("导入失败");
							}

						}
					});
		});
	}
});
Ext.define('Belle_Common.ux.BelleSizeEditor', {
	extend : 'Ext.window.Window',
	alias : 'widget.sizeeditor',
	constructor : function(config) {
		var me = this;

		Ext.apply(me, config);
		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;

		if (Ext.isEmpty(me.billNo)) {
			Belle.alert("请设置单据编号");
			return;
		}

		if (Ext.isEmpty(me.customerNo)) {
			Belle.alert("请设置客户编号");
			return;
		}

		if (Ext.isEmpty(me.regionNo)) {
			Belle.alert("拉取客户信息失败");
			return;
		}

		// 初始化数据
		me._initData();
		// 生成表单
		me.items = [me._createForm()];
		me.callParent(arguments);
		// 初始化按钮
		me._initTooblrStatus();

	},
	isEditor : false, // 是否编辑中
	layout : "vbox",
	title : "新增",
	modal : true,
	resizable : false,
	//constrainHeader : true,
	ATTRS : {
		btns : {},
		fields : {},
		grids : {}
	},
	initData : {
		body : {
			packingType : "C",
			packingQty : 0,
			sizeQty : 0,
			boxQty : 0,
			unitPrice : 0,
			materialCode : "",
			materialName : "",
			pointNo : "",
			pointName : "",
			materialNo : "",
			sizeTypeNo : ""
		},
		list : []
	},
	customerNo : "", // 客户编码
	billNo : "", // 单据编码
	getSizeUrl : Belle.mdmPath
			+ "bas_material/listMSizeVo.json?selectVoName=SelectListByVoMSizeByMaterial&mapperClassType=BasMaterialMapper",
	getPrice : Belle.sdsPath + "bl_co_dtl/getPrice.json",
	saveUrl : Belle.sdsPath + "bl_co_dtl/singlesave.json",
	updateUrl : Belle.sdsPath + "bl_co_dtl/singleupdate.json",
	initEvents : function() {
		// 注册事件
		var me = this, store = me.gridForm.getStore(), fields = me.ATTRS.fields, btns = me.ATTRS.btns;

		btns.btnClose.on("click", function() {
					me._canel();
				});

		btns.btnSave.on("click", function() {
					me._save();
				});

		btns.btnReduction.on("click", function() {
					me._reduction();
				});

		btns.btnSaveAndClose.on("click", function() {
					me._saveAndClose();
				});

		fields.materialNo.on("onBeforeShowWin", function(field) {
					if (!me.gridForm.isUpdate) {

						return true;
					} else {

						Belle.alert("网格数据编辑中！");
						return false;
					}
				});

		fields.pointNo.on("change", function(field) {
					var value = field.getValue();
					if (Ext.isEmpty(value)) {
						fields.pointName.setValue("");
					}

				});

		fields.boxQty.on("change", function(field) {
					me._initValues();

				});

		fields.packingQty.on("change", function(field) {
					me._initValues();

				});

		fields.packingType.on("change", function(field) {
					val = field.getValue();

					if (Ext.isEmpty(val)) {
						return;
					}

					me._initValues(val);

				});

		store.on("update", function() {
					me._initValues();
					me._initGridDataChangeStatus();
					me._initTooblrStatus();

				});

		store.on("datachanged", function() {
					me._initValues();
					me._initGridDataChangeStatus();
					me._initTooblrStatus();

				});

		me.form.on("dirtychange", function(field, dirty) {
					me.form.isUpdate = dirty;
					me._initTooblrStatus();
				});

		me.on("beforeclose", function() {
					if (!me.form.isUpdate && !me.gridForm.isUpdate) {
						return true;
					}

					if (me.isCanClose) {
						return true;
					} else {
						Belle.confirm("数据编辑中，是否确认退出？", function(flag) {

									if (flag == "no")
										return;
									me.isCanClose = true;
									me.close();
								});

						return false;
					}

				});

	},
	_initData : function() {
		var me = this, sizeQty = 0, gridQty = 0,
		body;
		if (!me.initData || !me.initData.body || !me.initData.body.packingType)
			return;

		var body = me.initData.body, list = me.initData.list;
		//箱包
		if(me.brandNo==="XB01"){
			
			body.packingQty = 1;
			body.packingType = "B";

		}
		
		Ext.each(list, function(item) {
					// 数量除
					if (body.packingType === "C") {
						item.qty = item.qty / body.boxQty;
					}

					gridQty += item.qty;

				});

		switch (me.initData.body.packingType) {
			case "C" :
				body.packingQty = gridQty;

				body.sizeQty = body.boxQty * body.packingQty;
				break;
			default :
				body.sizeQty = gridQty;
				body.boxQty = body.sizeQty / body.packingQty;
				break;
		}

	},
	_initValues : function() {
		var me = this, fields = me.ATTRS.fields, grid = me.gridForm, store = grid
				.getStore(), gridQty = 0, sizeQty = 0, packingType = fields.packingType
				.getValue();

		// 计算表格输入总数
		Ext.each(store.data.items, function(item) {

					gridQty += item.data.qty;

				});
		switch (packingType) {
			case "C" :
				// 配码

				fields.packingQty.setValue(gridQty);

				var boxQty = fields.boxQty.getValue(), packingQty = fields.packingQty
						.getValue();

				fields.boxQty.setReadOnly(false);
				fields.packingQty.setReadOnly(true);

				if (!Ext.isEmpty(boxQty) && !Ext.isEmpty(packingQty)) {
					sizeQty = boxQty * packingQty;
				}

				fields.sizeQty.setValue(sizeQty);
				break;
			default :
				//箱包品牌时，装箱对数和装箱方式不能修改。装箱对数默认为1，装箱方式默认为独码
				if(me.brandNo==="XB01"){
					fields.packingQty.setReadOnly(true);
					fields.packingType.setReadOnly(true);
					fields.packingQty.setValue(1);
					fields.packingType.setValue("B");
				}
				else{
					fields.packingQty.setReadOnly(false);
				}
				
				fields.boxQty.setReadOnly(true);
				

				fields.sizeQty.setValue(gridQty);
				sizeQty = fields.sizeQty.getValue();
				packingQty = fields.packingQty.getValue();

				if (packingQty <= 0) {
					fields.boxQty.setValue(0);
					return;
				}

				me.form.isValid();

				if (sizeQty % packingQty > 0) {
					// Belle.alert("请检查装箱对数，不允许非整箱订货");

					fields.packingQty.selectText();
					fields.boxQty.setValue(0);
					return;
				}
				if(me.brandNo==="XB01"){
					fields.boxQty.setValue(1);
				}
				else{
					fields.boxQty.setValue(sizeQty / packingQty);
				}
				
				break;
		}
	},
	_createGrid : function() {
		// 创建表单
		var me = this, grid, store
		initData = Belle.clone(me.initData);

		store = new Ext.data.Store({
					fields : ["sizeName", "sizeNo", {
								name : "qty",
								type : "number"
							}, {
								name : "seqNo",
								type : "number"
							}, "sizeTypeNo"],
					proxy : {
						type : 'ajax',
						url : me.getSizeUrl,
						reader : {
							type : 'json',
							rootProperty : 'list'
						},
						autoLoad : false
					}
				});

		grid = new Ext.create('Ext.grid.Panel', {
					// border:false,
					style : "margin:5px 0px 0px 0px;",
					columnWidth : 1,
					columnLines : true,
					height : 200,
					columns : [{
								header : "尺码",
								dataIndex : "sizeNo",
								width : 100
							}, {
								header : "数量",
								dataIndex : "qty",
								width : 100,
								value : 0,
								editor : {
									xtype : "numberfield",
									minValue : 0
								}
							}],
					plugins : [{
								ptype : 'cellediting',
								clicksToEdit : 1
							}],
					store : store
				});
		store.loadData(initData.list);
		me.ATTRS.grids.gridForm = grid;
		return grid;

	},
	_createForm : function() {
		// 创建表单
		var me = this, form, grid = me._createGrid(), initData = Belle
				.clone(me.initData);

		form = new Ext.form.Panel({
			timeout : "120",
			border : false,
			trackResetOnLoad : true, // 设置每次新加载数据为默认值
			width : 600,
			defaults : {
				style : 'margin:5px 5px 0px 5px;',
				columnWidth : 0.5,
				labelWidth : 80
			},
			layout : "column",
			items : [{
				itemId : "materialNo",
				name : "materialNo",
				xtype : "materialfield",
				reference : "materialNo",
				fieldLabel : "产品编号",
				brandNo : me.brandNo,// 品牌编码
				billNo : me.billNo,
				afterCall : function(field, data, record) {
					if (Ext.isEmpty(data)) {
						Belle.alert("返回数据异常");
						return;
					}
					var fields = me.ATTRS.fields;
					// 绑定数据
					fields.materialCode.setValue(data.materialCode);
					fields.materialName.setValue(data.materialName);
					fields.materialNo.setValue(data.materialNo);

					// 加载价格
					me._getPrice(data.materialNo, me.customerNo, data.textures,
							data.categoryNo, function(price,salePriceType) {
								fields.unitPrice.setValue(price);
								fields.salePriceType.setValue(salePriceType);
							});

					// 加载尺码信息
					var store = me.gridForm.getStore(), proxy = store
							.getProxy(), value = field.getValue();

					if (Ext.isEmpty(value)) {

						store.removeAll();
						fields.unitPrice.setValue("");
						fields.materialName.setValue("");
						fields.materialCode.setValue("");
						return;
					}

//					var filters = [{
//								"property" : "materialNo",
//								"value" : value,
//								"operator" : 10
//							}];
//					proxy.extraParams.queryCondition = Ext.encode(filters);
					proxy.extraParams.materialNo=value;
					store.loadPage(1);
				},
				allowBlank : false,
				readOnly : me.winType === "update"
			}, {
				itemId : "materialCode",
				name : "materialCode",
				xtype : "textfield",
				reference : "materialCode",
				fieldLabel : "产品ID",
				readOnly : true,
				allowBlank : false
			}, {
				itemId : "materialName",
				name : "materialName",
				xtype : "textfield",
				reference : "materialName",
				fieldLabel : "产品名称",
				readOnly : true,
				allowBlank : false
			}, {
				itemId : "unitPrice",
				name : "unitPrice",
				xtype : "numberfield",
				reference : "unitPrice",
				fieldLabel : "单价",
				readOnly : true,
				allowBlank : false,
				minValue : 0
			}, {
				itemId : "sizeQty",
				name : "sizeQty",
				xtype : "numberfield",
				reference : "sizeQty",
				fieldLabel : "数量",
				readOnly : true,
				allowBlank : false,
				minValue : 0,
				validator : function(val) {
					if (val <= 0) {
						return "数量不能小于1";
					} else {
						return true;
					}
				}
			}, {
				itemId : "boxQty",
				name : "boxQty",
				xtype : "numberfield",
				reference : "boxQty",
				fieldLabel : "箱数",
				readOnly : true,
				allowBlank : false,
				minValue : 0,
				validator : function(val) {
					if (val <= 0) {
						return "箱数不能小于1";
					} else {
						return true;
					}
				}
			}, {
				itemId : "packingQty",
				name : "packingQty",
				xtype : "numberfield",
				reference : "packingQty",
				fieldLabel : "装箱对数",
				allowBlank : false,
				minValue : 0,
				validator : function(val) {
					if (val <= 0) {
						return "装箱对数不能小于1";
					}
					var fields = me.ATTRS.fields, packingType = fields.packingType
							.getValue(), sizeQty = fields.sizeQty.getValue(), packingQty = fields.packingQty
							.getValue();
					if (packingType != "C") {

						if (packingQty <= 0) {
							return true;
						}

						if (sizeQty % packingQty > 0) {
							return "请检查装箱对数，不允许非整箱订货";
						} else {
							return true;
						}
					} else {
						return true;
					}
				}
			}, {
				name : "packingType",
				itemId : "packingType",
				xtype : "combo",
				reference : "packingType",
				fieldLabel : "装箱方式",
				allowBlank : false,
				store : new Belle_Common.store.CmnDict({
							dictCode : 'packing_type',
							module : me.module
						}),
				valueField : 'itemCode',
				displayField : "itemName"
			}, {
				name : "pointNo",
				itemId : "pointNo",
				xtype : "deliverypointfield",
				reference : "pointNo",
				fieldLabel : "收货地点",
				allowBlank : false,
				regionNo : me.regionNo,
				afterCall : function(field, data, record) {
					field.setValue(data.pointNo);
					me.ATTRS.fields.pointName.setValue(data.pointName);
				}
			}, {
				name : "pointName",
				itemId : "pointName",
				xtype : "textfield",
				reference : "pointName",
				fieldLabel : "收货地点名称",
				readOnly : true
			}, {
				name : "sizeTypeNo",
				itemId : "sizeTypeNo",
				xtype : "textfield",
				reference : "sizeTypeNo",
				fieldLabel : "尺码类型",
				readOnly : true,
				hidden : true
			}, {
				name : "salePriceType",
				itemId : "salePriceType",
				xtype : "textfield",
				reference : "salePriceType",
				fieldLabel : "价格类型",
				readOnly : true,
				hidden : true
			}, grid],
			dockedItems : [{
						xtype : 'toolbar',
						dock : 'top',
						items : [{
									itemId : me.id + "_BtnSave",
									text : '保存',
									glyph : Belle.Icon.btnSave
								}, {
									itemId : me.id + "_BtnReduction",
									text : '还原',
									glyph : Belle.Icon.btnUndo
								}, {
									itemId : me.id + "_BtnSaveAndClose",
									text : '保存并退出',
									glyph : Belle.Icon.btnSave
								}, {
									itemId : me.id + "_BtnClose",
									text : '退出',
									glyph : Belle.Icon.btnCancel
								}]
					}]
		});

		// 获取控件
		me.ATTRS.btns.btnSave = form.down('[itemId=' + me.id + '_BtnSave]');
		me.ATTRS.btns.btnReduction = form.down('[itemId=' + me.id
				+ '_BtnReduction]');
		me.ATTRS.btns.btnSaveAndClose = form.down('[itemId=' + me.id
				+ '_BtnSaveAndClose]');
		me.ATTRS.btns.btnClose = form.down('[itemId=' + me.id + '_BtnClose]');

		me.ATTRS.fields.materialNo = form.down('[itemId=materialNo]');
		me.ATTRS.fields.unitPrice = form.down('[itemId=unitPrice]');
		me.ATTRS.fields.materialName = form.down('[itemId=materialName]');
		me.ATTRS.fields.materialCode = form.down('[itemId=materialCode]');

		me.ATTRS.fields.sizeQty = form.down('[itemId=sizeQty]');
		me.ATTRS.fields.boxQty = form.down('[itemId=boxQty]');
		me.ATTRS.fields.pointName = form.down('[itemId=pointName]');
		me.ATTRS.fields.pointNo = form.down('[itemId=pointNo]');
		me.ATTRS.fields.sizeTypeNo = form.down('[itemId=sizeTypeNo]');

		me.ATTRS.fields.packingQty = form.down('[itemId=packingQty]');
		me.ATTRS.fields.packingType = form.down('[itemId=packingType]');
		
		me.ATTRS.fields.salePriceType = form.down('[itemId=salePriceType]');

		//箱包
		if(me.brandNo==="XB01"){
			me.ATTRS.fields.packingQty.setReadOnly(true);
			me.ATTRS.fields.packingType.setReadOnly(true);
		}
		
		var formStore = new Ext.data.Store({
					fields : ["materialNo", "materialCode", "materialName",
							"sizeQty", "boxQty"],
					data : initData.body
				});

		// 加载数据
		form.loadRecord(formStore.getAt(0));

		me.gridForm = grid;
		me.form = form;
		return form;
	},
	_getPrice : function(materialNo, customerNo, textures, categoryNo, callback) {
		var me = this;

		Ext.Ajax.request({
					url : me.getPrice,
					params : {
						materialNo : materialNo,
						customerNo : customerNo,
						priceType : 3,
						textures : textures,// 材质类别
						categoryNo : categoryNo
						// 小类
					},
					success : function(response) {
						var data = Ext.decode(response.responseText), price = 0,salePriceType="";
						if (!Ext.isEmpty(data) && !Ext.isEmpty(data.Price)
								&& !Ext.isEmpty(data.Price.unitPrice)) {
							price = data.Price.unitPrice;
							salePriceType = data.Price.priceType;
						} 
						if (typeof callback === "function") {
							callback(price,salePriceType);
						}
					},
					failure : function(response, opts) {
						if (typeof callback === "function") {
							callback(price,salePriceType);
						}
						Ext.MessageBox.show({
									title : '错误提示',
									msg : response.responseText,
									height : 300,
									width : 400
								});
					}
				});
	},
	_initTooblrStatus : function() {
		var me = this, grid = me.gridForm, btns = me.ATTRS.btns, isUpdate = grid.isUpdate
				|| me.form.isUpdate;

		btns.btnSave.setDisabled(!isUpdate);
		btns.btnSaveAndClose.setDisabled(!isUpdate);
		btns.btnReduction.setDisabled(!isUpdate);
	},
	_initGridDataChangeStatus : function() {
		var me = this, grid = me.gridForm, store = grid.getStore(), isUpdate = false;

		Ext.each(store.data.items, function(item) {

					if (item.dirty) {
						isUpdate = true
						return false;
					}
				});
		grid.isUpdate = isUpdate;
		return isUpdate;
	},
	_reduction : function() {
		var me = this, data = Belle.clone(me.initData), fields = me.ATTRS.fields, store = me.gridForm
				.getStore();
		if (me.winType == "add") {
			store.removeAll();
			me.form.reset(true);
		} else {

			store.loadData(data.list);

			fields.materialCode.setValue(data.body.materialCode);
			fields.materialName.setValue(data.body.materialName);
			fields.materialNo.setValue(data.body.materialNo);
			fields.packingType.setValue(data.body.packingType);
			if (data.body.packingType === "C") {
				fields.sizeQty.setValue(data.body.sizeQty);
				fields.boxQty.setValue(data.body.boxQty);
				fields.packingQty.setValue(data.body.packingQty);
			} else {
				fields.boxQty.setValue(data.body.boxQty);
				fields.packingQty.setValue(data.body.packingQty);
				fields.sizeQty.setValue(data.body.sizeQty);
			}

			fields.pointNo.setValue(data.body.pointNo);
			fields.pointName.setValue(data.body.pointName);
			fields.unitPrice.setValue(data.body.unitPrice);

			me.form.getForm().setValues(data.body);
		}
	},
	_saveAndClose : function() {
		var me = this;
		me._callSave(function(isSave) {

					if (isSave) {

						me.mastergrid.getStore().load();
						me.grid1.getStore().load();

						me.isCanClose = true;
						me.close();
					}
				});
	},
	_callSave : function(callback) {
		var me = this, form = me.form, grid = me.gridForm, store = grid
				.getStore(), saveData = Belle.clone(me.initData), list = [], values = form
				.getValues(), url = (me.winType == "update"
				? me.updateUrl
				: me.saveUrl), sizeList = [], sizeTypeNo;
		if (!form.isValid())
			return;

		Ext.each(store.data.items, function(item, i) {

					var qty = item.data.qty;

					if (qty > 0) {
						// 配码时数量*箱数
						if (values.packingType === "C") {
							list.push({
										qty : qty * values.boxQty,
										sizeQty : qty * values.boxQty,
										sizeNo : item.data.sizeNo
									});
						} else {
							list.push({
										qty : qty,
										sizeQty : qty,
										sizeNo : item.data.sizeNo
									});
						}

					}
					sizeTypeNo = item.data.sizeTypeNo;
					sizeList.push(Belle.clone(item.data));
				});

		saveData.body.billNo = me.billNo;
		Ext.apply(saveData.body, values);
		delete saveData.body.lineId;

		saveData.body.sizeTypeNo = values.sizeTypeNo || sizeTypeNo
		saveData.body.divisionNo = me.divisionNo;
		// 判断是否为编辑
		// if(me.winType=="update"){
		// saveData.body.seqNo=me.initData.body.seqNo;
		// }

		saveData.list = list; // 尺码
		me.form.mask("数据提交中...");
		Ext.Ajax.request({
					method : "POST",
					url : url,
					params : {
						body : Ext.encode(saveData.body),
						list : Ext.encode(saveData.list)
					},
					success : function(response) {

						var data = Ext.decode(response.responseText);

						me.form.unmask();
						if (data.result.resultCode == "0") {
							if (me.winType === "update") {
								// 设置默认值
								me.initData.body = Belle.clone(saveData.body);
								me.initData.body.materialName = values.materialName;
								me.initData.body.pointName = values.pointName;
								me.initData.list = sizeList;
							}
						} else {
							Belle.alert(data.result.msg);
						}

						if (typeof callback === "function") {
							callback(data.result.resultCode == "0");
						}

					},
					failure : function(response, opts) {
						me.form.unmask();
						Ext.MessageBox.show({
									title : '错误提示',
									msg : response.responseText,
									height : 300,
									width : 400
								});

						if (typeof callback === "function") {
							callback();
						}
					}
				});
	},
	_save : function() {

		var me = this;

		me._callSave(function(isSave) {

					if (isSave) {
						me._reduction();

						me.mastergrid.getStore().load();
						me.grid1.getStore().load();
					}

				});

	},
	_canel : function() {
		var me = this, grid = me.gridForm, form = me.form;

		me.close();
	}
});
/**
 * Description: comobox扩展控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月18日上午8:39:50
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月18日 yu.jh 2015年3月28日 liutao
 * 因联动下拉框修改 只有在联动的时候才会传入store 其他都以url方式请求处理 建议使用时务必指定valuemember/displaymember
 */
Ext.define('Belle_Common.ux.ComboCustom', {
	extend : 'Ext.form.field.ComboBox',
	alias : 'widget.extcombox',
	queryMode : 'local',
	displayField : null,
	displaymember : null,
	valueField : null,
	valuemember : null,
	store : null,
	displayvalue : null,
	emptyText : "请选择",
	forceSelection : true,
	autoLoad:true,
	initComponent : function() {
		var me = this;

		var tt = [];
		var sstore = null;
		if (me.store == null) {
			if (me.valuemember == null) {
				me.valueField = "num";
				me.displayField = "name";
				var s = me.displayvalue.split(":");
				for (var i = 0; i < s.length; i++) {
					var v = s[i].split("=");
					var obj = {};
					var s1 = v[0];
					var s2 = v[1];
					obj.num = s1;
					obj.name = s2;
					tt.push(obj);
				}
				sstore = Ext.create('Ext.data.Store', {
							fields : [me.valueField, me.displayField],
							data : tt
						});
			} else {

				me.valueField = me.valuemember;
				me.displayField = me.displaymember;
				sstore = Ext.create('Belle_Common.store.Base', {
					fields : [me.valueField, me.displayField, 'enableFlag'],
					proxy : {
						url : Belle.setUrlModuleInfo(me, me.displayvalue)
					},
					remoteSort : false,
					remoteFilter : false
				});
			}
			if (me.autoLoad){
				sstore.reload();
			}
			me.store = sstore;
		} else {
			me.valueField = me.valuemember;
			me.displayField = me.displaymember;
		}
		//只有一条记录时，默认选中
		//me.store.on('load',function(store){
		//	if(store && store.getCount()==1){
		//		me.setValue(store.getAt(0).get(me.valueField)||'');
		//	}
		//});
		me.callParent();
	}
});
/**
 * Description: 启用状态 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: wudefeng Createdate:
 * 2015/1/28 0028
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */
Ext.define('Belle_Common.ux.ComboUseFlag', {
			extend : 'Ext.form.field.ComboBox',

			alias : 'widget.combouseflag',
			store : [['', ''], [1, '启用'], [0, '禁用']],
			editable : false,

			initComponent : function() {
				this.callParent();
			}
		});
/**
 * Description: 是否选择控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: wudefeng Createdate:
 * 2015/1/23 0023
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */
Ext.define('Belle_Common.ux.ComboYesNo', {
			extend : 'Ext.form.field.ComboBox',

			alias : 'widget.comboyesno',
			store : [['', ''], [1, '是'], [0, '否']],
			editable : false,

			initComponent : function() {
				this.callParent();
			}
		});
/*
 * dwh
 * 客户选择器
 */

Ext.define('Belle_Common.ux.CustomerField', {
			extend : 'Belle_Common.ux.SearchHelpField',
			alias : 'widget.customerfield',
			constructor : function(config) {
				var me = this;

				if (Ext.isEmpty(config)) {
					config = {};
				}

				// 默认咧
				if (Ext.isEmpty(config.gridColumns)) {
					config.gridColumns = [{ //弹出框网格的列
	                    dataIndex: 'customerNo',
	                    text: '客户编号',
	                    flex: 0.5
	                },
	                {
	                    dataIndex: 'customerName',
	                    text: '客户名称',
	                    flex: 0.5
	                }];
				}

				// 默认查询参数
				if (Ext.isEmpty(config.searchItems)) {
					config.searchItems = [{
	                    name: 'customerNo',
	                    fieldLabel: '客户编号'
	                },
	                {
	                    name: 'customerName',
	                    fieldLabel: '客户名称'
	                }];
				}

				// 默认路径
				if (Ext.isEmpty(config.url)) {
					config.url = Belle.mdmPath + 'bas_customer/list.json' //后端的服务URL
				}
				
				// 附加参数
				if (!Ext.isEmpty(config.otherGridColumns)) {
					config.gridColumns = config.gridColumns
							.concat(config.otherGridColumns);
				}
				// 附加参数
				if (!Ext.isEmpty(config.otherSearchItems)) {
					config.searchItems = config.searchItems
							.concat(config.otherSearchItems);
				}
				
				Ext.apply(me, config);
				me.callParent(arguments);
			},
			initComponent : function() {
				var me = this;
				me.callParent(arguments);
			},
			winTitle : "客户选择器"
		});
/**
 * Description: 日期时间表单扩展控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月30日下午2:54:57
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月30日 yu.jh * 查询面板日期时间用法： {
 * xtype: 'bldatetime', fieldLabel: '建档时间', name: 'createFromTime' value: new
 * Date((new Date()).setDate(new Date().getDay()-5)) },{ xtype: 'bldatetime',
 * fieldLabel: ' 到 ', contype:"datetime", //若查询值为日期时间型设置datetime,默认查询值为日期型 name:
 * 'createToTime' }
 */
Ext.define('Belle_Common.ux.DateTimeField', {
	extend : 'Ext.form.field.Date',
	alias : 'widget.bldatetime',
	readOnly : false,
	contype : "date",
	initComponent : function() {
		this.format = this.format;
		this.afterMethod('afterRender', function() {
					this.getEl().applyStyles('top:0');
				});
		this.callParent();
	},
	createPicker : function() {
		var me = this, format = Ext.String.format;
		// Create floating Picker BoundList. It will acquire a floatParent by
		// looking up
		// its ancestor hierarchy (Pickers use their pickerField property as an
		// upward link)
		// for a floating component.
		if (this.contype == "datetime") {
			if (this.format == "Y-m-d" || this.format == null) {
				this.format = "Y-m-d H:i:s";
			}
			return new Belle_Common.ux.DateTimePicker({
						pickerField : me,
						ownerCt : me.ownerCt,
						floating : true,
						focusable : true, // Key events are listened from the
											// input field which is never
											// blurred
						hidden : true,
						minDate : me.minValue,
						maxDate : me.maxValue,
						disabledDatesRE : me.disabledDatesRE,
						disabledDatesText : me.disabledDatesText,
						disabledDays : me.disabledDays,
						disabledDaysText : me.disabledDaysText,
						format : me.format,
						showToday : me.showToday,
						startDay : me.startDay,
						minText : format(me.minText, me.formatDate(me.minValue)),
						maxText : format(me.maxText, me.formatDate(me.maxValue)),
						listeners : {
							scope : me,
							select : me.onSelect
						},
						keyNavConfig : {
							esc : function() {
								me.collapse();
							}
						}
					});
		} else if (this.contype == "date") {
			if (this.format == null) {
				this.format = "Y-m-d";
			}
			return new Ext.picker.Date({
						pickerField : me,
						ownerCt : me.ownerCt,
						floating : true,
						focusable : true, // Key events are listened from the
											// input field which is never
											// blurred
						hidden : true,
						minDate : me.minValue,
						maxDate : me.maxValue,
						disabledDatesRE : me.disabledDatesRE,
						disabledDatesText : me.disabledDatesText,
						disabledDays : me.disabledDays,
						disabledDaysText : me.disabledDaysText,
						format : me.format,
						showToday : me.showToday,
						startDay : me.startDay,
						minText : format(me.minText, me.formatDate(me.minValue)),
						maxText : format(me.maxText, me.formatDate(me.maxValue)),
						listeners : {
							scope : me,
							select : me.onSelect
						},
						keyNavConfig : {
							esc : function() {
								me.collapse();
							}
						}
					});
		}

	}
});

/**
 * Description: 日期时间扩展控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月30日下午2:54:57
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月30日 yu.jh
 */
Ext.define('Belle_Common.ux.DateTimePicker', {
			extend : 'Ext.picker.Date',
			alias : 'widget.datetimepicker',
			todayText : '确认',
			timeLabel : '时间',
			anchor : '100%',
			onRender : function(container, position) {
				var me = this;
				if (!this.h) {
					this.h = Ext.create('Ext.form.field.Number', {
								fieldLabel : this.timeLabel,
								labelWidth : 40,
								value : this.value.getHours(),
								minValue : 0,
								maxValue : 23,
								style : 'float:left',
								width : 90,
								baseCls : "inputh",
								selectOnFocus : true
							});
					this.m = Ext.create('Ext.form.field.Number', {
								value : this.value.getMinutes(),
								minValue : 0,
								maxValue : 59,
								style : 'float:left',
								inputType : 'text',
								width : 40,
								baseCls : "inputm",
								selectOnFocus : true
							});
					this.s = Ext.create('Ext.form.field.Number', {
								value : this.value.getSeconds(),
								minValue : 0,
								maxValue : 59,
								style : 'float:left',
								inputType : 'text',
								width : 40,
								baseCls : "inputs",
								selectOnFocus : true
							});
				}
				this.h.ownerCt = this;
				this.m.ownerCt = this;
				this.s.ownerCt = this;
				this.h.on('change', this.htimeChange, this);
				this.m.on('change', this.mtimeChange, this);
				this.s.on('change', this.stimeChange, this);

				me.callParent(arguments);
				me.todayBtn.tooltip = "";
				var table = Ext.get(Ext.DomQuery.selectNode('table',
						this.el.dom));
				var tfEl = Ext.core.DomHelper.insertAfter(table.last(), {
							tag : 'tr',
							style : 'border:0px;',
							children : [{
										tag : 'td',
										colspan : '7',
										cls : 'x-datepicker-footer ux-timefield'
									}]
						}, true);
				this.h.render(this.el.child('div tr td.ux-timefield'));
				this.m.render(this.el.child('div tr td.ux-timefield'));
				this.s.render(this.el.child('div tr td.ux-timefield'));
				this.ht = Ext.get(Ext.DomQuery.selectNode('div.inputh input',
						this.el.dom));
				this.ht.on("click", this.htclick, this);
				this.mt = Ext.get(Ext.DomQuery.selectNode('div.inputm input',
						this.el.dom));
				this.mt.on("click", this.mtclick, this);
				this.st = Ext.get(Ext.DomQuery.selectNode('div.inputs input',
						this.el.dom));
				this.st.on("click", this.stclick, this);
			},
			htclick : function() {
				this.ht.focus();
			},
			mtclick : function() {
				this.mt.focus();
			},
			stclick : function() {
				this.st.focus();
			},
			/**
			 * Respond to a date being clicked in the picker
			 * 
			 * @private
			 * @param {Ext.event.Event}
			 *            e
			 * @param {HTMLElement}
			 *            t
			 */
			handleDateClick : function(e, t) {
				var me = this, handler = me.handler;
				e.stopEvent();
				if (!me.disabled && t.dateValue
						&& !Ext.fly(t.parentNode).hasCls(me.disabledCellCls)) {
					me.setValue(this.fillDateTime(new Date(t.dateValue)));
					// me.fireEvent('select', me, me.value);
					if (handler) {
						handler.call(me.scope || me, me, me.value);
					}
					me.onSelect();
				}
			},
			/**
			 * Sets the value of the date field
			 * 
			 * @param {Date}
			 *            value The date to set
			 * @return {Ext.picker.Date} this
			 */
			setValue : function(value) {
				// If passed a null value just pass in a new date object.
				this.value = value;
				this.changeTimeFiledValue(value);
				return this.update(Ext.Date.clearTime(this.value || new Date(),
						true));
			},
			// @private
			changeTimeFiledValue : function(value) {
				this.h.un('change', this.htimeChange, this);
				this.m.un('change', this.mtimeChange, this);
				this.s.un('change', this.stimeChange, this);
				this.h.setValue(value.getHours());
				this.m.setValue(value.getMinutes());
				this.s.setValue(value.getSeconds());

				this.h.on('change', this.htimeChange, this);
				this.m.on('change', this.mtimeChange, this);
				this.s.on('change', this.stimeChange, this);
			},
			// listener 时间修改
			htimeChange : function(tf, time, rawtime) {
				this.value = this.fillDateTime(this.value);
			},
			mtimeChange : function(tf, time, rawtime) {
				this.value = this.fillDateTime(this.value);
			},
			stimeChange : function(tf, time, rawtime) {
				this.value = this.fillDateTime(this.value);
			},
			// @private
			fillDateTime : function(value) {
				if (this.h) {
					var h = this.h.value;
					var m = this.m.value;
					var s = this.s.value;
					value.setHours(h);
					value.setMinutes(m);
					value.setSeconds(s);
				}
				return value;
			},
			/**
			 * Gets the current selected value of the date field
			 * 
			 * @return {Date} The selected date
			 */
			getValue : function() {
				return this.fillDateTime(this.value);
			},
			selectToday : function() {
				var me = this, btn = me.todayBtn, handler = me.handler;
				if (btn && !btn.disabled) {
					me.setValue(this.fillDateTime(new Date(me.activeDate)));
					me.fireEvent('select', me, me.value);
					if (handler) {
						handler.call(me.scope || me, me, me.value);
					}
					me.onSelect();
				}
				return me;
			}
		});

/*
 * 产品选择器
 */

Ext.define('Belle_Common.ux.DeliveryPointField', {
			extend : 'Belle_Common.ux.SearchHelpField',
			alias : 'widget.deliverypointfield',
			constructor : function(config) {
				var me = this;

				if (Ext.isEmpty(config)) {
					config = {};
				}

				// 默认咧
				if (Ext.isEmpty(config.gridColumns)) {
					config.gridColumns = [{
								header : "收货地点编号",
								dataIndex : "pointNo"
							}, {
								header : "收货地点名称",
								dataIndex : "pointName"
							},

							{
								header : "收货地址",
								dataIndex : "contactAddress"
							}, {
								header : "联系人",
								dataIndex : "contacter"
							}, {
								header : "联系电话",
								dataIndex : "mobileNo"
							}];
				}

				// 默认查询参数
				if (Ext.isEmpty(config.searchItems)) {
					config.searchItems = [{
								name : "pointNo",
								fieldLabel : "收货地点编号"
							}, {
								name : "pointName",
								fieldLabel : "收货地点名称"
							}, {
								name : "contactAddress",
								fieldLabel : "收货地址"
							}, {
								name : "contacter",
								fieldLabel : "联系人"
							}, {
								name : "mobileNo",
								fieldLabel : "联系电话"
							}];
				}

				// 默认路径
				if (Ext.isEmpty(config.url)) {
					config.url = Belle.mdmPath
							+ "bas_delivery_point/listAll.json";
				}

				if (Ext.isEmpty(config.otherParams)) {
					config.otherParams = {
						regionNo : config.regionNo
					}
				}

				Ext.apply(me, config);
				me.callParent(arguments);
			},
			initComponent : function() {
				var me = this;
				me.callParent(arguments);
			},
			winTitle : "地址选择器",
			regionNo : ""
		});
/**
 * Description: 下拉框控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: wudefeng Createdate:
 * 2015/4/22 0022
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */
Ext.define('Belle_Common.ux.DropdownList', {
	extend : 'Ext.form.field.ComboBox',
	alias : 'widget.ddlfield',
	queryMode : 'local',
	displayField : 'textField',
	valueField : 'idField',
	localData : null,
	url : '',
	fromFields : '',
	async : true,

	// 读取过滤条件
	getFilters : function() {
		var me = this, params = [];

		if (!me.fromFields)
			return params;

		var fields = me.fromFields.split(','), context = me.up().context, val, form = me
				.up('form');
		if (context || form) {
			var record = context && context.record || form.getRecord();
			Ext.each(fields, function(f) {
						if (record) {
							val = record.get(f);
						}
						if (!val && !form) {
							var txt = Belle.getField(form, f);
							if (txt) {
								val = txt.getValue();
							}
						}
						params.push({
									property : f,
									value : val || '',
									operator : 10
								})
					});
		}
		return params;
	},

	// 加载数据后处理事件
	afterLoad : function() {
	},

	// 绑定数据
	reload : function() {
		var me = this;

		me.store.removeAll();

		if (me.localData) {
			me.store.loadData(me.localData);
			me.afterLoad();
		} else if (me.url) {

			me.url = Belle.setUrlModuleInfo(me, me.url);

			var param = me.getFilters(), options = {
				url : me.url,
				method : 'POST',
				async : me.async,
				success : function(d) {
					try {
						d = JSON.parse(d.responseText);
						if (d.list) {
							me.store.loadData(d.list);
						} else {
							me.store.loadData(d);
						}
						me.afterLoad();
					} catch (e) {
						Belle.alert('无法加载【' + me.fieldLabel + '】,服务端返回无效数据');
					}
				},
				failure : function() {
					Belle.alert('无法加载【' + me.fieldLabel + '】,请求服务器出错');
				}
			};
			if (!Ext.isEmpty(param)) {
				options.params = {
					queryCondition : JSON.stringify(param)
				}
			}
			Belle.callServer(options);
		}
	},

	initComponent : function() {
		var me = this;
		if (!me.store) {
			me.store = Ext.create('Ext.data.JsonStore', {
						fields : [me.displayField, me.valueField, 'enableFlag']
					});
			me.reload();
		}
		me.callParent(arguments);
	}
});
/**
 * Description: 通用的一些方法 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: wudefeng Createdate:
 * 2015/4/8 0008
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */

var Belle = Belle || {};

Belle.getField = function(form, fieldName) {
	if (!form)
		return null;
	return form.getForm().getFields().findBy(function(item) {
				return item.name == fieldName
			})
};

/*
 * alert 提示框 msg 提示内容 [fn] 点击按钮时执行的函数 [scope] 作用域
 */
Belle.alert = function(msg, fn, scope) {
	var title = '系统提示',
		win =Ext.Msg.alert(title, msg, fn, scope);
	//setTimeout(function(){win.focus()},1);
	return win;
};

/*
 * confirm 确认框 msg 提示内容 [fn] 点击按钮时执行的函数 [scope] 作用域
 */
Belle.confirm = function(msg, fn, scope) {
	var title = '系统提示';
	return Ext.Msg.confirm(title, msg, fn, scope);
};
/** 弹出框 */
Belle.show = function(options) {
	return Ext.Msg.show(options);
};
/** 深度复制对象 */
Belle.clone = function(obj) {
	var result;
	if (Ext.isArray(obj)) {
		result = [];
	} else if (Ext.isObject(obj)) {
		result = {};
	} else {
		return obj;
	}
	for (var key in obj) {
		var copy = obj[key];
		if ((Ext.isObject(copy) && copy.constructor.name == 'Object')
				|| Ext.isArray(copy)) {
			result[key] = arguments.callee(copy);// 递归调用
		} else {
			result[key] = obj[key];
		}
	}
	return result;
};

Belle.openUrl = function(url, title, w, h) {
	title = title || '弹出窗口';
	w = w || 1024;
	h = h || 500;
	if (url.indexOf('?') > 0) {
		url += '&_dc=';
	} else {
		url += '?_dc=';
	}
	url += new Date().getTime();
	return Ext.widget('window', {
				closeAction : 'destroy',
				modal : true,
				width : w,
				height : h,
				title : title,
				html : '<iframe frameborder=0 width="100%" height="100%" src="'
						+ url + '"></iframe>',
				autoShow : true
			});
};

Belle.callServer = function(options) {
	if (options.url.indexOf('noFilterData=') < 0) {
		options.url += (options.url.indexOf('?') > 0 ? '&' : '?') + 'noFilterData=TRUE';
	}
	Ext.Ajax.request(options);
};

Belle.setUrlModuleInfo = function(scrObj, url) {

	if (!url) return '';
	if (url.indexOf('sys_menu_moduleUrl=') > 0) return url;
	url += (url.indexOf('?') > 0 ? '&' : '?');

	var basePage = scrObj;
	if (!scrObj)
		return url + 'sys_menu_moduleUrl=/blf1-uc-web/main&sys_menu_moduleId=-110';

	if (!scrObj.is('basepage'))
		basePage = scrObj.up('basepage');

	if (!basePage)
		basePage = scrObj.scope;

	if (!basePage)
		return url + 'sys_menu_moduleUrl=/blf1-uc-web/main&sys_menu_moduleId=-110';

	url += 'sys_menu_moduleUrl=' + basePage.moduleUrl.replace(/#/g, '')
		+ '&sys_menu_moduleId=' + basePage.moduleId;
	return url;
};

/* 小于10补0 */
function fillzeno(n) {
	return n < 10 ? '0' + n : n;
}

/* 重写Js 标准的 toJSON方法，返回 yyyy-MM-dd HH:mm:ss */
Date.prototype.toJSON = function() {
	return isFinite(this.valueOf())
			? this.getFullYear() + '-' + fillzeno(this.getMonth() + 1) + '-'
					+ fillzeno(this.getDate()) + ' '
					+ fillzeno(this.getHours()) + ':'
					+ fillzeno(this.getMinutes()) + ':'
					+ fillzeno(this.getSeconds())
			: null;
};

Ext.onReady(function() {
	Ext.Ajax.timeout = 180000;
			// 验证 比较form中两个控件输入的值是否符合规则
			Ext.apply(Ext.form.VTypes, {
						compareTo : function(val, field) {
							this.compareToText = field.compareError
									|| '两次输入的值不一致';
							var form = field.up('form');
							var opt = field.compareType || '=';
							if (field.compareTo && form) {
								var val2 = form.getValues()[field.compareTo];
								if (opt == '>') {
									return (val > val2);
								} else if (opt == '>=') {
									return (val >= val2);
								} else if (opt == '<') {
									return (val < val2);
								} else if (opt == '<=') {
									return (val <= val2);
								} else {
									return (val == val2);
								}
							}
							return true;
						}
					});
		});

// 复制数组
Array.prototype.belleCopy = function() {
	var newArr = [];
	if (this.length <= 0)
		return newArr;
	for (var i = 0; i < this.length; i++) {
		var newObj = new Object();
		newObj[i] = this[i];
		newArr.push(newObj[i]);
	}
	return newArr
}

Array.prototype.maxValue = function(key) { // 最大值
	if (key) {
		var arrs = [];
		for (var i = 0; i < this.length; i++) {
			var keys = key.split("."), val = this[i], isCanAdd = false;
			// 获取当前key的值
			for (var j = 0; j < keys.length; j++) {
				val = val[keys[j]];
				if (!val) {

					isCanAdd = false
					return false;
				}
				isCanAdd = true;
			}
			// 如何有值则加入对比列表
			if (isCanAdd) {
				arrs.push(val);
			}
		}
		return Math.max.apply({}, arrs)
	} else {
		return Math.max.apply({}, this);
	}

};
Array.prototype.minValue = function(key) { // 最小值
	if (key) {
		var arrs = [];
		for (var i = 0; i < this.length; i++) {
			var keys = key.split("."), val = this[i], isCanAdd = false;
			// 获取当前key的值
			for (var j = 0; j < keys.length; j++) {
				val = val[keys[j]];
				if (!val) {

					isCanAdd = false
					return false;
				}
				isCanAdd = true;
			}
			// 如何有值则加入对比列表
			if (isCanAdd) {
				arrs.push(val);
			}
		}
		return Math.min.apply({}, arrs)
	} else {
		return Math.min.apply({}, this);
	}
};

Belle.minValue = function(array) {
	return Math.min.apply(Math, array);
};

Belle.maxValue = function(array) {
	return Math.max.apply(Math, array);
};

Belle.EventStop=function(e){
	e.returnValue = false;
	if (e.preventDefault) {
		e.preventDefault();
	}
	if (!e.stopPropagation) {
		e.cancelBubble = true;
	} else {
		e.stopPropagation();
	}
};

//enter+ctrl 查询
Belle.ctrlKey13=function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		if(objList && objList.btnSearch && !objList.btnSearch.isDisabled() && objList.btnSearch.isVisible()){
			objList.btnSearch.btnEl.dom.click();
		}
	}
};

//insert	新增
Belle.ctrlKey45	= function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnAdd && !objList.btnAdd.isDisabled() && objList.btnAdd.isVisible()){
			//模拟点击
			objList.btnAdd.btnEl.dom.click();
		}
	}
};

//ctrl+delete删除
Belle.ctrlKey46	= function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnDelete && !objList.btnDelete.isDisabled() && objList.btnDelete.isVisible()){
			//objList.btnDelete.focus();
			objList.btnDelete.btnEl.dom.click();
		}
	}
};

// backspace
Belle.keyCode8 =function(e,panel){
	if(!e || !e.target) return true;
	
	if(e.target.value==window.undefined || e.target.readOnly || e.target.disabled){
		e.keyCode=505;
		return false;
	}
	 return true;
};

//ctrl+s 保存快捷键
Belle.ctrlKey83 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnSave && !objList.btnSave.isDisabled() && objList.btnSave.isVisible()){

			objList.btnSave.btnEl.dom.click();
		}
		
		//单据保存
		if(objList && objList.btnBillSave && !objList.btnBillSave.isDisabled() && objList.btnBillSave.isVisible()){

			objList.btnBillSave.btnEl.dom.click();
		}
	}
};

//编辑 ctrl+e
Belle.ctrlKey69 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnEdit && !objList.btnEdit.isDisabled() && objList.btnEdit.isVisible()){

			objList.btnEdit.btnEl.dom.click();
			//return false;
		}
	}
};

//重置 ctrl +R
Belle.ctrlKey82 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnReset && !objList.btnReset.isDisabled() && objList.btnReset.isVisible()){

			objList.btnReset.btnEl.dom.click();
		}
	}
};

//还原 ctrl+U
Belle.ctrlKey85 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnUndo && !objList.btnUndo.isDisabled() && objList.btnUndo.isVisible()){

			objList.btnUndo.btnEl.dom.click();
		}
	}
};

//取消 alt+C
Belle.altKey67 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnUndo && !objList.btnUndo.isDisabled() && objList.btnCancel.isVisible()){

			objList.btnCancel.btnEl.dom.click();
		}
	}
};

//ctrl+D 删除单据
Belle.ctrlKey68 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnBillDel && !objList.btnBillDel.isDisabled() && objList.btnBillDel.isVisible()){

			objList.btnBillDel.btnEl.dom.click();
		}
	}
};

//ctrl+up 上单
Belle.ctrlKey38 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnBillPrev && !objList.btnBillPrev.isDisabled() && objList.btnBillPrev.isVisible()){

			objList.btnBillPrev.btnEl.dom.click();
		}
	}
};

//ctrl+down 下单
Belle.ctrlKey40 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnBillNext && !objList.btnBillNext.isDisabled() && objList.btnBillNext.isVisible()){

			objList.btnBillNext.btnEl.dom.click();
		}
	}
};

//ctrl+I 新单
Belle.ctrlKey73 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnBillNew && !objList.btnBillNew.isDisabled() && objList.btnBillNew.isVisible()){

			objList.btnBillNew.btnEl.dom.click();
		}
	}
};

//ctrl+B 返回
Belle.ctrlKey66 = function(e,panel){
	if(panel && panel.controller){
		var objList = panel.controller.getObjList();
		//找到新增按钮且处于可以点击状态
		if(objList && objList.btnBillBack && !objList.btnBillBack.isDisabled() && objList.btnBillBack.isVisible()){

			objList.btnBillBack.btnEl.dom.click();
		}
	}
};



Ext.onReady(function(){
	//初始化鼠标悬浮提示
	Ext.tip.QuickTipManager.init();
	
	//监听键盘全局
	window.document.onkeydown=function(e){
		var key="";
		///
		if(e.shiftKey){
			key="shiftkey";
		}
		
		if(e.ctrlKey){
			key+="ctrlKey";
		}
		
		if(e.altKey){
			key+="altKey";
		}
		
		if(Ext.isEmpty(key)){
			key+="keyCode"+e.keyCode;
		}
		else{
			key+=e.keyCode;
		}

		//2.0 打开 1.0 时， 1.0快捷方式处理 begin
		if(Belle.moduleWidget()) {

			if (key === 'keyCode8') {
				return Belle.keyCode8(e, null);
			}
			var isUserKey = false,
				panel = Ext.getBody().component;

			if(!panel.controller) return;

			//判断是否已打开子模块
			var dtlTab = panel.controller.getObj('selftab');
			if(dtlTab){
				var dtlPanel = dtlTab.getActiveTab();
				if(dtlPanel && dtlPanel.xtype===panel.dtlName){
					panel = dtlPanel;
				}
			}

			if (typeof panel.controller.onPageKeyDown == "function") {
				isUserKey = panel.controller.onPageKeyDown(panel, e);
			}
			if (isUserKey === true) {
				Belle.EventStop(e);
				return false;
			}
			if (isUserKey != true && typeof Belle[key] == "function") {
				Belle.EventStop(e);
				Belle[key](e, panel);
				return false;
			}
			return true;
		}
		//2.0 打开 1.0 时， 1.0快捷方式处理 end

		
		//判断是否已激活模块
		if(window.main && window.main.selectPanel){
			var panel = Ext.clone(window.main.selectPanel),
			panelUp=panel.up()?panel.up():panel,
			isUserKey = false,
			tab = window.main.tabPanel,
			actPanel = tab.getActiveTab(),
			dtlTab,
			dtlPanel,
			isWindow=panelUp.is("window");
			
			//当前激活模块类型
			if(isWindow){
				//判断是否已被隐藏
				if(panelUp.isHidden && panelUp.isHidden()){
					//获取当前激活模块打开子窗口
					panel = actPanel;
				}
			}
			else if(!panel.controller) {
				panel = actPanel;
			}
			
			if(!panel.controller) return ;
			//判断是否已打开子模块
			dtlTab = panel.controller.getObj('selftab');
			if(dtlTab){
				//获取子模块激活项
				//子窗口激活模块
				dtlPanel = dtlTab.getActiveTab();
			}
			
			//判断子窗口是否为当前激活模块的明细
			if(dtlPanel && dtlPanel.xtype===panel.dtlName){
				panel = dtlPanel;
			}
			
			//执行用户自定义事件
			//用户是否已重定义键盘事件
			if(panel.controller && typeof panel.controller.onPageKeyDown=="function"){
				isUserKey= panel.controller.onPageKeyDown(panel,e);
			}
			if(isUserKey===true){
				Belle.EventStop(e);
				return false;
			}
			
			//未定义
			if(isUserKey!=true && typeof Belle[key]=="function") {
				if (key === 'keyCode8') {
					return Belle.keyCode8(e, null);
				}
				Belle.EventStop(e);
				Belle[key](e, panel);
				return false;
			}
			else{
				return true;
			}
		}
		else{
			if(key==='keyCode8') {
				return Belle.keyCode8(e,null);
			}
			else{
				return true;
			}
			
		}
		
	};

	var jq=Belle.jq();
	if(jq){
		jq('.index-loadpage').hide();
	}
});

//获取用户模块权限信息
Belle.getUserModule=function(moduleNo,childNodes){
	var retData;
	Ext.each(childNodes,function(titem){
		//var 
		if(titem.data.moduleNo&&titem.data.moduleNo.toString()===moduleNo){
			retData=titem.data;
			return false;
		}
		
		if(titem.childNodes&&titem.childNodes.length>0){
			retData= Belle.getUserModule(moduleNo,titem.childNodes);
			
			if(retData) return false;
		}
		
	});
			
	return retData;
}

//加载应用js
Belle.loadAppJs=function(panel,userModule,callBack){
	var me=panel,data = userModule, path = data.url.split('#'),
	appCode = data.appCode.toLowerCase(),
	jspath = path[0] + data.jsUrl;

 	var isloaded = appCode + "IsLoaded";
 	
 	data.title=data.text;
    if (appCode != 'uc' && !Belle[isloaded]) {
    	
    	var panel=me;
    	if(panel && panel.mask){
    		panel.mask("加载应用Js文件中,请稍后...");
    	}
		//加载应用文件
        Ext.Loader.loadScript({
            url: jspath, onLoad: function () {
            	if(panel && panel.unmask){
            		panel.unmask();
            	}
            	Belle[isloaded] = true;
                if(Belle.checkModule(path[1])===false) return;
                
                if(typeof(callBack)==='function'){
                	callBack(data);
                }
                
            }, onError: function () {
            	if(panel && panel.unmask){
            		panel.unmask();
            	}
                Belle.alert('加载应用的Js文件出错');
            }
        });

    }
    else{
	 	if(Belle.checkModule(path[1])===false) return;
    	 
 		if(typeof(callBack)==='function'){
            	callBack(data);
        }
    }
}

//验证模块是否能正确打开
Belle.checkModule= function (moduleNo) {
        var className = Ext.ClassManager.getNameByAlias('widget.' + moduleNo);
        if (className) return true;

        Belle.alert('URL出错,无法创建此模块');
        return false;
}

Belle.getPageView=function(panel,params,callback){
		var panel=panel;
    	if(panel && panel.mask){
    		panel.mask("加载应用Js文件中,请稍后...");
    	}
		Belle.callServer({
						url:"/uc_layout_v1/getLayout.json?sys_menu_moduleId="+params.moduleNo+"&resCode="+params.moduleId,
                        //url:"/blf1-uc-web/itg_grid_layout/getPageLayouts.json?sys_menu_moduleUrl="+params.moduleUrl+"&sys_menu_moduleId="+params.moduleNo,
			params:{
				//userId:params.userCode,
				//moduleId:params.moduleId
				resCode:params.moduleId
				
			},
			success:function(response, opts){
				var obj = Ext.decode(response.responseText);
				if(panel && panel.mask){
    				panel.unmask();
    			}
				callback(obj.list);
			},
			failure: function(response, opts) {
				if(panel && panel.mask){
    				panel.unmask();
    			}
				callback(null);
			}
		});
};

//创建模块信息
Belle.createModule=function(openModule,maskPanel,callback){
	var obj;
	if(!openModule) {
		Belle.alert("请设置模块信息"); 
		return;
	}
	
	var data = openModule.userModule,
	userInfo = openModule.userInfo,
	params = openModule.params||{},
	oType= openModule.xtype,
	mItem;
	
	if(Ext.isEmpty(data)) {
		Belle.alert("请设置模块权限信息"); 
		return;
	}
	
	if(Ext.isEmpty(userInfo)) {
		Belle.alert("请设置用户限信息"); 
		return;
	}
	
	
	Belle.getPageView(maskPanel,{
		userCode:userInfo.userCode,
		moduleId:data.url.split('#')[1],
		moduleUrl:data.url.replace("#",""),
		moduleNo:data.moduleNo
	},function(gridCols){
	
		mItem={
				userGridCols:gridCols,
			 	xtype: data.url.split('#')[1],
	            moduleUrl:data.url,
	            moduleId:data.moduleNo,
	            moduleName:data.moduleName,
	            userRight: data.rights,
	            moduleRight: data.rightValue,
	            //userName: userInfo.userName,
	            //userCode: userInfo.userCode,
	            params:params||[],
	            billNo:params.billNo||"",
	            showType:"linkcolumn"
		};
		title=data.title+(params.billNo?'【'+params.billNo+'】':"");
		switch(oType){
					case "window":
						//创建弹出窗口
						var winConfig={
								layout: "fit",
								width:800,
								height:600,
								resizable : false,
								title : title,
								constrain : true,
								closeAction : 'destroy',
								autoShow : true,
								items : [mItem],
								params:params||[],
		            			billNo:params.billNo||"",
		            			showType:"linkcolumn"
						};
						
						Ext.applyIf(openModule.option,winConfig);
						obj=Ext.widget('window', openModule.option);
					break;
					default:
						//获取当前面板
						var tabPanel = Ext.getCmp('maintabpanel'),
					 	moduleNo = data.url.split('#')[1],
			            tabId = 'tab_' + data.moduleNo+data.menuName,
		           		tabitem;
		           		
		           		if(!Ext.isEmpty(params.billNo)){
		           			tabId+=params.billNo.replace(/\,/g,'');
		           		}
		           		
		            	tabitem = tabPanel.getComponent(tabId);
						if(!tabitem){
								var tab = {
									userGridCols:gridCols,
					                title: title,
					                itemId: tabId,
					                xtype: moduleNo,
					                moduleUrl:data.url,
					                moduleId:data.moduleNo,
					                moduleName:data.moduleName,
					                userRight: data.rights,
					                moduleRight: data.rightValue,
					                //userName: userInfo.userName,
					                //userCode: userInfo.userCode,
					                closable: true,
					                reorderable: true,
					                loadMask: '加载中...',
					                params:params||{},
						            billNo:params.billNo||"",
						            showType:"linkcolumn"
					            };
					            Ext.applyIf(openModule.option,tab);
					            tabitem = tabPanel.add(tab);
					            //tabPanel.setActiveTab(tabitem);
						}
						tabPanel.setActiveTab(tabitem);
							//已存在的页签重新绑定值
							panel=tabPanel.getActiveTab();
							if(panel && panel.controller && typeof(panel.controller.getObjList)=="function"){
								objList=panel.controller.getObjList();
								
								if(objList && objList.commonsearch){
									form=objList.commonsearch.getForm();
									form.setValues(params);
								}
								//执行刷新数据
								if(objList && objList.btnSearch && !objList.btnSearch.isDisabled() && objList.btnSearch.isVisible()){

									objList.btnSearch.btnEl.dom.click();
								}
							}
				 		
				 		obj=tabPanel;
						break;
				}
				
				if(typeof(callback)=="function")
				callback(obj);
	})
	
};

//打开模块
//moduleNo 模块ID,params 传递的参数, type:打开类型 window,tab,默认tab,
//width:宽 type=window时生效
//height:高  type=window时生效
//maskPanel 遮罩作用区域
Belle.openModel=function(moduleNo,params,type,winOption,maskPanel,callback){

	if(Belle.moduleWidget() && Belle.jq() && params.billNo){
		Belle.openModelInV2(moduleNo,params.billNo);
		return;
	}
	
	if(!maskPanel) maskPanel=Ext.getBody();
	
	if(maskPanel.view) maskPanel=maskPanel.view;
	
	if(window.main&&window.main.leftTree){
			userInfo = window.main.userInfo||{};
			leftTree = window.main.leftTree;
			leftTreeStore =leftTree.store;
			//查找树节点获取权限信息
			data=Belle.getUserModule(moduleNo,leftTreeStore.data.items);
			if(!data) { 	
			 	Belle.alert("您没有该模块权限！");
			 	return;
			 }
			if (!data.leaf || !data.url) return;
			//加载应用js
        	 Belle.loadAppJs(maskPanel,data,function(data){
        	 	//创建模块信息
        	 	Belle.createModule({
        	 		xtype: type,
        	 		option:winOption,
        	 		userInfo: userInfo,
        	 		userModule: data,
        	 		params:params
        	 	},maskPanel,callback);
        	 });
		}
		else{
			Belle.callServer({
				url:"/blf1-uc-web/itg_menu_list/getusermenulist.json?sys_menu_moduleUrl=/blf1-uc-web/main&sys_menu_moduleId=0&_dc=1440634535405&node=root",
				success:function(response){
					 var obj = Ext.decode(response.responseText),
					  data;
					 if(!obj||!obj.list||obj.list.length<=0){
					 	Belle.alert("获取模块信息失败");
					 	return;
					 }
					 
					 var cfn=function(moduleNo,data){
					 	var cdata;
					 	Ext.each(data,function(titem){
					 		if(titem.moduleNo&&titem.moduleNo.toString()===moduleNo){
					 			cdata=titem;
					 			return false;
					 		}
					 	});
					 	return cdata;
					 }
					 data=cfn(moduleNo,obj.list);
					 if(!data) {
					 	
					 	Belle.alert("您没有该模块权限！");
					 	return;
					 }
					
					userInfo = {userCode:'admin'}//window.main.userInfo||{};
					//加载应用js
		        	 Belle.loadAppJs(maskPanel,data,function(data){
		        	 	//创建模块信息
		        	 	Belle.createModule({
		        	 		xtype: type,
		        	 		option:winOption,
		        	 		userInfo: userInfo,
		        	 		userModule: data,
		        	 		params:params
		        	 	},maskPanel,callback);
		        	 });
				},
				failure:function(response, opts){
					maskPanel.unmask();
					Ext.MessageBox.show({
	                    title: '错误提示',
	                    msg: response.responseText,
	                    height: 300,
	                    width: 400
	                });
				}
			});
		}		
};
Belle.strEscape=function(str){
	return (str||'').replace(/\'/g, "").replace(/\"/g, "").replace(/\n/ig, "<br/>");
};

Belle.formatMoney=function(num,formatText){
	Ext.util.Format.number(num,formatText);
	
//	var decText='',numType='', result = '';
//	
//	var dt=new Date()
//	
//	if(Ext.isEmpty(num)) return '0';
//	
//	if(formatText>0){
//		//格式化为数字
//		if('number'!=typeof(num)){
//			num = window.parseFloat(num);
//		}
//		//截取长度
//		num = num.toFixed(formatText);
//	}
//
//	//负数转正数
//	if('number'==typeof(num) && num < 0){
//		numType='-';
//		num=0-num;
//	}
//	else if(num.indexOf && num.indexOf('-')===0){
//		numType='-';
//		num=num.substr(1,num.indexOf('-'));
//	}
//	
//	num=num.toString();
//	
//	//截取小数点
//	if(num.indexOf('.')>0){
//		decText =num.substr(num.indexOf('.'),num.length);
//		num=num.substr(0,num.indexOf('.'));
//	}
//
//    while (num.length > 3) {
//        result = ',' + num.slice(-3) + result;
//        num = num.slice(0, num.length - 3);
//    }
//    if (num) { result = numType+num + result+decText; }
//    var dt1=new Date();
//    
//    console.log(dt1-dt)
//    return result;
};



/**
 * 在v2.0框架中嵌入打开v1.0的模块
 * */
Belle.openInV2=function() {

	var param = {};

	param.moduleId = Belle.moduleWidget();
	if (!param.moduleId) return;

	param.moduleNo = Belle.getUrlParam('sys_menu_moduleId');
	param.userCode = Belle.getUrlParam('u');
	var appurl= location.href.replace(location.origin,'').substr(1);
	if(appurl.indexOf('/')>0) {
		appurl = appurl.substr(0, appurl.indexOf('/'));
	}

	param.moduleUrl = '/'+appurl+'/'+ Belle.moduleWidget();
	param.userRight = Belle.getUrlParam('ur');
	param.moduleRight = Belle.getUrlParam('mr');
	param.params = {};
	param.params.billNo = Belle.getUrlParam('bno') || '';
	
	var jq = Belle.jq(), userName = jq ? jq('#index_userInfo').text() : '';
	param.userName = userName;
	
	window.main = window.main||{};
	
	window.main.userInfo={userCode:param.userCode,userName:userName};
	
   // Belle.initApp();

	//处理模块中网格自定义布局
	Belle.getPageView(null, param, function (gridCols) {
		
		try {
			Ext.widget(param.moduleId, {
				plugins: 'viewport',
				moduleUrl: param.moduleUrl,
				moduleId: param.moduleNo,
				moduleName: param.moduleNo,
				userRight: param.userRight,
				moduleRight: param.moduleRight,
				userGridCols: gridCols,
				userName: param.userName,
				userCode: param.userCode,
				params: param.params,
				showType: (param.params.billNo ? 'linkcolumn' : ''),
				billNo: param.params.billNo
				//,loadMask: '加载中...'
			});			

		} catch (e) {
			Ext.Msg.alert('错误提示', '访问的URL不正确');
		}
	});
};



/**
 * 返回2.0框架的 jquery 库类
 * */
Belle.jq = function(){
	var v2W = Belle.v2Win();
	if(v2W) return v2W.$;
	return null;
};

/**
 * 查看2.0菜单中有没需要打开模块
 * */
Belle.getNodeInV2= function(treedata,moduleNo) {
	var node = null;
	Ext.each(treedata,function(item){
		if(item.moduleCode==moduleNo){
			node=item;
			return false;
		}
		if (!node && item.hasChildren && !Ext.isEmpty(item.children)) {
			node = Belle.getNodeInV2(item.children, moduleNo)
		}
	});
	return node;
};


/**
 *模块中打开模块（2.0框架，1。0模块中打开模块）
 */
Belle.openModelInV2=function(moduleNo,billNo) {
	var win = Belle.v2Win();
	if (!win) {
		Belle.alert('无法打开新模块,请在2.0主框架中进入');
		return;
	}
	var treeData = win.app.index.$leftTree._data;
	var node = Belle.getNodeInV2(treeData, moduleNo);
	if (!node) {
		Belle.alert('对不起，你没有权限打开对此进行操作');
		return;
	}
	var url = node.url;
	url = Belle.setUrlParam(url, 'mr', node.rightValue);
	url = Belle.setUrlParam(url, 'ur', node.rights);
	url = Belle.setUrlParam(url, 'u', Belle.getUrlParam('u'));
	url = Belle.setUrlParam(url, 'bno', billNo);
	var text = node.oldText || node.text + "【" + billNo + "】";
	win.app.index.addTabItem(win.app.index.$tab, text, url);
};

/**
 * 2.0框架打开时初始化v1.0所有工程
 * */
Belle.initApp=function() {
	var v2W= Belle.v2Win();
	if (v2W){
		if(v2W.v1Loaded) return;
		v2W.v1Loaded = true;
	}

	var app = ["pd", "sd", "mdm", "mm", "pp", "qm","cs","fa",'report'],
		root = window.location.origin, url;
	for (var i = 0; i < app.length; i++) {
		url = root +'/blf1-'+ app[i] + '-web/load_service.json?_dc=' + new Date().getTime();
		Ext.Loader.loadScript({url: url});
	}
}






/**
 * Description: All rights Reserved, Designed By BeLLE Copyright: Copyright(C)
 * 2014-2015 Company: Wonhigh. author: wudefeng Createdate: 2015/5/7 0007
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */

Ext.grid.plugin.CellEditing.override({
	onSpecialKey : function(ed, field, e) {
		var me = this;
		var sm;
		if (e.getKey() === e.TAB) {
			e.stopEvent();
			if (field.is('searchhelpfield') && field.needCall) {
				field.sendToServer();
				return;
			}
			if (ed) {
				ed.onEditorTab(e);
			}

			me._newRow(ed.editingPlugin, e);

			sm = ed.getRefOwner().getSelectionModel();
			return sm.onEditorTab(ed.editingPlugin, e);
		}else if(e.keyCode>36 && e.keyCode<41) {
			if (ed.grid.inEditStatus) return;

			if (field.is('searchhelpfield') && field.needCall) {
				field.sendToServer();
				return;
			}
			e.stopEvent();
			var editingPlugin = ed.editingPlugin,
				view = editingPlugin.context.view,
				record = editingPlugin.getActiveRecord(),
				position = editingPlugin.context,
				direction, lastPos;
			if (e.keyCode == 38) direction = 'up';
			else if (e.keyCode == 39) direction = 'right';
			else if (e.keyCode == 40) direction = 'down';
			else direction = 'left';
			do {
				lastPos = position;
				position = view.walkCells(position, direction, e, me.preventWrap);
				if (lastPos && lastPos.isEqual(position)) {
					return;
				}
			} while (position
			&& (!position.column.getEditor(record) || !editingPlugin
				.startEditByPosition(position)));

		}
		else if(e.getKey() === e.ENTER){
			if (field.is('searchhelpfield') && field.needCall) {
				e.stopEvent();
				field.sendToServer();
			}
		}
	},
	_newRow : function(editingPlugin, e) {
		var me = this, position = editingPlugin.context, view = editingPlugin.context.view, direction = e.shiftKey
				? 'left'
				: 'right', grid = editingPlugin.grid, store = grid.getStore(), rowIndex = store
				.getCount(), columns = grid.headerCt.columnManager.columns, cellIndex = -1;

		if (grid.isCandNewRow == false)
			return;

		// 判断是否为最后一个编辑列
		for (var i = position.column.fullColumnIndex; i < columns.length; i++) {
			if (position.column!=columns[i]&&columns[i].getEditor()) {
				cellIndex = i;
				break;
			}
		}

		if (cellIndex > -1 || position.rowIdx != rowIndex - 1)
			return;

		var model = view.store.model, newObj = model.create({
					_flag : 'A'
				});

		store.add(newObj);

		grid.getSelectionModel().select(rowIndex);
		//return;
//		if (grid.editModel != "cell" && grid.editModel != 'row')
//			return;
//
//		// 获取第一个编辑咧
//		for (var i = 0; i < columns.length; i++) {
//			if (columns[i].getEditor()) {
//				//计算编辑列
//				cellIndex = i+i-columns[i].fullColumnIndex;
//				break;
//			}
//		}
//
//		if (grid.editModel == 'cell') {
//			grid.editingPlugin.startEditByPosition({
//						row : rowIndex,
//						column : cellIndex
//					});
//		} else {
//			grid.editingPlugin.startEdit(rowIndex, cellIndex);
//		}
		// 刷新网格时会导致网格获取高度出错
		// grid.view.refresh();
	}
});

Ext.view.View.override({
			handleEvent : function(e) {
				var me = this, isKeyEvent = me.keyEventRe.test(e.type), nm = me
						.getNavigationModel();
				e.view = me;
				if (isKeyEvent) {
					e.item = nm.getItem();
					e.record = nm.getRecord();
				}

				if (!e.item) {
					e.item = e.getTarget(me.itemSelector);
				}
				if (e.item && !e.record) {
					e.record = me.getRecord(e.item);
				}
				if (me.processUIEvent(e) !== false) {
					me.processSpecialEvent(e);
				}

				// denny.wu 2015.4.9 grid cell中不能输入特殊字符

				// if (isKeyEvent && ((e.getKey() === e.SPACE &&
				// !Ext.fly(e.target).isInputField()) || e.isNavKeyPress(true)))
				// {
				//
				// /* e.preventDefault();*/
				// }
			}
		});

//Ext.Editor.override({
//			completeOnEnter : false
//		});

// dwh 解决表头输入框回车事件异常
Ext.grid.header.Container.override({

			privates : {
				onHeaderActivate : function(e) {
					var column = this.getFocusableFromEvent(e);
					// 添加当前触发选择判断
					if (column && column.bellefilterEl) {

						if (column.sortable && this.sortOnClick) {
							column.toggleSortState();
						}

						this.onHeaderClick(column, e, column.el);
					}
				}
			}
		});

Ext.form.field.Base.override({
	tabIndex: 1,
	initComponent: function () {
		var me = this;

		me.callParent();

		me.subTplData = me.subTplData || {};

		// Init mixins
		me.initLabelable();
		me.initField();

		// Default name to inputId
		if (!me.name) {
			me.name = me.getInputId();
		}
		// Add to protoEl before render
		if (me.readOnly) {
			me.addCls(me.readOnlyCls);
		}

		me.addCls(Ext.baseCSSPrefix + 'form-type-' + me.inputType);

		// 判断是否为必填
		if (me.allowBlank === false) {
			me.labelCls = Ext.baseCSSPrefix
			+ 'form-item-label notnull-field';
		}

		//dwh 添加tabIndex控制，禁用表单禁止光标tab键时跳入
		me.on("writeablechange", function (field, readOnly) {
			var tabIndex = me.oldTabIndex || me.getTabIndex() || 1;
			if (readOnly) {
				if (Ext.isEmpty(me.oldTabIndex)) {
					me.oldTabIndex = tabIndex;
				}

				me.setTabIndex(-1);
			}
			else {

				me.setTabIndex(tabIndex);
			}
		});

		me.on("disabled", function () {

			var tabIndex = me.oldTabIndex || me.getTabIndex();

			if (Ext.isEmpty(me.oldTabIndex)) {
				me.oldTabIndex = tabIndex;
			}

			me.setTabIndex(-1);
		});

		me.on("enable", function (field) {

			var tabIndex = me.oldTabIndex || me.getTabIndex();
			me.setTabIndex(tabIndex);
		});

		if (!me.plugins || !me.plugins.join) {
			me.plugins = [];
		}

//				me.plugins.push(Ext.create("Ext.grid.plugins.GridColumnFromFieldDragDrop",{
//							//dragGroup: "form"//,
//                    		//dropGroup: "grid"
//							
//				}));


		// 注册回车跳转到下一个
//				me.on("specialkey", function(txt, e) {
//							if (e.getKey() === e.ENTER) {
//
//								var form = me.up("form"), child;
//
//								// 获取查询面板
//								if (!form)
//									return;
//								// 下一个
//								child = form.nextChild(me);
//
//								if (!child)
//									return;
//								// 判断是否相同面板内
//								if (child.up() != form)
//									return;
//								// 设置鼠标焦点
//								child.focus();
//								child.selectText();
//
//							}
//						});

	},
	initEvents: function () {
		var me = this,
			labelEl = me.labelEl,
			inputEl = me.inputEl;
		me.callParent(arguments);

		if (labelEl) {
			//实现双击清空
			if (me.isDbClickClearValue != false) {
				labelEl.on('dblclick', function (obj, e) {
					if (me.readOnly || me.canInput == false
						|| me.isDisabled() == true) {
						return;
					}
					me.setValue("");
				});
			}
		}
		if (inputEl) {
			inputEl.on('dblclick', function () {
				if (me.up) {
					var grid = me.up('grid');
					if (grid) grid.inEditStatus = true;
				}
			})
		}
	}
});

Ext.data.field.Date.override({
			convert : function(v) {
				if (!v) {
					return null;
				}

				if (v instanceof Date) {
					return v;
				}

				var dateFormat = this.dateReadFormat || this.dateFormat, parsed;

				if (dateFormat) {
					return Ext.Date.parse(v, dateFormat);
				}

				// 解决FireFox浏览器无法显示日期bug begin denny.wu 2015.5.19
				if (typeof v == 'string') {
					v = v.replace(/-/g, "/");
				}
				// 解决FireFox浏览器无法显示日期问题 end

				parsed = Date.parse(v);
				return parsed ? new Date(parsed) : null;
			}
		});

Ext.toolbar.Paging.override({
			updateBarInfo : function() {
				var me = this;
				if (!me.store.isLoading() && me.store.autoLoad) { // 解决在tabpanel中切换时bug
																	// denny.wu
																	// 2015.5.21
					me.calledInternal = true;
					me.onLoad();
					me.calledInternal = false;
				}
			}
		});

Ext.selection.CheckboxModel.override({
			privates : {
				selectWithEventMulti : function(record, e, isSelected) {
					var me = this;
					if (!e.shiftKey && !e.ctrlKey
							&& e.getTarget(me.checkSelector)) {
						if (isSelected) {
							me.doDeselect(record, false); // 处理取消选择时，没有触法selectionchange事件bug
															// denny.wu
															// 2015.5.28
						} else {
							me.doSelect(record, true);
						}
					} else {
						me.callParent([record, e, isSelected]);
					}
				}
			}
		});

// 默认加入过滤插件
Ext.override(Ext.grid.Panel, {
	initComponent : function() {

		var me = this, plugins = me.getPlugins() ,isDDPlugin;

		if (plugins) {
			plugins.push("belleheaderfilter");
		} else {
			plugins = ["belleheaderfilter"];
		}

		me.plugins = plugins;
		
		
		if(!me.viewConfig){
			me.viewConfig={};
			me.viewConfig.plugins=[];
		}
		
		if(!me.viewConfig){
			me.viewConfig = {};
		}
		
		if(!me.viewConfig.plugins||!me.viewConfig.plugins.join){
			me.viewConfig.plugins=[];
		}
		
		Ext.each(me.viewConfig.plugins,function(plu){
			if(plu.ptype=="celldragdrop"){
				isDDPlugin=true;
				return false;
			}
		});
		
		//加入拖拽插件
//		if(!isDDPlugin){
//			me.viewConfig.plugins.push({
//                    ptype: 'celldragdrop',
//                    dtype:"update",
//                    pluginId:"ddCopyLogPanel",
//                    dragGroup: "g2",
//                    dropGroup: "g1"
//            });
//		}
		
		
		me.callParent();
	},
	isFilter : false, // 默认关闭
	isLocal : false
		// 本地过滤
	});

// 下拉控件支持模糊匹配
//添加禁用效果和排序
Ext.override(Ext.form.field.ComboBox, {
	initComponent : function() {
		var me = this, isDefined = Ext.isDefined, store = me.store, transform = me.transform, displayTpl = me.displayTpl, transformSelect, isLocalMode;
		if (me.typeAhead && me.multiSelect) {
			Ext.Error
					.raise('typeAhead and multiSelect are mutually exclusive options -- please remove one of them.');
		}
		if (me.typeAhead && !me.editable) {
			Ext.Error
					.raise('If typeAhead is enabled the combo must be editable: true -- please change one of those settings.');
		}
		if (me.selectOnFocus && !me.editable) {
			Ext.Error
					.raise('If selectOnFocus is enabled the combo must be editable: true -- please change one of those settings.');
		}

		if ('pinList' in me) {
			me.collapseOnSelect = !me.pinList;
		}

		if (transform) {
			transformSelect = Ext.getDom(transform);
			if (transformSelect) {
				if (!me.store) {
					store = Ext.Array.map(Ext.Array
									.from(transformSelect.options), function(
									option) {
								return [option.value, option.text];
							});
				}
				if (!me.name) {
					me.name = transformSelect.name;
				}
				if (!('value' in me)) {
					me.value = transformSelect.value;
				}
			}
		}
		me.bindStore(store || 'ext-empty-store', true, true);
		if (!displayTpl) {
			me.displayTpl = new Ext.XTemplate('<tpl for=".">'
					+ '{[typeof values === "string" ? values : values["'
					+ me.displayField + '"]]}' + '<tpl if="xindex < xcount">'
					+ me.delimiter + '</tpl>' + '</tpl>');
		} else if (!displayTpl.isTemplate) {
			me.displayTpl = new Ext.XTemplate(displayTpl);
		}
		isLocalMode = me.queryMode === 'local';
		if (!isDefined(me.queryDelay)) {
			me.queryDelay = isLocalMode ? 10 : 500;
		}
		if (!isDefined(me.minChars)) {
			me.minChars = isLocalMode ? 0 : 4;
		}
		me.callParent();
		me.doQueryTask = new Ext.util.DelayedTask(me.doRawQuery, me);

		if (transformSelect) {
			if (me.transformInPlace) {
				me.render(transformSelect.parentNode, transformSelect);
				delete me.renderTo;
			}
			Ext.removeNode(transformSelect);
		}
		var checkboxHtml=me.multiSelect==true?'<div class="combo-multi"></div>':'';
		// dwh 添加禁用效果颜色
		me.tpl = Ext.create('Ext.XTemplate',
						'<tpl for=".">',
							'{% ',
								'enableFlag=values.enableFlag; ',
								'if (enableFlag == undefined||enableFlag==null||enableFlag==""||enableFlag==1){',
									'enableFlag=1;',
								'}else{',
									'enableFlag=0;',
								'} ',
							'%}',
							'<tpl if="enableFlag==1">',
								'<div class="x-boundlist-item">',
								//'<tpl if="multiSelect==true">',
									' '+checkboxHtml+'',
								//'</tpl>',
								'{'+ me.displayField + '}',
								me.groupField?"&nbsp(<span style='color:red;'>{count}</span>)":"",
								'</div>',
							'</tpl>',
							'<tpl else if="enableFlag==0">',
								'<div class="x-boundlist-item x-boundlist-item-readonly" style="background-color: #EBEBE4;background-image: none;">',
								' '+checkboxHtml+'',
									'{'+ me.displayField + '}</div>', 
							'</tpl>',
						'</tpl>');
	},
	afterRender : function() {
		var me = this;
		me.callParent(arguments);
		me.setHiddenValue(me.value);

		var store = me.getStore(), sorts = [];

		// dwh 判断当前项是否可用被选中
		me.on("beforeselect", function(combox, rec) {
					return rec.get("enableFlag") != 0;
				});

		store.on("load", function(st) {
					sorts.push({
								property : 'enableFlag',
								direction : 'DESC'
							});
					store.remoteSort = false;
					store.remoteFilter = false;
					store.sort(sorts);

					store.oddData = store.getData().items.belleCopy();

				});

		me.on("beforequery", function(el) {
					store=me.store;
					if (store.getCount() > 0||(store.oddData&&store.oddData.length>0)) {
						me._filter(el.query);
						return false;
					} else {
						if(Ext.isEmpty(el.query)&&store.oddData&&store.oddData.length>0){
							me._filter("");
							return false;
						}
						
						//加载数据
						if(me.autoLoad==false&&!store.oddData){
							me.store.load({
								callback: function(records, operation, success) {
									if(success)	me.expand();
								}
							});
							
						}
						
						return true;
					}
				});

		//鼠标焦点移开时还原过滤状态
		me.on('blur',function(){
			if(me.store && me.store.oddData)
			me.store.loadData(me.store.oddData);
		});
//		me.on("specialkey", function(combo) {
//
//					if (Ext.isEmpty(combo.inputEl.getValue())) {
//						me._filter("");
//					}
//				});

	},
	delimiter : ",",
	_filter : function(val) {
		var combo = this, store = combo.getStore(), selectIndex = -1, filterData = [], items = [];
		// 缓存数据
		if (Ext.isEmpty(store.oddData)) {
			store.oddData = store.getData().items.belleCopy();
			store.loadData(store.oddData);
		}

		if (Ext.isEmpty(val)) {
			store.loadData(store.oddData || []);
			
			combo.expand();
			return;
		}

		items = val.split(combo.delimiter);

		if (!Ext.isEmpty(items)) {
			val = items[items.length - 1];
		}

		// 过滤符合条件数据
		filterData = store.oddData.filter(function(item) {
					index = item.data[combo.displayField].toUpperCase()
							.indexOf(val.toUpperCase());

					if (item.data[combo.displayField].toUpperCase() === val
							.toUpperCase()) {
						selectIndex = filterData.length;
					}

					return index >= 0;
				});
		// 重新加载数据
		store.loadData(filterData);

		// 设置全匹配项默认选中
		if (selectIndex >= 0) {
			combo.setSelection(store.getAt(selectIndex));
		}
		// 展开
		combo.expand();
	},
	isCanSelect : true
})

// 重载点击表头事件使其支持点击输入框
// 解决重新生成表头时无法创建belleFilter插件bug
Ext.grid.column.Column.override({
	childEls : ['titleEl', 'triggerEl', 'textEl', 'bellefilterEl'],
	headerTpl : [
			'<div id="{id}-titleEl" data-ref="titleEl" {tipMarkup}class="',
			Ext.baseCSSPrefix,
			'column-header-inner',
			'<tpl if="empty"> ',
			Ext.baseCSSPrefix,
			'column-header-inner-empty</tpl>">',
			'<span id="{id}-textEl" data-ref="textEl" class="',
			Ext.baseCSSPrefix,
			'column-header-text',
			'{childElCls}">',
			'{text}',
			'</span>',
			'<tpl if="!menuDisabled">',
			'<div id="{id}-triggerEl" data-ref="triggerEl" role="presentation" class="',
			Ext.baseCSSPrefix,
			'column-header-trigger',
			'{childElCls}" style="{triggerStyle}"></div>',
			'</tpl>',
			'</div>',
			'<div id="{id}-bellefilterEl" data-ref="bellefilterEl" class="bellefilter-common-body" style="{belleBodyStyle}"></div>',
			'{%this.renderContainer(out,values)%}'],
	onTitleElClick : function(e, t) {
		var me = this, activeHeader, prevSibling, bellefilterEl;

		// Tap on the resize zone triggers the menu
		if (Ext.supports.Touch) {
			prevSibling = me.previousSibling(':not([hidden])');

			// Tap on right edge, activate this header
			if (!me.menuDisabled
					&& me.isOnRightEdge(e, parseInt(me.triggerEl
									.getStyle('width')))) {
				if (!me.menuDisabled) {
					activeHeader = me;
				}
			}

			// Tap on left edge, activate previous header
			else if (prevSibling && !prevSibling.menuDisabled
					&& me.isOnLeftEdge(e)) {
				activeHeader = prevSibling;
			}
		} else {
			// Firefox doesn't check the current target in a within check.
			// Therefore we check the target directly and then within
			// (ancestors)
			activeHeader = me.triggerEl
					&& (e.target === me.triggerEl.dom || t === me.triggerEl.dom || e
							.within(me.triggerEl)) ? me : null;

			bellefilterEl = me.bellefilterEl
					&& (e.target === me.bellefilterEl.dom
							|| t === me.bellefilterEl.dom || e
							.within(me.bellefilterEl)) ? me : null;
		}

		// If it's not a click on the trigger or extreme edges. Or if we are
		// called from a key handler, sort this column.
		if (!bellefilterEl && !activeHeader && !me.isOnLeftEdge(e)
				&& !me.isOnRightEdge(e) || e.getKey()) {
			me.toggleSortState();
		}
		return activeHeader;
	},
	initComponent : function() {
		var me = this;

		// Preserve the scope to resolve a custom renderer.
		// Subclasses (TreeColumn) may insist on scope being this.
		me.rendererScope = me.initialConfig.scope;

		if (me.header != null) {
			me.text = me.header;
			me.header = null;
		}

		if (me.cellWrap) {
			me.tdCls = (me.tdCls || '') + ' ' + Ext.baseCSSPrefix + 'wrap-cell';
		}

		// A group header; It contains items which are themselves Headers
		if (me.columns != null) {
			me.isGroupHeader = true;

			// <debug>
			if (me.dataIndex) {
				Ext.Error
						.raise('Ext.grid.column.Column: Group header may not accept a dataIndex');
			}
			if ((me.width && me.width !== Ext.grid.header.Container.prototype.defaultWidth)
					|| me.flex) {
				Ext.Error
						.raise('Ext.grid.column.Column: Group header does not support setting explicit widths or flexs. The group header width is calculated by the sum of its children.');
			}
			// </debug>

			// The headers become child items
			me.items = me.columns;
			me.columns = me.flex = me.width = null;
			me.cls = (me.cls || '') + ' ' + me.groupHeaderCls;

			// A group cannot be sorted, or resized - it shrinkwraps its
			// children
			me.sortable = me.resizable = false;
			me.align = 'center';
		} else {
			// Flexed Headers need to have a minWidth defined so that they can
			// never be squeezed out of existence by the
			// HeaderContainer's specialized Box layout, the ColumnLayout. The
			// ColumnLayout's overridden calculateChildboxes
			// method extends the available layout space to accommodate the
			// "desiredWidth" of all the columns.
			if (me.flex) {
				me.minWidth = me.minWidth
						|| Ext.grid.plugin.HeaderResizer.prototype.minColWidth;
			}
		}

		me.addCls(Ext.baseCSSPrefix + 'column-header-align-' + me.align);

		var grid = me._getGrid();
		
		if(!grid.fieldDatas){
			grid.fieldDatas={};
		}
		// 判断网格是否允许编辑
		if (grid && !grid.isReadOnly && me.editor) {
			if (me.editor.allowBlank === false) {
				me.addCls('notnull-field');
			} else {
				me.addCls('cannull-field');
			}
		}

		me.renderTpl = me.headerTpl;
		var els = me.getChildEls();

		els.bellefilterEl = {
			itemId : "bellefilterEl",
			name : "bellefilterEl"
		};
		me.setChildEls(els);

		me.on("afterrender", function(col) {
					this.belleBodyStyle="height:30px;";
					me.createHeaderEl(this);
				});

		if (grid) {
			// 添加方法
			me.initGridMethods(grid);

			// 监听事件
			
			if(!grid.isAddEvent){
				me.initStoreEvents(grid);  //屏蔽在构建列的时候，去绑定监听事件，这样会事件叠加
				grid.isAddEvent = true;
			}
			

			if (grid.isFilter) {
				grid.removeCls("grid-filter-hide");
			} else {
				grid.addCls("grid-filter-hide");
				
			}

		}

		// Set up the renderer types: 'renderer', 'editRenderer', and
		// 'summaryRenderer'
		me.setupRenderer();
		me.setupRenderer('edit');
		me.setupRenderer('summary');

		// Initialize as a HeaderContainer
		me.callParent(arguments);

	},
	initStoreEvents : function(grid) {
		var me = this;
		var store = grid.getStore();

		store.on("load", function() {
					// 数据加载后缓存当前数据
					
					if(store.proxy&&store.proxy.reader&&store.proxy.reader.rawData&&store.proxy.reader.rawData.list){
						store.oddData = store.proxy.reader.rawData.list.belleCopy();
					}
					else{
						store.oddData = store.getData().items.belleCopy();
					}
					
					
					//加载完数据后执行
					if(grid.getFilterLocal()){
						me._filterLocal();
					}
		});

		// 初始化请求参数
		store.on("beforeload", function() {
					// 设置请求参数
					// me.initExtraParams();
				});

	},
	initRenderData : function() {
		var me = this, tipMarkup = '', tip = me.tooltip, text = me.text, attr = me.tooltipType === 'qtip'
				? 'data-qtip'
				: 'title';
		if (!Ext.isEmpty(tip)) {
			tipMarkup = attr + '="' + tip + '" ';
		}
		return Ext.applyIf(me.callParent(arguments), {
					text : text,
					empty : text === '&#160;' || text === ' ' || text === '',
					menuDisabled : me.menuDisabled,
					tipMarkup : tipMarkup,
					triggerStyle : this.getTriggerVisible()
							? 'display:block'
							: '',
					belleBodyStyle:me.belleBodyStyle
				});
	},
	createHeaderEl : function(col) {
		var me = this, grid = me._getGrid(), operator;
		// 判断是否为字段
		if (!col.dataIndex)
			return;
		// 2015-4-28 dwh
		// 获取过滤组件
		var bellefilterEl = col.bellefilterEl;
		if (bellefilterEl && !col.bellefilterObj) {
			// var property=col.sortField||col.belleFilter.sortField

			// 默认属性
			var config = {
				width : "100%",
				renderTo : bellefilterEl.dom,
				listeners:{
					afterrender:function(field){
						if(grid.fieldDatas[field.property]!=window.undefined){
							//保存过滤字段因为重构表头时数据丢失问题
							field.setValue(grid.fieldDatas[field.property]);
						}
						
					
					}
				}
			};

			if (!col.belleFilter) {
				// 默认显示扩展控件
				col.belleFilter = {
					xtype : 'bellefilter'
				};
			} else if (!col.belleFilter.xtype) {
				col.belleFilter.xtype = "bellefilter";
			}
			// 默认类型
			if (!col.belleFilter.filterType) {
				col.belleFilter.filterType = "like";
			}

			config.property = col.dataIndex;
			config.propertyName = col.belleFilter.dataIndex || col.dataIndex; // 别名

			switch (col.belleFilter.filterType) {
				case "like" :
					operator = "15";
					break;
				case "=" :
					operator = "10";
					break;
				case ">" :
					operator = "11";
					break;
				case "<" :
					operator = "12";
				case "likeleft" :
					operator = "19";
				case "likeright" :
					operator = "20";
					break;
				default :
					operator = "10";
					break;
			}
			col.belleFilter.operator = operator;
			col.belleFilter.hidden = col.belleFilter.isOpen == false
					? true
					: false;

			Ext.applyIf(config, col.belleFilter);
			col.bellefilterObj = Ext.widget(config);
			col.bellefilterObj.grid=grid;
			// 判断是否为扩展控件
			col.bellefilterObj.inputEl.on("focus",function(field){
				var val= col.bellefilterObj.getValue();
				
				if(!Ext.isEmpty(val)){
					col.bellefilterObj.selectText(0,val.length);
				}
				
				return false;
			});
			
			//记录当前字段数据
			col.bellefilterObj.on("change",function(field,newValue,oldValue){
				//记录过滤字段历史数据
				grid.fieldDatas[field.property]=newValue;
			});
			
			col.bellefilterObj.inputEl.on("dblclick",function(field){
			
				col.bellefilterObj.setValue('');
				return false;
			});
			if (col.belleFilter.xtype === "bellefilter") {

				col.bellefilterObj.on("onSelectFilter", function(val, type,
								operator, el) {
							me._changeValueFilter();

						});
				// 过滤所有
				col.bellefilterObj.on("onBelleFilterKeyup", function(val, type,
								operator, el, event) {
							var keyCode = event.keyCode;

							me._keyFilter(keyCode);

						});
				// 过滤本地数据
				col.bellefilterObj.on("onBelleFilterChange", function(val,
								type, operator, el, event) {
							// var isLocal=grid.getFilterLocal(),
							// isFilterSarver=false;
							//					
							// me._changeValueFilter();
						});
			} else {
				col.bellefilterObj.el.on("keyup", function(field, event) {
							var keyCode = event.keyCode;

							me._keyFilter(keyCode);

						});

				col.bellefilterObj.on("change", function(field, newValue,oddValue) {
					me._changeValueFilter();
				});
			}

		}
	},
	_getGrid:function(){
		var me = this ,panel = me,grid;
		
		while(!grid && panel){
			panel = panel.up();
			
			if(panel){
				grid = panel.grid;
			}
		}
		return grid;
	},
	// 按下回车时过滤方式
	_keyFilter : function(keyCode) {
		var me = this, grid =me._getGrid(), isLocal  ;
		
		if(!grid) return; 
		
		isLocal=grid.getFilterLocal();

		if (!isLocal) {
			if (keyCode != 13)
				return;
			me._filterServer();
		} else {
			me._filterLocal();
		}
	},
	// 输入数据时过滤方式
	_changeValueFilter : function() {
		var me = this, grid = me._getGrid(), isLocal  ;
		
		if(!grid) return; 

		isLocal=grid.getFilterLocal();
		
		if (!isLocal) {

			me._filterServer();
		} else {
			me._filterLocal();
		}
	},
	initExtraParams : function() {
		var me = this, grid = me._getGrid();
		
		if(!grid) return;
		
		var store = grid.getStore(), extraParams = store
				.getProxy().extraParams, filters = me._getFilters() || [];

		if(!extraParams) return;
		if (grid.otherfilter && !grid.supGrid) {
			filters = filters.concat(grid.otherfilter);
		}

		// 开启过滤所有状态时设置请求参数
		if (grid.getFilterStatus() == true && !grid.getFilterLocal()) {

			extraParams.queryCondition = Ext.encode(filters);
		} else {
			if (!Ext.isEmpty( grid.otherfilter)) {
				extraParams.queryCondition = Ext.encode(grid.otherfilter);
			} else {

				delete extraParams.queryCondition;
			}
		}

	},
	_getFilters : function() {
		var me = this, grid = me._getGrid();
		
		if(!grid) return;
		
		var  columns = grid.getColumnManager().columns, filters = [], isLocal = grid
				.getFilterLocal();

		if (!grid.isFilter)
			return [];

		Ext.each(columns, function(item) {
					var obj = item.bellefilterObj, filter = {}, val;
					if (obj && obj.inputEl && obj.inputEl.dom && !obj.hidden) {
						if (obj.config.xtype == "combo"
								|| obj.config.xtype == "combobox"||obj.config.xtype=="extcombox") {
							
							if(obj.filterKey==="text"){
								val = obj.getRawValue();
							}
							else{
								val = obj.getValue();
							}
							
						} else {
							val = obj.getRawValue();
						}
						
						if (Ext.isEmpty(val)) return true;
						
						var format,date;
						if(typeof(val)==='string') {
							if (val.trim().length === 19) {
								format = 'Y-m-d H:i:s';
							}
							else {
								format = 'Y-m-d';
							}
							date = Ext.Date.parse(val.trim(), format);
						}
						if(date){
							
							filter = {
									value : Ext.Date.format(date,'Y-m-d'),
									operator : "13",
									property : isLocal
											? obj.property
											: obj.propertyName
							};
							filters.push(filter);
							
							date.setDate(date.getDate()+1);
							filter = {
									value : Ext.Date.format(date,'Y-m-d'),
									operator : "12",
									property : isLocal
											? obj.property
											: obj.propertyName
							};
							filters.push(filter);
						}
						else{
							filter = {
									value : val,
									operator : obj.operator,
									property : isLocal
											? obj.property
											: obj.propertyName
							};
							filters.push(filter);
						}
						
						
					}
				});
		return filters;
	},
	_filterLocal : function() {
		var me = this, grid = me._getGrid();
		
		if(!grid) return;
		
		var store = grid.getStore(), belleFilters = me
				._getFilters();

		if (!store.oddData)
			return;
		var filterData = [];
		filterData = store.oddData.filter(function(item) {
			var isFilter = true,
			valType;
			Ext.each(belleFilters, function(filter) {
						var data=item.data||item;
						var property = data[filter.property];
						if (Ext.isEmpty(property)) {

							property = "";
						}
						if (Ext.isEmpty(filter.value)) {
							filter.value = "";
						}
						
						valType=typeof(property);
						
						if(Ext.isNumber(property)||Ext.isNumeric(property)){
							valType="number";
							property=parseFloat(property);
						}
						// 其中一个条件不满足时跳出
						switch (filter.operator) {
							// like
							case '15' :
								// 支持不区分大小写
								if (valType != "string") {
									isFilter = property.toString()
											.toLowerCase().indexOf(filter.value
													.toLowerCase()) >= 0;
								} else {
									isFilter = property
											.toLowerCase()
											.indexOf(filter.value.toLowerCase()) >= 0;
								}

								break;
							// ==
							case "19":
									
								if (valType != "string") {
									property = property.toString().toLowerCase();
								} 
								else{
									property = property.toLowerCase();
								}
								
								if(filter.value.length <= property.length){
									if(property.substring(0,filter.value.length)==filter.value.toLowerCase()){
										isFilter = true;
									}
									else{
										isFilter = false;
									}
								}
								else{
									isFilter = false;
								}
							break;
							case "20":
									
								if (valType != "string") {
									property = property.toString().toLowerCase();
								} 
								else{
									property = property.toLowerCase();
								}
								
								if(filter.value.length <= property.length){
									if(property.substring(property.length-filter.value.length,property.length)==filter.value.toLowerCase()){
										isFilter = true;
									}
									else{
										isFilter = false;
									}
								}
								else{
									isFilter = false;
								}
							break;
							case '10' :
								isFilter = property == filter.value;
								break;
							case '12' :
								switch (valType) {
									case "number" :
										isFilter = property < parseFloat(filter.value);
										break;
									case "string" :
										isFilter = me._comparisonDateValue(
												property, filter.value);

										break;
									default :
										isFilter = false;
										break;
								}
								break;
							case '11' :
								switch (valType) {
									case "number" :
										isFilter = property > parseFloat(filter.value);
										break;
									case "string" :
										isFilter = !me._comparisonDateValue(
												property, filter.value);

										break;
									default :
										isFilter = false;
										break;
								}
								break;
						}
						// 不满足当前条件时退出筛选
						if (!isFilter) {
							return false;
						}
					});
			return isFilter;
		});
		me.initExtraParams();
		store.loadData(filterData);
	},
	// 判断val1是否大于val2
	_comparisonDateValue : function(val1, val2) {
		var v1 = Ext.Date.parse(val1, "Y-m-d H:i:s")
				|| Ext.Date.parse(val1, "Y-m-d");
		var v2 = Ext.Date.parse(val2, "Y-m-d H:i:s")
				|| Ext.Date.parse(val2, "Y-m-d");

		if (v1 && v2) {
			return v1.getTime() < v2.getTime();
		} else {
			return false;
		}
	},
	_filterServer : function() {
		var me = this, grid = me._getGrid();
		
		if(!grid) return;
		
		store = grid.getStore();
		if (!grid.isFilter)
			return;

		me.initExtraParams();

		store.loadPage(1);
	},
	// 过滤数据
	initGridMethods : function(grid) {
		var me = this, columns = grid.getColumnManager().columns;
		grid.getFilterStatus = function() {
			return grid.isFilter;
		};
		grid.setFilterStatus = function(status) {

			var store = grid.getStore();
			if (status) {
				grid.isFilter = true;
				grid.removeCls("grid-filter-hide");
			} else {
				grid.isFilter = false;
				grid.addCls("grid-filter-hide");
				//关闭时清空过滤值
				me.clearFilterValue();
			}
			grid.fireEvent("onFilterStatusChange", grid.isLocal, grid.isFilter,
					grid);
		};

		grid.getFilterLocal = function() {
			return grid.isLocal;
		};

		grid.setFilterLocal = function(val) {
			grid.setFilterStatus(true);
			grid.isLocal = val;
			me.initExtraParams();
			grid.fireEvent("onFilterLocalChange", me.isLocal, me.isFilter, me);

			if (val) {
				Ext.each(columns, function(item) {
							if (item.bellefilterObj) {
								item.bellefilterObj.show();
							}
						});
			} else {
				Ext.each(columns, function(item) {
							if (item.bellefilterObj
									&& item.bellefilterObj.isOpen == false) {
								item.bellefilterObj.hide();
							}

						});
			}
		};
		//
		grid.setOtherFilters = function(filters) {
			grid.otherfilter = filters;
			me.initExtraParams();

		};

		grid.getFilters = function() {
			return me._getFilters();
		};
		
		
		grid.initExtraParams=function(){
			me.initExtraParams();
		}
	},
	clearFilterValue :function(){
		var me = this, grid = me._getGrid();
		if(!grid) return;
		var columns = grid.columns,store= grid.getStore();

		
		Ext.each(columns, function(item) {
			var obj = item.bellefilterObj, filter = {}, val;
			if (obj && obj.inputEl && obj.inputEl.dom && !obj.hidden) {
				obj.setValue("");
			}
		});
		
		if(grid&&grid.getFilterLocal()&&!Ext.isEmpty(store.oddData)){
			//加载数据
			store.loadData(store.oddData)
		}
		
		if(grid && !grid.getFilterLocal()){
			//初始化请求参数
			me.initExtraParams();
			//重载数据
			store.load();
		}
	}
});


Ext.override(Ext.grid.feature.Summary,{
	
	renderSummaryRow : function(values, out, parent) {
var view = values.view, me = view.findFeature('summary'), record;
		
		//dwh Ext网格行的显示行数是根据显示面板高度计算实际能够显示行数*2后得出实际渲染行数以提升网格的渲染速度。
		//为解决网格行数过多时合计列显示位置错误bug，本处控制只在最后一行是才显示合计列
		
		//判断当前页是否已渲染到最后一行
		if(values.view.grid.store.getCount()<=values.viewStartIndex+values.rows.length) {
			
			if (me.showSummaryRow) {
				record = me.summaryRecord;
				out.push('<table class="' + Ext.baseCSSPrefix + 'table-plain '
						+ me.summaryItemCls + '">');
				me.outputSummaryRecord((record && record.isModel) ? record : me
								.createSummaryRecord(view), values, out, parent);
				out.push('</table>');
			}
			
			
		}
		else{
			//拖动网格滚动条时重新渲染行时合计列未被清除，导致合计列显示异常。隐藏历史合计列
			var _sumRowEl = Ext.query(".x-table-plain.x-grid-item-summary");
			if(_sumRowEl)  {
				Ext.each(_sumRowEl,function(item){
					item.hidden=true;
				});
			}
			return;
		}
	}
});


//基类控制的网格添加保存布局功能
Ext.override(Ext.grid.header.Container,{
	//重新获取右键菜单网格列
	getColumnMenu : function(headerContainer) {
		var menuItems = [], i = 0, item, items = headerContainer
				.query('>gridcolumn[hideable]'), itemsLn = items.length, menuItem;
		for (; i < itemsLn; i++) {
			item = items[i];
			var disabled=false;
			
			//戴文辉控制必填列不允许隐藏
			if(item.config&&item.config.editor&&item.config.editor.allowBlank==false){
				disabled=true;
			}
			
			if(!disabled){
				menuItem = new Ext.menu.CheckItem({
						text : item.menuText || item.text,
						checked : !item.hidden,
						hideOnClick : false,
						headerId : item.id,
						menu : item.isGroupHeader
								? this.getColumnMenu(item)
								: undefined,
						checkHandler : this.onColumnCheckChange,
						scope : this
					});
				menuItems.push(menuItem);
			}
			
		}

		return menuItems.length ? menuItems : null;
	},
	getMenuItems : function() {
		var me = this, menuItems = [],grid = me.up(), hideableColumns = me.enableColumnHide
				? me.getColumnMenu(me)
				: null;
		if (me.sortable) {
			menuItems = [{
						itemId : 'ascItem',
						text : me.sortAscText,
						iconCls : me.menuSortAscCls,
						handler : me.onSortAscClick,
						scope : me
					}, {
						itemId : 'descItem',
						text : me.sortDescText,
						iconCls : me.menuSortDescCls,
						handler : me.onSortDescClick,
						scope : me
					}];
		}
		//dwh 判断是否允许保存布局信息 
		//尺码横排生成成的网格
		if(grid.canSaveGridLayout && grid.mSizeIdx<0) {
			menuItems.push({
				itemId: 'saveGridLayout',
				text: "保存布局",
				glyph: Belle.Icon.btnSave,
				handler: me.onSaveGridLayout,
				scope: me
			});
			menuItems.push({
				itemId: 'delGridLayout',
				text: "清除自定义布局",
				glyph: Belle.Icon.btnDelete,
				handler: me.onDeleteGridLayout,
				scope: me
			})
		}
		
		if (hideableColumns && hideableColumns.length) {
			if (me.sortable) {
				menuItems.push({
							itemId : 'columnItemSeparator',
							xtype : 'menuseparator'
						});
			}
			menuItems.push({
						itemId : 'columnItem',
						text : me.columnsText,
						iconCls : me.menuColsIcon,
						menu : hideableColumns,
						hideOnClick : false
					});
		}
		return menuItems;
	},
	onDeleteGridLayout:function(){
		var me = this,params={},grid=me.up(),moduleId,panel,layoutJson = [],userId;
		Belle.confirm("是否确认清除当前网格的自定义布局？",function(type){
			if(type!="yes") return;
			panel=grid.up();
			while(panel&&!panel.moduleUrl){
				panel=panel.up();
			}
			userId=window.main?window.main.userInfo.userCode:Belle.getUrlParam("u");

			if(panel){
				moduleId=panel.xtype;
			}
			if(!userId){
				Belle.alert("未获取到账号数据");
				return;
			}
			if(!moduleId){
				Belle.alert("未获取到模块信息");
				return;
			}
			params.gridId=grid.reference;
			//params.moduleId=moduleId;
			//params.userId=userId;
			params.resCode = moduleId;
			panel.mask("正在删除布局信息...");
			grid.up("basepage").controller.callServer({
				//url:"/blf1-uc-web/itg_grid_layout/deleteById.json",
				url:"/uc_layout_v1/deleteLayout.json?",
				params:params,
				success:function(response, opts){
					var obj = Ext.decode(response.responseText);
					
					panel.unmask();
					
					if(obj.flag &&obj.flag.retCode=="0"){
						Belle.alert("删除成功！");
					}
					else{
						Belle.alert("删除失败！");
					}
				},
				failure: function(response, opts) {
					Belle.alert("服务器异常！请联系管理员");
				}
			});

		});
	},
	onSaveGridLayout:function(){
		var me = this,params={},grid=me.up(),moduleId,panel,layoutJson = [],userId;
		
		Belle.confirm("是否确认保存当前网格布局？",function(type){
			if(type!="yes") return;
			
		 	panel=grid.up();
		 	
		 	while(panel&&!panel.moduleUrl){
		 		panel=panel.up();
		 	}
		 	
		 	userId=window.main?window.main.userInfo.userCode:Belle.getUrlParam("u");
		 	
		 	if(panel){
		 		moduleId=panel.xtype;
		 	}
			
		 	
		 	Ext.each(grid.headerCt.columnManager.getColumns(),function(col){
		 		if(col.isCheckerHd) return true;
		 		var gridCol={
		 			width:col.width,
		 			hidden:col.hidden?true:false
		 		};
		 		
		 		if(col.dataIndex){
		 			gridCol.dataIndex=col.dataIndex;
		 		}
		 		
		 		if(col.text && col.text!="&#160;"){
		 			gridCol.text=col.text;
		 		}
		 		
		 		
//		 		if(col.xtype && col.xtype=='rownumberer'){
//		 			gridCol.xtype=col.xtype;
//		 		}
		 		
		 		layoutJson.push(gridCol);
		 		
		 	});
		 	
		 	if(!userId){
		 		Belle.alert("未获取到账号数据");
		 		return;
		 	}
		 	
		 	if(!moduleId){
		 		Belle.alert("未获取到模块信息");
		 		return;
		 	}
		 	
		 	if(layoutJson.length<=0){
		 		
		 		return;
		 	}
		 	
			params.gridId=grid.reference;
			params.layoutJson=Ext.encode(layoutJson);
			//params.moduleId=moduleId;
			//params.userId=userId;
			params.resCode = moduleId;
			
			panel.mask("正在保存布局信息...");
			grid.up("basepage").controller.callServer({
				//url:"/blf1-uc-web/itg_grid_layout/save.json",
				url:"/uc_layout_v1/saveLayout.json",
				params:params,
				success:function(response, opts){
					var obj = Ext.decode(response.responseText);
					
					panel.unmask();
					
					if(obj.flag &&obj.flag.retCode=="0"){
						Belle.alert("保存布局信息成功！");
					}
					else{
						Belle.alert("保存布局信息失败！");
					}
				},
				failure: function(response, opts) {
					panel.unmask();
					Belle.alert("服务器异常！请联系管理员");
				}
			});
			
		});
		
	}
});

//为网格添加tip
Ext.override(Ext.view.Table,{
	cellTpl : [
			'{%',
            	'var tipValue="";',
            	'if(values.column.dataIndex&& values.column.openTip!=false) tipValue=values.column.tipText||Ext.util.Format.htmlEncode(values.value); ',
        	'%}',
			
			'<td data-qtip="{[tipValue]}" class="{tdCls}" {tdAttr} {[Ext.aria ? "id=\\"" + Ext.id() + "\\"" : ""]} style="width:{column.cellWidth}px;<tpl if="tdStyle">{tdStyle}</tpl>" tabindex="-1" {ariaCellAttr} data-columnid="{[values.column.getItemId()]}">',
			'<div {unselectableAttr} class="' + Ext.baseCSSPrefix
					+ 'grid-cell-inner {innerCls}" ',
			'style="text-align:{align};<tpl if="style">{style}</tpl>" {ariaCellInnerAttr}>{value}</div>',
			'</td>', {
				priority : 0
	}]
});

//设置超时时间长短为120s
Ext.override(Ext.data.Connection,{
	timeout:120000
});

//约束显示范围
Ext.override(Ext.window.Window,{
	constrainHeader : true
});

Ext.override(Ext.form.Panel,{

	initComponent : function() {
		var me = this;
		if (me.frame) {
			me.border = false;
		}
		me.initFieldAncestor();
		me.callParent();
		me.relayEvents(me.form, [

						'beforeaction',

						'actionfailed',

						'actioncomplete',

						'validitychange',

						'dirtychange']);

		if (me.pollForChanges) {
			me.startPolling(me.pollInterval || 500);
		}
		
		if(!me.plugins || !me.plugins.join){
			me.plugins=[];
		}
		
//		me.plugins.push(Ext.create("Ext.grid.plugins.GridColumnFromFieldDragDrop",{
//					dragGroup: "form",
//                    dropGroup: "grid"
//		}));
	}
});


//添加条件(|| previous==0)， 处理必填项数据为0时，无法自动绑定问题
Ext.override(Ext.app.bind.BaseBinding,{
	privates : {
		notify : function(value) {
			var me = this, options = me.options || me.defaultOptions, previous = me.lastValue;

			if (!me.calls || me.deep || previous !== value || previous==0
				|| Ext.isArray(value)) {
				++me.calls;
				me.lastValue = value;
				if (me.lateBound) {

					me.scope[me.callback](value, previous, me);
				} else {
					me.callback.call(me.scope, value, previous, me);
				}
				if (options.single) {
					me.destroy();
				}
			}
		}
	}

})


/*
 * 产品选择器
 */

Ext.define('Belle_Common.ux.MaterialField', {
	extend : 'Belle_Common.ux.SearchHelpField',
	alias : 'widget.materialfield',
	canQueryCondition: true,
	constructor : function(config) {
		var me = this;

		if (Ext.isEmpty(config)) {
			config = {};
		}
		config.canQueryCondition=me.canQueryCondition;
		// 默认咧
		if (Ext.isEmpty(config.gridColumns)) {
			config.gridColumns = [{
						header : "产品ID",
						dataIndex : "materialNo"
					}, {
						header : "产品编码",
						dataIndex : "materialCode"
					}, {
						header : "国际条码",
						dataIndex : "barcodeEan"
					}, {
						header : "产品名称",
						dataIndex : "materialName",
						belleFilter:{
							xtype:"sumfield",
							groupField:"materialName"
						}
					}, {
						header : "产品小类",
						dataIndex : "categoryName"
					}, {
						header : "款号",
						dataIndex : "styleNo"
					}];
		}

		// 默认查询参数
		if (Ext.isEmpty(config.searchItems)) {
			config.searchItems = [{
						name : "materialNo",
						fieldLabel : "产品ID",
						canQueryCondition: true
					}, {
						name : "materialCode",
						fieldLabel : "产品编码",
						canQueryCondition: true
					}, {
						name : "barcodeEan",
						fieldLabel : "国际条码",
						canQueryCondition: true
					}, {
						name : "materialName",
						fieldLabel : "产品名称",
						canQueryCondition: true
					}, {
						name : "categoryName",
						fieldLabel : "产品小类"
					}, {
						name : "styleNo",
						fieldLabel : "款号",
						canQueryCondition: true
					}];
		}

		// 默认路径
		if (Ext.isEmpty(config.url)) {
			config.url = Belle.mdmPath
					+ 'bas_material/listMSizeVo.json?selectVoName=SelectListByVoBasMaterialCategoryModel&mapperClassType=BasMaterialMapper&mType=2';
		}

		if (!Ext.isEmpty(config.brandNo)) {
			if (Ext.isEmpty(config.filters)) {
				config.filters = [];
			}

			config.filters.push({
						property : "brandNo",
						value : config.brandNo
					});
		}

		Ext.apply(me, config);
		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
	},
	// 重新验证方法
	checkFun : function(val, callback) {
		var me = this, valText;

		if (Ext.isString(val)) {
			valText = val;
		} else {
			valText = val[0].get("materialNo");
		}

		me._vailMateril(me.billNo, valText, function(rowCount) {
					var type = rowCount <= 0;

					if (type === false) {
						Belle.alert("产品不能重复添加");
					}
					callback(type);
				});
	},
	_vailMateril : function(billNo, materialNo, callback) {
		Ext.Ajax.request({
					method : "GET",
					url : Belle.sdsPath + 'bl_co_dtl/findCount.json',
					params : {
						'billNo' : billNo,
						materialNo : materialNo
					},
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						if (obj.count) {
							callback(obj.count);
						} else {
							callback(-1);
						}
					},
					failure : function(response, opts) {
						callback(-1);
					}
				});
	},
	winTitle : "产品选择器"
});
/*
 * 产品选择器
 */

Ext.define('Belle_Common.ux.MaterialListField', {
	extend : 'Belle_Common.ux.SearchHelpField',
	alias : 'widget.materiallistfield',
	constructor : function(config) {
		var me = this;

		if (Ext.isEmpty(config)) {
			config = {};
		}
		config.canQueryCondition=false;
		// 默认咧
		if (Ext.isEmpty(config.gridColumns)) {
			config.gridColumns = [{
                    dataIndex: 'materialNo',
                    text: '产品ID',
                    flex: 0.5
                },
                {
                    dataIndex: 'materialCode',
                    text: '产品编码',
                    flex: 0.5
                },
                {
                	dataIndex: 'materialName',
                    text: '产品名称',
                    flex: 0.5
                }];
		}

		// 默认查询参数
		if (Ext.isEmpty(config.searchItems)) {
			config.searchItems = [{
                    name: 'materialNo',
                    fieldLabel: '产品ID',
                    canQueryCondition:true
                },
                {
                    name: 'materialCode',
                    fieldLabel: '产品编码',
                    canQueryCondition:true
                },
                {
                    name: 'materialName',
                    fieldLabel: '产品名称',
                    canQueryCondition:true
                }];
		}

		// 默认路径
		if (Ext.isEmpty(config.url)) {
			config.url =Belle.mdmPath + 'bas_material/list.json'; //后端的服务URL
		}

		if (!Ext.isEmpty(config.brandNo)) {
			if (Ext.isEmpty(config.filters)) {
				config.filters = [];
			}
		}

		Ext.apply(me, config);
		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
	},
	winTitle : "产品选择器"
});
/*
 * 产品选择器
 */

Ext.define('Belle_Common.ux.MaterialSelectionWin', {
	extend : 'Belle_Common.ux.SearchWin',
	alias : 'widget.materialselectionwin',
	constructor : function(config) {
		var me = this;

		if (Ext.isEmpty(config)) {
			config = {};
		}

		// 默认咧
		if (Ext.isEmpty(config.gridColumns)) {
			config.gridColumns = [{
						header : "产品ID",
						dataIndex : "materialNo"
					}, {
						header : "产品编码",
						dataIndex : "materialCode"
					}, {
						header : "国际条码",
						dataIndex : "barcodeEan"
					}, {
						header : "产品名称",
						dataIndex : "materialName"
					}, {
						header : "产品小类",
						dataIndex : "categoryName"
					}, {
						header : "款号",
						dataIndex : "styleNo"
					}];
		}

		// 默认查询参数
		if (config.searchItems==undefined) {
			config.searchItems = [{
						name : "materialNo",
						fieldLabel : "产品ID"
					}, {
						name : "materialCode",
						fieldLabel : "产品编码"
					}, {
						name : "barcodeEan",
						fieldLabel : "国际条码"
					}, {
						name : "materialName",
						fieldLabel : "产品名称"
					}, {
						name : "categoryName",
						fieldLabel : "产品小类"
					}, {
						name : "styleNo",
						fieldLabel : "款号"
					}];
		}

		// 默认路径
		if (Ext.isEmpty(config.url)) {
			config.url = Belle.mdmPath
					+ 'bas_material/listMSizeVo.json?selectVoName=SelectListByVoBasMaterialCategoryModel&mapperClassType=BasMaterialMapper&mType=2';
		}

		if (!Ext.isEmpty(config.brandNo)) {
			if (Ext.isEmpty(config.filters)) {
				config.filters = [];
			}

			config.filters.push({
						property : "brandNo",
						value : config.brandNo,
						operator : "10"
					});
		}

		// 附加参数
		if (!Ext.isEmpty(config.otherGridColumns)) {
			config.gridColumns = config.gridColumns
					.concat(config.otherGridColumns);
		}
		// 附加参数
		if (!Ext.isEmpty(config.otherSearchItems)) {
			config.searchItems = config.searchItems
					.concat(config.otherSearchItems);
		}

		Ext.apply(me, config);
		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
	},
	winTitle : "产品选择器"
});
Ext.define('Ext.form.field.SummarySearchField', {
    extend:'Ext.form.field.ComboBox',
    alias: ['widget.summarysearchfield', 'widget.sumfield'],
    
    triggers : {
			clear: {
				cls: 'x-form-clear-trigger',
				handler: 'onClearTriggerClick',
				hidden: true,
				scope: 'this'
			}
	},
    //triggerCls:'x-form-search-trigger',
	onClearTriggerClick: function () {
		var me = this;
		me.setValue("");
		me.getTrigger('clear').hide();
	},
	onTriggerClick: function() {
        var me = this;
        
        me.duringTriggerClick = true;
        if (!me.readOnly && !me.disabled) {
            if (me.isExpanded) {
                me.collapse();
            } else {
                me.onFocus({});
                if (me.triggerAction === 'all') {
                    me.doQuery(me.allQuery, true);
                } else if (me.triggerAction === 'last') {
                    me.doQuery(me.lastQuery, true);
                } else {
                    me.doQuery(me.getRawValue(), false, true);
                }
            }
        }
        delete me.duringTriggerClick;
        
        
         
        
    },
    groupField:"",
    displayField:"text",
    valueField:"value",
    store:{
    	fields:['text',"value","count"]
    },
    initComponent:function(){
    	var me = this;
		me.callParent(arguments);
		
		me.on("expand",function(){
		
			me._bindData();
		});
    },
    _bindData:function(){
    	var me = this,data=[],
        groupData={},
        
        grid=me.grid,
        gridStore=grid.store;
        
        if(!me.grid) return;
     	if(!me.groupField) return;
        
        Ext.each(gridStore.getData().items,function(record){
        	//遍历所有行，相同行数据合并且计算数量
        	if(!groupData[record.get(me.groupField)]){
        		groupData[record.get(me.groupField)]={
        			text:record.get(me.groupField),
        			count:1,
        			value:record.get(me.groupField)
        		}
        	}
        	else{
        		groupData[record.get(me.groupField)].count+=1;
        	}
        
        });
        
        for(item in groupData){
        	data.push(groupData[item]);
        }
        groupData=[];
        
        me.store.oddData=data;
        me.store.loadData(data);
    }
});
/*
 * 产品选择器
 */

Ext.define('Belle_Common.ux.WorkShopField', {
			extend : 'Belle_Common.ux.SearchHelpField',
			alias : 'widget.workshopfield',
			constructor : function(config) {
				var me = this;

				if (Ext.isEmpty(config)) {
					config = {};
				}

				// 默认咧
				if (Ext.isEmpty(config.gridColumns)) {
					config.gridColumns = [{
	                    dataIndex: 'workshopNo',
	                    text: '车间编号',
	                    flex: 0.5
	                },
	                {
	                	dataIndex: 'workshopName',
	                	text: '车间名称',
	                	flex: 0.5
	                }];
				}

				// 默认查询参数
				if (Ext.isEmpty(config.searchItems)) {
					config.searchItems = [{
	                    name: 'workshopNo',
	                    fieldLabel: '车间编号'
	                },
	                {
	                    name: 'workshopName',
	                    fieldLabel: '车间名称'
	                }];
				}

				// 默认路径
				if (Ext.isEmpty(config.url)) {
					config.url = Belle.mdmPath + 'bas_workshop/list.json'
				}
				
				// 附加参数
				if (!Ext.isEmpty(config.otherGridColumns)) {
					config.gridColumns = config.gridColumns
							.concat(config.otherGridColumns);
				}
				// 附加参数
				if (!Ext.isEmpty(config.otherSearchItems)) {
					config.searchItems = config.searchItems
							.concat(config.otherSearchItems);
				}
				
				Ext.apply(me, config);
				me.callParent(arguments);
			},
			initComponent : function() {
				var me = this;
				me.callParent(arguments);
			},
			winTitle : "车间选择器"
		});


Ext.define('Belle_Common.ux.WorkshopSuppliersField', {
	extend : 'Belle_Common.ux.SearchHelpField',
	alias : 'widget.workshopsuppliersfield',
	constructor : function(config) {
		var me = this;

		if (Ext.isEmpty(config)) {
			config = {};
		}

		// 默认咧
		if (Ext.isEmpty(config.gridColumns)) {
			config.gridColumns = [{
                dataIndex: 'supplierNo',
                text: '供应商编号',
                flex: 0.5
            },
            {
                dataIndex: 'supplierName',
                text: '供应商名称',
                flex: 0.5
            }];
		}

		// 默认查询参数
		if (Ext.isEmpty(config.searchItems)) {
			config.searchItems = [{
                name: 'supplierNo',
                fieldLabel: '供应商编号'
            },
            {
                name: 'supplierName',
                fieldLabel: '供应商名称'
            }];
		}

		// 默认路径
		if (Ext.isEmpty(config.url)) {
			config.url = Belle.mdmPath + 'bas_supplier/list.json';
		}
		

		// 附加参数
		if (!Ext.isEmpty(config.otherGridColumns)) {
			config.gridColumns = config.gridColumns
					.concat(config.otherGridColumns);
		}
		// 附加参数
		if (!Ext.isEmpty(config.otherSearchItems)) {
			config.searchItems = config.searchItems
					.concat(config.otherSearchItems);
		}

		Ext.apply(me, config);
		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
	},
	winTitle : "供应商选择器"
});
/**
 * Description: 扩展网格列类,包装comobox控件 All rights Reserved, Designed By BeLLE
 * Copyright: Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月18日上午8:40:55
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月18日 yu.jh 2015年3月28日 liutao
 * bllookupedit一级联动修改 若需联动column需绑定一额外store 不能直接使用editor中的store
 * 若不绑定，则会出现下拉框值有部分数据无法正常展示
 */
Ext.define('Belle_Common.ux.extGridComoboxColumn', {
	extend: 'Ext.grid.column.Column',
	alias: 'widget.bllookupedit',
	estore: null, // Editor的store
	gstore: null, // 自身store
	readOnly: false, // 是否只读 在只读情况下不可以进行选择 没有必要创建下拉选择框
	editable: true,
	autoLoad: true,
	initComponent: function () {
		var me = this;
		me.type = "0"; // 默认为0 只有当未指定valuemember的时候才置为1
		if (me.readOnly) {
			// me.editor = false;
			if (me.gstore == null) {
				var sstore = null;
				if (me.valuemember == null) {
					me.valuemember = "num";
					me.displaymember = "name";
					var tt = tt || [];
					var s = me.displayvalue.split(":");
					for (var i = 0; i < s.length; i++) {
						var v = s[i].split("=");
						var obj = {};
						var s1 = v[0];
						var s2 = v[1];
						obj.num = s1;
						obj.name = s2;
						tt.push(obj);
					}
					sstore = Ext.create('Ext.data.Store', {
						fields: [me.valuemember,
							me.displaymember],
						data: tt
					});
					me.type = "1";
				} else {
					sstore = Ext.create('Belle_Common.store.Base', {
						fields: [me.valuemember,
							me.displaymember,
							me.autoLoad],
						proxy: {
							url: Belle.setUrlModuleInfo(me
									.up().grid,
								me.displayvalue)
						}
					});
				}
				sstore.reload();
				me.store = sstore;
			} else {
				me.store = me.gstore;
			}
		} else {

			var allowBlank = me.editor && me.editor.allowBlank;
			var multiSelect = (me.editor && me.editor.multiSelect) || me.multiSelect || false;
			if (me.valuemember)
				me.displayvalue = Belle.setUrlModuleInfo(me.up().grid,
					me.displayvalue);
			me.editor = Ext.create("Belle_Common.ux.ComboCustom", {
				displaymember: me.displaymember,
				valuemember: me.valuemember,
				displayvalue: me.displayvalue,
				store: me.estore,
				editable: this.editable,
				multiSelect: multiSelect
			});

			if (allowBlank === false) {
				me.editor.allowBlank = false;
			}


			// 是否存入字段名 字段名称
			if (me.valuemember == null) {
				me.valuemember = "num";
				me.displaymember = "name";
				me.type = "1";
			}
			if (me.gstore == null) {
				me.store = me.editor.store;
			} else {
				me.store = me.gstore;
			}
		}
		try {
			me.callParent();
		} catch (e) {
			Belle.alert(e);
		}
	},
	defaultRenderer: function (value) {
		var me = this, index = 0;
		if (value == null)
			return;
		if (value.toString().indexOf(',') == -1 && !value.join) {
			if (me.type == "0") {
				index = me.store.findExact(me.valuemember, value);
			} else {
				index = me.store
					.findExact(me.valuemember, value.toString());
			}
			return index > -1
				? me.store.getAt(index).data[me.displaymember]
				: value;
		} else {
			var val = value.join ? value : value.split(','), txt = [];
			for (var i = 0; i < val.length; i++) {
				index = me.store.findExact(me.valuemember, val[i]);
				txt.push(index > -1 ? me.store.getAt(index).data[me.displaymember] : val[i]);
			}
			return txt.join(',');
		}
	}
});

/**
 * Description: 日期网格扩展控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月30日下午3:48:10
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月30日 yu.jh
 */
Ext.define('Belle_Common.ux.extGridDateColumn', {
			extend : 'Ext.grid.column.Date',
			alias : 'widget.blgriddate',
			format : "Y-m-d",
			readOnly : false,
			editable : false,
			initComponent : function() {
				if (!this.readOnly) {
					var allowBlank = this.editor && this.editor.allowBlank;
					this.editor = Ext.create("Belle_Common.ux.DateTimeField", {
								format : this.format,
								readOnly : this.readOnly,
								value : this.value,
								contype : "date",
								editable : this.editable
							});
					if (allowBlank === false) {
						this.editor.allowBlank = false;
					}

					if (this.minValue) {
						this.editor.minValue = this.minValue;
					}
					if (this.maxValue) {
						this.editor.maxValue = this.maxValue;
					}
				}
				this.callParent();
			}
		});
/**
 * Description: 日期时间网格扩展控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月30日下午2:53:29
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月30日 yu.jh 用法： {dataIndex:
 * 'modifyTime', text: '修改时间',xtype: 'blgriddatetime'}
 * 默认可编辑，若只读，请设置readOnly:true
 */
Ext.define('Belle_Common.ux.extGridDateTimeColumn', {
			extend : 'Ext.grid.column.Date',
			alias : 'widget.blgriddatetime',
			format : "Y-m-d H:i:s",
			readOnly : false,
			initComponent : function() {
				if (!this.readOnly) {
					var allowBlank = this.editor && this.editor.allowBlank;
					this.editor = Ext.create("Belle_Common.ux.DateTimeField", {
								format : this.format,
								readOnly : this.readOnly,
								value : this.value,
								contype : "datetime"
							});
					if (allowBlank === false) {
						this.editor.allowBlank = false;
					}
				}
				this.callParent();
			}
		});
/**
 * Description:启用状态网格列控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月18日上午8:52:49
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月18日 yu.jh
 */
Ext.define('Belle_Common.ux.gridComboUseFlag', {
			extend : 'Belle_Common.ux.extGridComoboxColumn',

			alias : 'widget.gridcombouseflag',
			displayvalue : "1=启用:0=禁用",
			editable : false,

			initComponent : function() {

				this.callParent();
			}
		});
/**
 * Description:是否选择网格列控件 All rights Reserved, Designed By BeLLE Copyright:
 * Copyright(C) 2014-2015 Company: Wonhigh. author: yu.jh Createdate:
 * 2015年3月18日上午8:51:07
 * 
 * 
 * Modification History: Date Author What
 * ------------------------------------------ 2015年3月18日 yu.jh
 */
Ext.define('Belle_Common.ux.gridComboYesNo', {
			extend : 'Belle_Common.ux.extGridComoboxColumn',

			alias : 'widget.gridcomboyesno',
			displayvalue : "1=是:0=否",
			editable : false,

			initComponent : function() {

				this.callParent();
			}
		});
/*
 * 日志查看
 */

Ext.define('Belle_Common.ux.Log', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.log',
	constructor : function(config) {
		var me = this;

		if (!config.masterGrid)
			return;

		var gridColumns = Belle.clone(config.masterGrid.vcolumn), gridFields = config.masterGrid.store.model
				.getFields(), logFields = ["logTime", "logUser", "logAttribute"], logColumns = [
				{
					header : "操作类型",
					dataIndex : "logAttribute",
					renderer : function(val) {
						var text = '';
						switch (val) {
							case "a" :
								text = "新增";
								break;
							case "u" :
								text = "更新";
								break;
							case "d" :
								text = "删除";
								break;
						}
						return text;
					}
				}, {
					header : "记录人",
					dataIndex : "logUser"
				}, {
					header : "记录时间",
					dataIndex : "logTime"
				},];

		Ext.each(gridColumns, function(item) {
					if (item.editor) {
						delete item.editor;
					}

					if (item.renderer === "renderUseFlag") {
						item.renderer = me.renderUseFlag;
					}

					if (item.renderer === "renderYesNo") {
						item.renderer = me.renderYesNo;
					}

				});

		gridColumns = gridColumns.concat(logColumns);
		gridFields = gridFields.concat(logFields);

		config.gridColumns = gridColumns;

		config.gridFields = gridFields;

		config.pageSize = config.parentView.pageSize;

		var url = config.parentView.gridLoadUrl, parent;

		if (url.indexOf('?') >= 0) {
			parent = "&moduleEntity=";
		} else {
			parent = "?moduleEntity=";
		}

		config.gridLoadUrl = url + parent + config.parentView.gridModelText;
		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.columnWidth = 1;
		me.layout = "border";

		var toolbar = me.createToolbar(), form = me.createSearchForm(), grid = me
				.createGrid()

		if (!me.items) {
			me.items = [];
		}

		if (toolbar) {
			me.items.push(toolbar);
		}
		if (form && form.items && form.items.length > 0) {
			me.items.push(form);
		}
		if (grid) {
			me.items.push(grid);
		}

		me.callParent(arguments);
	},
	initEvents : function() {
		var me = this;
		me.toolbar.down("button[itemId='closeLog']").on("click", function(btn) {
					me.up().close();
				});

		me.toolbar.down("button[itemId='btnLogSearch']").on("click",
				function(btn) {
					me.onSearch(btn);
				});
	},
	toolbarView : {
		xtype : 'toolbar',
		reference : 'logToolbar',
		region : 'north',
		items : [{
					text : '查询',
					itemId : 'btnLogSearch',
					reference : 'btnLogSearch',
					glyph : Belle.Icon.btnSearch

				}, {
					text : "返回",
					itemId : "closeLog",
					glyph : Belle.Icon.btnUndo
				}]
	},
	gridView : {
		xtype : 'grid',
		reference : 'logGrid',
		region : 'center',
		columnLines : true,
		columns : [],
		bbar : {
			xtype : 'pagingtoolbar',
			displayInfo : true
		},
		viewConfig : {
			enableTextSelection : true
		},
		selModel : {},
		features : []
	},
	searchFormView : {
		columns : 4,
		xtype : 'form',
		region : 'north',
		reference : 'searchLog',
		collapsible : true,
		title : '查询面板',

		layout : {
			type : 'table'
		},
		header : {
			height : 20,
			padding : 0
		},
		defaults : {
			labelAlign : 'right',
			width : '100%',
			labelWidth : 80,
			style : "margion-top:5px; margion-right:5px;:"
		},
		defaultType : 'textfield',
		bodyPadding : 3,
		autoScroll : true,
		items : []
	},
	createToolbar : function() {
		var me = this;
		if (!me.toolbar) {
			me.toolbar = Ext.create('Ext.toolbar.Toolbar', me.toolbarView);
		}

		return me.toolbar;
	},
	createSearchForm : function() {
		var me = this,
		sitems = me.parentView.searchItems;
		if (!me.searchForm && sitems) {

			me.searchFormView.layout.columns = me.parentView.searchColumn;
			if (sitems.length > 3) {
				me.searchFormView.layout.type = 'table';
			} else if (sitems.length > 0) {
				me.searchFormView.layout.type = 'column';
				Ext.each(sitems, function(item) {
							item.columnWidth = item.columnWidth || "0.25";
						})
			}

			me.searchFormView.items = sitems;
			me.searchForm = Ext.create('Ext.form.Panel', me.searchFormView);
		}
		return me.searchForm;
	},
	createGrid : function() {
		var me = this;
		if (!me.grid) {

			var store = Ext.create('Ext.data.Store', {
						autoLoad : true,
						fields : me.gridFields,
						pageSize : me.pageSize,
						proxy : {
							url : me.gridLoadUrl,
							type : 'ajax',
							reader : {
								type : 'json',
								rootProperty : 'list',
								totalProperty : 'totalCount'
							},
							pageParam : 'pageNum',
							limitParam : 'pageSize',
							startParam : ''
						}
					});
			me.gridView.store = store;
			me.gridView.columns = me.gridColumns;
			me.gridView.bbar.store = store;
			me.gridView.bbar.plugins = Ext.create('Ext.ux.ComboPageSize', {
						defaultSize : me.pageSize
					});

			me.grid = Ext.create('Ext.grid.Panel', me.gridView);
		}
		return me.grid;
	},
	onSearch : function(btn) {
		var me = this, grid = me.grid, form = me.searchForm, store = grid
				.getStore(), extraParams = store.getProxy().extraParams || {}, values = form
				.getValues();

		btn.setDisabled(true);

		for (var v in values) {
			var val = values[v];

			if (!Ext.isEmpty(val)) {
				extraParams[v] = values[v];
			}else{
				delete extraParams[v];
			}
		}

		store.getProxy().extraParams = extraParams;

		store.loadPage(1, {
					callback : function(records, operation, success) {
						btn.setDisabled(false);
					}
				});
	}
});
