package com.purpleio.backend.controller;

import com.purpleio.backend.exception.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
public class ContentsController {

    @GetMapping("/contents")
    public JSONObject getResponse(HttpServletRequest request) throws ExceptionHandler {
        log.info("요청 url : {}", request.getParameter("url"));

        String url = request.getParameter("url");
        Pattern pattern = Pattern.compile("\\www.(.*?)\\.");
        Matcher matcher = pattern.matcher(url);

        if(matcher.find()){
            System.out.println(matcher.group(1));
        }else throw new ExceptionHandler();

        return null;
    }
}
