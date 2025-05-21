package com.example.refoam.controller;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
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
import java.util.Map;
import java.util.Random;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MaterialService materialService;
    private final ProcessRepository processRepository;

    // 제품 생산 페이지
    @GetMapping("/new")
    public String createOrderform(Model model, HttpSession session){
        // 재료 재고 확인
        Map<MaterialName, Long> materialQuantities = materialService.getMaterialQuantities();
        log.info("총 재료 {}", materialQuantities);

        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("orderForm",new OrderForm());
        return "order/createOrderForm";
    }

    // 제품 생산 주문 저장
    @PostMapping("/new")
    public String createOrder(@Valid OrderForm orderForm, BindingResult bindingResult, @ModelAttribute("loginMember") Employee loginMember){
        // productName에 따른 수량 가져오기
        Map<MaterialName, Long> requiredMaterialStock = materialService.getRequiredMaterialStock(orderForm.getProductName());
        log.info("productName에 따른 수량 {}", requiredMaterialStock);

        if(bindingResult.hasErrors()){
            return "order/createOrderForm";
        }

        //재고가 충분한지 검사, 부족하면 글로벌 에러 발생
        if(!materialService.isEnoughMaterial(orderForm.getProductName(),orderForm.getOrderQuantity())){
            bindingResult.reject("notEnoughMaterial","재고가 부족합니다.");
            return "order/createOrderForm";
        }
        //processRepository.findAllByOrder_Id()
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

    @PostMapping("/{id}/first-process")
    public String mixOrder(@PathVariable("id") Long id) {
        Orders order = orderService.findOneOrder(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문은 존재하지 않습니다."));

        // 80% 확률로 '배합완료', 20% 확률로 '배합실패'
        String state = Math.random() < 0.8 ? "배합완료" : "배합실패";
        order.setOrderState(state);

        orderService.save(order);

        return "redirect:/order/list";
    }

    @GetMapping("/list")
    public String list(Model model){
        List<Orders> ordersList = orderService.findOrders();
        model.addAttribute("ordersList", ordersList);
        return "order/orderList";
    }

    // 주문 취소
    @GetMapping("/{id}/delete")
    public String deleteOrder(@PathVariable ("id") Long id){
        orderService.findOneOrder(id).orElseThrow(() -> new IllegalArgumentException("해당 주문은 존재하지 않습니다."));
        orderService.deleteOrder(id);

        return "redirect:/order/list";
    }
}
