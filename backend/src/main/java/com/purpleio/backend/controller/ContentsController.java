package com.purpleio.backend.controller;

import com.purpleio.backend.service.OembedService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

        // request에서 provider 파싱
        String provider = oembedService.getProvider(url);
        log.info("provider : {}", provider);
        JSONObject response = null;

        // provider_url에 provider 포함하는지
        if(
                oembedService.isValidProvider(
                        oembedService.getProviderList(), provider)
        ){
            log.info("provider 매칭 성공");
            String requestUrl = oembedService.getRequest(url, provider);
            response = oembedService.getResponse(requestUrl);
        }else{
            log.info("provider 매칭 실패");
            throw new Exception();
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
