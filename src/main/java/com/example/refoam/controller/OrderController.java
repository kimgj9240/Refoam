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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MaterialService materialService;
    private final ProcessRepository processRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // GET 요청
    @GetMapping("/new")
    public String createOrderform(Model model, HttpSession session){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return "redirect:/login";
        }

        setChartData(model);
        model.addAttribute("orderForm", new OrderForm());
        return "order/createOrderForm";
    }

    // POST 요청
    @PostMapping("/new")
    public String createOrder(@Valid OrderForm orderForm, BindingResult bindingResult,
                              @ModelAttribute("loginMember") Employee loginMember, Model model){

        if(bindingResult.hasErrors()){
            setChartData(model);
            return "order/createOrderForm";
        }

        if(!materialService.isEnoughMaterial(orderForm.getProductName(),orderForm.getOrderQuantity())){
            bindingResult.reject("notEnoughMaterial","재고가 부족합니다.");
            setChartData(model);
            return "order/createOrderForm";
        }

        if (orderForm.getOrderQuantity() < 10 || orderForm.getOrderQuantity() % 10 != 0) {
            bindingResult.rejectValue("orderQuantity", "invalidQuantity", "주문 수량은 10개 이상이며, 10단위로만 가능합니다.");
            setChartData(model);
            return "order/createOrderForm";
        }

        Orders order = Orders.builder()
                .productName(orderForm.getProductName())
                .orderQuantity(orderForm.getOrderQuantity())
                .orderDate(LocalDateTime.now())
                .employee(loginMember)
                .orderState("준비 중")
                .build();

        orderService.newOrder(order);
        return "redirect:/order/list";
    }

    // 배합 공정
    @PostMapping("/{id}/first-process")
    public String mixOrder(@PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        Orders order = orderService.findOneOrder(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문은 존재하지 않습니다."));
        //여기부터
        if (!order.getOrderState().equals("준비 중")) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 진행된 공정입니다.");
            return "redirect:/order/list?page=" + page;
        }

        // 95% 배합 완료 : 5% 배합 실패
        String state = Math.random() < 0.95 ? "배합완료" : "배합실패";
        order.setOrderState(state);

        // WebSocket으로 배합 상태 브로드캐스트
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", id);
        payload.put("state", state);

        messagingTemplate.convertAndSend("/topic/mixing", payload);
        // 저장
        orderService.save(order);

        return "redirect:/order/list?page=" + page;
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Orders> paging = this.orderService.getList(page);
        paging.forEach(order -> System.out.println("order=" + order.getProductName() + ", emp=" + order.getEmployee().getUsername()));
        paging.forEach(order -> {
            if (order.getEmployee() != null) order.getEmployee().getUsername(); // 강제 초기화
        });
        paging.forEach(order -> processRepository.countTodayByOrderAndStatusNot(order,"OK",order.getProductName()));
        model.addAttribute("ordersList", paging);
        model.addAttribute("activeMenu", 3);
        return "order/orderList";
    }

    // 주문 취소
    @GetMapping("/{id}/delete")
    public String deleteOrder(@PathVariable ("id") Long id, RedirectAttributes redirectAttributes){
        try {
            Orders order = orderService.findOneOrder(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문은 존재하지 않습니다."));

            if (!order.getOrderState().equals("준비 중")) {
                redirectAttributes.addFlashAttribute("errorMessage", "이미 진행된 공정입니다.");
                return "redirect:/order/list";
            }

            orderService.deleteOrder(id);
            return "redirect:/order/list";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 삭제된 주문입니다.");
            return "redirect:/order/list";
        }
    }

    private void setChartData(Model model) {
        Map<MaterialName, Long> rawMap = materialService.getMaterialQuantities();

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

        model.addAttribute("materialLabels", materialLabels);
        model.addAttribute("materialData", materialData);
        model.addAttribute("materialColors", materialColors);
    }
}

