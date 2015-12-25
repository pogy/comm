//返回顶部
$(function(){
	$('body').append('<a href="#" title="回到顶部" class="msGoTop">回到顶部</a>');
})
function returnTop() {
	var oScroll = getScrollTop();
	if (oScroll+getClientHeight() > getScrollHeight()-50) {
		$('.msGoTop').animate({ opacity: '1' }, 800, 'ease-in-out', function () { $('.msGoTop').show(); });
		$('.regEntrance').css('position','relative');
	}else if (oScroll > $(window).height()) {
		$('.msGoTop').animate({ opacity: '1' }, 800, 'ease-in-out', function () { $('.msGoTop').show(); });
		$('.regEntrance').css('position','fixed');
	} else {
		$('.msGoTop').animate({ opacity: '0' }, 800, 'ease-in-out', function () { $('.msGoTop').hide(); });
		$('.regEntrance').css('position','fixed');
	}
}
returnTop();
var navScrollTop =$(".navMain").scrollTop()

$(window).scroll(function () {
	returnTop();
	if($(window).scrollTop() > navScrollTop){
		$("nav").addClass("fixed")
	}else{
		$("nav").removeClass("fixed")
	}
});
$(function(){
	//菜单列表
	$('.mainMenu').click(function () {
		$('html').addClass('sift-move');
		//var oHeight = getClientHeight() - 100;
		var oHeight = document.documentElement.clientHeight;
		var oBack = getScrollHeight();
		$('.scrBack').css({ 'height': oHeight });
		$('.scrRolling').css({ 'height': oHeight-88 });
		$('.scrDown').css({ 'height': oBack });
		$('#listLeft').show();	
	});
	$('.scrDown').click(function () {
		closeLeft();
	});
	$('.pageNum').click(function () {
		$('.pageSelect').trigger('click');
	});
	
	//下拉框选择
	if($('.navMain').length!=0){
		$('.navMain span').click(function(){
			$(this).next().toggle();
			$(this).parents('.navMain').find('em').toggleClass('active');
		});
	}
	//选项卡
	$('.tabUp li').click(function(){
		$(this).addClass('active').siblings().removeClass('active');	
		$(this).parents('.tabUp').next().find('.tabList').eq($(this).attr('dome-num')).show().siblings().hide();
	});
	
	//选项卡
	$('.regEntrance .close').click(function(){
		$('.regEntrance').hide();
	});
});
function closeLeft(){
	$('html').addClass('sift-back');
	$('#listLeft').hide();
	$('html').attr('class', '');
}
/*取窗口滚动条高度*/
function getScrollTop() {
    var scrollTop = 0;
    if (document.documentElement && document.documentElement.scrollTop) {
        scrollTop = document.documentElement.scrollTop;
    }
    else if (document.body) {
        scrollTop = document.body.scrollTop;
    }
    return scrollTop;
}


/*取窗口可视范围的高度*/
function getClientHeight() {
    var clientHeight = 0;
    if (document.body.clientHeight && document.documentElement.clientHeight) {
        var clientHeight = (document.body.clientHeight < document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
    }
    else {
        var clientHeight = (document.body.clientHeight > document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
    }
    return clientHeight;
}

/*取文档内容实际高度*/
function getScrollHeight() {
    return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
} 



var vipkidMobile = vipkidMobile || {};
vipkidMobile.namespace = function(str){
    var parts = str.split("."),
    parent = vipkidMobile,
    i=0,
    l=0;

    if(parts[0]==="vipkidMobile"){
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

vipkidMobile.wwwmobile = {
	init:function(){
		vipkidMobile.wwwmobile.waterflow();
		vipkidMobile.wwwmobile.teacherVideo();
		$(".commonAsk li span a").bind("click",vipkidMobile.wwwmobile.videoLinkFun);
		$(".teacherList li").bind("click",vipkidMobile.wwwmobile.videoLinkFun);
		$(".studentVideo li a").bind("click",vipkidMobile.wwwmobile.videoLinkFun);
	},
	waterflow:function(){
		if($('#classListArea').length>0){
			var $classListArea = $('#classListArea'), cdata = vipkidMobile.classDiaries,diarylistTtml;
			for(var i = 0; i < cdata.length; i++){
				diarylistTtml = $("<li><section class='title'><a href="+cdata[i].url+">"+cdata[i].title+"</a></section>"+
	            "<time>"+cdata[i].date+"</time>"+
	            "<a href="+cdata[i].url+"><img src='"+cdata[i].img+"'/></a>"+
	            "<p>"+cdata[i].description+"</p>"+
	            "<a class='more' href="+cdata[i].url+" title='阅读全文'><span>阅读全文</span><em></em></a></li>");

				$classListArea.append(diarylistTtml)	
			}
	    }
	},
	teacherVideo:function(){
		var teacherVideoMain = $(".teacherList");
	    if(teacherVideoMain.length>0){    	
	    	var tdata = vipkidMobile.englishteachers,teacherHtml;
	    	teacherHtml = "";
	    	for(var i = 0; i < tdata.length; i++){
				teacherHtml += "<li rel='"+tdata[i].url+"'>"+
		        	"<section class='img'><img src='"+tdata[i].img+"' /><em></em></section>"+
		            "<span>"+tdata[i].name+"</span>"+
		            "<p>"+tdata[i].introduction+"</p>"+
		        "</li>"	;
			}	    	
	    	teacherVideoMain.append(teacherHtml)
	    }
	},
	videoLinkFun:function(){
		var videourlid = $(this).attr("rel");
    	var videoHtml ="";
    	videoHtml+="<div class=\"videoPop\"><div class=\"videoArea\">";
    	videoHtml+="<iframe border='none' width='670' height='502' src='http://v.qq.com/iframe/player.html?vid="+videourlid+"&amp;width=670&amp;height=502&amp;auto=0'  scrolling='no'   frameborder='no'></iframe>";    	
    	videoHtml+="</div></div>";
    	$("body").append(videoHtml);
    	$(".videoPop").show();    
    	//$("body").delegate("","tap",function(){$(".videoPop").remove();}) 
    	$('.videoPop').on('touchend',function(event){
            $(this).remove();
            event.preventDefault();
        }); 	
	}
}

vipkidMobile.englishteachers=[
{ 
	name:'Alexis L', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Alexis_Letner.jpg', 
	url:'j01537fjsas',
	introduction:'我毕业于UCLA的英文专业，现在一所小学任教5年，并且是专业的儿童家教。所以我有丰富的辅导小朋友的经验。我喜欢小朋友，尤其喜欢帮助非英语母语的小朋友学习英语。', 
	}, 
	{ 
	name:'Alyssa C', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Alyssa.jpg', 
	url:'l0153bqxu8l', 
	introduction:'我毕业于密西根州立大学，现攻读教育学硕士学位。我是美国一所初中的英语和法语老师，拥有12年教育经验。我还是很多小朋友和成人的家庭教师，帮助他们学习各种科目。', 
	}, 
	{ 
	name:'Chris W', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_chris_williams.jpg', 
	url:'b0153svownk', 
	introduction:'我毕业于Ashland大学，有为不同年龄段的学生授课经验，教他们英文听说读写以及托福考试的准备。我相信只要指导正确，每位学生都可以达到他们的学习目标。', 
	}, 

	{ 
	name:'Debbie W', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Debbie.jpg', 
	url:'x0153id0yyu', 
	introduction:'本科毕业于巴克内尔大学，研究生毕业于罗文大学，在大洋城当了35年的小学老师，懂教育更懂孩子，对英语启蒙的自然拼读法有专业级别的研究哦。', 
	}, 

	{ 
	name:'Elayne L', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Elayne_Lemieux.jpg', 
	url:'p0153g9i9hf', 
	introduction:'音乐专业学士学位，教育学学士学位，15年英语教育经验，是一个有创造性的教育者，拥有教育不同年龄，不同背景学生的丰富经验。在授课时充满活力，富有耐心，总是能敏感察觉到学生的需求，并给予学生积极的引导和鼓励。', 
	}, 
	{ 
	name:'Henry W', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Henry_Woodburn.jpg', 
	url:'u015395io0l', 
	introduction:'帅帅的Henry是名副其实的“孩子王”，毕业于玛丽威廉学院就读东亚历史和中文专业，曾来北大交换并游历中国多个地方，多年的ESL的教学经验，对于中国孩子学习英语有深刻的认识。', 
	}, 
	{ 
	name:'Janice A', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Janice.jpg', 
	url:'v0153e3x1jz', 
	introduction:'体育教育荣誉学士，布鲁克大学辅修音乐，受过系统阅读训练，擅长引导小朋友建立终生阅读好习惯，拥有17年针对4-12岁小朋友的教育教学经验。', 
	}, 

	{ 
	name:'Joe K', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Joe_Kleinhenr.jpg', 
	url:'b0153c4fhj9', 
	introduction:'毕业于北卡罗兰纳大学，主修语言学，关注孩子内心世界的成长，绅士教育的王牌老师。曾在中国教授好几年ESL，不仅注重成绩，而且关心小孩与他人和世界的建立良好关系。', 
	}, 
	{ 
	name:'Jojo Francisco', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Jojo_Francisco.jpg', 
	url:'u0153tnnyka', 
	introduction:'拥有德保罗大学法语学士学位和会计学士学位，5年英语教师经验，学生来自世界各地，24年教会学校业余老师经验，深刻领悟教育孩子的方法方式。当过10年的银行家。', 
	}, 
	{ 
	name:'Joseph T', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Joseph_Turman.jpg', 
	url:'b01539zuc64', 
	introduction:'拥有北卡罗来纳大学攻读人类学和政治学双学位，曾作为教学顾问，教授5-6岁的小朋友艺术、音乐和手工。语言天才，学习过法语、西班牙语、阿拉伯、立陶宛语等语言。', 
	}, 
	{ 
	name:'Joshua J', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Joshua_Jones.jpg', 
	url:'m01532uk96r', 
	introduction:'毕业于音乐教育专业和国际关系专业，专业小提琴演奏者，游历爱尔兰、突尼斯、意大利、中国等多个国家，拥有四年的针对中国学生的教学经验，热爱音乐，热爱柴可夫斯基。', 
	}, 
	{ 
	name:'Kristina S', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Kristina.jpg', 
	url:'y0153l2p6hn', 
	introduction:'在威斯康星大学主修新闻与大众传播专业，拥有TESL国际教师认证，曾花五年自由行走于世界，在韩国和土耳其教过小孩学英文。', 
	}, 
	{ 
	name:'Lauren B', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Lauren_Barnett.jpg', 
	url:'a01530o90vn', 
	introduction:'拥有音乐艺术学士学位，即将在多伦多大学教育学专业毕业，喜爱创作演奏音乐,尤其喜爱演奏萨克斯管！拥有十年的私立学校教学经验。', 
	}, 
	{ 
	name:'Max C', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Max_cordova.jpg', 
	url:'w0153qdru04', 
	introduction:'本科毕业于哈特福德大学，在韩国和墨西哥有两年的教学经历，拥有TEFL国际教师认证，在美国取得教师认证，爱好广泛，足迹遍布多个国家，文化多元。', 
	}, 
	{ 
	name:'McKay R', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_McKayRoozen.jpg', 
	url:'n0153jkrjc4', 
	introduction:'毕业于哈佛大学教育研究生院攻读国际教育政策专业，游历全球，到过全国30多个国家，精通汉语、德语、法语，热爱舞蹈艺术，是“美丽中国”(Teach For China)的成员，曾在云南农村支教两年。', 
	}, 
	{ 
	name:'Megan S', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Megan_Sadler.jpg', 
	url:'s0153y67eme', 
	introduction:'毕业于东亚研究专业和中文专业，曾经在日本、中国大陆、台湾和西班牙居住，以在课上用唱歌、演奏音乐充分调动孩子们的积极性！', 
	}, 
	{ 
	name:'Meg S', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Meghan.jpg', 
	url:'a01531co3xd', 
	introduction:'拥有皇后大学商业学学士学位和多伦多大学儿童研究和教育学硕士学位，声音特别，被朋友成为“老师的声音”，拥有9年的教学经验，非常擅长幼儿教学和小学初级教学，曾经参与了多项儿童教育研究。', 
	}, 
	{ 
	name:'Melody P', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Meoldy_Pinkard.jpg', 
	url:'x015343jx6k', 
	introduction:'田纳西大学物理学与理学双学士，拥有国际英语教师证书，教学方法多样，孩子学习更高效。近两年定居中国，熟知中国孩子学英语的特点。', 
	}, 
	{ 
	name:'Mikela M', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Mikela_Moore.jpg', 
	url:'a0153o9wc8z', 
	introduction:'华盛顿特区教师，心理学学士，在中国成都教过英语，了解中国的文化和风俗，在华盛顿特区任教多年，喜欢用更有趣更有创意的方式与小朋友一起学习。', 
	},  
	{ 
	name:'Samantha S', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Samantha_Song.jpg', 
	url:'g0153uy4y1v', 
	introduction:'哈佛大学国际教育政策硕士，是一位合格的小学教师，她在加州长大，纽约及波士顿求学，她认为教育最棒的就是激发孩子学习的兴趣，也强烈地相信每个孩子都有学习和成功的潜能。', 
	}, 
	{ 
	name:'Carolina Z', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Zambrano.jpg', 
	url:'m01538whfnz', 
	introduction:'本科毕业于初等教育专业，硕士专攻科技教育学，具有ESOL国际权威语言认证，13年小学教育经验，美国K12媒体教育专家资格认证，热爱不同文化，足迹遍布全球，真正的“世界公民。”', 
	}, 
	{ 
	name:'Zoey E', 
	img:'http://resource.vipkid.com.cn/static/images/home_portal/teacher_img/teacher_Zoey_Erdenebileg.jpg', 
	url:'s0153ub5lqo', 
	introduction:'我毕业于俄亥俄州凯尼恩学院，经济和中文专业。我教过汉语课程，发现教孩子永远是一件非常快乐的事情，因为他们参与学习的投入程度，以及对新知识的应用能力，总是超乎我的想象。', 
	}]
vipkidMobile.classDiaries = [{
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

vipkidMobile.wwwmobile.init();