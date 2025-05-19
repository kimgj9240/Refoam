package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.dto.LoginForm;
import com.example.refoam.service.LoginService;
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

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
    private final LoginService loginService;
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "home";
    }

    @PostMapping("/")
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

        // 원래 가려던 URL로 이동
        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session != null){
            session.invalidate();
        }

        return "redirect:/home";
    }

    @GetMapping("/main")
    public String main(){
        return "main";}

    @GetMapping("/table")
    public String table(){
        return "table";
    }
    @GetMapping("/form")
    public String form(){
        return "form";
    }
}
