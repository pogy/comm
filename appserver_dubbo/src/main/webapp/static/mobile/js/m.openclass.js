$(function(){
	//菜单列表
	$('.filterType').click(function () {		
		var oHeight = document.documentElement.clientHeight;
		var oBack = getScrollHeight();
		$('.downMenu').css({ 'height': oBack-44});
		$('.downMenubg').css({ 'height': oBack-44});
		$('.downMenu').show();	
	});
	
	//下拉框选择
	if($('.downMenu').length!=0){
		$('.downMenu ul li a').click(function(){
			console.log($(this).text())
			$(this).parents(".downMenu").toggle();
		});
	}

	$(".handle").click(function(){
		$('#classpop').show();
	})
	
	$(".time").each(function(){
		var timetext = $(this).text().substr(5, 50)
		$(this).text(timetext)
	})
	
	
//	var closeMenu = $("#downMenu");
//	new FastClick(closeMenu);
	
	window.addEventListener('load', function() {
		new FastClick(document.getElementById('classpop'))	
		document.addEventListener('click', function(event) {
			if (event.target.className.toLowerCase() === 'classpopclose') {				
				$("#classpop").hide();
			}
		}, false);
		
		
		new FastClick(document.getElementById('downMenu'))
		document.addEventListener('click', function(event) {
			if (event.target.className.toLowerCase() === 'downmenubg') {				
				$(".downMenu").hide();
			}
		}, false);

	}, false);
	
	
});
$(window).scroll(function () {	
	if($(window).scrollTop() > 44){
		$('.downMenu ul').css({"top":0})
	}else{
		$('.downMenu ul').css({"top":"44px"})
	}
});
/*取文档内容实际高度*/
function getScrollHeight() {
    return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
} 




/*
 * 
 * function closeFilter(){
	$('.downMenu').hide();
}

function hidePop(){
	$('#classpop').hide();
}

var domh1 = document.getElementsByTagName("h1")[0];
var dmbg = document.getElementById("downMenubg");
function closeDownlist(event) {
	closeFilter();
	event.preventDefault();
}

dmbg.addEventListener("touchstart", closeDownlist(event), false);
domh1.addEventListener("touchstart", closeDownlist(event), false);

var classpopdiv = document.getElementById("classpop");
function closeTipspop(event) {
	hidePop();
    event.preventDefault();
}
classpopdiv.addEventListener("touchstart", closeTipspop(event), false);
*/

