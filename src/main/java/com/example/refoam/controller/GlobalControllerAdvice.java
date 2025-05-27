package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.service.BadgeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final HttpSession session;
    private final BadgeService badgeService;

    @ModelAttribute
    public void addLoginMemberToModel(Model model){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("loginMember", loginMember);
    }

    // 시큐리티가 없어서 이 방법으로
    // 모든 뷰에 badgeCount를 추가
    @ModelAttribute
    public void addBadgeCount(Model model) {
        // 1) 세션에서 로그인 회원 꺼내기
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember != null) {
            // 2) 서비스에서 미확인 배지 개수 조회
            long count = badgeService.getUnreadCount(loginMember.getId());
            // 3) 모델에 추가
            model.addAttribute("badgeCount", count);
        }
    }





}
