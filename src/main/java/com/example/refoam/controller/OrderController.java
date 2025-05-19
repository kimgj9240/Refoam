package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.dto.OrderForm;
import com.example.refoam.service.MaterialService;
import com.example.refoam.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MaterialService materialService;

    @GetMapping("/new")
    public String createOrderform(Model model, HttpSession session){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("orderForm",new OrderForm());
        return "order/createOrderForm";
    }
    @GetMapping("/list")
    public String list(){
        return "order/orderList";
    }
}
