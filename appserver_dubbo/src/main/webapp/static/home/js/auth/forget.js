/*忘记密码*/
var errors = {
			MOBILE_ERROR:'请正确输入11位手机号码',
			PASSWORD_ERROR:'密码应为6-20位数字、字母、字符',
			CONFIRM_PASSWORD_ERROR:"两次输入的密码不一致",
			MOBILE_NOT_SIGNUP:"此手机号码未注册",
			VERIFYCODE_NOT_RIGHT:"输入的验证码不正确，请重新获取",
			NULL:null,
	}
var verifyCode = 0;

//第一步各种验证
//移除注册面板的所有注册提示
function removeAllRegisteError(){
	$("#mobile").html(errors.NULL);
	$("#verifyCode").html(errors.NULL);
	$("#pwd1error").html(errors.NULL);
	$("#pwd2error").html(errors.NULL);
}

//手机号规则验证
function isMobile(mobile){
	var reg = /^1[3|4|5|7|8]\d{9}$/;
	return reg.test(mobile);
}

//验证手机号输入
function verifyMobile(){
	var mobile = $('#mobile').val();
	if(mobile == null  || mobile == ""){
		return false;
	}else{
		if(!isMobile(mobile)){
			$("#mobileerror").html(errors.MOBILE_ERROR);
			return false;
		}else{
			if($("#mobileerror").html() != errors.MOBILE_ALREADY_USE){
				$("#mobileerror").html(errors.NULL);
			}	
			return true;
		}
	}	
}

//mobile blur validation
$("#mobile").blur(function(){
	verifyMobile();
})

var imageCodePass = false;
//获取图片验证码
function getimagecode(){
	var i = Math.random();
	$("#imgCodeButton").attr('src',"/getImageCode?random=" + i);
	$("#imgYzmnumber").val("");
	imageCodePass = false;
}

//图片验证码光标移除是验证
$("#imgYzmnumber").blur(function(){
	checkImageCode();
})

//校验图片验证码
function checkImageCode(){
	var imagecode = $.cookie('imagecode');
	var imagecode2 = $("#imgYzmnumber").val();
	if(imagecode == sha256_digest(imagecode2)){
		imageCodePass = true;
		$("#usertpyzmtips").html("");
	}else{
		imageCodePass = false;
		$("#usertpyzmtips").html("输入的图片验证码不正确！");
	}
}

//获取验证码失败
function getIdentifyCodeFail(){
	$("#mobileerror").html(errors.IDENTIFY_CODE_GET_ERROR);
	$("#identifyCodeButton").attr('disabled',false);
}

//验证码60秒的倒计时
function countDown(){
	var count = 60;
	var timer = setInterval(function(){
		if(count > 0){
			count--;
			$("#identifyCodeButton").html("已发送（"+count+"）");
		}else{
			$("#identifyCodeButton").html("获取验证码");
			$("#identifyCodeButton").attr('disabled',false);
			getimagecode();
			clearTimeout(timer);
		}		
	},1000);
}

//查找后台是否已经有这个手机号
function doSearchMobile(){
	var mobile = $('#mobile').val();
	
	$.get( "/checkUserExist",{"username":mobile}, function( data ) {
		if(data){
			$("#identifyCodeButton").text("获取中…");
			$("#mobileerror").html(errors.NULL);
			var mobile = $('#mobile').val();
			var imagecode = $.cookie('imagecode');
			$.get( "/sendVerifyCode",{"mobile":mobile, "imagecode":imagecode}, function( data ) {
				if(data){
					verifyCode = data;
					countDown();
				}else{
					getimagecode();
					getIdentifyCodeFail();
				}  
			});
		}else{
			$("#identifyCodeButton").text("获取验证码");
			$("#mobileerror").html(errors.MOBILE_NOT_SIGNUP);
			$("#identifyCodeButton").attr('disabled',false);
		}
	});
}
	
//获取验证码
function doGetIdentifyCode(){
	$("#mobileerror").html(errors.NULL);
	if(verifyMobile()){
		if(imageCodePass == false){
			$("#usertpyzmtips").html("输入的图片验证码不正确！");
			return;
		}
		$("#identifyCodeButton").attr('disabled',true);
		doSearchMobile();
	}else{
		$("#mobileerror").html(errors.MOBILE_ERROR);
		$("#identifyCodeButton").attr('disabled',false);
	}
}

//检查输入的验证码是否是发送的验证码
function checkVerifyCode(inputCode){
	var verifyCodeReg = /^\d{4}$/;
	return verifyCodeReg.test(inputCode);
}

//verify code blur validation
$("#verifyCode").blur(function(){
	var inputCode = $("#verifyCode").val();
	if(inputCode == null  || inputCode == ""){
		return false;
	}else{
		if(!checkVerifyCode(inputCode)){
			$("#identifyCodeError").html(errors.VERIFYCODE_NOT_RIGHT);
			return false;
		}else{
			$("#identifyCodeError").html(errors.NULL);
			return true;
		}
	}
})

//检查所有输入是否有问题
function checkAllInfo(){
	var result = true;

	//手机号
	var mobile = $('#mobile').val();
	if(mobile == null  || mobile == ""){
		removeAllRegisteError();
		$("#mobileerror").html(errors.MOBILE_ERROR);
		result = false;
		return result;
	}else{
		if(!isMobile(mobile)){
			removeAllRegisteError();
			$("#mobileerror").html(errors.MOBILE_ERROR);
			result = false;
			return result;
		}else{
			$("#mobileerror").html(errors.NULL);
		}
	}
	
	checkImageCode();
	
	//验证码
	var code = $('#verifyCode').val();
	if(code == null  || code == ""){
		result = false;
		$("#identifyCodeError").html(errors.VERIFYCODE_NOT_RIGHT);
		return result;
	}else{
		if(verifyCode != sha256_digest(code)){
			result = false;
			$("#identifyCodeError").html(errors.VERIFYCODE_NOT_RIGHT);
			return result;
		}else{
			$("#identifyCodeError").html(errors.NULL);
		}
	}	
	return result;
}

//注册
function doNext(){
	if(checkAllInfo()){
		$("#Next").text("验证中…");
		var parentMobile = $('#mobile').val();
		var inventionCode = $('#verifyCode').val();			

		//跳转到第二步
		$("#step1").hide();
		$("#step2").show();
		$("#Next").text("验证");
		$("#fpStepTwo").addClass("cur");
	}
}

//第二步各种验证
function passwordBlur(){
	var password = $("#pwd1").val();
	if(password != null && password != ""){
		if(password.length < 6){
			$("#pwd1error").html(errors.PASSWORD_ERROR);
			return false;
		}else{
			$("#pwd1error").html(errors.NULL);
			return true;
		}
	}else{
		return false;
	}
}

//password blur validation
$("#pwd1").blur(function(){
	passwordBlur();
})

function confirmPasswordBlur(){
	var password = $("#pwd1").val();
	var confirmPassword = $("#pwd2").val();
	if(confirmPassword != "" && confirmPassword != $("#pwd1").val()){
		$("#pwd2error").html(errors.CONFIRM_PASSWORD_ERROR);
		return false;
	}else{
		$("#pwd2error").html(errors.NULL);
		return true;
	}
}

//confirm password blur validation
$("#pwd2").blur(function(){
	confirmPasswordBlur();
})

function doResetPassword(){
	if(!passwordBlur()){
		removeAllRegisteError();
		$("#pwd1error").html(errors.PASSWORD_ERROR);
	}else if(passwordBlur() && !confirmPasswordBlur()){
		removeAllRegisteError();
		$("#pwd2error").html(errors.CONFIRM_PASSWORD_ERROR);
	}
	else{
		removeAllRegisteError();
		$("#finishButton").attr("disabled",true);
		$("#finishButton").text("重置密码中…");
		
		var parentMobile = $('#mobile').val();
		var inventionCode = $('#verifyCode').val();
		var parentPassword = $("#pwd1").val();
		
		
		//发送request到后台
		$.post( "/forget",{"parentMobile":parentMobile,"parentPassword":parentPassword,"inventionCode":sha256_digest(inventionCode)}, function( data ) {
			if(data == "parentCenter"|| data == "studentInfo"){
				//parentCenter跳到首页
				//studentInfo跳到引导页
				$("#fpStepThr").addClass("cur");
				$("#step2").hide();
				$("#step3").show();
			}else if(data == "validationFail"){
				console.log("validationFail");
			}
		}).error(function() { 
			console.log("重置密码失败"); 
		});
		
	}
}

$(".reginput").focus(function(){
	$(this).addClass("on")
})
$(".reginput").blur(function(){
	$(this).removeClass("on")
})
$("a").click(function() {
	var url = window.location.href;
	if (url.indexOf("channel_id=") > 0||url.indexOf("channel_keyword=") > 0) {
		var input = url.substr(url.indexOf('?'));
		this.href = this.href + input;
	}
})
