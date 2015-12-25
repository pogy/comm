//加载数据
function queryByParams(mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx){
	if(currNum==0||currNum>Number($('#totalPage').val())){
		if(isPageDo==1&&currNum!=$('#currNum').val()){
			return;
		}
	}
	window.location.href=$("#links").attr("href") +"?mode="+mode+"&seaType="+seaType+"&teacherId="+teacherId+"&week="+week+"&day="+encodeURI(encodeURI(day))+"&timeStart="+encodeURI(encodeURI(timeStart))+"&timeEnd="+encodeURI(encodeURI(timeEnd))+"&courseType="+courseType+"&currNum="+currNum+"&isPageDo="+isPageDo+"&idx="+idx+"&teacherName="+encodeURI(encodeURI($('#teacherName_').text()));
}
function doBook(newOnlineClassId,oldOnlineClassId,
		mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx){
	$.ajax({
        type: "POST",
        url: '/parent/book',
        data: {
			'onlineClassId':newOnlineClassId,
			'oldOnlineClassId':oldOnlineClassId,
			'courseType':courseType},
        success: function(data){
        	if(data==''){
        		Alert.info("NO", "<p class='tc'>恭喜你，约课成功</p>", "知道了", function(){queryByParams(mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx)});
        	}else{
        		Alert.error("错误提示", data, "知道了", null);
        	}
        }
    });
}

//约课
function book(scheduledDateTime,newTeacherId,oteacherId,newOnlineClassId,oldOnlineClassId,newTeacherName,oldTeacherName,
		mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx){
	if(oteacherId){
		Alert.confirm("约课提示", "你已经在"+scheduledDateTime+"约了"+oldTeacherName+"的课程,确定改约"+newTeacherName+"的课程吗？", "确定", "取消", function(){
			doBook(newOnlineClassId,oldOnlineClassId,
					mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx)}, null);
	}else{
		Alert.confirm("约课提示", "确定预约"+newTeacherName+" "+scheduledDateTime+"的课程吗？", "确定", "取消", function(){
			doBook(newOnlineClassId,oldOnlineClassId,
				mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx)}, null);
	}
}

//取消约课
function doCancel(scheduledDateTime,newTeacherId,oteacherId,newOnlineClassId,oldOnlineClassId,newTeacherName,oldTeacherName,
		mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx){
	Alert.confirm("取消提示", "确定取消"+oldTeacherName+"在"+scheduledDateTime+"的课程吗?(如果24小时内取消约课，课时-1)", "取消预约", "再想想", function(){
		cancelOnlineClass(oldOnlineClassId,
				mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx)}, null);
}

function cancelOnlineClass(oldOnlineClassId,
		mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx){
	$.ajax({
        type: "POST",
        url: '/parent/cancelOnlineClass',
        data: {
			'oldOnlineClassId':oldOnlineClassId
			},
        success: function(data){
        	if(data==''){
        		Alert.info("NO", "<p class='tc'>恭喜你，取消成功</p>", "知道了", function(){queryByParams(mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx)});
        	}else{
        		Alert.error("错误提示", data, "知道了", null);
        	}
        }
    });
}
//日历模式book
function bookForCal(size,type,scheduledDateTime,newTeacherId,oteacherId,newOnlineClassId,oldOnlineClassId,newTeacherName,oldTeacherName,
		mode,seaType,teacherId,week,day,timeStart,timeEnd,courseType,currNum,isPageDo,idx){
	$('#tabScheduledDateTime').val(scheduledDateTime);
	$('#tabSeaType').val(seaType);
	$('#tabTeacherId').val(teacherId);
	$('#tabCourseType').val(courseType);
	$('#tabOnlineClassId').val('');//每次弹出教师列表框是 清空内容
	findTeacherForCal(scheduledDateTime,seaType,teacherId,courseType,1);
}

//日历模式下 教师弹出列表 样式控制 start 
$("body").delegate("#teacherCalendarModel li","click",function(){
    $(this).addClass("cur").siblings().removeClass("cur");
    
})
function closeYuekepop(){
    $("#teacherCalendarPop").hide();
    if($('#tabOnlineClassId').val()==''){
    	var mode = parseInt($('#mode').val());
    	var seaType = parseInt($('#seaType').val());
    	if(mode ==2&&seaType==2){
    		queryByParams(mode,seaType,$('#teacherId').val(),$('#week').val(),$('#day').text(),$('#timeStart').text(),$('#timeEnd').text(),$('#courseType').val(),$('#currNum').val(),$('#isPageDo').val(),$('#idx').val());
    	}
	}
}

$("body").delegate(".teacherPupopConfirm","click",function(){
	closeYuekepop();    
})
$("body").delegate(".teacherPupopClose","click",function(){
	closeYuekepop();    
})
//日历模式下 教师弹出列表 样式控制 end

//日历模式下 教师弹出列表获取
function findTeacherForCal(scheduledDateTime,seaType,teacherId,courseType,currNum){
	$.ajax({
        type: "GET",
        url: '/parent/findTeacherForCal',
        data: {
        	'scheduledDateTime':scheduledDateTime,
        	'seaType':seaType,
        	'teacherId':teacherId,
        	'courseType':courseType,
        	'currNum':currNum,
        	'teacherName':$('#teacherName_').text()
			},
        success: function(data){
        	if(data.message==''){
        		$('#tabCurrNum').val(data.tabCurrNum);
        		var teacherViews = data.teachersViews;
        		var htmlView = '';
        		htmlView +='<div class="tipsPop" id="teacherCalendarPop">';
        		htmlView +='<div class="teacherPupop">';
        		htmlView +='<div class="teacherPupopTitle">选择要预约的老师...（点击完成按钮完成约课）</div>';
        		htmlView +='<ul class="yuekeTeacherList" id="teacherCalendarModel">';
        		htmlView +='</ul>';
        		htmlView +='<div class="teacherPupopHandle">';
        		htmlView +='<p class="teacherPage"><a href="javascript:;" id="prePage">上一页</a><span id="pageText"></span><a href="javascript:;" id="nextPage"">下一页</a></p>';
        		htmlView +='<a class="teacherPupopConfirm" href="javascript:;" ';
        		htmlView +=' onclick="doBookForTeacherTab()"';
        		htmlView +=' >完成</a>';
        		htmlView +='</div>';
        		htmlView +='<a class="teacherPupopClose" href="javascript:;"></a>';
        		htmlView +='</div>';
        		htmlView +='<p class="tipsBg"></p></div>';
        		$("body").append(htmlView);
        		$('#teacherCalendarModel').html('');
        		var htmlView1='';
        		for(var i=0;i<teacherViews.length;i++){
        			htmlView1 +='<li';
        			htmlView1 +=' id="'+teacherViews[i].onlineClassId+'"';
        			htmlView1 +='>';
        			htmlView1 +='<p class="ykTlavatar">';
        			htmlView1 +='<img src="http://resource.vipkid.com.cn/';
        			htmlView1 +=teacherViews[i].avatar;
        			htmlView1 +='" alt='+teacherViews[i].name+' />';
        			htmlView1 +='<a class="ykTlAmore" href="';
        			htmlView1 +='/parent/teacherdetail?teacherId='+teacherViews[i].teacherId;
        			htmlView1 +='"  target="_blank">详细介绍</a>	';
        			htmlView1 +='</p>';
        			htmlView1 +='<div class="ykTlInfo">';
        			htmlView1 +='<p class="ykTlIname">';
        			htmlView1 +=teacherViews[i].name;
    				if(teacherViews[i].studentId==data.studentId){
    					htmlView1 +='<span class="ykTlLike haslike"';
    					htmlView1 +=' id="'+teacherViews[i].teacherId+'"';
    					htmlView1 +='>已收藏</span></p>';
    				}else{
    					htmlView1 +='<span class="ykTlLike"';
    					htmlView1 +=' id="'+teacherViews[i].teacherId+'"';
    					htmlView1 +='>加收藏</span></p>';
    				}
    				
    				htmlView1 +='<p class="ykTlIdesc">';
    				for(var j in teacherViews[i].tag){
    					if(teacherViews[i].tag[j]!=''){
    						htmlView1 +='<span>';
    						htmlView1 +=teacherViews[i].tag[j];
    						htmlView1 +='</span>';
    					}
    				}
    				htmlView1 +='</div><span class="ykTlStatus"></span></li>';
        		}
        		$('#teacherCalendarModel').html(htmlView1);
        		$('#pageText').text(data.pageText);
        		$('#tabTotalPage').val(data.tabTotalPage);
        		$('#teacherCalendarPop').show();
        	}else{
        		Alert.error("错误提示", data.message, "", null);
        	}
        }
    });
}
//日历模式教师列表弹出页 向前翻页
$("body").delegate('#prePage','click',function(){
	var currNum = parseInt($('#tabCurrNum').val())-1;
	if(currNum==0){
		return;
	}else{
		findTeacherForCal($('#tabScheduledDateTime').val(),$('#tabSeaType').val(),$('#tabTeacherId').val(),$('#tabCourseType').val(),currNum);
	}
});
//日历模式教师列表弹出页 向后翻页
$("body").delegate('#nextPage','click',function(){
	var currNum = parseInt($('#tabCurrNum').val())+1;
	if(currNum>parseInt($('#tabTotalPage').val())){
		return;
	}else{
		findTeacherForCal($('#tabScheduledDateTime').val(),$('#tabSeaType').val(),$('#tabTeacherId').val(),$('#tabCourseType').val(),currNum);
	}
});

//日历模式下 约课
function doBookForTeacherTab(){
	if($('#tabOnlineClassId').val()==''){
		return;
	}
	book($('#tabScheduledDateTime').val(),-1,null,$('#tabOnlineClassId').val(),0,$('#tabTeacherName').val(),'',
			$('#mode').val(),$('#seaType').val(),$('#teacherId').val(),$('#week').val(),$('#day').text(),$('#timeStart').text(),$('#timeEnd').text(),$('#courseType').val(),$('#currNum').val(),$('#isPageDo').val(),$('#idx').val());
}
