	
/*** 注册 */
var verifyCode = '';
var errors = {
		MOBILE_ERROR:'请正确输入11位手机号码',
		MOBILE_ALREADY_USE:'此手机号码已经被注册',
		VERIFYCODE_NOT_RIGHT:'输入的验证码不正确',
		PASSWORD_NULL:'密码不能为空',
		PASSWORD_LENGTH_ERROR:'密码为6-20位数字、字母、字符',
		CMDCODE_ERROR:'请正确输入推荐人的11位手机号码',
		CMDCODE_ERROR2:'推荐人手机号不能与注册人手机号重复',
		NULL:null,
}

$(function($){
	$('#childrenPhoneNumber').focus();
});


//手机号规则验证
function isMobile(mobile){
	var reg = /^1[3|4|5|7|8]\d{9}$/;
	return reg.test(mobile);
}

//验证手机号输入
function verifyMobile(){
	var mobile = $('#childrenPhoneNumber').val();
	if(mobile == null  || mobile == ""){
		return false;
	}else{
		if(!isMobile(mobile)){
			$("#userphonetips").html(errors.MOBILE_ERROR);
			return false;
		}else{
			if($("#userphonetips").html() != errors.MOBILE_ALREADY_USE){
				$("#userphonetips").html(errors.NULL);
			}	
			return true;
		}
	}
	
}

//mobile blur validation
$("#childrenPhoneNumber").blur(function(){
	verifyMobile();
})


//获取验证码失败
function getIdentifyCodeFail(){
	$("#useryzmtips").html(errors.IDENTIFY_CODE_GET_ERROR);
	$("#identifyCodeButton").attr('disabled',false);
}

//验证码60秒的倒计时
function countDown(){
	var count = 60;
	var timer = setInterval(function(){
		if(count > 0){
			count--;
			$("#identifyCodeButton").html("已发送（"+count+"）").addClass("normal");
		}else{
			$("#identifyCodeButton").html("获取验证码").removeClass("normal");;
			$("#identifyCodeButton").attr('disabled',false);
			getimagecode();
			clearTimeout(timer);
		}		
	},1000);
}

//查找后台是否已经有这个手机号
function doSearchMobile(){
	var mobile = $('#childrenPhoneNumber').val();
	
	$.get( "/checkUserExist",{"username":mobile}, function( data ) {
		if(!data){
			$("#identifyCodeButton").text("获取中…");
			$("#userphonetips").html(errors.NULL);
			var mobile = $('#childrenPhoneNumber').val();
			var imagecode = $.cookie('imagecode');
			$.get( "/sendVerifyCode",{"mobile":mobile,"imagecode":imagecode}, function( data ) {
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
			$("#userphonetips").html(errors.MOBILE_ALREADY_USE);
			$("#identifyCodeButton").attr('disabled',false);
		}
	});
}
	
//获取验证码
function doGetIdentifyCode(){
	$("#useryzmtips").html(errors.NULL);
	if(verifyMobile()){
		if(imageCodePass == false){
			$("#usertpyzmtips").html("输入的图片验证码不正确！");
			return;
		}
		$("#identifyCodeButton").attr('disabled',true);
		doSearchMobile();
	}else{
		$("#userphonetips").html(errors.MOBILE_ERROR);
		$("#identifyCodeButton").attr('disabled',false);
	}
}

//检查输入的验证码是否是发送的验证码
function checkVerifyCode(inputCode){
	var verifyCodeReg = /^\d{4}$/;
	return verifyCodeReg.test(inputCode);
}

var imageCodePass = false;
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

function getimagecode(){
	var i = Math.random();
	$("#imgCodeButton").attr('src',"/getImageCode?random=" + i);
	$("#imgYzmnumber").val("");
	imageCodePass = false;
}

//verify code blur validation
$("#childrenYzmnumber").blur(function(){
	var inputCode = $("#childrenYzmnumber").val();
	if(inputCode == null  || inputCode == ""){
		return false;
	}else{
		if(!checkVerifyCode(inputCode)){
			$("#useryzmtips").html(errors.VERIFYCODE_NOT_RIGHT);
			return false;
		}else{
			$("#useryzmtips").html(errors.NULL);
			return true;
		}
	}
})


//password blur validation
$("#childrenPassword").blur(function(){
	var password = $("#childrenPassword").val();
	
	if(password == null  || password == ""){
		return false;
	}else{
		if(password.length < 6){
			$("#userpwdtips").html(errors.PASSWORD_LENGTH_ERROR);
		}else{
			$("#userpwdtips").html(errors.NULL);
		}	
	}
})

	
//检查所有输入是否有问题
function checkAllInfo(){
	var result = true;

	//手机号
	var mobile = $('#childrenPhoneNumber').val();
	if(mobile == null  || mobile == ""){
		removeAllRegisteError();
		$("#userphonetips").html(errors.MOBILE_ERROR);
		result = false;
		$("#regSubmitBtn").attr("disabled",false);
		return result;
	}else{
		if(!isMobile(mobile)){
			removeAllRegisteError();
			$("#userphonetips").html(errors.MOBILE_ERROR);
			result = false;
			$("#regSubmitBtn").attr("disabled",false);
			return result;
		}else{
			$("#userphonetips").html(errors.NULL);
		}
	}
	
	checkImageCode();
	
	//验证码
	var code = $('#childrenYzmnumber').val();
	if(code == null  || code == ""){
		result = false;
		removeAllRegisteError();
		$("#useryzmtips").html(errors.VERIFYCODE_NOT_RIGHT);
		$("#regSubmitBtn").attr("disabled",false);
		return result;
	}else{
		if(verifyCode != sha256_digest(code)){
			result = false;
			removeAllRegisteError();
			$("#useryzmtips").html(errors.VERIFYCODE_NOT_RIGHT);
			$("#regSubmitBtn").attr("disabled",false);
			return result;
		}else{
			$("#useryzmtips").html(errors.NULL);
		}
	}
	
	//密码
	var pwd = $('#childrenPassword').val();
	if(pwd == null  || pwd == ""){
		result = false;
		removeAllRegisteError();
		$("#userpwdtips").html(errors.PASSWORD_LENGTH_ERROR);
		$("#regSubmitBtn").attr("disabled",false);
		return result;
	}else{
		if(pwd.length < 6){
			result = false;
			removeAllRegisteError();
			$("#userpwdtips").html(errors.PASSWORD_LENGTH_ERROR);
			$("#regSubmitBtn").attr("disabled",false);
			return result;
		}else{
			$("#userpwdtips").html(errors.NULL);
		}
	}
	
	//推荐码
	var cmdcode = $('#cmdcode').val();
	if(cmdcode == null  || cmdcode == ""){
	}else{
		if(!isMobile(cmdcode)){
			removeAllRegisteError();
			$("#cmdcodetips").html(errors.CMDCODE_ERROR);
			result = false;
			$("#regSubmitBtn").attr("disabled",false);
			return result;
		}else if(mobile == cmdcode){
			removeAllRegisteError();
			$("#cmdcodetips").html(errors.CMDCODE_ERROR2);
			result = false;
			$("#regSubmitBtn").attr("disabled",false);
			return result;
		}else{
			$("#cmdcodetips").html(errors.NULL);
		}
	}
	return result;
}


//注册
function doSignup(){
	$("#regSubmitBtn").attr("disabled",true);
	var url = window.location.href;
	if(checkAllInfo()){
		$("#regSubmitBtn").text("注册中…");
		var parentMobile = $('#childrenPhoneNumber').val();
		var parentPassword = $('#childrenPassword').val();
		var inventionCode = $('#childrenYzmnumber').val();
		var recommendCode = $("#cmdcode").val();
		$.post( "/signup",{"parentMobile":parentMobile,"parentPassword":parentPassword,"inventionCode":sha256_digest(inventionCode),"cmdCode":recommendCode,"url":url}, function( data ) {
			if(data){
				//跳转到studentInfo
				window.location.href = '/welcome';
			}else{
				console.log("login fail");
				$("#regSubmitBtn").text("注册");
			}  
		});
	}
}

	
//移除注册面板的所有注册提示
function removeAllRegisteError(){
	$("#userphonetips").html(errors.NULL);
	$("#useryzmtips").html(errors.NULL);
	$("#userpwdtips").html(errors.NULL);
	$("#userexisttips").html(errors.NULL);
}

//最后一个输入框回车进行登录
$("#childrenPassword").keypress(function(event){
	if(event.keyCode == 13){
		doSignup();
	}
})

$(".reginput").focus(function(){
	$(this).addClass("on")
})
$(".reginput").blur(function(){
	$(this).removeClass("on")
})

$("a").click(function() {
	var url = window.location.href;
	if (url.indexOf("channel_id=") > 0 || url.indexOf("channel_keyword=") > 0) {
		var input = url.substr(url.indexOf('?'));
		this.href = this.href + input;
	}
})


function showProtocol(){
	$(".protocolPop").css("opacity","0").show();
	var winHeight = $(window).height();	
	var bodyHeight = $("body").height();
	if(winHeight < bodyHeight){
		winHeight = bodyHeight;
	}
	$(".protocolScroll").height($(window).height()-160)
	$(".protocolText").height($(window).height()-80)
	$(".protocolPop").css({"opacity":1,"height":winHeight});
}
$(".argeeProtocol").click(function(){
	showProtocol()
});
$(".protocolPopClose").click(function(){
	$(".protocolPop").css("opacity","0").hide();
})
$(".porotolAgreeBtn").click(function(){
	$(".protocolPop").css("opacity","0").hide();
})
