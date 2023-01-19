package com.purpleio.backend.controller;

import com.purpleio.backend.exception.ExceptionHandler;
import com.purpleio.backend.service.OembedService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
public class ContentsController {

    @Autowired
    private OembedService oembedService;

    @GetMapping("/contents")
    public ResponseEntity<JSONObject> getResponse(HttpServletRequest request) throws ExceptionHandler, Exception {
        log.info("요청 url : {}", request.getParameter("url"));

        String url = request.getParameter("url");
        Pattern pattern = Pattern.compile("\\bhttps(.*?)\\b/");
        Matcher matcher = pattern.matcher(url);

        JSONObject response = null;

        if(matcher.find()){
            String requestUrl = oembedService.getRequest(url, matcher.group(0));
            response = oembedService.getResponse(requestUrl);
        }else throw new ExceptionHandler();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
