package com.example.refoam.controller;

import com.example.refoam.domain.AlertLog;
import com.example.refoam.domain.Employee;
import com.example.refoam.dto.AlertLogForm;
import com.example.refoam.service.AlertService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertRestController {

    private final AlertService alertService;

    @GetMapping("/unread")
    public ResponseEntity<List<AlertLogForm>> getUnreadAlerts(HttpSession session) {
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        List<AlertLog> unread = alertService.getUnreadAlerts(loginMember);

        List<AlertLogForm> result = unread.stream()
                .map(AlertLogForm::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(HttpSession session) {
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        long count = alertService.countUnreadAlerts(loginMember);
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
