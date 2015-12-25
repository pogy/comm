/**
 * Mobile登录
 */

$(document).ready(function() {
	var errors = {
			MOBILE_ERROR:'请正确输入11位手机号码',
			PASSWORD_ERROR:'密码为6-16位数字、字母、字符',
			NULL:null,
			USER_NOT_EXIST:'手机号或密码错误',
			USER_ALREADY_EXSITS:'用户已存在',
			MOBILE_ILLIGEL_CHAR:'手机号码只能包含数字',
			WRONG_STUDENT_CHINESE_NAME:'学生中文名为可输入2-8个汉字',
			WRONG_STUDENT_ENGLISH_NAME:'学生英文名为0-16个英文字母'
	};
	
	var error_code = {
			USER_NO_VERIFY_CODE: 603
	}
	
	var CHARS = {
			BACKSPACE : 8,
			TAB : 9,
	}
	
	function isMobile(mobile) {
		return mobile && /^1[3|4|5|7|8]\d{9}$/.test(mobile);
	}
	
	function isPassword(password) {
		return password && password.length >= 6 && password.length <= 16;
	}
	
	function checkInputIsReady() {
		var mobile = $('#loginMobile').val();
		var password = $('#loginPassword').val();
		
		if (isMobile(mobile) && isPassword(password)) {
			$('#loginButton').addClass('on');
			$('#loginButton').prop('disabled', false);
		} else {
			$('#loginButton').removeClass('on');
			$('#loginButton').prop('disabled', true);
		}
	}
	
	$('#loginMobile').on({
		keydown: function() {
			if ($(this).val().length >= 11 && event.which !== 8 && event.which !== 9 || event.keyIdentifier === 'U+0000') {
				//alert(event.keyIdentifier);
				event.preventDefault();
			}
			
			$('#loginFailedErrorMessage').css({
				display: 'none'
			});
		},
		
		keyup: function() {
			checkInputIsReady();
			if ($(this).val().length >= 11) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 11));
			}
		},
		
		change: function() {
			var mobile = $(this).val();
			if (!isMobile(mobile)) {
				$('#loginErrorMessage').html(errors.MOBILE_ERROR);
			} else {
				$('#loginErrorMessage').html(errors.NULL);
			}
			checkInputIsReady();
		}
	});
	
	$('#loginPassword').on({
		keydown: function() {
			if ($(this).val().length >= 16 && event.which !== 8 && event.which !== 9) {
				event.preventDefault();
			}
			
			$('#loginFailedErrorMessage').css({
				display: 'none'
			});
		},
		
		keyup: function() {
			checkInputIsReady();
			
			if ($(this).val().length >= 16) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 16));
			}
		},
		
		change: function() {
			var password = $(this).val();
			if (!isPassword(password)) {
				$('#loginErrorMessage').html(errors.PASSWORD_ERROR);
			} else {
				$('#loginErrorMessage').html(errors.NULL);
			}
			checkInputIsReady();
		}
	});
	
	$('#loginButton').on({
		click: function() {
			$(this).html("登录中...");
		}
	});
	
	// ==================signup================== //
	
	$('#signup-mobile').on({
		change : function() {
			if (isMobile($(this).val())) {
				$('#signup-step1-error-message').addClass('on');
				$('#signup-step1-next-button').prop('disabled', false);
				$('#signup-step1-error-message').html(errors.NULL);
				return true;
			} else if (/[a-zA-Z]/.test(mobile)) {
				$('#signup-step1-next-button').removeClass('on');
				$('#signup-step1-next-button').prop('disabled', true);
				$('#signup-step1-error-message').html(errors.MOBILE_ILLIGEL_CHAR);
				return false;
			} else {
				$('#signup-step1-next-button').removeClass('on');
				$('#signup-step1-next-button').prop('disabled', true);
				$('#signup-step1-error-message').html(errors.MOBILE_ERROR);
				return false;
			}
		},
		
		keydown: function() {
			if ($(this).val().length >= 11 && event.which !== 8 && event.which !== 9) {
				//alert($(this).val() + "  " + $(this).val().length);
				event.preventDefault();
			}
			if ($('#signup-step1-mobile-error-message').length > 0) {
				$('#signup-step1-mobile-error-message').css({display: 'none'});
			}
		},
		
		keyup: function() {
			if ($(this).val().length >= 11) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 11));
			}
			
			if (/^[0-9]*$/.test($(this).val())) {
				$('#signup-step1-next-button').removeClass('on');
				$('#signup-step1-next-button').prop('disabled', true);
				$('#signup-step1-error-message').html(errors.NULL);
			}
			if (isMobile($(this).val())) {
				$('#signup-step1-next-button').addClass('on');
				$('#signup-step1-next-button').prop('disabled', false);
			} else if (/[^0-9]/.test($(this).val())) {
				$('#signup-step1-next-button').removeClass('on');
				$('#signup-step1-next-button').prop('disabled', true);
				$('#signup-step1-error-message').html(errors.MOBILE_ILLIGEL_CHAR);
			}
		}
	});
	
	$('#signup-step1-next-button').on('click', function() {
		$(this).html('验证中...');
	});
	
	// =================设置密码页面==================== //
	
	function verifySetPasswordForm() {
		var verifyCodeOk = /^[0-9]{6}$/.test($('#verifyCode').val());
		var passwordOk = /^[0-9a-zA-Z]{6,16}$/.test($('#password').val());
		if (verifyCodeOk && passwordOk) {
			$('#finishButton').addClass('on');
			$('#finishButton').prop('disabled', false);
		} else {
			$('#finishButton').removeClass('on');
			$('#finishButton').prop('disabled', true);
		}
		
		if (!passwordOk) {
			$('#signup-step2-error-message').html(errors.PASSWORD_ERROR);
			$('#signup-step2-error-message').css({
				display: 'initial'
			});
		} else {
			$('#signup-step2-error-message').html(errors.NULL);
			$('#signup-step2-error-message').css({
				display: 'none'
			});
		}
	}
	
	$('#verifyCode').on({
		keydown: function() {
			if (($(this).val().length >= 6 && event.which !== 8 && event.which !== 9) || event.keyIdentifier === 'U+0000') {
				event.preventDefault();
			}
		},
		
		keyup: function () {
			if ($(this).val().length >= 6) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 6));
			}
			
			verifySetPasswordForm();
		}
	});
	
	$('#password').on({
		keydown: function() {
			if ($(this).val().length >= 16 && event.which !== 8 && event.which !== 9) {
				event.preventDefault();
			}
		},
		
		keyup: function () {
			if ($(this).val().length >= 16 && event.which !== 8 && event.which !== 9) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 16));
			}
			
			verifySetPasswordForm();
		}
	});
	
	$('#getVerifyCode').ready(function() {
		var countDown = 60;
		var timer = setInterval(function() {
			if (countDown <= 0) {
				clearInterval(timer);
				$('#getVerifyCode').prop('disabled', false);
				$('#getVerifyCode').css({'background-color': 'grey'});
			} else {
				countDown --;
				$('#getVerifyCode').prop('disabled', true);
				$('#getVerifyCode').css({'background-color': 'red'});
			}
			
			$('#getVerifyCode').html('获取验证码（' + countDown + '秒）');
		}, 1000);
	});
	
	$('#getVerifyCode').click(function() {
		var mobile = $('#signup-step2-username').val();
		if (isMobile(mobile)) {
			$.post('/mobile/setpassword/verifyCode', {
				username: mobile
			}, function(code, status) {
				if (status == error_code.USER_NO_VERIFY_CODE) {
					alert("您的验证码没有发送成功，请稍后再试，或向客服求助");
				}
			});
		}
	});
	
	// ===============forget password1=================== //
	$('#forgetpassword1Mobile').on({
		change : function() {
			if (isMobile($(this).val())) {
				$('#forgetpassword1-error-message').addClass('on');
				$('#forgetpassword1-next-button').prop('disabled', false);
				$('#forgetpassword1-error-message').html(errors.NULL);
				return true;
			} else if (/[a-zA-Z]/.test(mobile)) {
				$('#forgetpassword1-next-button').removeClass('on');
				$('#forgetpassword1-next-button').prop('disabled', true);
				$('#forgetpassword1-error-message').html(errors.MOBILE_ILLIGEL_CHAR);
				return false;
			} else {
				$('#forgetpassword1-next-button').removeClass('on');
				$('#forgetpassword1-next-button').prop('disabled', true);
				$('#forgetpassword1-error-message').html(errors.MOBILE_ERROR);
				return false;
			}
		},
		
		keydown: function() {
			if ($(this).val().length >= 11 && event.which !== 8 && event.which !== 9) {
				event.preventDefault();
			}
			if ($('#forgetpassword1-mobile-not-find').length > 0) {
				$('#forgetpassword1-mobile-not-find').css({display: 'none'});
			}
		},
		
		keyup: function() {
			if ($(this).val().length >= 11) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 11));
			}
			
			if (/^[0-9]*$/.test($(this).val())) {
				$('#forgetpassword1-next-button').removeClass('on');
				$('#forgetpassword1-next-button').prop('disabled', true);
				$('#forgetpassword1-error-message').html(errors.NULL);
			}
			if (isMobile($(this).val())) {
				$('#forgetpassword1-next-button').addClass('on');
				$('#forgetpassword1-next-button').prop('disabled', false);
			} else if (/[^0-9]/.test($(this).val())) {
				$('#forgetpassword1-next-button').removeClass('on');
				$('#forgetpassword1-next-button').prop('disabled', true);
				$('#forgetpassword1-error-message').html(errors.MOBILE_ILLIGEL_CHAR);
			}
		}
	});
	
	$('forgetpassword1-next-button').click(function() {
		$(this).text("验证中...");
	});
	
	// ===============forget password2=================== //
	
	function verifyChangePasswordForm() {
		var verifyCodeOk = /^[0-9]{6}$/.test($('#forgetpassword2-verifyCode').val());
		var passwordOk = /^[0-9a-zA-Z]{6,16}$/.test($('#forgetpassword2-password').val()) || $('#forgetpassword2-password').val() <= 0;
		if (verifyCodeOk && passwordOk) {
			$('#forgetpassword2-finishButton').addClass('on');
			$('#forgetpassword2-finishButton').prop('disabled', false);
		} else {
			$('#forgetpassword2-finishButton').removeClass('on');
			$('#forgetpassword2-finishButton').prop('disabled', true);
		}
		
		if (!passwordOk) {
			$('#forgetpassword2-errorMessage').html(errors.PASSWORD_ERROR);
			$('#forgetpassword2-errorMessage').css({
				display: 'initial'
			});
		} else {
			$('#forgetpassword2-errorMessage').html(errors.NULL);
			$('#forgetpassword2-errorMessage').css({
				display: 'none'
			});
		}
		
		$('#forgetpassword2-wrong-verifyCode').css({
			display: 'none'
		});
	}
	
	$('#forgetpassword2-verifyCode').on({
		keydown: function() {
			if (($(this).val().length >= 6 && event.which !== 8 && event.which !== 9) || event.keyIdentifier === 'U+0000') {
				event.preventDefault();
			}
		},
		
		keyup: function () {
			if ($(this).val().length >= 6) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 6));
			}
			
			verifyChangePasswordForm();
		}
	});
	
	$('#forgetpassword2-getVerifyCode').click(function() {
		var mobile = $('#forgetpassword2-username').val();

		var countDown = 60;
		var timer = setInterval(function() {
			if (countDown <= 0) {
				clearInterval(timer);
				$('#forgetpassword2-getVerifyCode').prop('disabled', false);
				$('#forgetpassword2-getVerifyCode').css({'background-color': 'grey'});
				$('#forgetpassword2-getVerifyCode').val('获取中验证码');
			} else {
				countDown --;
				$('#forgetpassword2-getVerifyCode').prop('disabled', true);
				$('#forgetpassword2-getVerifyCode').css({'background-color': 'red'});
			}
			
			$('#forgetpassword2-getVerifyCode').val('获取中...（' + countDown + '秒）');
		}, 1000);
		
		if (isMobile(mobile)) {
			$.post('/mobile/forgetpassword2/verifyCode', {
				username: mobile
			}, function(code, status) {
				if (status == error_code.USER_NO_VERIFY_CODE) {
					clearInterval(timer);
					$('#forgetpassword2-getVerifyCode').prop('disabled', false);
					$('#forgetpassword2-getVerifyCode').css({'background-color': 'grey'});
					alert("您的验证码没有发送成功，请稍后再试，或向客服求助");
				}
			});
		}
	});
	
	$('#forgetpassword2-password').on({
		keydown: function() {
			if ($(this).val().length >= 16 && event.which !== 8 && event.which !== 9) {
				event.preventDefault();
			}
		},
		
		keyup: function () {
			if ($(this).val().length >= 16 && event.which !== 8 && event.which !== 9) {
				var overLongValue = $(this).val();
				$(this).val(overLongValue.substr(0, 16));
			}
			
			verifyChangePasswordForm();
		}
	});
	
	$('#forgetpassword2-finishButton').on('click', function() {
		$(this).html('正在重置密码...');
	});
	
	// ==================孩子信息===================== //
	$('.info-input').on({
		keyup: function() {
			var chineseName = $('#chineseName').val();
			var englishName = $('#englishName').val();
			if (chineseName && englishName && chineseName.length > 0 && englishName.length > 0) {
				$('#submit-button').prop('disabled', false);
				$('#submit-button').addClass('on');
			}
		}
	});
	
	$('#submit-button').click(function() {
		var chineseName = $('#chineseName').val();
		var englishName = $('#englishName').val();
		
		var isChinese = /^[\u4e00-\u9fa5]{2,8}$/.test(chineseName);
		var isEnglish = /^[a-zA-Z]{0,16}$/.test(englishName);
		if (!isChinese) {
			$('#child-info-error').text(errors.WRONG_STUDENT_CHINESE_NAME);
			event.preventDefault();
			return;
		}
		if (!isEnglish) {
			$('#child-info-error').text(errors.WRONG_STUDENT_ENGLISH_NAME);
			event.preventDefault();
			return;
		}
	});
	
	$('#girl-label').click(function() {
		$('#girl-label').addClass('on');
		$('#boy-label').removeClass('on');
	});
	
	$('#boy-label').click(function() {
		$('#girl-label').removeClass('on');
		$('#boy-label').addClass('on');
	});
	
});
