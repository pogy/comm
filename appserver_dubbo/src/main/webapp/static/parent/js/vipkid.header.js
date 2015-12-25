$(".vipHdList li").hover(function(){
	if($(this).children(".vipHdWxPop").length > 0){
		if($(this).hasClass("wxon")){
			$(this).children(".vipHdWxPop").hide();
			$(this).removeClass("wxon");
		}else{
			$(this).children(".vipHdWxPop").show();
			$(this).addClass("wxon");
		}
	}	
	
	if($(this).children(".vipHdUserInfo").length > 0){
		if($(this).hasClass("myon")){
			$(this).children(".vipHdUserInfo").hide();
			$(this).removeClass("myon");
		}else{
			$(this).children(".vipHdUserInfo").show();
			$(this).addClass("myon");
		}
	}
})

$(".vipHdChildren li a").click(function(){
	$(this).parent("li").addClass('cur').siblings().removeClass('cur')
})
//退出系统
function signout(){
	window.location.href = "/signout";
}