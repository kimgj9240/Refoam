package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.StateCode;
import com.example.refoam.dto.OrderForm;
import com.example.refoam.repository.ProcessRepository;
import com.example.refoam.service.MaterialService;
import com.example.refoam.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MaterialService materialService;
    private final ProcessRepository processRepository;

    @GetMapping("/new")
    public String createOrderform(Model model, HttpSession session){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("orderForm",new OrderForm());
        return "order/createOrderForm";
    }
    @PostMapping("/new")
    public String createOrder(@Valid OrderForm orderForm, BindingResult bindingResult, @ModelAttribute("loginMember") Employee loginMember){
        if(bindingResult.hasErrors()){
            return "order/createOrderForm";
        }

        Orders order = Orders.builder()
                .productName(orderForm.getProductName())
                .orderQuantity(orderForm.getOrderQuantity())
                .orderDate(LocalDateTime.now())
                .employee(loginMember)
                .orderState("준비 중")
                .build();

        orderService.save(order);

        return "redirect:/order/list";
    }

    @GetMapping("/list")
    public String list(Model model){
        List<Orders> ordersList = orderService.findOrders();
        model.addAttribute("ordersList", ordersList);
        return "order/orderList";
    }

}
