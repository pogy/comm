//资料设置
var locationObject = {};
var provinceValue = '';
var cityValue = '';
var districtValue = '';
var studentAvatar = '';
var globalStudentId ;
var addStudentFlag = false;
var globalProv;
var globalCity;
var globalDist;

var errors = {
		NAME_ERROR:'请正确输入名字，2-10个汉字',
		MOBILE_ERROR:'请正确输入手机号',
		EMAIL_ERROR:'请正确输入邮箱',
		ADDRESS_ERROR:'请正确输入地址',
		ZIPCODE_ERROR:'请正确输入邮政编码',
		STUDENT_CH_NAME_ERROR:'请正确输入孩子中文名，2-10个汉字',
		STUDENT_EN_NAME_ERROR:'请正确输入孩子英文名，2-20个英文字母',
		NULL:null,
}

function editParentInfo(){
    $("#parentEditPop").addClass("editPopOpen");
	$("#addProviceCityDistrict").text('');
	removeAllErrors();
	
	$.get("/parent/getParentInfo",function(data){
		showParentInfo(data);
		addressParse(data.address);
	}).error(function(){
		console.log("get parent address fail.")
	})
		
    return false;
}

function addStudentConfirm(){
	Alert.confirm('',"创建孩子信息后不可自行删除，是否确认添加？",'取消返回','确认添加',null,addChildren);	
}


function addChildren(){
    $("#childrenEditPop").addClass("editPopOpen");
    addStudentFlag = true;
    
    $(".childrenAvatarList li").removeClass("cur");
    $("#boy_3").addClass("cur");
    studentAvatar = "boy_3";
    $("#"+studentAvatar).insertBefore($("#"+studentAvatar).siblings("li:first"));
    
    $("#childrenGirl").attr('checked',true);
    $("#childrenBirth").val('2010-01-01');
}
  

function addressCombine(){
	var province = $("#addSelectProvinceValue  option:selected").text();
	var city = $("#addSelectCityValue  option:selected").text();
	var district = $("#addSelectDistrictValue  option:selected").text();												
	var address = $('#parentAddress').val();
	var result = '';
	if(province && province != "" && province != "选择省" ){
		result = province;
	}if(city && city != "" && city != "选择市"){
		result = result +' '+ city;
	}if(district && district != "选择区" && district != ""){
		result = result +' '+ district;
	}if(address && address != ""){
		result = result +' '+ address;
	}
	return result;
}

function addressParse(data){
	if(data && data != '' && data != "null" && data != '   '){
		$("#addProviceCityDistrict").show();
		var arr = data.split(' ');		
		if(arr.length > 3){
			var province = arr[0];
			var city = arr[1];
			var district = arr[2];
			var address = arr[3];
			
			globalProv = province;
			globalCity = city;
			globalDist = district;
			
			$(".editAddressLine").citySelect({prov:province, city:city,dist:district});
			$('#parentAddress').val(address);
			$("#addProviceCityDistrict").text(province+city+district);
		}else if(arr.length > 2){
			var province = arr[0];
			var city = arr[1];
			var district = arr[2];
			
			globalProv = province;
			globalCity = city;
			globalDist = district;
			
			$(".editAddressLine").citySelect({prov:province, city:city,dist:district});
			$('#parentAddress').val('');
			$("#addProviceCityDistrict").text(province+city+district);
		}else if(arr.length > 1){
			var province = arr[0];
			var city = arr[1];
			
			globalProv = province;
			globalCity = city;
			
			$(".editAddressLine").citySelect({prov:province, city:city});
			$('#parentAddress').val('');
			$("#addProviceCityDistrict").text(province+city);
		}else if(arr.length > 0){
			var province = arr[0];
			
			globalProv = province;
			
			$(".editAddressLine").citySelect({prov:province});
			$('#parentAddress').val('');
			$("#addProviceCityDistrict").text(province);
		}
	}else{
		$(".editAddressLine").citySelect({prov:"北京", city:"北京市", dist:"海淀区"}); 
		$("#addProviceCityDistrict").hide();
		$('#parentAddress').val('');
	}
}

//弹窗显示家长信息
function showParentInfo(parent){
	$("#parentName").val(parent.name);
	$("#parentPhone").val(parent.mobile);
	$("#parentEmail").val(parent.email);
	$("#parentZipcode").val(parent.zipcode);
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

//手机号规则验证
function isMobile(mobile){
	var reg = /^[\d]{11}$/;
	return reg.test(mobile);
}

//email规则验证
function isEmail(email){
	var reg = /^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
	return reg.test(email);
}

//zipcode验证
function isZipcode(zipcode){
	var reg = /^[\d]{6}$/;
	return reg.test(zipcode);
}

//address验证
function addressIsNull(data){
	if(data == null){
		return true;
	}else{
		var arr = data.split(' ');
		if(arr.length < 4){
			return true;
		}
		return false;
	}
}

//检查编辑家长信息表单
function checkAllInfo(parent){
	if(parent.name == "" || !checkChinese(parent.name)){
		removeAllErrors();
		$("#parentNameTips").html(errors.NAME_ERROR).show();
		$("#saveButton").attr('disabled',false);
		return false;
	}else if(parent.mobile == "" || !isMobile(parent.mobile)){
		removeAllErrors();
		$("#parentMobileTips").html(errors.MOBILE_ERROR).show();
		$("#saveButton").attr('disabled',false);
		return false;
	}else if(parent.email== "" || !isEmail(parent.email)){
		removeAllErrors();
		$("#parentEmailTips").html(errors.EMAIL_ERROR).show();
		$("#saveButton").attr('disabled',false);
		return false;
	}else if(addressIsNull(parent.address)){
		removeAllErrors();
		$("#parentAddressTips").html(errors.ADDRESS_ERROR).show();
		$("#saveButton").attr('disabled',false);
		return false;
	}else if(parent.zipcode == "" || !isZipcode(parent.zipcode)){
		removeAllErrors();
		$("#parentZipcodeTips").html(errors.ZIPCODE_ERROR).show();
		$("#saveButton").attr('disabled',false);
		return false;
	}
	removeAllErrors();
	return true;
}

function removeAllErrors(){
	$(".regtips").html(errors.NULL).hide();
}

function closeEditPop(){
    $(".editPop").removeClass("editPopOpen");
    $(".editAddressLine").citySelect({prov:"北京", city:"北京市", dist:"海淀区"}); 
    return false;
}

function doSave(){
	$("#saveButton").attr('disabled',true);
	var name = $("#parentName").val();
	var mobile = $("#parentPhone").val();
	var email = $("#parentEmail").val();
	var zipcode = $("#parentZipcode").val();
	var address = addressCombine();
	var data = {'name':name,'mobile':mobile,'email':email,'zipcode':zipcode,'address':address};
	if(checkAllInfo(data)){
		$.post("/parent/modifyParentInfo",{'name':data.name,'mobile':data.mobile,'email':data.email,'zipcode':data.zipcode,'address':data.address},function(data){
			if(data){
				$(".editPop").removeClass("editPopOpen");
				$("#saveButton").attr('disabled',false);
				window.location.reload(true);
			}
		}).error(function(){
			console.log("get parent address fail.")
		})
	}else{
		$("#saveButton").attr('disabled',false);
	}
}


/*编辑学生信息等*
 * */
$(".acChildrenEdit").click(function(){
	removeStudentAllErrors();
	addStudentFlag = false;
	var studentId = $(this).attr("id");
	$("#childrenEditPop").addClass("editPopOpen");
	$(".childrenAvatarList li").removeClass("cur");
	globalStudentId = studentId;
    studentInfoParse(studentId);
})

function studentInfoParse(studentId){
	$.get("/parent/getStudentInfoByStudentId",{"studentId":studentId},function(data){
		if(data != null){
			var studentInfo = data;
			
			if(data.avatar != null || data.avatar != ''){
				$('#'+data.avatar).addClass("cur");
				$("#"+data.avatar).insertBefore($("#"+data.avatar).siblings("li:first"));
				studentAvatar = data.avatar;
			}else{
				if(data.gender == 'FEMALE'){
					$('#girl_1').addClass("cur");
					studentAvatar = 'girl_1';
				}else{
					$('#boy_3').addClass("cur");
					studentAvatar = 'boy_3';
				}
			}
			
			if(data.chineseName != null || data.chineseName != ''){
				$("#childrenCnName").val(data.chineseName);
			}
			
			if(data.englishName != null || data.englishName != ''){
				$("#childrenEnName").val(data.englishName);
			}
			
			
			if(data.gender == 'MALE'){
				$("#childrenGirl").removeAttr('checked');
				$("#childrenBoy").prop('checked','checked');				
			}else{
				$("#childrenGirl").prop('checked','checked');
				$("#childrenBoy").removeAttr('checked');
			}
			
			if(data.birth != null && data.birth != ''){
				$("#childrenBirth").val(data.birth);
			}else{
				$("#childrenBirth").val("2010-01-01");
			}
			
		}
	}).error(function(){
		console.log("get parent address fail.")
	})
}

function removeStudentAllErrors(){
	$("#studentCHNameTips").html(errors.NULL).hide();
	$("#studentENNameTips").html(errors.NULL).hide();
}

function checkAllStudentInfo(student){
	if(student.chineseName == '' || !checkChinese(student.chineseName)){
		removeStudentAllErrors();
		$("#studentCHNameTips").html(errors.STUDENT_CH_NAME_ERROR).show();
		$("#saveStudentInfo").attr('disabled',false);
		return false;
	}else if(student.englishName == '' || !checkEnglish(student.englishName)){
		removeStudentAllErrors();
		$("#studentENNameTips").html(errors.STUDENT_EN_NAME_ERROR).show();
		$("#saveStudentInfo").attr('disabled',false);
		return false;
	}else if(student.avatar == '' || student.gender == '' || student.birth == ''){
		return false;
	}
	return true;
}

//保存学生信息
$("#saveStudentInfo").click(function(){
	$("#saveStudentInfo").attr('disabled',true);
	var avatar = studentAvatar;
	var chineseName = $("#childrenCnName").val();
	var englishName = $("#childrenEnName").val();
	if($("#childrenGirl").prop('checked') == true){
		var gender = "FEMALE";
	}else{
		var gender = "MALE";
	}
	var birth = $("#childrenBirth").val();
	date = new Date();
	date.setYear(Number(birth.substr(0,4)));
	date.setMonth(Number(birth.substr(5,2))-1);
	date.setDate(Number(birth.substr(8,2)));
	
	var studentInfo = {"avatar":avatar,"chineseName":chineseName,"englishName":englishName,"gender":gender,"birth":date};
	if(addStudentFlag){
		studentId = '';
	}else{
		studentId = globalStudentId;
	}
			
	if(checkAllStudentInfo(studentInfo)){
		$.post('/parent/saveStudentInfoByStudentId',{"avatar":avatar,"chineseName":chineseName,"englishName":englishName,"gender":gender,"birth":date,"studentId":studentId},function(data){
			if(data){
				$("#childrenEditPop").removeClass("editPopOpen");
				window.location.reload(true);
			}
		})
	}
})

//关闭编辑孩子详情页
function closeChildrenPop(){
	 $("#childrenEditPop").removeClass("editPopOpen");
	 $(".childrenAvatarList").addClass("normal").animate({width: "68px"});
	 $(".childrenMoreAvatar").show();
	 $('#childrenCnName').val('');
	 $('#childrenEnName').val('');
}

//选择用户头像
$(".childrenAvatarList li").click(function(){
    $(this).addClass("cur").siblings().removeClass("cur");
    $(this).parent("ul").animate({width: "68px"},300);
    studentAvatar = $(this).attr('id');
    setTimeout(function(){$(".childrenMoreAvatar").show();
    $(".childrenAvatarList").addClass("normal")},300);
    $(this).insertBefore($(this).siblings("li:first"))
})
$(".childrenMoreAvatar").click(function(){
    $(this).hide();
    $(this).siblings("ul").animate({width: "544px"},200).removeClass("normal").children("li").show();    
})


//日期插件
var newdate = new Date();
var currentday = newdate.getFullYear()+"-"+(newdate.getMonth()+1)+"-"+newdate.getDate();
$("#childrenBirth").click(function(){
	WdatePicker({    		
		maxDate:currentday,
		qsEnabled:false,
		isShowClear:false,
		isShowOK:false,
		isShowToday:false
	});     	
});

//男孩女孩选择
$("#childrenGirl").click(function(){
	$('#childrenBoy').attr('checked',false);
	$("#childrenGirl").attr('checked',true);
})
$("#childrenBoy").click(function(){
	$('#childrenBoy').attr('checked',true);
	$("#childrenGirl").attr('checked',false);
})

//地址选择
$(".editAddressLine").citySelect({prov:"北京", city:"北京市", dist:"海淀区"}); 
$('#parentAddress').val('');

$(".prov").bind("change",function(){
	$("#addProviceCityDistrict").show();
    var sProv = $(this).children("option:selected").text(); 
    globalProv = sProv;
    $("#addProviceCityDistrict").text('');

});
$(".city").bind("change",function(){
	$("#addProviceCityDistrict").show();
    var sCity = $(this).children("option:selected").text();
    globalCity = sCity;
    $("#addProviceCityDistrict").text('');    

});
$(".dist").bind("change",function(){
	$("#addProviceCityDistrict").show();
    var sDist = $(this).children("option:selected").text();  
    var province = $("#addSelectProvinceValue  option:selected").text();
	var city = $("#addSelectCityValue  option:selected").text();
    $("#addProviceCityDistrict").text('');
    $("#addProviceCityDistrict").append(province,city,sDist);
});

