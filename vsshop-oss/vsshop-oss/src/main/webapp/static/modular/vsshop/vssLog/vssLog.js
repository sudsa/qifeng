/**
 * 下载日志管理初始化
 */
var VssLog = {
    id: "VssLogTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssLog.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '激活码', field: 'code', visible: true, align: 'center', valign: 'middle',width:'250px'},
            {title: '是否复用', field: 'reuse', visible: true, align: 'center', valign: 'middle',width:'80px'},
            {title: '需要积分', field: 'coin', visible: true, align: 'center', valign: 'middle',width:'80px'},
            {title: '下载时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle',width:'150px'},
            {title: '区域', field: 'region', visible: true, align: 'center', valign: 'middle',width:'80px'},
            {title: '拥有者', field: 'owner', visible: true, align: 'center', valign: 'middle',width:'150px'},
            {title: '下载地址', field: 'downUrl', visible: true, align: 'center', valign: 'middle',width:'500px',cellStyle:formatTableUnit, formatter:paramsMatter},
            {title: '文件地址', field: 'filePath', visible: true, align: 'center', valign: 'middle',width:'300px',cellStyle:formatTableUnit, formatter:paramsMatter},
            {title: '网站用户', field: 'vssuser', visible: true, align: 'center', valign: 'middle',width:'80px'},
            {title: '网站', field: 'source', visible: true, align: 'center', valign: 'middle',width:'80px'},
            {title: 'ip', field: 'ip', visible: true, align: 'center', valign: 'middle',width:'120px',cellStyle:formatTableUnit, formatter:paramsMatter},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssLog.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssLog.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加下载日志
 */
VssLog.openAddVssLog = function () {
    var index = layer.open({
        type: 2,
        title: '添加下载日志',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssLog/vssLog_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看下载日志详情
 */
VssLog.openVssLogDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '下载日志详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssLog/vssLog_update/' + VssLog.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除下载日志
 */
VssLog.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssLog/delete", function (data) {
            Feng.success("删除成功!");
            VssLog.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssLogId",this.seItem.uuid);
        ajax.start();
    }
};
/**
 * 导出记录
 */
VssLog.export = function () {
    var queryData = {};
    queryData['code'] = $("#code").val();
    queryData['reuse'] = $("#reuse").val();
    window.location.href="vssLog/export"+_urlEncode(queryData).replace("&","?");

};
/**
 * param 将要转为URL参数字符串的对象
 * key URL参数字符串的前缀
 * encode true/false 是否进行URL编码,默认为true
 *
 * return URL参数字符串
 */
function _urlEncode(param, key, encode) {
    if(param==null) return '';
    var paramStr = '';
    var t = typeof (param);
    if (t == 'string' || t == 'number' || t == 'boolean') {
        paramStr += '&' + key + '=' + ((encode==null||encode) ? encodeURIComponent(param) : param);
    } else {
        for (var i in param) {
            var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i);
            paramStr += _urlEncode(param[i], k, encode);
        }
    }
    return paramStr;
};
/**
 * 查询下载日志列表
 */
VssLog.search = function () {
    var queryData = {};
    queryData['code'] = $("#code").val();
    queryData['reuse'] = $("#reuse").val();
    VssLog.table.refresh({query: queryData});
};
function paramsMatter(value,row,index) {
    var span=document.createElement('span');
    span.setAttribute('title',value);
    span.innerHTML = value;
    return span.outerHTML;
}

function formatTableUnit(value, row, index) {
    return {
        css: {
            "white-space": 'nowrap',
            "text-overflow": 'ellipsis',
            "overflow": 'hidden'
        }
    }
}
$(function () {
    var defaultColunms = VssLog.initColumn();
    var table = new BSTable(VssLog.id, "/vssLog/list", defaultColunms);
    table.setPaginationType("server");
    VssLog.table = table.init();
});
