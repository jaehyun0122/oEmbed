package com.purpleio.backend.controller;

import com.purpleio.backend.service.OembedService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;

@RestController
@Slf4j
public class ContentsController {

    private final OembedService oembedService;

    public ContentsController(OembedService oembedService) {
        this.oembedService = oembedService;
    }

    @GetMapping("/contents")
    public ResponseEntity<JSONObject> getResponse(HttpServletRequest request) throws Exception {
        log.info("요청 url : {}", request.getParameter("url"));

        String url = request.getParameter("url");
        Matcher matcher = oembedService.getMatch(url);
        JSONObject response = null;

        if(matcher.find()){
            log.info("Base url : {}", matcher.group(0));
            String requestUrl = oembedService.getRequest(url, matcher.group(0));
            log.info("Oembed 요청 url : {}", requestUrl);
            response = oembedService.getResponse(requestUrl);
        }else throw new Exception();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
