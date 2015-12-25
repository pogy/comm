(function($){
	var bigAutocomplete = new function(){
		this.currentInputText = null;//目前获得光标的输入框（解决一个页面多个输入框绑定自动补全功能）
		this.functionalKeyArray = [9,20,13,16,17,18,91,92,93,45,36,33,34,35,37,39,112,113,114,115,116,117,118,119,120,121,122,123,144,19,145,40,38,27];//键盘上功能键键值数组
		this.holdText = null;//输入框中原始输入的内容
		
		//初始化插入自动补全div，并在document注册mousedown，点击非div区域隐藏div
		this.init = function(){
			$("body").append("<div id='autoCompleteList' class='autoCompleteList'></div>");
			$(document).bind('mousedown',function(event){
				var $target = $(event.target);
				if((!($target.parents().andSelf().is('#autoCompleteList'))) && (!$target.is(bigAutocomplete.currentInputText))){
					bigAutocomplete.hideAutocomplete();
				}
			})
			
			//鼠标悬停时选中当前行
			$("#autoCompleteList").delegate("li", "mouseover", function() {
				$("#autoCompleteList li").removeClass("hover");
				$(this).addClass("hover");
			}).delegate("li", "mouseout", function() {
				$("#autoCompleteList li").removeClass("hover");
			});		
			
			
			//单击选中行后，选中行内容设置到输入框中，并执行callback函数
			$("#autoCompleteList").delegate("li", "click", function() {
				bigAutocomplete.currentInputText.val( $(this).find("li:last").html());
				var callback_ = bigAutocomplete.currentInputText.data("config").callback;
				if($("#autoCompleteList").css("display") != "none" && callback_ && $.isFunction(callback_)){
					callback_($(this).data("jsonData"));
					
				}				
				bigAutocomplete.hideAutocomplete();
			})			
			
		}
		
		this.autocomplete = function(param){
			
			if($("body").length > 0 && $("#autoCompleteList").length <= 0){
				bigAutocomplete.init();//初始化信息
			}			
			
			var $this = $(this);//为绑定自动补全功能的输入框jquery对象			
			var $downList = $("#autoCompleteList");			
			this.config = {
			               //width:下拉框的宽度，默认使用输入框宽度
			               width:$this.outerWidth() - 2,
			               //url：格式url:""用来ajax后台获取数据，返回的数据格式为data参数一样
			               url:null,
			               /*data：格式{data:[{title:null,result:{}},{title:null,result:{}}]}
			               url和data参数只有一个生效，data优先*/
			               data:null,
			               //callback：选中行后按回车或单击时回调的函数
			               callback:null};
			$.extend(this.config,param);
			
			$this.data("config",this.config);
			
			//输入框keydown事件
			$this.keydown(function(event) {
				switch (event.keyCode) {
				case 40://向下键
					
					if($downList.css("display") == "none")return;
					
					var $nextitem = $downList.find(".hover");
					if($nextitem.length <= 0){//没有选中行时，选中第一行
						$nextitem = $downList.find("li:first");
					}else{
						$nextitem = $nextitem.next();
					}
					$downList.find("li").removeClass("hover");
					
					if($nextitem.length > 0){//有下一行时（不是最后一行）
						$nextitem.addClass("hover");//选中的行加背景
						$this.val($nextitem.children("span").text());//选中行内容设置到输入框中						
						//div滚动到选中的行,jquery-1.6.1 $nextitem.offset().top 有bug，数值有问题
						$downList.scrollTop($nextitem[0].offsetTop - $downList.height() + $nextitem.height() );
						
					}else{
						$this.val(bigAutocomplete.holdText);//输入框显示用户原始输入的值
					}										
					break;
				case 38://向上键
					if($downList.css("display") == "none")return;
					
					var $previtem = $downList.find(".hover");
					if($previtem.length <= 0){//没有选中行时，选中最后一行行
						$previtem = $downList.find("li:last");
					}else{
						$previtem = $previtem.prev();
					}
					$downList.find("li").removeClass("hover");
					
					if($previtem.length > 0){//有上一行时（不是第一行）
						$previtem.addClass("hover");//选中的行加背景
						$this.val($previtem.children("span").text());//选中行内容设置到输入框中
						
						//div滚动到选中的行,jquery-1.6.1 $$previtem.offset().top 有bug，数值有问题
						$downList.scrollTop($previtem[0].offsetTop - $downList.height() + $previtem.height());
					}else{
						$this.val(bigAutocomplete.holdText);//输入框显示用户原始输入的值
					}					
					break;
				case 27:					
					bigAutocomplete.hideAutocomplete();
					break;
				}
			});		
			
			//输入框keyup事件
			$this.keyup(function(event) {
				var k = event.keyCode;
				var ctrl = event.ctrlKey;
				var isFunctionalKey = false;//按下的键是否是功能键
				for(var i=0;i<bigAutocomplete.functionalKeyArray.length;i++){
					if(k == bigAutocomplete.functionalKeyArray[i]){
						isFunctionalKey = true;
						break;
					}
				}
				//k键值不是功能键或是ctrl+c、ctrl+x时才触发自动补全功能
				if(!isFunctionalKey && (!ctrl || (ctrl && k == 67) || (ctrl && k == 88)) ){
					var config = $this.data("config");
					
					var offset = $this.offset();
					$downList.width(config.width);
					var h = $this.outerHeight() + 2;
					$downList.css({"top":offset.top + h,"left":offset.left-2,"z-index":"100000","position":"absolute"});
					
					var data = config.data;
					var url = config.url;
					var keyword_ = $.trim($this.val());
					if(keyword_ == null || keyword_ == ""){
						bigAutocomplete.hideAutocomplete();
						return;
					}					
					if(data != null && $.isArray(data) ){
						var data_ = new Array();
						for(var i=0;i<data.length;i++){
							if(data[i].title.indexOf(keyword_) > -1){								
								data_.push(data[i]);
							}else{
								$downList.hide();
							}
						}						
						makeContAndShow(data_);
					}else if(url != null && url != ""){//ajax请求数据
						$.post(url,{keyword:keyword_},function(result){							
							if(result.data.length <= 0){
								$downList.hide();
							}
							makeContAndShow(result.data)
						},"json")
					}
					bigAutocomplete.holdText = $this.val();
				}
				//回车键
				if(k == 13){
					var callback_ = $this.data("config").callback;
					if($downList.css("display") != "none"){
						if(callback_ && $.isFunction(callback_)){
							callback_($downList.find(".hover").data("jsonData"));
						}
						$downList.hide();						
					}
				}
				
			});	
			
					
			//组装下拉框html内容并显示
			function makeContAndShow(data_){
				if(data_ == null || data_.length <=0 ){
					return;
				}
				
				var cont = "<ul class='autocompleteDownlist'>";
				for(var i=0;i<data_.length;i++){
					cont += "<li><img src='" + data_[i].avatar + "' alt='" + data_[i].id + "' /><span>" + data_[i].title + "</span></li>"
				}
				cont += "</ul>";
				$downList.html(cont);
				$downList.show();
				
				//每行tr绑定数据，返回给回调函数
				$downList.find("li").each(function(index){
					$(this).data("jsonData",data_[index]);
				})
			}			
					
			
			//输入框focus事件
			$this.focus(function(){
				bigAutocomplete.currentInputText = $this;
			});
			
		}
		//隐藏下拉框
		this.hideAutocomplete = function(){
			var $downList = $("#autoCompleteList");
			if($downList.css("display") != "none"){
				$downList.find("li").removeClass("hover");
				$downList.hide();
			}			
		}
		
	};	
	
	$.fn.bigAutocomplete = bigAutocomplete.autocomplete;
	
})(jQuery)