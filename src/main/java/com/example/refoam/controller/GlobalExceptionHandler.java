package com.example.refoam.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleOpenAiError(HttpClientErrorException e, Model model) {
        model.addAttribute("error", "OpenAI API 호출 실패: " + e.getStatusCode());
        return "error/err";
    }
}
