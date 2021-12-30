/**
 * 激活码管理初始化
 */
var VssCode = {
    id: "VssCodeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssCode.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},

            {title: '激活码', field: 'code', visible: true, align: 'center', valign: 'middle',width:'250px'},
            {title: '积分', field: 'coin', visible: true, align: 'center', valign: 'middle',width:'50px'},
            // 1:未使用，2已使用，3作废
            {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
                    if (value == '1') {
                        return "未使用";
                    } else if(value == '2'){
                        return "已使用";
                    }else if(value == '3'){
                        return "作废";
                    }
                }
            },
            {title: '创建者', field: 'owner', visible: true, align: 'center', valign: 'middle'},
            {title: '下载地址', field: 'downUrl', visible: true, align: 'center', valign: 'middle',width:'450px',cellStyle:formatTableUnit, formatter:paramsMatter},
            {title: '文件地址', field: 'filePath', visible: true, align: 'center', valign: 'middle',width:'300px',cellStyle:formatTableUnit, formatter:paramsMatter},
            {title: '文件名称', field: 'name', visible: true, align: 'center', valign: 'middle',width:'300px',cellStyle:formatTableUnit, formatter:paramsMatte},
            {title: '文件大小', field: 'fileSize', visible: true, align: 'center', valign: 'middle',width:'80px'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle',width:'150px'},
            {title: '使用时间', field: 'usedTime', visible: true, align: 'center', valign: 'middle',width:'150px'},

};

/**
 * 检查是否选中
 */
VssCode.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssCode.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加激活码
 */
VssCode.openAddVssCode = function () {
    var index = layer.open({
        type: 2,
        title: '添加激活码',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssCode/vssCode_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看激活码详情
 */
VssCode.openVssCodeDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '激活码详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssCode/vssCode_update/' + VssCode.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除激活码
 */
VssCode.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssCode/delete", function (data) {
            Feng.success("删除成功!");
            VssCode.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssCodeId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询激活码列表
 */
VssCode.search = function () {
    var queryData = {};
    queryData['status'] = $("#status").val();
    queryData['code'] = $("#code").val();
    VssCode.table.refresh({query: queryData});
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
    var defaultColunms = VssCode.initColumn();
    var table = new BSTable(VssCode.id, "/vssCode/list", defaultColunms);
    table.setPaginationType("server");
    VssCode.table = table.init();
});
