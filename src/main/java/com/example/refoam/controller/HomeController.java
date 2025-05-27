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
    private final OrderRepository orderRepository;
    private final MonitoringService monitoringService;
    private final QualityCheckRepository qualityCheckRepository;


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
                MaterialName.EVA, "rgba(102, 204, 204, 1)",
                MaterialName.CARBON_BLACK, "rgba(50, 50, 50, 1)",
                MaterialName.TITANIUM_DIOXIDE, "rgba(230, 230, 230, 1)",
                MaterialName.ULTRAMARINE_BLUE, "rgba(54, 92, 235, 1)",
                MaterialName.IRON_OXIDE_RED, "rgba(180, 60, 60, 1)"
        );


        List<String> materialColors = materialMap.keySet().stream()
                .map(colorMap::get)
                .toList();

        // 공정 건수 그래프 연습용

        model.addAttribute("materialLabels", materialLabels);
        model.addAttribute("materialData", materialData);
        model.addAttribute("materialColors", materialColors);
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
