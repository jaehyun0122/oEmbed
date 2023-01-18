package com.purpleio.backend.controller;

import com.purpleio.backend.exception.ExceptionHandler;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@SpringBootTest
public class ContentsControllerTest {

    @Test
    public void getProvider(){
        String testUrl = "https://www.youtube.com/watch?v=dBD54EZIrZo";

        Pattern pattern = Pattern.compile("\\bhttps(.*?)\\b/");
        Matcher matcher = pattern.matcher(testUrl);
        if(matcher.find()){
            System.out.println(matcher.group(0));
        }else System.out.println("null");

    }

    @Test
    public void providerSearch() {
        ClassPathResource resource = new ClassPathResource("provider.json");
        Assertions.assertThat(resource.getFilename()).isEqualTo("provider.json");
    }

    @Test
    public void getRequest() throws IOException, ParseException, ExceptionHandler {
        String url = "https://www.youtube.com/";
        String testUrl = "https://www.youtube.com/watch?v=Oxd2n27ZznE";

        ClassPathResource resource = new ClassPathResource("provider.json");
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        String endPoint = "";
        String format = "format=json";

        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            if(
                    url.equals(object.get("provider_url")) || (url+"/").equals(object.get("provider_url"))

            ) {
                JSONArray endpointArray = (JSONArray) object.get("endpoints");
                JSONObject endpointObject = (JSONObject) endpointArray.get(0);

                endPoint = endpointObject.get("url").toString();

                break;
            }
//            else{
//                throw new ExceptionHandler();
//            }
        }

        String request = endPoint+"?url="+testUrl+"&"+format;
        System.out.println(request);
    }


}
