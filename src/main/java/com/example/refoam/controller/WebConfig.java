package com.example.refoam.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")                     // 모든 요청에 인터셉터 적용
                .excludePathPatterns(                       // 로그인 필요 없는 페이지는 제외
                        "/", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/error"
                );
    }
}
