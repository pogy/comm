$(function(){
	if($('#hasStars').val()){
		$('#ocBotConLink').hide();
		$('#mr50').show();
	}else{
		$('#ocBotConLink').show();
		$('#mr50').hide();
	}
	if($('#hasOrder').val()){
		$('#wyyy').hide();
		$('#yyy').show();
	}else{
		$('#wyyy').show();
		$('#yyy').hide();
	}
});

function addHandler(element, type, handler) {
		if (element.addEventListener) {
			element.addEventListener(type, handler, false);
		} else if (element.attachEvent) {
			element.attachEvent("on", + type, handler);
		} else {
			element["on" + type] = handler;
		} 
	}
	function getId(id) {
        return document.getElementById(id);
	}
	var finishedClassBtn = getId("finishedClassBtn");
	var closeWxBtn = getId("closeWxBtn");
	function closeFinishedPop(popid){
		getId(popid).style.display="none";
	}
	addHandler(finishedClassBtn,"click",function(){closeFinishedPop("finishedClassPop")})
	addHandler(closeWxBtn,"click",function(){closeFinishedPop("classEwmPop")});
	
	var scheduleTime1;
	var currentTime1;
	$(function(){
		var timer = setInterval(function(){
			if(scheduleTime1 == null || currentTime1 == null){
				scheduleTime1 = parseInt($("#scheduleTime").val());
				currentTime1 = parseInt($("#currentTime").val());
			}else{
				currentTime1 = parseInt(currentTime1) + 1000;
			}
			if(currentTime1 != null && scheduleTime1 != null){
				calcTime1(scheduleTime1, currentTime1);
			}
		},1000);
	})
	
	function calcTime1(scheduleTime, currentTime){
		var remainingTime;
		if(scheduleTime > currentTime){
			remainingTime = scheduleTime - currentTime;
		}else{
			remainingTime = currentTime - scheduleTime;
		}
		countDownMinute = Math.floor((remainingTime / (60*1000))% 60);
		countDownSecond =  Math.floor((remainingTime / (1000))% 60);
		var timeLabel = countDownMinute + "'" + countDownSecond + '"';
		$("#timeLabel").html(timeLabel);
	}
	function closeWin(){
		Alert.confirm("提示","确认退出教室？","退出","不退出",function(){
			window.open('about:blank','_self'); 
			window.close();
		},null);
	}
 $(".ocShare").click(function(){
	 $(".ocShareCon").show();
 })
  $(".ocShareConClose").click(function(){
	 $(".ocShareCon").hide();
 })
 
 
 function doTakeStar(teacherId,onlineClassId){
	 $.ajax({
	        type: "POST",
	        url: '/parent/doTakeStar',
	        data: {
	        	'teacherId':teacherId,
	        	'onlineClassId':onlineClassId,
	        },
	        success: function(data){
	        	if(data==''){
	        		Alert.info("NO", "<p class=\"tc\">领取成功</p>", "知道了", null);
	        		$('#ocBotConLink').hide();
	        		$('#mr50').show();
	        	}else{
	        		Alert.error("NO", data, "知道了", null);
	        	}
	        }
	    });
 }
 
 function doOrderOnlineClass(lessonSerialNumber){
	 $.ajax({
	        type: "POST",
	        url: '/parent/doOrderOnlineClass',
	        data: {
	        	'lessonSerialNumber':lessonSerialNumber,
	        },
	        success: function(data){
	        	if(data==''){
	        		Alert.info("NO", "<p class=\"tc\">预约成功</p>", "知道了", null);
	        		$('#wyyy').hide();
	        		$('#yyy').show();
	        	}else{
	        		Alert.error("NO", data, "知道了", null);
	        	}
	        }
	    });
 }