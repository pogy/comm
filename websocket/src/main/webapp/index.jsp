<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Testing websockets</title>
<script src="jquery-2.1.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
var socket =null;
$(function(){
	
    function parseObj(strData){//转换对象
        return (new Function( "return " + strData ))();
    };
    
    //创建socket对象
    socket = new WebSocket("ws://"+ window.location.host+"/${pageContext.request.contextPath}/game");
    //连接创建后调用
    socket.onopen = function() {
        $("#showMsg").append("连接成功...<br/>");
    };
    //接收到服务器的消息后调用
    socket.onmessage = function(message) {
        var data = parseObj(message.data);
        if(data.type=="message"){
            $("#showMsg").append("<span style='display:block'>"+data.text+"</span>");
        }else if(data.type=="background"){
            $("#showMsg").append("<span style='display:block'>系统改变背景地址,背景地址是:"+data.text+"</span>");
            $("body").css("background","url("+data.text+")");
        }
    };
    //关闭连接的时候调用
    socket.onclose = function(){
        alert("close");
    };
    //出错时调用
    socket.onerror = function() {
        alert("error");
    };
    //发送数据
    $("#sendButton").click(function() {
        socket.send($("#msg").val());
        $("#msg").val("");
        return false;
    });
});
</script>
</head>
<style>
	.message{border: 1px solid; width: 500px; height: 400px; overflow: auto; margin-bottom:20px;padding:10px;}
</style>
<body>
    <div id="showMsg" class="message"></div>
    <div>
        <input type="text" id="msg"/> 
        <input type="button" id="sendButton" value="发送" />
    </div>
</body>
</html>
