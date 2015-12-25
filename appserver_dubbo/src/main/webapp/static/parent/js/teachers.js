$(function(){
	var rowNum = 10;
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
			return $("#links").attr("href") +"?courseType="+$('#courseType').val()+"&tabSign="+$('#tabSign').val()+"&timeWeek="+$('#timeWeek').val()+"&teacherName="+$('#teacherName').val()+"&seachSign="+$('#seachSign').val()+"&rowNum="+rowNum+"&currNum="+currNum;
		}
	});
});

function queryByParams(courseType,tabSign,timeWeek,seachSign,rowNum,currNum){
	var teacherName;
	if(seachSign==1){
		if(tabSign==-1){
			teacherName=$('#classSeachCon').val();
		}else{
			teacherName=$('#classSeachCon1').val();
			tabSign=-1;
		}
		teacherName=teacherName=='按姓名查找老师'?'':teacherName;
	}else{
		teacherName='';
	}
	window.location.href=$("#links").attr("href") +"?courseType="+courseType+"&tabSign="+tabSign+"&timeWeek="+timeWeek+"&teacherName="+teacherName+"&seachSign="+seachSign+"&rowNum="+rowNum+"&currNum="+currNum;
}