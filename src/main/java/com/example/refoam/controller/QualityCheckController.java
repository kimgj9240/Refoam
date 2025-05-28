package com.example.refoam.controller;

import com.example.refoam.domain.Process;
import com.example.refoam.service.ProcessService;
import com.example.refoam.service.QualityCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/quality")
@RequiredArgsConstructor
public class QualityCheckController {
    private final QualityCheckService qualityCheckService;
    private final ProcessService processService;
    @GetMapping("/{id}/check")
    public String check(@PathVariable("id") Long orderId){
        qualityCheckService.getQualityCheck(orderId);

        return "redirect:/process/{id}/list";
    }

    @GetMapping("/modal/{processId}")
    public String getQualityModal(@PathVariable Long processId, Model model) {
        Process process = processService.findOneProcess(processId).orElseThrow(()-> new IllegalArgumentException("해당 공정이 존재하지 않습니다."));
        model.addAttribute("process", process);
        return "fragments/qualityModal :: modalContent"; // Thymeleaf 조각 반환
    }
}
