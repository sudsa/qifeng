var VssAgent={};
var emailReg=/(\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*)/;
var downUrlReg=/(https:\/\/download.csdn.net\/download\/\w+\/\w+)/;
var downUrl2Reg=/(https:\/\/download.csdn.net\/index.php\/mobile\/source\/download\/\w+)/;
VssAgent.down=function() {
    $("#file").attr("href","")
    $("#file").attr("download","")
    var downUrl=$("#downUrl").val();
    var loading = layer.load();
    $.ajax({ url: "/api/down",data: {"downUrl":downUrl,"email":""}, success: function(response){
            layer.close(loading);
            if(response.status==1){
                Feng.success("下载成功！"+response.msg);
                var path=response.result;
                // download(path,path.substr(path.lastIndexOf("/")+1));
                var a = $('<a></a>');
                a.attr('href',  path);
                a.prop('download',path.substr(path.lastIndexOf("/")+1));
                a.get(0).click();
            }else{
                Feng.error(response.msg);
            }
        }});
}

VssAgent.email=function() {
    var downUrl=$("#downUrl").val();
    var email=$("#email").val();
    if(email=="" || typeof email =='undefined'){
        Feng.error("邮箱不能为空！");
        return;
    }
    var loading = layer.load();
    $.ajax({ url: "/api/down",data: {"downUrl":downUrl,"email":email}, success: function(response){
            layer.close(loading);
            if(response.status==1){
                Feng.success("邮件发送成功！"+response.msg);
            }else{
                Feng.error(response.msg);
            }
        }});
}

VssAgent.clear=function() {
    $("#downUrl").val("");
    $("#email").val("");
    $("#content").val("");
}

VssAgent.analyze=function() {

    var content=$("#content").val();
    if(content!="" && typeof content !='undefined'){
        var emailStr=content.match(emailReg);
        if(emailStr!=null && emailStr.length>0){
            $("#email").val(emailStr[1]);
        }
        var downUrlStr=content.match(downUrlReg);
        if(downUrlStr!=null && downUrlStr.length>0){
            $("#downUrl").val(downUrlStr[1]);
        }

        if( $("#downUrl").val() =="" || typeof $("#downUrl").val() =='undefined'){
            $("#downUrl").val(content.match(downUrl2Reg)[1]);
        }
    }else{
        Feng.error("聊天文本不能为空！");
    }
}


