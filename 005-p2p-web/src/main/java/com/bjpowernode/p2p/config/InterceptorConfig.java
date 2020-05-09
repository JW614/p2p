package com.bjpowernode.p2p.config;

import com.bjpowernode.p2p.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName:InterceptorConfig
 * Package:com.bjpowernode.springboot.config
 * Description:
 *
 * @date:2020/4/22 9:44
 * @author:动力节点
 */

//当前类用于定一个配置类,替换xml文件
@Configuration  //该注解就是用于定义某个类为配置类,该就相当于一个xml配置文件,我们将在此类在配置拦截器
public class InterceptorConfig implements WebMvcConfigurer {


    //该方法相当于之前的mvc:interceptors标签
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //配置拦截路径
        String[] addPathPatterns = {
                "/loan/**"
        };

        //排除不需要拦截的路径
        String[] excludePathPatterns = {
                "/loan/loan",
                "/loan/loanInfo",
                "/loan/page/register",
                "/loan/page/login",
                "/loan/checkPhone",
                "/loan/register",
                "/loan/messageCode",
                "/loan/login",
                "/loan/loadStat"
        };


        //指定拦截器,该方法相当于之前的mvc:interceptor标签
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns(addPathPatterns)
                .excludePathPatterns(excludePathPatterns);

    }
}
