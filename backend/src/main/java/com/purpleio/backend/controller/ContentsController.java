package com.purpleio.backend.controller;

import com.purpleio.backend.exception.InValidProvider;
import com.purpleio.backend.exception.InValidUrl;
import com.purpleio.backend.service.OembedService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

        if(!oembedService.isValidUrl(url)){
            log.info("유효하지 않은 URL");
            throw new InValidUrl("유효하지 않은 URL 입니다.");
        }

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
            throw new InValidProvider("제공하지 않는 플랫폼 입니다.");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
