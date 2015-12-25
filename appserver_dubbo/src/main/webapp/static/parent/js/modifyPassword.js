/*修改密码*/

var errors = {
		ORIGINAL_PASSWORD_ERROR:'您的当前密码不正确，请重新输入',
		NEW_PASSWORD_ERROR:'密码长度不能小于六位',
		CONFIRM_PASSWORD_ERROR:'两次输入的密码不一致',
		NULL:null,
}
var tips = {
		ORIGINAL_PASSWORD_NOTE:'请输入原登录密码',
		NEW_PASSWORD_NOTE:'请输入新的登录密码',
		CONFIRM_PASSWORD_NOTE:'请再次输入新登录密码',
		NULL:null,
}

function focusBlurPassword(object,note,cls){    
	object.blur(function() {
        object.parent().removeClass(cls);
        object.removeClass('on');
        if ($.trim(object.val()) == '') {
        	object.val(note);
        	object.attr('type','text');
        }
    }).focus(function() {
    	object.addClass('on');
    	object.parent().addClass(cls);
        if ($.trim(object.val()) == note) {
        	object.val("");
        	object.attr('type','password');
        }
    });
}

focusBlurPassword($("#password"),tips.ORIGINAL_PASSWORD_NOTE,'on');
focusBlurPassword($("#newpassword"),tips.NEW_PASSWORD_NOTE,'on');
focusBlurPassword($("#cfmnewpassword"),tips.CONFIRM_PASSWORD_NOTE,'on');

//老密码blur
$("#password").blur(function(){
	var value = $("#password").val();
	if(value != tips.ORIGINAL_PASSWORD_NOTE && value.length < 6){
		$("#passworderror").html(errors.NEW_PASSWORD_ERROR);
	}else if(value.length >= 6){
		$("#passworderror").html(errors.NULL);
	}
})

//新密码blur
$("#newpassword").blur(function(){
	var value = $("#newpassword").val();
	if(value != tips.NEW_PASSWORD_NOTE && value.length < 6){
		$("#newpassworderror").html(errors.NEW_PASSWORD_ERROR);
	}else if(value.length >= 6){
		$("#newpassworderror").html(errors.NULL);
	}
})

//确认新密码blur
$("#cfmnewpassword").blur(function(){
	var value = $("#cfmnewpassword").val();
	if(value != tips.CONFIRM_PASSWORD_NOTE  && value != $("#newpassword").val()){
		$("#cfmnewpassworderror").html(errors.CONFIRM_PASSWORD_ERROR);
	}else if(value == $("#newpassword").val()){
		$("#cfmnewpassworderror").html(errors.NULL);
	}
	
})

function ModifyPassword(){
	$("#confirmModifyPwd").attr("disabled",true);
	
	var inputPassword = $("#password").val();
	if(inputPassword == '' || inputPassword == tips.ORIGINAL_PASSWORD_NOTE || inputPassword.length < 6){
		removeAllErrors();
		$("#passworderror").html(errors.NEW_PASSWORD_ERROR);
		$("#confirmModifyPwd").attr("disabled",false);
		return;
	}if(inputPassword != '' && inputPassword != tips.ORIGINAL_PASSWORD_NOTE){ 
		$.get('/verifyOriginalPasswordByMobile',{password:inputPassword},function(data){
			if(!data){
				removeAllErrors();
				$("#passworderror").html(errors.ORIGINAL_PASSWORD_ERROR);
				$("#confirmModifyPwd").attr("disabled",false);
				return;
			}else{
				if($("#newpassword").val() == '' || $("#newpassword").val() == tips.NEW_PASSWORD_NOTE || $("#newpassword").val().length < 6){
					removeAllErrors();
					$("#newpassworderror").html(errors.NEW_PASSWORD_ERROR);
					$("#confirmModifyPwd").attr("disabled",false);
					return ;
				}else if($("#cfmnewpassword").val() == '' || $("#cfmnewpassword").val() == tips.NEW_PASSWORD_NOTE || $("#cfmnewpassword").val() != $("#newpassword").val()){
					removeAllErrors();
					$("#cfmnewpassworderror").html(errors.CONFIRM_PASSWORD_ERROR);
					$("#confirmModifyPwd").attr("disabled",false);
					return ;
				}else{
					var oldPassword = $("#password").val();
					password = $("#newpassword").val();
					$.post('/parent/modifyPassword',{oldPassword:oldPassword,password:password},function(data){
						if(data){
							console.log("modify password success");
							Alert.info('',"<p class='tc'>修改密码成功！</p>",'',skipHome);							
						}else{
							$("#confirmModifyPwd").attr("disabled",false);
							console.log("modify password fail");
						}
					}).error(function(){
						$("#confirmModifyPwd").attr("disabled",false);
						console.log("modify password fail");
					})
				}
			}
		}).error(function(){
			console.log("get password fail");
		})
	}	
}


function removeAllErrors(){
	$("#passworderror").html(errors.NULL);
	$("#newpassworderror").html(errors.NULL);
	$("#cfmnewpassworderror").html(errors.NULL);
}

function skipHome(){
	window.location.href = "/parent/home";
}





