/**
 * 登录
 */

var errors = {
			MOBILE_ERROR:'请正确输入11位手机号码',
			EMAIL_ERROR:'请正确输入电子邮件地址',
			PASSWORD_ERROR:'密码为6-20位数字、字母、字符',
			NULL:null,
			USER_NOT_EXIST:'手机号、管理员账号或密码错误',
	}
$(function($){
	$('#loginName').focus();
});


//手机号规则验证
function isMobile(mobile){
	var reg = /^1[3|4|5|7|8]\d{9}$/;
	return reg.test(mobile);
}

//邮件规则验证
function isEmail(email){
	var reg = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    return email != '' && reg.test(email);
}

//mobile blur validation
$("#loginAdminName").blur(function(){
	var email = $("#loginAdminName").val();
	if(email != null && email != ""){
		if(isEmail(email)){
			$("#loginadminnametips").html(errors.NULL);
		}else{
			$("#loginadminnametips").html(errors.EMAIL_ERROR);
		}
	}
})

//mobile blur validation
$("#loginUserName").blur(function(){
	var mobile = $("#loginUserName").val();
	if(mobile != null && mobile != ""){
		if(isMobile(mobile)){
			$("#loginusernametips").html(errors.NULL);
		}else{
			$("#loginusernametips").html(errors.MOBILE_ERROR);
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
	var mobile = $('#loginUserName').val();
	if(mobile == null  || mobile == ""){
		removeAllError();
		$("#loginusernametips").html(errors.MOBILE_ERROR);
		return false;
	}else{
		if(!isMobile(mobile)){
			removeAllError();
			$("#loginusernametips").html(errors.MOBILE_ERROR);
			return false;
		}else{
			$("#loginusernametips").html(errors.NULL);
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
	var parentMobile = $('#loginUserName').val();
	var password = $('#loginPassword').val();
	var adminUserName = $('#loginAdminName').val();
	var autoLogin;
	
	if(checkLoginInfo()){
		$.get( "/checkUserExist",{"username":parentMobile}, function( data ) {
			if(data){
				$.post( "/sudo_login", {"userName":parentMobile,"adminName":adminUserName, "password":password}, function( data ) {
					if(data == "success_guide"){
						window.location.href = '/welcome';
					}else if(data=="success_parentHome"){
						window.location.href = '/parent/home';
					}else if(data=="success_openclass"){
						window.location.href = '/parent/openclass';
					}else if(data == "userNamePasswordError"){
						$("#loginusernametips").html(errors.USER_NOT_EXIST);
						$("#loginButton").attr('disabled',false);
						$("#loginButton").text("登录");
					}else if(data == "login"){
						window.location.href = '/login';
					}
				})
			}else{
				console.log("login fail");
				$("#loginusernametips").html(errors.USER_NOT_EXIST);
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
