$(function () {
    $("#loginBtn").on("click", function () {

        var phone = $.trim($("#phone").val());
        if ("" == phone) {
            $("#showId").html("请输入手机号码");
            return;
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            $("#showId").html("请输入正确的手机号码");
            return;
        } else {
            $("#showId").html("");
        }

        var loginPassword = $.trim($("#loginPassword").val());
        if ("" == loginPassword) {
            $("#showId").html("请输入登录密码");
            return;
        } else {
            $("#showId").html("");
        }

        var messageCode = $.trim($("#messageCode").val());
        if ("" == messageCode) {
            $("#showId").html("请输入短信验证码");
            return;
        } else {
            $("#showId").html("");
        }

        $("#loginPassword").val($.md5(loginPassword));
        var redirectUrl = $("#redirectUrl").val();

        $.ajax({
            url:contextPath+"/loan/login",
            type:"post",
            data:{
                "phone":phone,
                "loginPassword":$.md5(loginPassword),
                "messageCode":messageCode
            },
            success:function (data) {
                if (data.code == 1) {
                    window.location.href = redirectUrl;
                } else {
                    $("#loginPassword").val("");
                    $("#messageCode").val("");
                    $("#showId").html(data.message);
                }
            },
            error:function () {
                $("#loginPassword").val("");
                $("#messageCode").val("");
                $("#showId").html("用户名或密码有误");
            }
        });
    });

    $("#dateBtn1").on("click", function () {

        //判断：如果正在倒计时就无需再实现倒计时效果
        if (!$("#dateBtn1").hasClass("on")) {
            var phone = $.trim($("#phone").val());
            var loginPassword = $.trim($("#loginPassword").val());

            if ("" == phone) {
                $("#showId").html("请输入手机号码");
                return;
            } else if (!/^1[1-9]\d{9}$/.test(phone)) {
                $("#showId").html("请输入正确的手机号码");
                return;
            } else if ("" == loginPassword) {
                $("#showId").html("请输入登录密码");
                return;
            } else {
                $("#showId").html("");
            }

            $.ajax({
                url:contextPath+"/loan/messageCode",
                type:"get",
                data:"phone="+phone,
                success:function (data) {
                    if (data.code == 1) {
                        alert("您的短信验证码是：" + data.data);
                        $.leftTime(60,function (d) {
                            if (d.status) {
                                $("#dateBtn1").addClass("on");
                                $("#dateBtn1").html((d.s == "00"?"60":d.s)+"s重新获取");
                            } else {
                                $("#dateBtn1").removeClass("on");
                                $("#dateBtn1").html("获取验证码");
                            }
                        });
                    } else {
                        $("#showId").html(data.message);
                    }
                },
                error:function () {
                    $("#showId").html("系统繁忙，请稍后重试");
                }
            });


        }

    });

    $.ajax({
        url:contextPath+"/loan/loadStat",
        type:"get",
        success:function (data) {
            $(".historyAverageRate").html(data.historyAverageRate);
            $("#allBidMoney").html(data.allBidMoney);
            $("#allUserCount").html(data.allUserCount);
        }
    });

});


