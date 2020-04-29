//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});


	//验证手机号码
	$("#phone").on("blur",function () {

		//获取手机号码
		var phone = $.trim($("#phone").val());

		if ("" == phone) {
			showError("phone","请输入手机号码");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone", "请输入正确的手机号码");
		} else {

			$.ajax({
				url:contextPath+"/loan/checkPhone",
				type:"post",
				data:"phone="+phone,
				success:function (data) {
					if (data.code == 1) {
						showSuccess("phone");
					} else {
						showError("phone",data.message);
					}
				},
				error:function () {
					showError("phone","系统异常，请重新输入")
				}
			});


		}

	});

	//验证注册密码
	$("#loginPassword").on("blur",function () {
		var loginPassword = $.trim($("#loginPassword").val());

		if ("" == loginPassword) {
			showError("loginPassword","请输入密码");
		} else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
			showError("loginPassword","密码只可使用数字和大小写英文字母");
		} else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
			showError("loginPassword","密码必须同时包含英文和数字");
		} else if (loginPassword.length < 6 || loginPassword.length > 20) {
			showError("loginPassword", "密码长度应为6-20位");
		} else {
			showSuccess("loginPassword");
		}
	});

	//验证短信验证码是否有值
	$("#messageCode").on("blur",function () {
		var messageCode = $.trim($("#messageCode").val());

		if ("" == messageCode) {
			showError("messageCode", "请输入您的短信验证码");
		} else {
			showSuccess("messageCode");
		}

	});

	$("#btnRegist").on("click",function () {
		$("#phone").blur();
		$("#loginPassword").blur();
		$("#messageCode").blur();
		var flag = true;

		// $(".form-hint").size();
		// $("div[id$='Err']").size();
		/*$("div[id$='Err']").each(function () {
			var errorText = $(this).html();

			if ("" != errorText) {
				flag = false;
				return;
			}
		});

		if (flag) {
			alert("发起注册请求");
		}*/

		// alert($("div[id$='Err']").html());
		// alert($("div[id$='Err']").text());

		var errorTexts = $("div[id$='Err']").text();

		if ("" == errorTexts) {

			var loginPassword = $.trim($("#loginPassword").val());
			var phone = $.trim($("#phone").val());
			var messageCode = $.trim($("#messageCode").val());
			

			$("#loginPassword").val($.md5(loginPassword));
			
			$.ajax({
				url:contextPath+"/loan/register",
				type:"post",
				data:{
					"phone":phone,
					"loginPassword":$.md5(loginPassword),
					"messageCode":messageCode
				},
				success:function (data) {
					if (data.code == 1) {
						window.location.href = contextPath + "/loan/page/realName";
					} else {
						$("#loginPassword").val("");
						$("#messageCode").val("");
						hideError("loginPassword");
						hideError("messageCode");
						showError("messageCode",data.message);
					}
				},
				error:function () {
					$("#loginPassword").val("");
					$("#messageCode").val("");
					hideError("loginPassword");
					hideError("messageCode");
					showError("messageCode","系统繁忙，请稍后重试");
				}
			});
		}


	});

	//获取短信验证码
	$("#messageCodeBtn").on("click",function () {

		//判断：如果正在倒计时就无需再实现倒计时效果
		if (!$("#messageCodeBtn").hasClass("on")) {

			//触发手机号失去焦点事件
			$("#phone").blur();
			//获取验证消息
			var phoneErrorText = $("#phoneErr").text();
			if ("" != phoneErrorText) {
				return;
			}

			//触发密码失去焦点事件
			$("#loginPassword").blur();
			//获取验证消息
			var loginPasswordErrorText = $("#loginPasswordErr").text();
			if ("" != loginPasswordErrorText) {
				return;
			}

			var phone = $.trim($("#phone").val());


			$.ajax({
				url:contextPath+"/loan/messageCode",
				type:"get",
				data:"phone="+phone,
				success:function (data) {
					if (data.code == 1) {
						alert("您的短信验证码是：" + data.data);
						$.leftTime(60,function (d) {
							if (d.status) {
								$("#messageCodeBtn").addClass("on");
								$("#messageCodeBtn").html((d.s == "00"?"60":d.s)+"s重新获取");
							} else {
								$("#messageCodeBtn").removeClass("on");
								$("#messageCodeBtn").html("获取验证码");
							}
						});
					} else {
						showError("messageCode",data.message);
					}
				},
				error:function () {
					showError("messageCode","系统繁忙，请稍后重试");
				}
			});
		}
	});

});
