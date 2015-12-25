//D:Info guide 
var errors = {
		CHINESE_NAME_ERROR:'请正确输入孩子中文名，2-10个汉字',
		ENGLISH_NAME_ERROR:'请正确输入孩子英文名，2-20个英文',
		GENDER_ERROR:'选性别',
		BIRTH_ERROR:'生日为空',
		NULL:null,
}

//检测是否为中文
function checkChinese(str){
	var re1 = /^[\u4e00-\u9fa5]{2,10}$/;	
	return re1.test(str);
}

//检测是否为2-16英文,大小写
function checkEnglish(str){
	if(str.length <2){
		return false;
	}else{
		var re = /^([A-Za-z]+\s?)*[A-Za-z]$/;
		return re.test(str);
	}
}

//孩子中文名输入框focus，blur
$("#childrenChineseName").focus(function(){
	if(this.value == '用于班主任沟通'){
		this.value = '';
		$("#childrenChineseName").css('color','black');
	}
});

$("#childrenChineseName").blur(function(){
	if(this.value == ''){
		this.value = '用于班主任沟通';
		$("#childrenChineseName").css('color','#afafaf');
		return false;
	}
	else{
		if(checkChinese(this.value)){
			$("#childrenChineseNameTips").html(errors.NULL);
			return true;
		}
		else{
			$("#childrenChineseNameTips").html(errors.CHINESE_NAME_ERROR);
			return false;
		}
	}
});

//孩子英文名输入框focus，blur
$("#childrenEnglishName").focus(function(){
	if(this.value == '用于外教老师沟通'){
		this.value = '';
		$("#childrenEnglishName").css('color','black');
	}
});

$("#childrenEnglishName").blur(function(){
	if(this.value == ''){
		this.value = '用于外教老师沟通';
		$("#childrenEnglishName").css('color','#afafaf');
		return false;
	}
	else{
		if(checkEnglish(this.value)){
			$("#childrenEnglishNameTips").html(errors.NULL);
			return true;
		}
		else{
			$("#childrenEnglishNameTips").html(errors.ENGLISH_NAME_ERROR);
			return false;
		}	
	}
});

//性别radio控制
$("#childrenGenderGirl").click(function(){
	$("#childrenGenderMan").attr("checked",false);
})

$("#childrenGenderMan").click(function(){
	$("#childrenGenderGirl").attr("checked",false);
})



function checkAllInfo(){
	var chineseName = $("#childrenChineseName").val();
	var englishName = $("#childrenEnglishName").val();
	
	if(chineseName == "用于班主任沟通" || !checkChinese(chineseName)){
		removeAllErrors();
		$("#childrenChineseNameTips").html(errors.CHINESE_NAME_ERROR);
		$("#welcomeSubmit").attr('disabled',false);
		return false;
	}
	else if(englishName == "用于外教老师沟通" || !checkEnglish(englishName)){
		removeAllErrors();
		$("#childrenEnglishNameTips").html(errors.ENGLISH_NAME_ERROR);
		$("#welcomeSubmit").attr('disabled',false);
		return false;
	}
	
	return true;

}

function commitStudentInfo(){
	$("#welcomeSubmit").attr('disabled',true);
	if(checkAllInfo()){
		var chineseName = $("#childrenChineseName").val();
		var englishName = $("#childrenEnglishName").val();
		if($("#childrenGenderGirl").prop("checked")){
			var gender = "FEMALE";
		}
		else{
			var gender = "MALE";
		}
		var birth = $("#childrenBirthday").val();
		date = new Date();
		date.setYear(Number(birth.substr(0,4)));
		date.setMonth(Number(birth.substr(5,2))-1);
		date.setDate(Number(birth.substr(8,2)));
		
		$.post( "/parent/signupStudentInfo", {"chineseName":chineseName,"englishName":englishName,"gender":gender,"birth":date}, function( data ) {
			if(data){
				if(data == "welcome"){
					window.location.href="/parent/home";	
				}else if(data == "openclass"){
					window.location.href="/parent/openclass";	
				}else{
					$("#welcomeSubmit").attr('disabled',false);
				}
			}else{
				$("#welcomeSubmit").attr('disabled',false);
				console.log("signupStudentInfo fail");
			} 
		}).error(function(){
			$("#welcomeSubmit").attr('disabled',false);
			console.log("login fail");
		})
	}
}



//program start from here
if($.cookie('skipGuide')){
	window.location.href = '/parent/home';
}else{	
	var arr = $.cookie('Authorization').split(' ');
	var id = arr[1];
	
	$.get("/parent/getParentMobileByParentId",{"id":id},function(data){
		if(data){
			var mobile = data.substr(0,3);
			$("#parentMobile").html(mobile.concat("****",data.substr(7,4))); 
			
			checkTrailClass();
			
		}
	}).error(function(){
		console.log("find parent by id fail.");
	})	
}


//日期插件
var newdate = new Date();
var currentday = newdate.getFullYear()+"-"+(newdate.getMonth()+1)+"-"+newdate.getDate();
$("#childrenBirthday").click(function(){
	WdatePicker({    		
		maxDate:currentday,
		qsEnabled:false,
		isShowClear:false,
		isShowOK:false,
		isShowToday:false
	});     	
});

function removeAllErrors(){
	$("#childrenChineseNameTips").html(errors.NULL);
	$("#childrenEnglishNameTips").html(errors.NULL);
}

//检查是否已经有trail课
function checkTrailClass(){
	var arr = $.cookie('Authorization').split(' ');
	var studentId = arr[1];
	$.get("/parent/checkTrailClassByStudentId",{"id": studentId}, function(data){
		if(data){
			$("#trailClassTime").html('体验课时间：' + data);
			$("#trailClassFlag").addClass('cur');
		}else{
			$("#trailClassTime").html('稍后将有VIPKID客服专员与您联系确认时间');
		}
	}).error(function(){
		console.log("can not check trail class");
	})
}

//第二步：
function PreStep(){
	window.location.href="/parent/guide_step2";	
}

function nextStep(){
	window.location.href="/parent/guide_step3";	
}

function skipGuide(){
	$.cookie('skipGuide',true,{path:'/',domain:'.vipkid.com.cn'});
	window.location.href = '/parent/home';
}

$("a").click(function() {
	var url = window.location.href;
	if (url.indexOf("channel_id=") > 0 || url.indexOf("channel_keyword=") > 0) {
		var input = url.substr(url.indexOf('?'));
		this.href = this.href + input;
	}
})



