package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.PositionName;
import com.example.refoam.dto.EmployeeDto;
import com.example.refoam.dto.EmployeeForm;
import com.example.refoam.dto.EmployeeUpdateForm;
import com.example.refoam.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        model.addAttribute("activeMenu", 1);
        return "employee/createEmployeeForm";
    }

    @PostMapping("/new")
    public String createForm(@Valid EmployeeForm employeeForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "employee/createEmployeeForm";
        }
        Employee findEmployee = employeeService.validateDuplicate(employeeForm.getLoginId());

        if (findEmployee != null) {
            bindingResult.reject("DuplicateId","id가 존재합니다.");
            return "employee/createEmployeeForm";
        }

        Employee employee = Employee.builder()
                .loginId(employeeForm.getLoginId())
                .username(employeeForm.getUsername())
                .password(employeeForm.getPassword())
                .position(PositionName.valueOf(employeeForm.getPosition()))
                .email(employeeForm.getEmail())
                .sendMail(employeeForm.isSendMail())
                .build();

        employeeService.save(employee);
        return "redirect:/employee/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
//        List<Employee> employees = employeeService.employeeList();

        Page<EmployeeDto> paging = employeeService.getList(page); // DTO 기반 페이징

        model.addAttribute("paging", paging);

//        model.addAttribute("employees", employees);
        model.addAttribute("activeMenu", 1);
        return "employee/employeeList";
    }

    @GetMapping("/{employeeId}/edit")
    public String update(@PathVariable("employeeId") Long employeeId, Model model){
        Employee updEmployee =  employeeService.findOneEmployee(employeeId).orElseThrow(()-> new IllegalArgumentException("해당 직원을 찾을 수 없습니다"));

        EmployeeForm employeeForm = EmployeeForm.builder()
                .id(updEmployee.getId())
                .loginId(updEmployee.getLoginId())
                .username(updEmployee.getUsername())
                .password(updEmployee.getPassword())
                .position(String.valueOf(updEmployee.getPosition()))
                .email(updEmployee.getEmail())
                .sendMail(updEmployee.isSendMail())
                .build();

        model.addAttribute("employeeForm", employeeForm);
        model.addAttribute("activeMenu", 1);
        return "employee/editEmployeeForm";
        }



    @PostMapping("/{employeeId}/edit")
    public String updateEmployeeForm(@Valid @ModelAttribute("employeeForm")EmployeeUpdateForm employeeForm, BindingResult bindingResult, Model model , HttpSession session){
        if (bindingResult.hasErrors()) {
            Employee employee = employeeService.findOneEmployee(employeeForm.getId()).orElseThrow(()-> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

            employeeForm.setId(employee.getId());
            employeeForm.setLoginId(employee.getLoginId());
            employeeForm.setUsername(employee.getUsername());
            employeeForm.setSendMail(employee.isSendMail());
            model.addAttribute("employeeForm", employeeForm);
            return "employee/editEmployeeForm";
        }

        Employee employee = employeeService.findOneEmployee(employeeForm.getId()).orElseThrow(()-> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

        String newPassword = employeeForm.getPassword();
        if (newPassword == null || newPassword.isBlank()) {
            newPassword = employee.getPassword();
        }

        Employee updateEmployee = employee.toBuilder()
                .password(newPassword)
                .position(PositionName.valueOf(employeeForm.getPosition()))
                .email(employeeForm.getEmail())
                .sendMail(employeeForm.isSendMail())
                .build();
        employeeService.save(updateEmployee);
        // 로그인한 사용자 직위 확인 후 리다이렉트
        Employee loginUser = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginUser != null && loginUser.getPosition() == PositionName.ADMIN) {
            return "redirect:/employee/list";
        } else {
            return "redirect:/main";
        }
    }

    @GetMapping("/{employeeId}/delete")
    public String deleteEmployee(@PathVariable("employeeId") Long employeeId, RedirectAttributes redirectAttributes, HttpSession session) {
        log.info("직원 id ? {}", employeeId);
        Employee employee = employeeService.findOneEmployee(employeeId).orElseThrow(()-> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

        Employee loginUser = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 로그인 안 되어있거나 매니저가 아님
        if (loginUser == null || loginUser.getPosition() != PositionName.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 권한이 없습니다.");
            return "redirect:/employee/list";
        }

        // 본인 계정은 삭제 불가
        if (loginUser.getId().equals(employeeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "자기 자신의 계정은 삭제할 수 없습니다.");
            return "redirect:/employee/list";
        }
        //여기부터
        if (employee.getLoginId().equals("test")) {
            redirectAttributes.addFlashAttribute("errorMessage", "관리자계정은 삭제 할 수 없습니다.");
            return "redirect:/employee/list";
        }
        log.info("삭제 id? {}", employee.getId());
        employeeService.deleteEmployee(employee.getId());
        return "redirect:/employee/list";
    }
}
