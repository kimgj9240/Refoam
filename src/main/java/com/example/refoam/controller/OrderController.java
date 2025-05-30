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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        model.addAttribute("materialLabels", materialLabels);
        model.addAttribute("materialData", materialData);
        model.addAttribute("materialColors", materialColors);
        model.addAttribute("orderForm",new OrderForm());
        return "order/createOrderForm";
    }

    // 제품 생산 주문 저장
    @PostMapping("/new")
    public String createOrder(@Valid OrderForm orderForm, BindingResult bindingResult, @ModelAttribute("loginMember") Employee loginMember){
        // productName에 따른 수량 가져오기
        Map<MaterialName, Long> requiredMaterialStock = materialService.getRequiredMaterialStock(orderForm.getProductName());
        log.info("productName에 따른 수량 {}", requiredMaterialStock);

        // 주문 수량이 10 이상, 10단위인지 확인
        if (orderForm.getOrderQuantity() < 10 || orderForm.getOrderQuantity() % 10 != 0) {
            bindingResult.rejectValue("orderQuantity", "invalidQuantity", "주문 수량은 10개 이상이며, 10단위로만 가능합니다.");
            return "order/createOrderForm";
        }

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

        orderService.newOrder(order);

        return "redirect:/order/list";
    }

    @PostMapping("/{id}/first-process")
    public String mixOrder(@PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "0") int page) {
        Orders order = orderService.findOneOrder(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문은 존재하지 않습니다."));

        // 95% 배합 완료 : 5% 배합 실패
        String state = Math.random() < 0.95 ? "배합완료" : "배합실패";
        order.setOrderState(state);

        // 저장
        orderService.save(order);

        return "redirect:/order/list?page=" + page;
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
//        List<Orders> ordersList = orderService.findOrders();
        Page<Orders> paging = this.orderService.getList(page);
        paging.forEach(order -> System.out.println("order=" + order.getProductName() + ", emp=" + order.getEmployee().getUsername()));
        paging.forEach(order -> {
            if (order.getEmployee() != null) order.getEmployee().getUsername(); // 강제 초기화
        });
//        model.addAttribute("ordersList", ordersList);
        model.addAttribute("ordersList", paging);
        model.addAttribute("activeMenu", 3);
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
