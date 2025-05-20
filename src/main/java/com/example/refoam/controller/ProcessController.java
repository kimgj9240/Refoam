package com.example.refoam.controller;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.service.OrderService;
import com.example.refoam.service.ProcessService;
import com.example.refoam.service.ProductStandardValue;
import com.example.refoam.service.StandardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final OrderService orderService;
    private final StandardService standardService;

    @GetMapping("/{id}/list")
    public String processList(@PathVariable("id") Long orderId, Model model){
        List<Process> processes = processService.findAllOrder(orderId);
        model.addAttribute("processes",processes);
        model.addAttribute("orderId",orderId);

        return "process/processList";
    }

    @PostMapping("/{id}/list")
    public String startProcess(@PathVariable("id") Long orderId) {
        Orders order = orderService.findOneOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        ProductStandardValue productStandardValue = new ProductStandardValue();

        int quantity = order.getOrderQuantity();
        for (int i = 0; i < quantity; i++) {
            Standard standard = Standard.builder()
                    .meltTemperature(productStandardValue.getRandomValue(ProductStandardValue.MIN_MELT_TEMPERATURE, ProductStandardValue.MAX_MELT_TEMPERATURE))
                    .moldTemperature(productStandardValue.getRandomValue(ProductStandardValue.MIN_MOLD_TEMPERATURE, ProductStandardValue.MAX_MOLD_TEMPERATURE))
                    .timeToFill(productStandardValue.getRandomValue(ProductStandardValue.MIN_TIME_TO_FILL, ProductStandardValue.MAX_TIME_TO_FILL))
                    .plasticizingTime(productStandardValue.getRandomValue(ProductStandardValue.MIN_PLASTICIZING_TIME, ProductStandardValue.MAX_PLASTICIZING_TIME))
                    .cycleTime(productStandardValue.getRandomValue(ProductStandardValue.MIN_CYCLE_TIME, ProductStandardValue.MAX_CYCLE_TIME))
                    .closingForce(productStandardValue.getRandomValue(ProductStandardValue.MIN_CLOSING_FORCE, ProductStandardValue.MAX_CLOSING_FORCE))
                    .clampingForcePeak(productStandardValue.getRandomValue(ProductStandardValue.MIN_CLAMPING_FORCE_PEAK, ProductStandardValue.MAX_CLAMPING_FORCE_PEAK))
                    .torquePeak(productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_PEAK, ProductStandardValue.MAX_TORQUE_PEAK))
                    .torqueMean(productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_MEAN, ProductStandardValue.MAX_TORQUE_MEAN))
                    .backPressurePeak(productStandardValue.getRandomValue(ProductStandardValue.MIN_BACK_PRESSURE_PEAK, ProductStandardValue.MAX_BACK_PRESSURE_PEAK))
                    .injPressurePeak(productStandardValue.getRandomValue(ProductStandardValue.MIN_INJ_PRESSURE_PEAK, ProductStandardValue.MAX_INJ_PRESSURE_PEAK))
                    .screwPosEndHold(productStandardValue.getRandomValue(ProductStandardValue.MIN_SCREW_POS_END_HOLD, ProductStandardValue.MAX_SCREW_POS_END_HOLD))
                    .shotVolume(productStandardValue.getRandomValue(ProductStandardValue.MIN_SHOT_VOLUME, ProductStandardValue.MAX_SHOT_VOLUME))
                    .order(order)
                    .build();

            standardService.save(standard);

            List<Label> labelList = new ArrayList<>();

            Process process = Process.builder()
                    .step("2공정")
                    .status("COMPLETED")
                    .standard(standard)
                    .order(order)
                    .labels(labelList)
                    .processDate(LocalDateTime.now())
                    .build();

            processService.save(process);
        }



        order.setOrderState("공정완료");
        orderService.save(order);

        return "redirect:/process/" + orderId + "/list";
    }

}
