/**
 * 信息提示
 * 1. 普通信息提示
 * 2. 确认信息提示
 * 3. 错误提示
 * 4. 关闭
 */
var Alert = new Object();

//普通信息提示
Alert.info = function(title, message, okLabel, callback){
	if(title != "" && title != null){
		$("#info_title").html(title);
	}else if(title != "NO" && title != null){
		$("#info_title").html("");
	}else{
		$("#info_title").html("提示");
	}
	
	if(okLabel != "" && okLabel != null){
		$("#info_ok").html(okLabel);
	}else{
		$("#info_ok").html("确认");
	}
	
	$("#info_message").html(message);
	$("#alert_info").show().addClass("tipsPopOpen")
	
	$("#info_ok").unbind("click");
	$("#info_ok").bind("click", function(){
		Alert.close();
		if(callback != null){
			callback();
		}
	})
}

//确认信息提示
Alert.confirm = function(title, message, okLabel, cancelLabel, okCallback, cancelCallback){
	if(title != "" && title != null){
		$("#confirm_title").html(title);
	}else{
		$("#confirm_title").html("提示");
	}
	
	if(okLabel != "" && okLabel != null){
		$("#confirm_ok").html(okLabel);
	}else{
		$("#confirm_ok").html("是");
	}
	
	if(cancelLabel != "" && cancelLabel != null){
		$("#confirm_cancel").html(cancelLabel);
	}else{
		$("#confirm_cancel").html("否");
	}
	
	$("#confirm_message").html(message);
	$("#alert_confirm").show().addClass("tipsPopOpen");
	
	$("#confirm_ok").unbind("click");
	$("#confirm_ok").click(function(){
		Alert.close();
		if(okCallback != null){
			okCallback();
		}
	})
	
	$("#confirm_cancel").unbind("click");
	$("#confirm_cancel").click(function(){
		Alert.close();
		if(cancelCallback != null){
			cancelCallback();
		}
	})
}

//错误提示
Alert.error = function(title, message, okLabel, callback){
	if(title != "" && title != null){
		$("#error_title").html(title);
	}else{
		$("#error_title").html("提示");
	}
	
	if(okLabel != "" && okLabel != null){
		$("#error_ok").html(okLabel);
	}else{
		$("#error_ok").html("确认");
	}
	
	$("#error_message").html(message);
	$("#alert_error").show().addClass("tipsPopOpen");
	
	$("#error_ok").unbind("click");
	$("#error_ok").click(function(){
		Alert.close();
		if(callback != null){
			callback();
		}
	})
}


//关闭(关闭以上三种所有弹出框)
Alert.close = function(){
	$("#alert_info").removeClass("tipsPopOpen").hide()
	$("#alert_confirm").removeClass("tipsPopOpen").hide()
	$("#alert_error").removeClass("tipsPopOpen").hide()
}


