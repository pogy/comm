$(function(){
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
			return "/classlist?ageRange="+$("#ageRange").val()+"&rowNum="+rowNum+"&currNum="+currNum+"&sign=1&type="+$("#type").val();
		}
	});
	
	$(".ocHandle a").click(function(){
		var channelId = $(this).siblings("#channelId").val();
		Alert.confirm("","您需要先登录或注册，才能加入公开课！", "登录/注册","取消",function(){
			var search = "";
			if(window.location.search){
				search = window.location.search  + "&channel_id=" + channelId;
			}else{
				search = "?" + "channel_id=" + channelId;
			}
			
			window.location.href = "/login" + search;
		},null)
	})
});


//function gotoLogin(){
//	var openid = $(this).siblings("#openid").val();
//	console.log(openid);
//	Alert.confirm("","您需要先登录或注册，才能加入公开课！", "登录/注册","取消",function(){
//		
//		window.location.href = "/login?source=" + openid;
//	},null)
//}
