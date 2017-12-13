Ext.define('MyApp.store.BaseStore', {
    extend: 'Ext.data.Store',
    alias : 'store.basestore',

	buffered : false,
	autoLoad : false,
	remoteSort : true,
	remoteFilter : true,
	module : null,

	proxy : {
		type : 'ajax',
		reader : {
			type : 'json',
			rootProperty : 'list',
			totalProperty : 'totalCount'
		},
		writer : {
			type : 'json',
			writeAllFields : true,
			rootProperty : 'items'
		},
		actionMethods : {
			create : "POST",
			read : "POST",
			update : "POST",
			destroy : "POST"
		},
		pageParam : 'pageNum',
		limitParam : 'pageSize',
		startParam : '',
		timeout:180000

	},
	listeners : {
		beforeload : function(store) {
			var sort = store.getSorters();
			if (sort && sort.items.length > 0) {
				var sortParam = [];
				Ext.Array.each(sort.items, function(item) {
							sortParam.push(item.getProperty() + ' '
									+ item.getDirection().toLowerCase());
						});
				store.proxy.extraParams.sort = sortParam.join(',');
			} else {
				delete store.proxy.extraParams.sort
			}
			if (store.module && Ext.isObject(store.module)) {
				store.proxy.url = Belle.setUrlModuleInfo(store.module,
						store.proxy.url);
			}
		},
		load : function(store, records, state, opts) {
			if (opts.success) {
				var resp = opts.getResponse();
				if (!resp) {
					Belle.alert('无法访问服务器接口或接口处理超时,请与系统管理员联系');
					return;
				}
				var result = resp.responseText;
				try {
					result = JSON.parse(result);
					if (result.result) {
						if (result.result.resultCode != 0) {
							var msg = result.result.msg;
							if (result.result.resultCode != 'timeout') {
								msg += ' <a onclick="Belle.alert(\''+Belle.strEscape(result.result.retData || resp.responseText)+'\');"  href="javascript:void(0)"> 查看详情</a>';
							}
							Belle.alert(msg, function () {
								if (result.result.resultCode == 'timeout') {
									var v2W = Belle.v2Win(); //处理2.0框架超时返回登陆页面
									if(v2W){
										v2W.location.href = '/bl-uc-web/logout.json';
									}else {
										location.href = Belle.basePath	+ 'logout.json';
									}
								}
							});
						}
					}
				} catch (e) {
					Belle.alert('服务器返回不是有效的JSON数据 <a onclick="Belle.alert(\'' + Belle.strEscape(resp.responseText) + '\');" href="javascript:void(0)"> 查看详情</a>');
				}
			} else {
				try {
					var d = JSON.parse(opts.error.response.responseText);
					var msg = d.result.msg;
					msg += ' <a onclick="Belle.alert(\'' + Belle.strEscape(d.result.retData || opts.error.response.responseText)+ '\');" href="javascript:void(0)"> 查看详情</a>';

					Belle.alert(msg);
				} catch (e) {
					console.info('本次请求异常!',e);
				}
			}
		}
	}
});