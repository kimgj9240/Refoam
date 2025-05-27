package com.example.refoam.controller;

import com.example.refoam.domain.AlertLog;
import com.example.refoam.domain.Employee;
import com.example.refoam.dto.ProductionMonitoring;
import com.example.refoam.repository.AlertLogRepository;
import com.example.refoam.service.BadgeService;
import com.example.refoam.service.MonitoringService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final HttpSession session;
    private final BadgeService badgeService;
    private final MonitoringService monitoringService;
    private final AlertLogRepository alertLogRepository;

    @ModelAttribute
    public void addLoginMemberToModel(Model model){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("loginMember", loginMember);

        // ✅ 로그인된 사용자일 때만 알림 정보 조회
        if (loginMember != null) {
            long alertCount = alertLogRepository.countByEmployeeAndCheckedFalse(loginMember);
            List<AlertLog> alertList = alertLogRepository.findAllByEmployeeAndCheckedFalse(loginMember);

            model.addAttribute("alertCount", alertCount);
            model.addAttribute("alertList", alertList);
        }
    }
}
