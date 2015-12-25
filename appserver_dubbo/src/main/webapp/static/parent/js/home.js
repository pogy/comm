var lc = window.location.href;

$(".weekpage a").click(function(){
	if($(this).index()==0){
		window.location.href = "/parent/home?whichWeek=-1";		
	}else if($(this).index()==1){
		window.location.href = "/parent/home?whichWeek=0";	
	}else if($(this).index()==2){
		window.location.href = "/parent/home?whichWeek=1";	
	}
})



// 2015-08-29 添加水平测试引导层
/**
 * 以后再测 处理
 */
function skipStudentExam() {
	// 进入home
	window.location.href="/parent/home";
}	

/**
 * 开始测试
 */
function startStudentExam() {
	// 进入测试
	// need cookie -- studentId
	window.location.href="/parent/startexam";
}	

$("#levelTestGuide_bg").click(function() {
	//
	$("#levelTestGuide_bg").css("display","none");
	return false;
})



$("#levelTestGuideOK").click(function() {
	startStudentExam();
})


$("#levelTestGuideCancel").click(function() {
	$("#levelTestGuide_bg").css("display","none");
	return false;
})



$("#levelTestGuide").click(function() {
	return false;
})
