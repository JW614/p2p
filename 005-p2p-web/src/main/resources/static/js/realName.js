
//同意实名认证协议
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
		var phone = $.trim($("#phone").val());
		if ("" == phone) {
			showError("phone","请输入手机号码");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone", "请输入正确的手机号码");
		} else {
			showSuccess("phone");
		}
	});

	//验证真实姓名
	$("#realName").on("blur",function () {
		var realName = $.trim($("#realName").val());

		if ("" == realName) {
			showError("realName","请输入真实姓名");
		} else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)) {
			showError("realName", "真实姓名只支持中文");
		} else {
			showSuccess("realName");
		}

	});

	//验证身份证号码
	$("#idCard").on("blur",function () {
		var idCard = $.trim($("#idCard").val());

		if ("" == idCard) {
			showError("idCard","请输入身份证号码");
		} else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
			showError("idCard", "请输入正确的身份证号码");
		} else {
			showSuccess("idCard");
		}
	});

	//验证短信验证码
	$("#messageCode").on("blur",function () {
		var messageCode = $.trim($("#messageCode").val());

		if ("" == messageCode) {
			showError("messageCode", "请输入短信验证码");
		} else {
			showSuccess("messageCode");
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

			//触发真实姓名失去焦点事件
			$("#realName").blur();
			//获取验证消息
			var realNameErrorText = $("#realNameErr").text();
			if ("" != realNameErrorText) {
				return;
			}

			//触发身份证号码失去焦点事件
			$("#idCard").blur();
			//获取验证消息
			var idCardErrorText = $("#idCardErr").text();
			if ("" != idCardErrorText) {
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


	$("#btnRegist").on("click",function () {

		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();
		$("#messageCode").blur();

		var errorTexts = $("div[id$='Err']").text();
		if ("" == errorTexts) {

			var phone = $.trim($("#phone").val());
			var realName = $.trim($("#realName").val());
			var idCard = $.trim($("#idCard").val());
			var messageCode = $.trim($("#messageCode").val());


			$.ajax({
				url:contextPath+"/loan/realName",
				type:"post",
				data:{
					"phone":phone,
					"realName":realName,
					"idCard":idCard,
					"messageCode":messageCode
				},
				success:function (data) {
					if (data.code == 1) {
						window.location.href = contextPath + "/index";
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


