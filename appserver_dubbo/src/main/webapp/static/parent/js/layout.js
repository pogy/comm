var studentId;

//点击头像
$(".uChangeArea a").click(function(){
	studentId = $(this).children().children("input").val();
})

//更换学生
function doChangeStudent(){
	if(studentId == null || studentId == "") return;
	var obj = new Object();
	obj.studentId = studentId;
	$.get( "/changeCurrentStudent",obj, function(data, status) {
		if(data == "OK"){
			window.location.href="/parent/home";
		}else{
		}
	});
}


$('.vipHdChildren li a').click(function(){
	studentId = $(this).siblings("input").val();
	doChangeStudent();
	return false;
})

