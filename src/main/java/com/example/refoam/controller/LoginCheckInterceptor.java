package com.example.refoam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;

public class LoginCheckInterceptor implements HandlerInterceptor {

//    공통 로그인 검사 로직을 처리하는 Spring 인터셉터(Interceptor) // 일일이 모델추가 안해도됨
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            String requestURI = request.getRequestURI();
            // 로그인 안 되어 있음 → 로그인 페이지로 리다이렉트
            response.sendRedirect("/?redirectURL=" + URLEncoder.encode(requestURI, "UTF-8"));
            return false;
        }


        // 로그인 되어 있음 → 정상 진행
        return true;
    }

}
