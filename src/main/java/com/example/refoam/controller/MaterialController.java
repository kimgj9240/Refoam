package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.Material;
import com.example.refoam.dto.MaterialForm;
import com.example.refoam.dto.OrderPredictionInput;
import com.example.refoam.dto.PredictRequest;
import com.example.refoam.dto.PredictResult;
import com.example.refoam.service.MaterialService;
import com.example.refoam.service.PredictService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/material")
public class MaterialController {
    private final MaterialService materialService;
    private final PredictService predictService;
    @GetMapping("/new")
    public String createForm(HttpSession session, Model model){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        PredictRequest predictRequest = new PredictRequest();
        if(loginMember==null){
            return "redirect:/login";
        }

        //반드시 orders초기화
        ArrayList<OrderPredictionInput> list = new ArrayList<>();
        list.add(new OrderPredictionInput());
        list.add(new OrderPredictionInput());
        list.add(new OrderPredictionInput());

        log.info("list{}", list);

        predictRequest.setOrders(list);

        model.addAttribute("predictRequest", predictRequest);
        model.addAttribute("materialForm",new MaterialForm());
        model.addAttribute("activeMenu", 2);
        return "material/createMaterialForm";
    }
    @PostMapping("/new")
    public String create(@Valid MaterialForm materialForm, BindingResult bindingResult, @ModelAttribute("loginMember") Employee loginMember){
        if(bindingResult.hasErrors()){
            return "material/createMaterialForm";
        }
        Material material = Material.builder()
                .materialName(materialForm.getMaterialName())
                .materialQuantity(materialForm.getMaterialQuantity())
                .employee(loginMember)
                .materialDate(LocalDateTime.now())
                .build();
        materialService.save(material);
        return "redirect:/material/list";
    }

    @PostMapping("/orderPredict")
    public String predict(@ModelAttribute PredictRequest predictRequest, Model model) {
        /*if(bindingResult.hasErrors()){
            return "material/createMaterialForm";
        }*/
        PredictResult result = predictService.getPrediction(predictRequest);
        log.info("예측결과는? {} ", predictRequest.getOrders());
        model.addAttribute("materialForm",new MaterialForm());
        model.addAttribute("prediction", result.getPrediction());
        model.addAttribute("predictedDate", result.getPredictedDate());
        return "material/createMaterialForm";
    }
    @GetMapping("/list")
    public String list(Model model){
        List<Material> materialList = materialService.selectAll();
        model.addAttribute("activeMenu", 2);
        model.addAttribute("materialList",materialList);
        return "material/materialList";
    }
    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable("id") Long id, Model model){
        Material findMaterial = materialService.findOne(id).orElseThrow(()-> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));
        Material materialForm = Material.builder()
                .id(findMaterial.getId())
                .materialName(findMaterial.getMaterialName())
                .materialQuantity(findMaterial.getMaterialQuantity())
                .employee(findMaterial.getEmployee())
                .materialDate(findMaterial.getMaterialDate())
                .build();
        model.addAttribute("materialForm",materialForm);
        return "material/editMaterialForm";
    }
    @PostMapping("/{id}/edit")
    public String update(@Valid MaterialForm materialForm, BindingResult bindingResult, @PathVariable("id") Long id, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("materialForm",materialForm);
            return "/material/editMaterialForm";
        }
        Material findMaterial = materialService.findOne(id).orElseThrow(()-> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));
        Material updateMaterial = findMaterial.toBuilder()
                .materialName(materialForm.getMaterialName())
                .materialQuantity(materialForm.getMaterialQuantity())
                .build();
        materialService.save(updateMaterial);
        return "redirect:/material/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id){
        Material delMaterial = materialService.findOne(id).orElseThrow(()-> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));
        materialService.delete(delMaterial.getId());
        return "redirect:/material/list";
    }
}
