/**
 * Description: 统一引用 css,js 资源文件 All rights Reserved, Designed By BeLLE
 * Copyright: Copyright(C) 2014-2015 Company: Wonhigh. author: wudefeng
 * Createdate: 2015/1/22 0022
 * 
 * Modification History: Date Author What
 * ------------------------------------------
 * 
 */


//定义公共属性
var Belle = Belle || {};

var mt = mt || {};

/*
 * alert 提示框 msg 提示内容 [fn] 点击按钮时执行的函数 [scope] 作用域
 */
mt.alert = function(msg, fn, scope) {
	var title = '系统提示',
		win =Ext.Msg.alert(title, msg, fn, scope);
	//setTimeout(function(){win.focus()},1);
	return win;
};

/*
 * confirm 确认框 msg 提示内容 [fn] 点击按钮时执行的函数 [scope] 作用域
 */
mt.confirm = function(msg, fn, scope) {
	var title = '系统提示';
	return Ext.Msg.confirm(title, msg, fn, scope);
};
/** 弹出框 */
mt.show = function(options) {
	return Ext.Msg.show(options);
};
/** 深度复制对象 */
mt.clone = function(obj) {
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

// region 前置公共方法，全局调用

//处理getComputedStyle 在firefox浏览器下 iframe 的display:none 时返回 null 值问题
if (/firefox/i.test(navigator.userAgent)){
	window.oldGetComputedStyle = window .getComputedStyle;
	window.getComputedStyle = function (element, pseudoElt) {
		var t = window.oldGetComputedStyle(element, pseudoElt);
		if (t === null) {
			return {};
		}
		return t;
	};
}

/**
 * 获取url中的get参数值
 * */
Belle.getUrlParam=function(key) {
	var reg = new RegExp("(^|&)" + key + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) {
		return unescape(r[2]);
	}
	return null;
};

/**
 * 设置url参数
 */
Belle.setUrlParam = function(url,name, value) {
	var newUrl = "";
	var reg = new RegExp("(^|)" + name + "=([^&]*)(|$)");
	var tmp = name + "=" + value;
	if (url.match(reg) != null) {
		newUrl = url.replace(eval(reg), tmp);
	}
	else {
		if (url.match("[\?]")) {
			newUrl = url + "&" + tmp;
		} else {
			newUrl = url + "?" + tmp;
		}
	}
	return newUrl;
};


/**
 * 嵌入其它框架中打开时模块widget，需要通过 ?mno=模块widget方式传参过来
 * */
Belle.moduleWidget=function() {
	return Belle.getUrlParam('mno');
};



/**
 * v2.0的主框架Window
 * */
Belle.v2Win = function() {
	if (Belle.moduleWidget() && window.parent && window.parent.$) return window.parent;
	return null;
};

// endregion


/* Extjs主题 */
Belle.theme = 'classic';

/* js、css版本 */
Belle.version = 'v2.01';

/* 子系统路径初始化,会在mainController中更改（读取DB中的配置） */
mt.mcPath='/host-mt/';

/* 字体图标 */
Belle.Icon = {
	btnUser : 0xf007,
	btnSetting : 0xf013,
	btnHelp : 0xf059,
	btnExit : 0xf011,
	btnMsg : 0xf0e0,
	btnSearch : 0xf002,
	btnReset : 0xf079,
	btnRefresh : 0xf021,
	btnAdd : 0xf15c,
	btnCopy : 0xf0c5,
	btnEdit : 0xf044,
	btnDelete : 0xf014,
	btnSave : 0xf0c7,
	btnUndo : 0xf0e2,
	btnCancel : 0xf112,
	btnImport : 0xf022,
	btnExport : 0xf022,
	btnPrint : 0xf02f,
	btnLock : 0xf023,
	btnUnLock : 0xf09c,
	btnHome : 0xf015,
	btnOther : 0xf080,
	btnMoveUp : 0xf062,
	btnMoveDown : 0xf063,
	btnMoveLeft : 0xf060,
	btnMoveRight : 0xf061,
	btnPrev : 0xf048,
	btnNext : 0xf051,
	btnAudit : 0xf14a,
	btnAddressList : 0xf02d,
	btnGroup : 0xf0c0
};

/*
 * 加载Js、Css函数 url 路径 isVersion 是否需要加版本号 hasDebug 是否存在Debug 文件 *
 */
Belle.loadJsCss = function(url, isVersion, hasDebug) {
	var isLocal = location.href.indexOf('localhost') > -1
			|| location.href.indexOf('127.0.0.1') >= -1, isJs = url
			.substr(url.length - 3) == ".js";

	if (hasDebug && isLocal && isJs) {
		url = url.substr(0, url.length - 3) + '-debug.js';
	}

	if (isVersion) {
		url = Belle.setUrlParam(url, "v", Belle.version);
	}

	if (isJs) {
		document.write('<script src="' + url + '" type="text/javascript"></'
				+ 'script>');
	} else {
		document.write('<link type="text/css" rel="stylesheet" href="' + url
				+ '"/>');
	}
};

(function() {
	var preRoot = '';
	if (Belle.v2Win()) {
		preRoot = '/v1/';
	}
	Belle.loadJsCss(preRoot + 'static/js/extjs/packages/ext-theme-'
	+ Belle.theme + '/ext-theme-' + Belle.theme + '-all.css');
	Belle.loadJsCss(preRoot + 'static/css/font-awesome.css');
	Belle.loadJsCss(preRoot + 'static/css/font-awesome.min.css');
	Belle.loadJsCss(preRoot + 'static/css/belle-all.css', true);
	Belle.loadJsCss(preRoot + 'static/js/extjs/ext-all.js', false);
	Belle.loadJsCss(preRoot + 'static/js/extjs/ext-ux-all.js', true);
	Belle.loadJsCss(preRoot + 'static/js/belle/belle-ux-all.js', true);
	Belle.loadJsCss(preRoot + 'static/js/extjs/packages/ext-locale/ext-locale-zh_CN.js');
	Belle.loadJsCss(preRoot + 'static/js/security.js');
})();

