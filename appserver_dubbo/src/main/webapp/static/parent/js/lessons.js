$(function(){
	var rowNum = 5;
	var totalPage = (parseInt($("#totalRecords").val()) + rowNum -1) / rowNum;
	var totalRecords =parseInt($("#totalRecords").val());
	var pageNo = parseInt($("#currNum").val());
	if(!pageNo){pageNo = 1;}
	//生成分页
	kkpager.generPageHtml({
		pno : pageNo,
		total : totalPage,//总页码
		totalRecords : totalRecords,//总数据条数
		getLink : function(currNum){
			return $("#links").attr("href") + "?currNum="+currNum+"&rowNum="+rowNum;
		}
	});
});