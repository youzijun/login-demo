package com.example.demo.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
@Configuration
public class UserInterceptor extends WebMvcConfigurerAdapter {


    @Bean
    public UserLoginInterceptor loginInterceptor(){
        return new UserLoginInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 添加登录拦截器
        registry.addInterceptor(loginInterceptor());
    }

}
