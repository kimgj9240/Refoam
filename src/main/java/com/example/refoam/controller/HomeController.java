package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.MaterialName;
import com.example.refoam.domain.Orders;
import com.example.refoam.domain.QualityCheck;
import com.example.refoam.dto.LoginForm;
import com.example.refoam.dto.ProductionMonitoring;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.QualityCheckRepository;
import com.example.refoam.service.LoginService;
import com.example.refoam.service.MaterialService;
import com.example.refoam.service.MonitoringService;
import com.example.refoam.service.OpenAiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
    private final LoginService loginService;
    private final MaterialService materialService;
    private final MonitoringService monitoringService;



    @GetMapping("/")
    public String home(HttpSession session, Model model,
                       @RequestParam(value = "redirectURL", defaultValue = "/main") String redirectURL) {
        if (session.getAttribute(SessionConst.LOGIN_MEMBER) != null) {
            return "redirect:" + redirectURL;
        }
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("redirectURL", redirectURL);
        return "home";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, @RequestParam(value = "redirectURL", defaultValue = "/main")String redirectURL, HttpServletRequest request){
        if (bindingResult.hasErrors()){
            return "home";
        }

        Employee loginMember = loginService.login(loginForm.getLoginId(),loginForm.getPassword());

        if (loginMember == null){
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
            return "home";
        }
        // 로그인 성공
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER,loginMember);
        log.info("직위: {}", loginMember.getPosition());

        // 원래 가려던 URL로 이동
        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session != null){
            session.invalidate();
        }
        // 로그아웃 후 로그인 페이지에서 로그인 할 시 새로고침 한 번 일어나는거 때문에 /로 다시 바꿈
        return "redirect:/";
    }

    @GetMapping("/main")
    public String main(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Map<MaterialName, Long> rawMap = materialService.getMaterialQuantities();

        // 재고 차트 순서 고정 (새로고침시 순서 바뀌는 거 방지)
        Map<MaterialName, Long> materialMap = rawMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a,b) -> a,
                        LinkedHashMap::new
                ));

        List<String> materialLabels = materialMap.keySet().stream()
                .map(Enum::name)
                .toList();

        List<Long> materialData = materialMap.values().stream().toList();

        // 원자재 그래프 막대 색 지정
        Map<MaterialName, String> colorMap = Map.of(
                MaterialName.EVA, "rgba(217,240,240, 1)",
                MaterialName.P_BLACK, "rgba(202,202,202, 1)",
                MaterialName.P_WHITE, "rgba(255,255,255, 1)",
                MaterialName.P_BLUE, "rgba(213,234,249, 1)",
                MaterialName.P_RED, "rgba(253,207,223, 1)"
        );


        List<String> materialColors = materialMap.keySet().stream()
                .map(colorMap::get)
                .toList();


        List<ProductionMonitoring> productionMonitorings = monitoringService.productionMonitorings();

        Map<String, Integer> kpiMap = monitoringService.targetAchievement(100, 400);
        Map<String, Long> errorCounts = monitoringService.errorCounts();

        int target = kpiMap.get("targetQuantity");
        int ok = kpiMap.get("okCount");
        int rate = kpiMap.get("achievementRate");
        long errTemp = errorCounts.getOrDefault("ERR_TEMP", 0L);
        long errTime = errorCounts.getOrDefault("ERR_TIME", 0L);
        long mixFail = errorCounts.getOrDefault("배합실패", 0L);


        String prompt = String.format(
                """
                오늘의 생산 리포트를 작성해줘. 다음 정보를 참고해서 다음 4가지 항목을 포함해줘:
                1. 전반적인 생산 요약
                2. 불량 유형별 통계와 원인 분석
                3. 성과 분석 (목표 대비 달성률)
                4. 내일을 위한 개선 방향 또는 경고
            
                📊 생산 성과:
                - 목표 수량: %d개
                - OK 수량: %d개
                - 달성률: %d%%
            
                ⚠️ 에러 통계:
                - 온도 에러: %d건
                - 시간 에러: %d건
                - 배합 실패: %d건
            
                관리자에게 보고하는 형식으로 작성해줘. 포맷은 깔끔하고 핵심 위주로, 너무 길지 않게.
                """, target, ok, rate, errTemp, errTime, mixFail
        );



        model.addAttribute("materialLabels", materialLabels);
        model.addAttribute("materialData", materialData);
        model.addAttribute("materialColors", materialColors);
        model.addAttribute("productionMonitorings", productionMonitorings);
        model.addAttribute("achievementRate", kpiMap.get("achievementRate"));
        model.addAttribute("targetRate", 80);
        model.addAttribute("targetQuantity", kpiMap.get("targetQuantity"));//오늘의 달성목표수량 100단위로만 생성되도록
        model.addAttribute("targetAchieveQuantity", kpiMap.get("targetAchieveQuantity"));
        model.addAttribute("okCount", kpiMap.get("okCount"));
        return "main";}
    
    // 로그아웃 후 다른 아이디로 로그인했을 때 404 에러 뜨는거 방지용으로 만듦
    @GetMapping("/home")
    public String homeRedirect(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "home";
    }



    @GetMapping("/table")
    public String table(){
        return "table";
    }
    @GetMapping("/form")
    public String form(){
        return "form";
    }
}
