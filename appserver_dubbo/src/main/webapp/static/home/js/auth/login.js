/**
 * 登录
 */

var errors = {
			MOBILE_ERROR:'请正确输入11位手机号码',
			PASSWORD_ERROR:'密码为6-20位数字、字母、字符',
			NULL:null,
			USER_NOT_EXIST:'手机号或密码错误',
	}
$(function($){
	$('#loginName').focus();
});


//手机号规则验证
function isMobile(mobile){
	var reg = /^1[3|4|5|7|8]\d{9}$/;
	return reg.test(mobile);
}

//mobile blur validation
$("#loginName").blur(function(){
	var mobile = $("#loginName").val();
	if(mobile != null && mobile != ""){
		if(isMobile(mobile)){
			$("#loginnametips").html(errors.NULL);
		}else{
			$("#loginnametips").html(errors.MOBILE_ERROR);
		}
	}
})

//password blur validation
$("#loginPassword").blur(function(){
	var password = $("#loginPassword").val();
	if(password != null && password != ""){
		if(password.length < 6){
			$("#loginpasswordtips").html(errors.PASSWORD_ERROR);
		}else{
			$("#loginpasswordtips").html(errors.NULL);
		}
	}
})
	
//check loginName
function checkLoginInfo(){
	var mobile = $('#loginName').val();
	if(mobile == null  || mobile == ""){
		removeAllError();
		$("#loginnametips").html(errors.MOBILE_ERROR);
		return false;
	}else{
		if(!isMobile(mobile)){
			removeAllError();
			$("#loginnametips").html(errors.MOBILE_ERROR);
			return false;
		}else{
			$("#loginnametips").html(errors.NULL);
		}
	}
	
	var pwd = $('#loginPassword').val();
	if(pwd == null  || pwd == ""){
		removeAllError();
		$("#loginpasswordtips").html(errors.PASSWORD_ERROR);
		return false;
	}else{
		if(pwd.length < 6){
			removeAllError();
			$("#loginpasswordtips").html(errors.PASSWORD_ERROR);
			return false;
		}else{
			$("#loginpasswordtips").html(errors.NULL);
		}
	}
	
	return true;
}

//login
function login(){
	//disable
	$("#loginButton").attr('disabled',true);
	$("#loginButton").text("登录中…");
	var mobile = $('#loginName').val();
	var password = $('#loginPassword').val();
	var autoLogin;
	
	if(checkLoginInfo()){
		$.get( "/checkUserExist",{"username":mobile}, function( data ) {
			if(data){
				if($('#autoLogin').is(':checked')){
					autoLogin = "true";
				}else{
					autoLogin = "false";
				}
				$.post( "/login", {"username":mobile,"password":password,"autoLogin":autoLogin}, function( data ) {
					if(data == "success_guide"){
						window.location.href = '/welcome';
					}else if(data=="success_parentHome"){
						window.location.href = '/parent/home';
					}else if(data=="success_openclass"){
						window.location.href = '/parent/openclass';
					}else if(data == "userNamePasswordError"){
						$("#loginnametips").html(errors.USER_NOT_EXIST);
						$("#loginButton").attr('disabled',false);
						$("#loginButton").text("登录");
					}else if(data == "login"){
						window.location.href = '/login';
					}
				})
			}else{
				console.log("login fail");
				$("#loginnametips").html(errors.USER_NOT_EXIST);
				$("#loginButton").attr('disabled',false);
				$("#loginButton").text("登录");
			}
		})
	}else{
		$("#loginButton").text("登录");
		$("#loginButton").attr('disabled',false);
	}	
}

function removeAllError(){
	$("#loginnametips").html(errors.NULL);
	$("#loginpasswordtips").html(errors.NULL);
}

//最后一个输入框回车进行登录
$("#loginPassword").keypress(function(event){
	if(event.keyCode == 13){
		login();
	}
})

$("a").click(function() {
	var url = window.location.href;
	if (url.indexOf("channel_id=") > 0||url.indexOf("channel_keyword=") > 0) {
		var input = url.substr(url.indexOf('?'));
		this.href = this.href + input;
	}
})

$(".loginInput").focus(function(){
	$(this).parent(".loginPar").addClass("on")
})
	
$(".loginInput").blur(function(){
	$(this).parent(".loginPar").removeClass("on")
})


$(".reginput").focus(function(){
	$(this).addClass("on")
})
$(".reginput").blur(function(){
	$(this).removeClass("on")
})
