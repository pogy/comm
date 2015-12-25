	$(".askList li a").click(function(){
		$(this).parent("li").addClass("on").siblings().removeClass("on")
	})	
	var toTopHeight = $(".askList").offset().top;
	console.log(toTopHeight)
	$(window).scroll(function() {		
	    if( $(document).scrollTop() > toTopHeight ){ 
	        if ('undefined' == typeof(document.body.style.maxHeight)) { 
	            var scrollTop = $(document).scrollTop();  
	            $(".askList").css({'position':'absolute','top':scrollTop+'px'});
	        }else{              
	        	$(".askList").addClass("fixed");
	        } 
	    }else{
	        if ('undefined' == typeof(document.body.style.maxHeight)) {    
	        	$(".askList").css({'position':'absolute','top':toTopHeight+'px'});                       
	        }else{  
	        	$(".askList").removeClass("fixed");        
	        }  
	    } 
	    
	    
	    var x = $(window).scrollTop();		 
		//if(x>=40){$(".helpLeft").css({"position":"fixed","top":"0px"});}
		//if(x==100){$(".helpLeft").css({"position":"fixed","top":"20px"});}
		if(x==0){
			//$(".helpLeft").css({"position":"fixed","top":"120px"});
			$(".askList li").eq(0).addClass("on").siblings().removeClass("on");
		}else if(400<=x && x<900){
			$(".askList li").eq(1).addClass("on").siblings().removeClass("on");
		}else if(900<=x && x<1000){
			$(".askList li").eq(2).addClass("on").siblings().removeClass("on");
		}
		else if(2300<=x && x<2600){
			$(".askList li").eq(3).addClass("on").siblings().removeClass("on");
		}else if(3000<=x && x<3300){
			$(".askList li").eq(4).addClass("on").siblings().removeClass("on");			
		}
		else if(3900<=x && x<4200){
			$(".askList li").eq(5).addClass("on").siblings().removeClass("on");
		}
		else if(4200<x){
			$(".askList li").eq(6).addClass("on").siblings().removeClass("on");
		}
	});