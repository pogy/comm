var vipkidHome = vipkidHome || {};
vipkidHome.namespace = function(str){
    var parts = str.split("."),
    parent = vipkidHome,
    i=0,
    l=0;

    if(parts[0]==="vipkidHome"){
        parts = parts.slice(1);
    }
    for(i=0,l=parts.length; i<l;i++){
        if(typeof parent[parts[i]] === "undefined"){
            parent[parts[i]] = {};
        }
        parent = parent[parts[i]];
    }
    return parent;
}
vipkidHome.newhome = {
	init:function(){
		vipkidHome.newhome.indexFocus();
		vipkidHome.newhome.parentWebelive();
		vipkidHome.newhome.tqFun();	
		vipkidHome.newhome.waterflow();
		vipkidHome.newhome.teacherVideo();
		vipkidHome.newhome.processFlowingfun();
		vipkidHome.newhome.backtop();
		vipkidHome.newhome.otherEventFun();
		$(".vList li").bind("mouseenter mouseleave",vipkidHome.newhome.headerWxHover);
		//$(".asklist li a").bind("click",vipkidHome.newhome.videoLinkFun);
		//$(".teacherVideoList li").bind("click",vipkidHome.newhome.videoLinkFun);
		//$("#parentTopVideo a").bind("click",vipkidHome.newhome.videoLinkFun);		
		//$(".otherStuClass li a").bind("click",vipkidHome.newhome.videoLinkFun);
		
	},
	indexFocus:function(){
		if($(".focusWaper").length > 0){
			$(".focusWaper").slide({ 
				titCell:".focusNumber" , 
				mainCell:".focusItem" , 
				effect:"fold", 
				autoPlay:true, 
				delayTime:300 ,
				interTime:3000,
				autoPage:true,
				prevCell:".focusPrev",
				nextCell:".focusNext"
			});

			$(".parentFocus").slide({ 
				titCell:".parentNumber ul", 
				mainCell:".parentItem", 
				effect:"fold", 
				autoPlay:true, 
				delayTime:300,
				interTime:10000,
				autoPage:true,
				prevCell:".parentPrev",
				nextCell:".parentNext"
			});
		}
	},
	headerWxHover:function(){
		var ele = $(this);	
		var wxPopDiv = ele.children('.wxPop');
		if(wxPopDiv.length > 0){
			if(ele.hasClass("wxon")){
				wxPopDiv.hide();
				ele.removeClass("wxon");
			}else{
				wxPopDiv.show();
				ele.addClass("wxon");
			}
		}
	},
	parentWebelive:function(){
		var parentAllNum = $(".parentItem li").length;
		$(".parentNumber").children('span').text(parentAllNum)		
		$(".webelieve li").hover(function(){		
			$(this).children(".weitempra").slideDown()
		},function(){
			$(this).children(".weitempra").slideUp()
		})
	},
	tqFun:function(){
		$(".tqdiv").hover(function(){
			$(".tqSmall").stop().animate({ left:-25 }, 200,function(){
				$(".tqBig").animate({ left: 0 }, 300);
			});		
		},function(){		
			$(".tqBig").stop().animate({ left: -120 }, 300,function(){
				$(".tqSmall").animate({ left:0 }, 200);
			});
		})
	},
	backtop:function(){
		var bthtml = "<div class='backTop'><a href='javascript:;' class='tqlink' onclick=\"window.open('http://vipwebchat.tq.cn/pageinfo.jsp?version=vip&amp;admiuin=8868810&amp;ltype=0&amp;iscallback=1&amp;page_templete_id=86543&amp;is_message_sms=0&amp;is_send_mail=0&amp;action=acd&amp;acd=1&amp;type_code=1','','width=700,height=520')\"></a><a href='javascript:;' class='feedback'></a>";
		$("body").append(bthtml);
		$(window).scroll(function(){  
            if ($(window).scrollTop() > $(window).height()){  
            	$(".feedback").addClass("on")
            }else{  
            	$(".feedback").removeClass("on")
            }  
        }); 
        $(".feedback").click(function(){  
            $('body,html').animate({scrollTop:0},1000);  
            return false;  
        });
	},
	waterflow:function(){
		if($('#diarylist').length>0){
			var $diarylist = $('#diarylist'), cdata = vipkidHome.classDiaries,diarylistTtml;	
			$diarylist.masonry({
				itemSelector : '.waterItem',
			    transitionDuration: '0.8s',
				gutter: 20,
				columnWidth : 300
			});
			for(var i = 0; i < cdata.length; i++){
				diarylistTtml = $("<div class='waterItem'>"+
					"<a href="+cdata[i].url+"  target='blank'><img src='"+cdata[i].img+"' style='height:"+cdata[i].height+"' /></a>"+
					"<p class='waterTitle'><a href="+cdata[i].url+"  target='blank'>"+cdata[i].title+"</a></p>"+
					"<p class='waterTime'>"+cdata[i].date+"</p>"+
					"<div class='waterInfo'>"+
						"<p>"+cdata[i].description+"</p>"+
						"<p class='waterLink'><a href="+cdata[i].url+"  target='blank'>详情&gt;&gt;</a></p>"+
					"</div>"+
				"</div>");			
				$diarylist.append(diarylistTtml).masonry( 'appended', diarylistTtml, true );		
			}
	    }	
	    $('.waterItem').mouseover(function(){
			$(this).css('box-shadow', '2px 0 3px 0 rgba(200,200,200,0.2),-2px 0 3px 0 rgba(200,200,200,0.2),0 -2px 3px 0 rgba(200,200,200,0.2),0 2px 3px 0 rgba(200,200,200,0.2)');
			$(this).find("img").stop().animate({ opacity:.6 }, 100);
		}).mouseout(function(){
			$(this).css('box-shadow', '2px 0 3px 0 rgba(200,200,200,0.5),-2px 0 3px 0 rgba(200,200,200,0.5),0 -2px 3px 0 rgba(200,200,200,0.5),0 2px 3px 0 rgba(200,200,200,0.5)');
			$(this).find("img").stop().animate({ opacity: 1 }, 100);
		});
	},
	teacherVideo:function(){
		var teacherVideoMain = $(".teacherVideoMain");
	    if(teacherVideoMain.length>0){    	
	    	var tdata = vipkidHome.englishteachers,teacherHtml;
	    	teacherHtml = "<ul class='teacherVideoList tabShow clear'>";
	    	for(var i = 0; i < tdata.length; i++){    				
	    		teacherHtml += "<li rel='"+tdata[i].url+"'>"+
					"<div class='teacherVideoPhoto'>"+
						"<img src='"+tdata[i].img+"' />"+
						"<span class='teacherVideoIcon'></span>"+
					"</div>"+
					"<p class='teacherVideoTitle'>"+tdata[i].name+"</p>"+
					"<p class='teacherVideoDesc'>"+tdata[i].introduction+"</p>"+
				"</li>"     		   		
	    		if((i+1)%6==0&& i<(tdata.length-1)){    			
	    			teacherHtml +="</ul><ul class='teacherVideoList tabHide clear'>"
	    		}     		
			}
	    	teacherHtml +="</ul>";
	    	teacherVideoMain.append(teacherHtml)
	    }
	  
	    $(".teacherPage a").click(function(){
	    	var tindex = $(this).index();
	    	$(this).addClass("cur").siblings().removeClass("cur");
	    	teacherVideoMain.children("ul").eq(tindex-1).addClass("tabShow").removeClass("tabHide").siblings().addClass("tabHide").removeClass("tabShow");
	    })
	    $(".tprev").click(function(){    	
	    	var tindex = $(this).siblings("a.cur").index()-1;     	
	    	if(tindex==0){    		
	    		return false;
	    	}else{     		
	    		$(this).siblings("a").eq(tindex-1).addClass("cur").siblings().removeClass("cur")
	    		teacherVideoMain.children("ul").eq(tindex-1).addClass("tabShow").removeClass("tabHide").siblings().addClass("tabHide").removeClass("tabShow");
	    	}    	
	    })
	    $(".tnext").click(function(){    	
	    	var tindex = $(this).siblings("a.cur").index()-1;  
	    	
	    	if(tindex>3){    		
	    		return false;
	    	}else{     		
	    		$(this).siblings("a").eq(tindex+1).addClass("cur").siblings().removeClass("cur");    		
	    		teacherVideoMain.children("ul").eq(tindex+1).addClass("tabShow").removeClass("tabHide").siblings().addClass("tabHide").removeClass("tabShow");
	    	}    	
	    })
	      	
	    /*
		$('.teacherVideoList li').mouseover(function(){
			$(this).css('box-shadow', '2px 0 3px 0 rgba(200,200,200,0.2),-2px 0 3px 0 rgba(200,200,200,0.2),0 -2px 3px 0 rgba(200,200,200,0.2),0 2px 3px 0 rgba(200,200,200,0.2)');
			$(this).find("img").stop().animate({ opacity:.6 }, 100);
		}).mouseout(function(){
			$(this).css('box-shadow', '2px 0 3px 0 rgba(200,200,200,0.5),-2px 0 3px 0 rgba(200,200,200,0.5),0 -2px 3px 0 rgba(200,200,200,0.5),0 2px 3px 0 rgba(200,200,200,0.5)');
			$(this).find("img").stop().animate({ opacity: 1 }, 100);
		});
		*/
	},
	videoLinkFun:function(){
		var videourlid = $(this).attr("rel");
    	var videoHtml ="";
    	videoHtml+="<div class=\"videoPop\"><div class=\"videoArea\">";			
	    videoHtml+="<a class=\"videoCloseIcon\" href=\"javascript:;\"></a>";
    	videoHtml+="<iframe border='none' width='670' height='502' src='http://v.qq.com/iframe/player.html?vid="+videourlid+"&amp;width=670&amp;height=502&amp;auto=1'  scrolling='no'   frameborder='no'></iframe>";    	
    	videoHtml+="</div></div>"
    	videoHtml+="</div></div>"
    	$("body").append(videoHtml);
    	$(".videoPop").show();    
    	$("body").delegate(".videoCloseIcon","click",vipkidHome.newhome.closeVideoPop)    	
	},
	closeVideoPop:function(){
		$(this).parent().parent(".videoPop").remove(); 
	},
	processFlowingfun:function(){
	   $(".flowStuActiveList").css("top","30px");
	   $(".flowStuActiveList li:last").clone().prependTo(".flowStuActiveList");
	   $(".flowStuActiveList li:first").css("opacity",0);
	   $(".flowStuActiveList li:first").animate({opacity:1},800);
       setTimeout(function(){
    	   $(".flowStuActiveList li:last").detach(); 
    	   $(".flowStuActiveList").animate({top:100},800,vipkidHome.newhome.processFlowingfun);
       },2000) 
    },
	otherEventFun:function(){
		$(".waterItem").hover(function(){
	    	$(this).children("img").animate({opacity:0.8},200)
	    },function(){
	    	$(this).children("img").animate({opacity:1},200)
	    })

	    $(".flowPaIcon").click(function(){    	
    		$(this).siblings(".flowParentEwm").show().siblings().not("div").hide();
	    })
	    $("a").click(function() {
			var url = window.location.href;
			if (url.indexOf("channel_id=") > 0 || url.indexOf("channel_keyword=") > 0) {
				var input = url.substr(url.indexOf('?'));
				this.href = this.href + input;
			}
		})
	    
	    $(".flowParentEwm").click(function(){    	
	    	$(this).siblings().show();
	    	$(this).hide();
	    })
	    
	    $(".botBlackClose").click(function(){
	    	$(this).parent(".botBlackBar").hide();
	    })
	   
	    //$(".loginBarIttest").attr("href","http://help.duobeiyun.com/room/test/?result=" + itTestCallBaclURL + "&lang=zh&role=FAMILY&id=" + docCookies.getItem("familyId"));	
	}
}

vipkidHome.englishteachers=[
{
	name:'Alexander S',
	img:'http://resource.vipkid.com.cn//image/6a5f43ff-6c99-404d-a869-14be1495fb3a.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=f016713eibr&auto=1',
	introduction:'Alexander来自科罗拉多州的英语培训师，在亚洲教学超过两年，现居清迈。拥有TEFL双认证，热爱教学。喜爱户外活动：登山、攀岩、漂流和滑雪。',
},
{
	name:'Andrew J',
	img:'http://resource.vipkid.com.cn//image/e251edbb-74a3-4df8-a0a5-76104666229b.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=l0167yn64zx&auto=1',
	introduction:'Andrew老师来自加拿大，在匈牙利获得了硕士学位，精通四国语言（英语，法语，德语和日语）。曾经做过哲学跟政治学的导师。',
},			
{
	name:'Ashley P',
	img:'http://resource.vipkid.com.cn//image/ff201499-e5fc-42b9-9fe4-22051253ceef.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=r0167tsko5u&auto=1',
	introduction:'Ashley在西雅图长大,住在旧金山。在加州大学伯克利分校获得学位。现在居住在泰国清迈，教授泰国的孩子英语。非常喜欢孩子，关注孩子在学习中获得乐趣和知识，从而享受教育。 ',
},			
{
	name:'Brianne B',
	img:'http://resource.vipkid.com.cn//image/1fbc66af-b516-4ad8-8eb4-cbe7c7a075be.jpg',
	url:'',
	introduction:'Brianne来自美国西雅图，有两年的非母语英语教学经验，拥有新闻学学士学位，以及CELTA成人英语教学证书。热爱英语教学。最喜欢的事情之一就是看到学生开心笑，因为他们发现了学习英语的乐趣！',
},			
{
	name:'Brittany L',
	img:'http://resource.vipkid.com.cn//image/7cb9143a-766c-475b-838f-375e362a89a3.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=b0167ydxxrx&auto=1',
	introduction:'Brittany拥有英文和全球学双学士学位，以及商学的辅修学位。在泰国和台湾有超过3年的非母语英语教学经验，热爱亚洲文化。课堂快节奏、充满乐趣是Brittany老师的教学特点，她期待着你的到来！',
},			
{
	name:'Celina K',
	img:'http://resource.vipkid.com.cn//image/469f5fb5-eba8-4c45-ab13-54be349ea903.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=e0167urhl8u&auto=1',
	introduction:'Celina拥有TEFL认证、心理学学士学位。现居纽约市，老家在俄勒冈州。超过10年一对一少儿教育工作经历。认为语言是体现文化差异的重要一点。',
},			
{
	name:'Cody R',
	img:'http://resource.vipkid.com.cn//image/915d327c-07b9-48f9-9844-2908c1304721.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=a0167iyuz1u&auto=1',
	introduction:'Cody持有TESOL证书，并且有美国公立学校教学背景，传媒专业毕业。喜欢孩子，并且喜欢教孩子英语。是个有趣的老师。爱好旅游、徒步穿越和写作。',
},			
{
	name:'Daniel D',
	img:'http://resource.vipkid.com.cn//image/4182f362-75a4-49ed-94ac-78baa32bb7ee.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=i0167lppg61&auto=1',
	introduction:'Daniel老师来自美国纽约，毕业于埃尔迈拉学院，拥有TESOL国际英语教师资格证。他的学生来自世界各地，比如韩国，菲律宾，缅甸和沙特阿拉伯。拥有8年多的英语教学经验，包括4年的教儿童英语教学和4年的成人英语教学。',
},			
{
	name:'David G',
	img:'http://resource.vipkid.com.cn//image/1ccca41c-7b21-45a7-87e7-36417c089acd.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=t0167wj6x6h&auto=1',
	introduction:'David老师来自美国，在阿根廷和墨西哥有4年的英语教学经验。目前David老师居住在韩国釜山，在一所小学教授4-12岁的孩子英语。David老师喜欢与孩子们有关的工作，期待着他们的英语水平每天都会有进步。',
},			
{
	name:'Elijah M',
	img:'http://resource.vipkid.com.cn//image/51dd3f10-51f8-4958-b294-3190e10b59d3.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=h01678ejl1v&auto=1',
	introduction:'Elijah老师来自美国，拥有国际关系学士学位，还拥有6年的英语教学经验，学生年龄从4岁到成人，曾任教于韩国，格鲁吉亚和美国。Elijah老师热爱教学和与学生互动。他喜欢与家人一起运动和看体育比赛。',
},			
{
	name:'Jennifer W',
	img:'http://resource.vipkid.com.cn//image/4469ef8a-aafc-432f-a922-8185b4e9ed20.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=h0167wjdzxj&auto=1',
	introduction:'Jennifer老师来自加拿大。她是一位白衣天使哦，做过13年护士，更厉害的还有超过14年的教学经验，并拥有TESOL国际英语教师资格证。Jennifer老师热爱旅游，去过许多不同的国家，渴望了解独特的文化和风俗。',
},			
{
	name:'Jenny W',
	img:'http://resource.vipkid.com.cn//image/20a5d2f3-6ffe-45e4-a825-cafcbba9930d.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=u0167qmkp8y&auto=1',
	introduction:'Jenny是加拿大本土教师，已经有12年教龄。吸收了不同国家不同文化，在加拿大，韩国，坦桑尼亚，印尼，泰国和香港都教授过英文，本科毕业于生玛丽大学教育学专业。',
},			
{
	name:'Jeri C',
	img:'http://resource.vipkid.com.cn//image/ce50cd6f-2ac4-409a-b588-5b6ab800c96f.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=q0167vxe3ht&auto=1',
	introduction:'Jeri老师出生在加拿大，在学习了英国文学和艺术专业学士学位后又获得了专业国际教育的学位。在加勒比海教学，做过很多志愿工作。在韩国和新加坡也教过学员。喜欢旅游、阅读、喜剧，喜欢陪着家人。',
},			
{
	name:'Josh W',
	img:'http://resource.vipkid.com.cn//image/f8ed222c-bb88-4474-a5c6-e998161c36f7.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=v016736rfno&auto=1',
	introduction:'Josh是美国本土教师，热爱教学，喜爱艺术，会制作动画电视节目。他的课堂活泼，会自己制作绘本。',
},			
{
	name:'Kathryn M',
	img:'http://resource.vipkid.com.cn//image/d30100da-9b77-4ea1-bfb1-6d433a0518bc.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=o016717e8yg&auto=1',
	introduction:'Kathryn老师来自加拿大多伦多，毕业于美国杨百翰大学。她在日本、中国和蒙特利尔有八年多的英语教学经验。非常热衷于将创意引入课堂，寓教于乐。Kathryn老师爱好广泛，喜欢玩音乐，阅读，绘画，游泳，做瑜伽。',
},			
{
	name:'Matt M',
	img:'http://resource.vipkid.com.cn//image/dfd7fc80-8fe7-4440-97c6-49e8de40adb3.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=t0167h9bdbb&auto=1',
	introduction:'Matt老师是来自美国，毕业于北密歇根大学，拥有两个TEFL国际英语教师资格证。Matt老师热爱教育事业，有5年的英语教学经验。Matt老师认为，学习应该是有趣的。他期待着在课堂上与你相见！',
},			
{
	name:'Meghan H',
	img:'http://resource.vipkid.com.cn//image/43a28ae7-a4a8-4ceb-acce-4cc2956f9990.jpg',
	url:'',
	introduction:'Meghan在北卡罗来纳大学教堂山分校获得德语和语言学学士学位,并将去巴塞罗那获得第二语言习得硕士学位。还有在剑桥大学获得的成人英语教学证书。她非常喜欢学习语言，会丹麦语,德语,美国手语,和日语，还很喜欢绘画。',
},			
{
	name:'Melissa K',
	img:'http://resource.vipkid.com.cn//image/170b527a-a4b5-4be7-8f8e-9fc2ad48f89b.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=j0167zhn798&auto=1',
	introduction:'Melissa大学获得音乐教育学位。她在韩国有超过3年的英语教育，学生从幼儿园到上班族都有。2014年在美国获得教师资格证。',
},			
{
	name:'Patricia H',
	img:'http://resource.vipkid.com.cn//image/b66e67a9-0807-4d6f-99f2-ff5aa8891fff.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=p0167vz3m63&auto=1',
	introduction:'毕业于圣地亚哥大学美语学院，在澳大利亚的韦斯利学院留学，TEFL和TESL专业。4年以上少儿教育经验，在南韩教过英语。热爱教学，来自加州，喜爱冲浪和烹饪的老师。',
},			
{
	name:'Rebecca D',
	img:'http://resource.vipkid.com.cn//image/9ff9552d-3cb5-443d-94fc-26413e87e648.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=z0167izfz04&auto=1',
	introduction:'Rebecca拥有人文和社会科学双学士学位，有TESOL和TESL认证。 5年以上英语教龄，学员覆盖亚洲、欧洲、中东和南北美洲。',
},			
{
	name:'Susan M',
	img:'http://resource.vipkid.com.cn//image/f8b983a5-0f3e-45b2-afbe-c887e8c26bf6.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=y0167tdocma&auto=1',
	introduction:'Susan老师从2007年开始一直从事英语教学事业，拥有ESOL（英语作为外语）教学系统开发和双语教育的硕士双学位。在美国、中国、越南、蒙古和泰国，Susan为各个年龄段的孩子和成人教授过英语。',
},			
{
	name:'Tyler H',
	img:'http://resource.vipkid.com.cn//image/62139b58-cccb-45ea-aae3-4bc740f90574.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=w0167m1mn0r&auto=1',
	introduction:'Tyler老师来自美国，拥有新墨西哥大学的语言学士学位、TEFL国际英语教师资格证和TEK幼儿英语教师资格证。拥有丰富的英语教学经验，曾在智利教授3岁至19岁的学生英语三年，以及在美国的中学教授英语两年。',
},			
{
	name:'Zach M',
	img:'http://resource.vipkid.com.cn//image/85c8acd8-6028-4e91-908b-bdaefa216dbc.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=w01676c0vdn&auto=1',
	introduction:'Zach老师来自加拿大，毕业于多伦多大学，拥有生物学和政治学的双学士学位。他热爱英语教学，有丰富的儿童教育工作经验。',
},			
{
	name:'Zarrin M',
	img:'http://resource.vipkid.com.cn//image/f29f12a0-25fc-4084-aa6d-a7821eb51485.jpg',
	url:'http://static.video.qq.com/TPout.swf?vid=z0167cuj2vu&auto=1',
	introduction:'Zarrin 老师拥有4年以上青少儿教育工作经验。非常喜欢英语教学，并且长期参与社区志愿者活动。爱好广泛：游泳、旅行、 散步、看书、烹饪、烘焙和绘画等。',
},
]
vipkidHome.classDiaries = [{
	'title' : 'VIPKID新系统上线｜更多精彩看过来！',
	'date' : '2015-03-18',
	'description' : 'VIPKID全新系统，是一次基于PC端及微信服务号平台的双重改版更新…',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic30.jpg',
	'url' : 'http://t.cn/RA2Ge57',
	'height' : '216px'
},
{
	'title' : 'VIPKID悦读｜经典书籍带来快乐学习',
	'date' : '2015-03-12',
	'description' : '本期为大家推荐《神奇的校车》及《丁丁历险记》两款国外优秀系列图书，均为孩子学习语言及能力培养方面的优秀书籍。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic29.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=205287364&idx=1&sn=f11d484be35b5eab7cf860763f5467c9#rd',
	'height' : '216px'
},
{
	'title' : '应试教育向应用教育转变？看VIPKID如何带来美国小学“能力习得”教育',
	'date' : '2015-03-09',
	'description' : '两会中，应试教育向应用型教育转变，继续被大家提上议题。以“实用”为目的的教育方式是美国小学长久以来培养人才的理念。看看VIPKID如何带来美国小学既快乐，又令能力有所提高的教育模式。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic28.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=205212989&idx=1&sn=5257622cf8e332498ab571bbc578322a#rd',
	'height' : '216px'
},{
	'title' : 'Lane老师专栏｜哈佛大学 vs 北京大学，一个亲历两者学生的有感',
	'date' : '2015-03-06',
	'description' : '有一群非常棒的有着海外留学和生活背景的小伙伴们，他们出众的学习经历是不是也会给家长们带来一些思考？一点启发？',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic27.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=205112520&idx=1&sn=f1b4c88489e7c523a9ca4b05ce93728e#rd',
	'height' : '458px'
},{
	'title' : 'Lane专栏｜七个让孩子终生受益的礼貌小习惯～',
	'date' : '2015-02-05',
	'description' : '当有了小孩，我们的天职就是帮助孩子在这个世界上取得成功。良好的社交礼仪对于建立孩子的自尊自信至关重要，而培养的秘诀就是强调这些好习惯并且坚持下去。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic26.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=203733618&idx=1&sn=73e0b14c241128b22171058b9066dbab#rd',
	'height' : '216px'
},{
	'title' : 'Lane专栏｜还在为孩子不开口说英语发愁？你out辣！',
	'date' : '2015-01-22',
	'description' : '家长担心孩子开口说英语太慢太少，恨不得睡一觉，天一亮，孩子就开始噼里啪啦滴讲英语。殊不知，揠苗助长，弊大于利。来，我们一起和Lane学习一下孩子习得第二语言的规律吧！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic25.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=203270770&idx=1&sn=928a071887aa798bded79bedc85e71fc#rd',
	'height' : '216px'
},
{
	'title' : 'Lane专栏｜宝宝变聪明？请多睡一会儿',
	'date' : '2015-01-15',
	'description' : '多年以前，当我和丈夫决定带着孩子在中国生活的时候，我就预感到会有一些文化差异。比如说吃的东西－我们在小朋友1岁的时候就带着她吃微辣的东西；比如说穿衣服－小女儿经常光着脚丫满地跑；再比如说养宠物－在美国这是很普遍的。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic24.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=203083450&idx=1&sn=a4646e2bd27633153d5ca8854c47571c#rd',
	'height' : '216px'
},
{
	'title' : 'Lane专栏 | 育儿经---爸爸为什么在孩子的成长过程中如此重要？',
	'date' : '2015-01-08',
	'description' : '有爸爸妈妈们的陪伴和爱护，小朋友们正在幸福滴成长。可有的爸爸因为工作忙和小朋友们相处的时间不多，这可是会影响小朋友各方面的发展哦！为什么这么说呢，看完本文粑粑们就会明白啦！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic23.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=202909123&idx=1&sn=03e1b0c1f1f3973395de7bc97d0ba37f#rd',
	'height' : '216px'
},
{
	'title' : '【报名链接来啦】报名V亲子阅读俱乐部，妈妈再也不用担心我的阅读啦！',
	'date' : '2014-12-11',
	'description' : 'V亲子阅读俱乐部第一次招募，1天有200+名粑粑麻麻报名加入，俱乐部短短3天，拥有了4个分群。更有教育专家和明星麻麻加入。家长和孩子在群里共读英文书、讨论英文学习、和外教一起上阅读复习课程......不亦乐乎！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic22.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=202045618&idx=1&sn=1723649a71a835fa752c761149cd51dc#rd',
	'height' : '216px'
},
{
	'title' : 'Lane专栏｜美国孩子在家做的亲子小实验（附图附步骤）',
	'date' : '2014-11-27',
	'description' : '我们的“科学达人”Lane今天为我们奉上了科学试验大餐——神奇的蛋壳，百变的“塑料牛奶”还有超级无敌的发霉面包片～还等什么，让我们一起动手动脑做实验吧！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic21.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=201703138&idx=1&sn=3d1ead3fcd2280ca0cc08c8778350360#rd',
	'height' : '216px'
},
{
	'title' : 'Lane 专栏｜理财，要从娃娃抓起哦—美国辣妈看“零花钱”',
	'date' : '2014-11-20',
	'description' : '关于孩子“零花钱”的问题，相信很多爸爸妈妈对此都有疑问：零花钱该不该给？给多少？怎么给？',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic20.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=201554002&idx=1&sn=3708e3394fd829cb4688cf2195024b4d#rd',
	'height' : '216px'
},
{
	'title' : 'Lane专栏｜如何和孩子悦读“自然拼读书籍”，既开心又学知识？',
	'date' : '2014-11-12',
	'description' : '《零基础，照样和孩子畅读英文书》告诉了家长如何选择阅读材料，以及简单的和孩子互动阅读方法。粑粑麻麻们又迫不及待提出第二个问题：自然拼读书籍如此重要，如何能更好利用这些材料，帮助孩子提升各种阅读技能，同时又让孩子觉得有趣呢？',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic19.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=201396325&idx=1&sn=690d43ff2b1a675b991bd63716bb8b5c#rd',
	'height' : '216px'
},
	{
	'title' : '记一次产品研发大会——讲述VIPKID‘家长端’上线前的故事',
	'date' : '10月23日',
	'description' : '10月23日，我们的微信家长端上线啦！产品部门的童鞋特意为大家写了一封‘信’。我读后差点晕过去，真是浓浓的互联网范儿啊！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic18.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=201028299&idx=1&sn=f3f4f4b578872a81286f375d2e1b2e22#rd',
	'height' : '200px'
},
{
	'title' : 'VIPKID原创视频新鲜出炉—《“小鬼当家”之万圣节》',
	'date' : '10月22日',
	'description' : '对于孩子们来说，快乐的周末的最重要的一个组成部分，那就是在创新工场“希望树”活动室里上演的万圣节版《小鬼当家》。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic17.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=201010641&idx=1&sn=1086b4e52099df184b0e6813e596ddc0#rd',
	'height' : '200px'
},
{
	'title' : '家长会回顾—美国辣妈教分享“培养适应未来的孩子”',
	'date' : '10月21日',
	'description' : '上周日，20位家长来参加由VIPKID组织的Parent workshop ，辣妈Lane生动又客观地向家长们展现了“真实的美式教育”。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic16.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=201005338&idx=1&sn=16f798cba26e8dad0e6ac971453ccba5#rd',
	'height' : '200px'
},
	{
	'title' : 'VIPKID号外| 始于创业，CEO小米出演创新工场五周年宣传片',
	'date' : '09月26日',
	'description' : '致那些永远以非同凡想视角看待事物的人,那些不甘于循规蹈矩，而一直追逐理想的人。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic15.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200851022&idx=1&sn=defdde7a05167c1e3a9e4e71a50d5837#rd',
	'height' : '170px'
},
{
	'title' : 'VIPKID 小学员 Mggie, Dora, Susie 和外教一起High翻天！英语课堂秀一秀',
	'date' : '09月26日',
	'description' : '本期，我们为大家展示几位VIPKID女孩学员，她们进步很快，优秀得不得了。同时，在勇气和想法方面，也是巾帼不让须眉哦！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic14.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200843811&idx=1&sn=e651ae56a658d20dc109e47926731e93#rd',
	'height' : '152px'
},
	{
	'title' : '有趣、有范儿！VIPKID老师，不给自己的人生下定义',
	'date' : '09月05日',
	'description' : '小编耗费1周时间，视频访谈了在世界各地的老师。访谈中，老师们丰富有趣的生活让我们非常地惊奇，迫不及待地想让大家一睹为快。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic13.jpg',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200686258&idx=1&sn=5b797e1b9d7c247abc2c5213399cc3fd#rd',
	'height' : '228px'
},
{
	'title' : 'VIPKID —“Teamwork Activity in August”',
	'date' : '08月04日',
	'description' : '8月2日，VIPKID的小学员们变身小厨师，和外教老师一起玩转“面团”，制作了属于自己的小蛋糕，惊呆了粑粑麻麻。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic10.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200411715&idx=1&sn=d55a4f66ba16606071ad76b6ef49cf4b#rd',
	'height' : '228px'
},
{
	'title' : 'VIPKID原创｜孩子不听话？外教教你三个实用妙招',
	'date' : '07月15日',
	'description' : '上周日，Jeremy老师为VIPKID的家长们做了第一次Parent Workshop，主题是《How to Communicate with Kids》',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic11.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200271589&idx=1&sn=cb0086939869b18258b11139da3d516d#rd',
	'height' : '175px'
},
{
	'title' : 'VIPKID —“Teamwork Activity in July”',
	'date' : '07月11日',
	'description' : '7月6日，阳光明媚，VIPKID的小学员们在外教老师的带领下开展了一次以“团队精神”为主题的拓展活动。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic12.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200271010&idx=1&sn=5a1040d655d1fb55fab4674271d78963#rd',
	'height' : '190px'
},
{
	'title' : 'VIPKID六一产品发布会现场视频新鲜出炉啦！',
	'date' : '06月30日',
	'description' : '发布会当天，VIPKID按照美国boy scouts 童子军文化传统为小朋友准备了丰富多彩的任务。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic1.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200184402&idx=1&sn=97d2c0055debc8d88198b60b1144d2d3#rd',
	'height' : '186px'
},
{
	'title' : 'VIPKID原创 | 美国专家Lane答家长问-孩子不自信？谈谈孩子自尊心',
	'date' : '06月13日',
	'description' : '自尊自信来自一个人内心的归属感，相信自己能行，知道我们对这个世界的贡献是有价值的。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic7.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200203791&idx=1&sn=9e06f026bd13750bfefa24f152d77d2a#rd',
	'height' : '240px'
},
{
	'title' : 'VIPKID原创 | 美国专家Lane答家长问-孩子不听话？来点“小规矩”吧！',
	'date' : '06月10日',
	'description' : 'Lane特别开心的给我们分享了她自己的“育儿经”，一起来看看吧！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic9.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200197860&idx=1&sn=fd14a9518b5568a006b2fc3eb80731cc#rd',
	'height' : '240px'
},
{
	'title' : '复活节精彩VIDEO新鲜出炉啦！',
	'date' : '05月05日',
	'description' : '一起看看VIPKID复活节活动的精彩片段吧！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic2.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200133027&idx=1&sn=da240c5970ecb7b1a7e27b5d1351791e#rd',
	'height' : '221px'
},

{
	'title' : '老师的快乐就是这么简单！',
	'date' : '03月25日',
	'description' : '老师的快乐就是看到孩子们的笑容，就是这么简单!',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic4.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200072744&idx=1&sn=7d4955bb8d05182e0d93ab956ae8b31f#rd',
	'height' : '203px'
},
{
	'title' : '小朋友们，和小米老师一起来做线上课程热身操吧！',
	'date' : '03月19日',
	'description' : 'VIPKID实验班开班啦，小朋友们准备好了么？快来和小米老师一块儿做热身操吧！',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic6.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200064188&idx=1&sn=72319df190b7c20d2dd20f18fbfc2941#rd',
	'height' : '198px'
},
{
	'title' : 'VIPKID家庭大联欢 | I AM SPECIAL！',
	'date' : '03月15日',
	'description' : '认识独一无二的自己，做最好的自己！"',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic3.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200058130&idx=1&sn=520503f047afa810e1c6bbe9d011b987#rd',
	'height' : '175px'
},
{
	'title' : '两个比一个好？',
	'date' : '03月02日',
	'description' : '看到两个娃娃一起快乐学习的场景，小编也好想生两个娃娃，你们呢？',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic8.png',
	'url' : 'http://mp.weixin.qq.com/mp/appmsg/show?__biz=MzA5NDMxODgxNg==&appmsgid=200037296&itemidx=1&sign=f652873137a6de35b5cedc8fa5e61d42#wechat_redirect',
	'height' : '215px'
},
{
	'title' : '英文课上的甜蜜时刻！',
	'date' : '02月20日',
	'description' : 'Theo扮演了Bear，Lily爱上了Muffin，Crystal造出了I don\'t like, Lovely学会了pepperoni。',
	'img' : 'http://resource.vipkid.com.cn/static/images/home_portal/classdiary/pic5.png',
	'url' : 'http://mp.weixin.qq.com/s?__biz=MzA5NDMxODgxNg==&mid=200023508&idx=1&sn=37d8eb620da5f322ba9c84fc99a79fef#rd',
	'height' : '216px'
}]
vipkidHome.newhome.init();
