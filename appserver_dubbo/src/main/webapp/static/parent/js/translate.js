$(function(){
	var loading = false;
	$("#translateBtn").click(function(){
		var commentDiv = $("#tcComment"); 
		var commentId = $(".commentEN", commentDiv).data("comment-id");
		if (commentId && !loading) {
			loading = true;
			$(this).text("翻译中...");
			$(".fClassTrans .commentCN", commentDiv).html("").hide();
			$(".fClassTrans .loading", commentDiv).show();
			$(".fClassTrans", commentDiv).show();
			var _this = this;
			$.get("/parent/translate", {"commentId" :commentId}, function(data) {
				if (data && data.content) {
					$(".commentCN",commentDiv).html(data.content).show();
					$(_this).text("刷新翻译");
				} else {
					$(_this).text("翻译成中文");
					$(".commentCN",commentDiv).html("翻译结果为空，请稍后再试。").show();
				}
				$(".fClassTrans .loading",commentDiv).hide();
				loading = false;
			}).fail(function(){
				$(_this).text("翻译成中文");
				$(".fClassTrans .loading", commentDiv).hide();
				$(".commentCN", commentDiv).html("翻译失败，请稍后再试。").show();
				loading = false;
			});
		}
	});
});