$(function(){
	$('.reloadPage').click(function(){
		setTimeout("reloadPage()", 2000);
	});
	$("#privileged a").hover(function(){
		$(this).children('span').addClass('show');
	},function(){
		$(this).children('span').removeClass('show');
	});
	if($('#sign').val()!=-1){
		var lt =$('.topTab').offset().top;
		$('body').scrollTop(lt-200);
	}
	
	var rowNum = 5;
	var totalPage = (parseInt($("#totalRecords").val()) + rowNum -1) / rowNum;
	var totalRecords =parseInt($("#totalRecords").val());
	var pageNo = parseInt($("#currNum").val());
	if(!pageNo){pageNo = 1;}
	//生成分页
	kkpager.generPageHtml({
		pagerid:'kkpager',
		pno : pageNo,
		total : totalPage,//总页码
		totalRecords : totalRecords,//总数据条数
		getLink : function(currNum){
			return "/parent/openclass?ageRange="+$("#ageRange").val()+"&rowNum="+rowNum+"&currNum="+currNum+"&sign=1&type="+$("#type").val();
		}
	});
	
});

//function(title, message, okLabel, cancelLabel, okCallback, cancelCallback){
function gotoLogin(){
	Alert.confirm("","您需要先登录或注册，才能加入公开课！", "登录/注册","取消",function(){
		window.location.href = "/login";
	},null)
}

function reloadPage(){
	window.location.reload();
}

function doSignUpOpenClass(tid,sid,oid,time){
	
	
	$.ajax({
        type: "POST",
        url: '/parent/doSignUpOpenClass',
        data: {
        	'teacherId':tid,
			'studentId':sid,
			'onlineClassId':oid,
			'time':time},
        success: function(data){
        	if(data==''){
        		Alert.info("NO", "<p class=\"tc\">占座成功</p>", "知道了", function(){reloadPage()});
        	}else{
        		Alert.error("NO", data, "知道了", null);
        	}
        }
    });
}

function doSignUpOpenClassAndEnterClassRoom(tid,sid,oid,time){
	$.ajax({
        type: "POST",
        url: '/parent/doSignUpOpenClass',
        data: {
        	'teacherId':tid,
			'studentId':sid,
			'onlineClassId':oid,
			'time':time},
        success: function(data){
        	if(data==''){
        		reloadPage();
        		var newTab=window.open('about:blank');
        		newTab.location.href="/parent/classroom?studentId="+sid+"&onlineClassId="+oid,"_blank";
        	}else{
        		Alert.error("NO", data, "知道了", null);
        	}
        }
    });
}

//没有上专属公开课权限提示
function permissionAlert(){
	Alert.info(null,"亲，您的精品公开课已剩余0课时，请联系VIPKID服务人员，400-005-6666。","好的",null);
}