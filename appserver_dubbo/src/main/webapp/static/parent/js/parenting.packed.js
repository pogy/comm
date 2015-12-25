//学习端教室地址前缀
var classroom_path = "/learning/classroom/";
var replay_path = "/adventureLogs/replay/";

//var classroom_prefix = "http://learning.vipkid.com:8043/learning/classroom/";
	
$(".studyProcess li.third").hover(function(){
	if (parseInt($(this).find('strong').text()) <= 0) {
		return;
	}
    if($(this).hasClass('on')){
        $(this).removeClass('on');        
    }else{
        $(this).addClass('on');        
    }
})

var ums_timeout;
$(".uMedalStar a").mouseleave(function() {
	ums_timeout = setTimeout(function() {
        $(".smDownDiv").hide();
        $(".uMedalStar a").removeClass("cur");
    }, 200);
    
}).mouseenter(function() { 
	clearTimeout(ums_timeout);
    if($(this).hasClass("uStar")){
    	$(".smDownDiv").hide();
    	$(".uMedalStar a").removeClass("cur");
    	$("#smDownDivStar").fadeIn(200);
    	$(this).addClass("cur");
    }else{
    	$(".smDownDiv").hide();
    	$(".uMedalStar a").removeClass("cur");
    	$("#smDownDivMedal").fadeIn(200);
    	$(this).addClass("cur");
    }    
    $(".smDownDiv").mouseenter(function() {
        clearTimeout(ums_timeout);
    }).mouseleave(function() {
    	ums_timeout = setTimeout(function() {
        	$(".smDownDiv").stop().hide();
        	$(".uMedalStar a").removeClass("cur");
        }, 0);
    });
});


$(".uChange").click(function(){
    $(".uChangePop").show().animate({opacity: "1",left: "180px"},200);
})

function hideChangePop(){
    var fideOutCp = $(".uChangePop").animate({opacity: "0",left: "80px"},200).hide(200)
}

$(".uChangeClose").bind('click',hideChangePop);
$(".uChangeHandle a").bind('click',hideChangePop);

$(".uChangeArea a").click(function(){
    $(this).addClass('on').siblings().removeClass('on');
})


$("#fClassTab span").click(function(){ 
    var index = $(this).parent().children().removeClass().index($(this).addClass("cur"));
    $('.classTabText').removeClass("tabShow").addClass("tabHide").eq(index).removeClass("tabHide").addClass("tabShow");
})
$("#teacherInventList span").click(function(){ 
    var index = $(this).parent().children().removeClass().index($(this).addClass("cur"));
    $('.teacherTabCon').removeClass("tabShow").addClass("tabHide").eq(index).removeClass("tabHide").addClass("tabShow");
})

//搜索相关
$(".classSeachInput").blur(function() {
    $(this).parent().removeClass('on');
    if ($.trim($(this).val()) == "") {
        $(this).val("按姓名查找老师")
    }
}).focus(function() {
    if ($.trim($(this).val()) == "按姓名查找老师") {
        $(this).val("")        
    }
    $(this).parent().addClass('on');
});



$("#teacherModelSeach").bigAutocomplete({
    width:258,
    url:'/parent/listTeachersView?courseType='+$('#courseType').val(),
    callback:function(data){
        //$("#teacherModelSeach").val(data.title);
       // alert(data.id);
    	window.location.href=$("#links").attr("href") +"?mode=1&seaType=-1&teacherId="+data.id+"&week="+$('#week').val()+"&day="+encodeURI(encodeURI($('#day').text()))+"&timeStart="+encodeURI(encodeURI($('#timeStart').text()))+"&timeEnd="+encodeURI(encodeURI($('#timeEnd').text()))+"&courseType="+$('#courseType').val()+"&currNum=1&isPageDo=-1&idx=0&teacherName="+data.title;
       // queryByParams(1,1,data.id,$('#week').val(),$('#day').text(),$('#timeStart').text(),$('#timeEnd').text(),$('#courseType').val(),1,-1,0);
    }
});



$("#classTeacherSearch").bind("click",function() { if(checkInputEmpty()) searchTeacherName()});
$("#teacherModelSeach").keydown(function(e) { if (e.keyCode == 13) { $("#classTeacherSearch").click(); }});

function searchTeacherName(tureInput){
    console.log(encodeURI(encodeURI($("#teacherModelSeach").val())));
    window.location.href=$("#links").attr("href") +"?mode=1&seaType=-1&teacherId=-1&week="+$('#week').val()+"&day="+encodeURI(encodeURI($('#day').text()))+"&timeStart="+encodeURI(encodeURI($('#timeStart').text()))+"&timeEnd="+encodeURI(encodeURI($('#timeEnd').text()))+"&courseType="+$('#courseType').val()+"&currNum=1&isPageDo=-1&idx=0&teacherName="+encodeURI(encodeURI($("#teacherModelSeach").val()));
}

function checkInputEmpty() {
    var tureInput = $(".classSeachInput");
    String.prototype.Trim = function() { return this.replace(/^\s+|\s+$|(?:^ )+|(?: $)+/g, ""); }
    var keywords = $.trim(tureInput.val())
    if (keywords == "" || $.trim(tureInput.val()) == "按姓名查找老师") {
      tureInput.attr("style", "background-color:#F5EDD8");
      setTimeout(function() { tureInput.attr("style", ""); }, 200);
      setTimeout(function() { tureInput.attr("style", "background-color:#F5EDD8"); }, 300);
      setTimeout(function() { tureInput.attr("style", ""); }, 500);      
      return false;
    }
    return true;
}

var onlineClassCancleId;
var teacherNametext;
var onlineClassTime;
var my_timeout;
$(".stShow a").mouseleave(function() {
    my_timeout = setTimeout(function() {
        $(".teacherInfo").hide();
    }, 200);
    //$(".stShow a").unbind('mouseleave');
}).mouseenter(function() {
    clearTimeout(my_timeout);
    var staW = $(this).width();
    if(staW>89){
        staW=89;
    }
    var posy = $(this).position().top;
    var posx = $(this).position().left;
    var tinfoW = $(".teacherInfo").width();
    if(posx >490){
        $(".teacherInfo").addClass('pointerL');
        $(".teacherInfo").stop().attr("style","left:"+(posx-tinfoW-35)+"px;top:"+(posy-12)+"px;display:block;");
    }else{
        $(".teacherInfo").removeClass('pointerL').stop().attr("style","left:"+(posx+staW+20)+"px;top:"+(posy-12)+"px;display:block;");
    }
    $(".teacherInfo .teacherTt").html($(this).children(".indexCtime").text() + " " + $(this).children(".indexCname").text());
    $("#cname").text($(this).attr("rel"));
    $("#enterClassRoom").attr("href", $('#learningUrl').val() + classroom_path + $(this).attr("id"));
    onlineClassCancleId=$(this).attr("id");
    teacherNametext=$(this).children('.indexCname').text();
    onlineClassTime=$(this).children('.fullTime').text();
    showLink(onlineClassTime);
    $(".teacherInfo").mouseenter(function() {
        clearTimeout(my_timeout);
    }).mouseleave(function() {
        my_timeout = setTimeout(function() {
            $(".teacherInfo").stop().hide();
        }, 0);
    });
    
});


$(".stHas a").mouseleave(function() {
    my_timeout = setTimeout(function() {
        $(".teacherInfo").hide();
    }, 200);
}).mouseenter(function() {
    clearTimeout(my_timeout);
    var staW = $(this).width();
    if(staW>89){
        staW=89;
    }
    var posy = $(this).position().top;
    var posx = $(this).position().left;
    var tinfoW = $(".teacherInfo").width();
    if(posx >490){
        $(".teacherInfo").addClass('pointerL');
        $(".teacherInfo").stop().attr("style","left:"+(posx-tinfoW-35)+"px;top:"+(posy-12)+"px;display:block;");
    }else{
        $(".teacherInfo").removeClass('pointerL').stop().attr("style","left:"+(posx+staW+10)+"px;top:"+(posy-12)+"px;display:block;");
    }
    $(".teacherInfo .teacherTt").html($(this).children("span")[0].innerText + " " + $(this).children("span")[1].innerText);
    $("#cname").text($(this).attr("rel"));
    $("#replayOnlineClass").attr("href",$('#learningUrl').val() + replay_path + $(this).attr("id") + "/home");
    $('#onlineClassCancle').text('');
	$('#enterClassRoom').text('');
    $('#replayOnlineClass').text('查看回放');
    $(".teacherInfo").mouseenter(function() {
        clearTimeout(my_timeout);
    }).mouseleave(function() {
        my_timeout = setTimeout(function() {
            $(".teacherInfo").stop().hide();
        }, 0);
    });
    
});


function showLink(time){
	$('#onlineClassCancle').text('');
	$('#enterClassRoom').text('');
	$('#replayOnlineClass').text('');
	$.ajax({
        type: "GET",
        url: '/parent/DateTimeForHome',
        data: {
			'time':time
			},
        success: function(data){
        	if(data=='3'){
        		$('#onlineClassCancle').text('取消课程');
        		$('#enterClassRoom').text('进入教室');
        	}else if(data=='1'){
        		$('#onlineClassCancle').text('取消课程');
        	}else if(data=='2'){
        		$('#enterClassRoom').text('进入教室');
        	}
        }
    });
}


function cancelOnlineClass(){
	Alert.confirm("取消提示", "确定取消"+teacherNametext+"在"+onlineClassTime+"的课程吗？(如果24小时内取消约课，课时-1)", "取消预约", "再想想", function(){
		doCancel(onlineClassCancleId)}, null);
}
function doCancel(onlineClassCancleId){
	$.ajax({
        type: "POST",
        url: '/parent/cancelOnlineClass',
        data: {
			'oldOnlineClassId':onlineClassCancleId
			},
        success: function(data){
        	if(data==''){
        		Alert.info("NO", "<p class='tc'>恭喜你，取消成功</p>", "知道了", function(){window.location.reload()});
        	}else{
        		Alert.error("错误提示", data, "知道了", null);
        	}
        }
    });
}

  
/*约课老师和星期跟随滚动*/
var toTopHeight;
if($(".yuekeTeacherArea").length > 0){
    toTopHeight = $(".yuekeTeacherArea").offset().top;
}  
//监听页面滚动  
$(window).scroll(function() {  
    if( $(document).scrollTop() > toTopHeight-20 ){ 
        if ('undefined' == typeof(document.body.style.maxHeight)) { 
            var scrollTop = $(document).scrollTop();  
            $(".yuekeTeacherArea").css({'position':'absolute','top':scrollTop+'px'});  
            $(".ylTlTableHead").css({'position':'absolute','top':scrollTop+'px'});  
            
        }else{              
            $(".yuekeTeacherArea").addClass("teacherlistFixed");  
            $(".ylTlTableHead").addClass("ylTlTableFixed");           
        }  
    }else{ 
    	
        if ('undefined' == typeof(document.body.style.maxHeight)) {    
            $(".yuekeTeacherArea").css({'position':'absolute','top':toTopHeight+'px'}); 
            $(".ylTlTableHead").css({'position':'absolute','top':toTopHeight+'px'});             
        }else{  
            $(".yuekeTeacherArea").removeClass("teacherlistFixed"); 
            $(".ylTlTableHead").removeClass("ylTlTableFixed");             
        }  
    } 
});
//预约课程日历模式和教师模式切换
$("#teacherYuekeList span").click(function(){
    var index = $(this).index();
    $(this).addClass("cur").siblings().removeClass("cur");
    $(".yuekeWaper").children(".yuekeCon").eq(index).addClass("tabShow").removeClass("tabHide").siblings().addClass("tabHide").removeClass("tabShow");
});
//DIV模拟SELECT
jQuery.selectToDiv = function(divselectid,inputselectid) {
    var downSelectTime;
    var inputselect = $(inputselectid);
    $(divselectid).click(function(event){
        event.stopPropagation();
        var ul = $(divselectid).children('ul');
        var tbar = $(divselectid).children('strong');
        if(ul.css("display")=="none"){
            ul.show();
            tbar.addClass('select');
        }else{
            ul.hide();
            tbar.removeClass('select');
        }  
        
        $(this).mouseenter(function() {
            clearTimeout(downSelectTime);
        }).mouseleave(function() {
            downSelectTime = setTimeout(function() {
                ul.stop().hide();
                tbar.removeClass('select');
            }, 0);
        });      
    });
    $(divselectid+" ul li a").click(function(){
        var txt = $(this).text();
        $(divselectid).children('strong').html(txt);
        var value = $(this).attr("rel");
        inputselect.val(value);
        $(divselectid).children('ul').hide();
        $(divselectid).children('strong').removeClass('select');        
    });
};

$.selectToDiv("#teacherListAll","#teacherListAllValue");
$.selectToDiv("#teacherListMy","#teacherListMyValue");

$.selectToDiv("#classViewTeacher","#classTeacherValue");
$.selectToDiv("#classViewWeek","#classWeekValue");
$.selectToDiv("#classViewCourse","#classTypeValue");

$.selectToDiv("#classTimeOne","#classTimeOneValue");
$.selectToDiv("#classTimeTwo","#classTimeTwoValue");
$.selectToDiv("#classTimeThr","#classTimeThrValue");

$(".searchTeacherDel").click(function(){
	window.location.href=$("#links").attr("href") +"?mode="+$('#mode').val()+"&seaType=3&teacherId=-1&week="+$('#week').val()+"&day="+encodeURI(encodeURI($('#day').text()))+"&timeStart="+encodeURI(encodeURI($('#timeStart').text()))+"&timeEnd="+encodeURI(encodeURI($('#timeEnd').text()))+"&courseType="+$('#courseType').val()+"&currNum=1&isPageDo=-1&idx=0&teacherName=";
    $(this).parent().hide().siblings('#classViewTeacher').show();
})
$(".classViewModel span").click(function(){
    $(this).addClass('cur').siblings().removeClass('cur');
})
$("body").delegate(".ykTlAmore","click",function(event){
	event.stopPropagation();
})	
$("body").delegate(".ykTlLike","click",function(event){	
    var _this = $(this);
    var teacherId = _this.attr("id")
    if(_this.hasClass('haslike')){
        _this.text("加收藏")
        _this.removeClass("haslike")
    }else{
         _this.text("已收藏")
        _this.addClass("haslike")
    }
    $.ajax({
        type: "POST",
        url: '/parent/doCollect',
        data: {
			'teacherId':teacherId
			},
        success: function(data){
        	if(data.errMessage==''){
        		Alert.info("NO", data.message, "知道了", null);
        	}else{
        		Alert.error("NO", data.errMessage, "知道了", null);
        	}
        }
    });
    event.stopPropagation();
})

$("body").delegate("#teacherCalendarModel li","click",function(){	
	var onlineClassid = $(this).attr("id");
	var name = $(this).find("img").attr("alt");	
	$('#tabOnlineClassId').val(onlineClassid);
	$('#tabTeacherName').val(name);
})

$(".mcPlayIcon").click(function(){
	var videoUrl = $(this).attr("id")
	var videoWidth = "500";
	var videoHeight = "400";
	var videoPlayerUrl ="http://resource.vipkid.com.cn/static/libraries/flvplayer/flvplayer.swf";
	var videoHtml = "";
	videoHtml+="<div class=\"tipsPop tipsPopOpen\" id=\"videoPlayer\">"
	videoHtml+="<div class=\"tipsCon\"><object codebase=\" http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0\" width=\""+videoWidth+"\" height=\""+videoHeight+"\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\">";
	videoHtml+="<param name=\"movie\" value=\""+videoPlayerUrl+"\" />";
	videoHtml+="<param name=\"quality\" value=\"high\" />";
	videoHtml+="<param name=\"allowFullScreen\" value=\"true\" />";
	videoHtml+="<param name=\"FlashVars\" value=\"vcastr_file="+videoUrl+"&IsAutoPlay=1&IsContinue=1\">"
	videoHtml+="<embed src=\""+videoPlayerUrl+"\" allowfullscreen=\"true\" flashvars=\"vcastr_file="+videoUrl+"&IsAutoPlay=1&LogoUrl=http://resource.vipkid.com.cn/newhome/logo.png\" quality=\"high\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" type=\"application/x-shockwave-flash\" width=\""+videoWidth+"\" height=\""+videoHeight+"\"></embed>";
	videoHtml+="</object><a href=\"javascript:;\" class=\"closeVideoIcon\"></a></div>";
	videoHtml+="<p class=\"tipsBg\"></p>";
	videoHtml+="</div>";
	$("body").append(videoHtml);
	$("#videoPlayer").show();
})

$("body").delegate(".closeVideoIcon","click",function(){	
	$("#videoPlayer").remove();
})


if($("#currNum").val()=="1"){
	$(".ykTlPagePrev").addClass("gray");
	$(".ykTlPagePrev").text("首页");
}else if($("#totalPage").val()==$("#currNum").val()){
	$(".ykTlPageNext").addClass("gray");
	$(".ykTlPageNext").text("尾页");
}



function isSurportvideo() {
  return !!document.createElement('video').canPlayType; // boolean
}
if (!isSurportvideo) {
    $(".videoControlIcon").hide();
}
var guideVideoItem = $("#newStudentVideoItem");
var guideVideoIcon = $(".videoControlIcon span");
$(".videoControlIcon a").click(function(){
	if (guideVideoIcon.hasClass("show")) {
		guideVideoItem.trigger("play");
		guideVideoIcon.removeClass("show");
	} else {
		guideVideoItem.trigger("pause");
		guideVideoIcon.addClass("show");
	}
})

