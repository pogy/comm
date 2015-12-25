//处理订单业务

var selectType = "ALIPAY";
var order;
$(function(){
	addEvents();
	$('#banks').hide();
	$('#transferAccountButton').hide();
})

function addEvents(){
	$('.bankItem img, .paymentItem img').click(function(){
		selectType = $(this).attr('alt');
		$('#curimg').attr("src", $(this).attr('src'));
		$('#banks').hide();
		if(selectType == 'TRANSFERACCOUNTS'){
			$('#transferAccountButton').show();
			$('#alipayButton').hide();
			$('.bankBoxRight').show();
		}else{
			$('#alipayButton').show();
			$('#transferAccountButton').hide();
			$('.bankBoxRight').hide();
		}
	})
	
	$('.cancelButton').click(function(){
		var id = $(this).next().val();
		cancelOrder(id);
	});
}

function cancelOrder(id){
	Alert.confirm("","<p class=\"tc\">确认要取消订单？</p>","确认取消","不取消",function(){doCancel(id)}, null);
}

function doCancel(id){
	$.ajax({
        type: "POST",
        url: '/cancelOrder',
        data: {
			'orderId':id
			},
        success: function(data){
        	if(data!=null){
        		Alert.info("","<p class=\"tc\">您的订单取消成功</p>","好的",function(){window.location.reload();});
        	}else{
        		Alert.error("","订单取消失败,请稍后再试");
        	}
        }
    });
}


//变换付款方式
function showPayType(){
	$('#banks').show();
	$('#alipayButton').hide();
	$('#transferAccountButton').hide();
	$('.bankBoxRight').hide();
}

function changePayType(){
	$('#order_confirm').removeClass("tipsPopOpen").hide();
	showPayType();
}

function doPay(){
	if(selectType == 'ALIPAY'){
		payByAlipay();
	}else{
		payByNetBank();
	}
}

//支付宝支付
function payByAlipay(){
	var requestParaTemp = {
			seller_email:$('#selleremail').val(),
			out_trade_no:$('#orderid').val(),
			subject:$('#serialNumber').val(),
			total_fee:$('#totalDealPrice').val(),
			body:$('#serialNumber').val(),
			notify_url:$('#notifyUrl').val(),
			return_url:$('#returnUrl').val()
	};
	
	$.get("/api/service/public/alipay/buildRequest", requestParaTemp, function(data, status){
		var requestPara = data;
		window.open("").document.write("<form id='alipaysubmit' name='alipaysubmit'\
		 		action='https://mapi.alipay.com/gateway.do?_input_charset=utf-8'\
		 		method='get'><input type='hidden' name='sign'\
		 		value='"+requestPara.sign+"' />" +
		 				"<input type='hidden' name='body' value='"+requestPara.body+"'/>"
		 				+"<input type='hidden'\
		 		name='_input_charset' value='utf-8' /><input type='hidden'\
		 		name='total_fee' value='"+requestPara.total_fee+"'/><input type='hidden' name='subject'\
		 		value='"+requestPara.subject+"'/><input type='hidden' name='sign_type' value='MD5' />" +
		 		"<input type='hidden' name='service' value='create_direct_pay_by_user' /><input\
		 		type='hidden' name='notify_url'\
		 		value='"+requestPara.notify_url+"'/><input\
		 		type='hidden' name='partner' value='"+requestPara.partner+"' /><input\
		 		type='hidden' name='seller_email' value='"+requestPara.seller_email+"' /><input\
		 		type='hidden' name='out_trade_no' value='"+requestPara.out_trade_no+"' /><input type='hidden'\
		 		name='payment_type' value='1'/><input type='hidden' name='return_url'\
		 		value='"+requestPara.return_url+"' /><input\
		 		type='submit' value='确认' style='display: none;'>\
 				</form>"+
 				"<script>document.forms['alipaysubmit'].submit();</script>");  
		showAckedPanel();
	})
}

//支付宝网银支付
function payByNetBank(){
	var requestParaTemp = {
			seller_email:$('#selleremail').val(),
			out_trade_no:$('#orderid').val(),
			subject:$('#serialNumber').val(),
			total_fee:$('#totalDealPrice').val(),
			body:$('#serialNumber').val(),
			notify_url:$('#notifyUrl').val(),
			return_url:$('#returnUrl').val(),
			defaultbank:selectType
	};
	$.get("/api/service/public/alipay/buildRequest", requestParaTemp, function(data, status){
		var requestPara = data;
		window.open().document.write("<form id='alipaysubmit' name='alipaysubmit'\
		 		action='https://mapi.alipay.com/gateway.do?_input_charset=utf-8'\
		 		method='get'><input type='hidden' name='sign'\
		 		value='"+requestPara.sign+"' />" +
		 				"<input type='hidden' name='body' value='"+requestPara.body+"'/>"
		 				+"<input type='hidden'\
		 		name='_input_charset' value='utf-8' /><input type='hidden'\
		 		name='total_fee' value='"+requestPara.total_fee+"'/><input type='hidden' name='subject'\
		 		value='"+requestPara.subject+"'/><input type='hidden' name='sign_type' value='MD5' />" +
		 		"<input type='hidden' name='service' value='create_direct_pay_by_user' /><input\
		 		type='hidden' name='notify_url'\
		 		value='"+requestPara.notify_url+"'/><input\
		 		type='hidden' name='partner' value='"+requestPara.partner+"' /><input\
		 		type='hidden' name='seller_email' value='"+requestPara.seller_email+"' /><input\
		 		type='hidden' name='out_trade_no' value='"+requestPara.out_trade_no+"' /><input type='hidden'\
		 		name='payment_type' value='1'/><input type='hidden' name='return_url'\
		 		value='"+requestPara.return_url+"' /><input type='hidden' name='paymethod'\
		 		value='"+requestPara.paymethod+"' /><input type='hidden' name='defaultbank'\
		 		value='"+requestPara.defaultbank+"' /><input\
		 		type='submit' value='确认' style='display: none;'>\
 				</form>"+
 				"<script>document.forms['alipaysubmit'].submit();</script>");  
		showAckedPanel();
	}).error(function(data, status, headers, config) {
		$log.debug('构建支付宝请求参数失败. status:'+status +" data:" + JSON.stringify(data));
	});
}

function showAckedPanel(){
	$('#order_confirm').addClass("tipsPopOpen").show();
}

//转账确认
function doTransfer(){
	var oid = $('#oid').val();
	var param = {orderId:oid};
	$.post("/transferConfirm", param, function(data, status){
		if(data != null){
			window.location.reload();
		}else{
			Alert.error("提示","转账确认失败, 请稍后再试!","确认");
		}
	})
}


