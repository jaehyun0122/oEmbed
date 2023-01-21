package com.purpleio.backend.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Map<String, String> test(Exception e){
        Map<String, String> res = new HashMap<>();
        res.put("errorMsg", e.getMessage());
        res.put("status", "error");
        return res;
    }


}
