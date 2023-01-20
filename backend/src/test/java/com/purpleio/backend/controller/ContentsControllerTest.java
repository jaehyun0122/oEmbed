package com.purpleio.backend.controller;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class ContentsControllerTest {

    @Test
    public void getProvider(){
        String testUrl = "https://www.youtube.com/watch?v=dBD54EZIrZo";

        Pattern pattern = Pattern.compile("\\bhttps(.*?)\\b/");
        Matcher matcher = pattern.matcher(testUrl);
        if(matcher.find()){
            System.out.println(matcher.group(0)); // https://www.youtube.com/
        }else System.out.println("null");

    }

    @Test
    public void searchProvider() {
        ClassPathResource resource = new ClassPathResource("provider.json");
        Assertions.assertThat(resource.getFilename()).isEqualTo("provider.json");
    }

    @Test
    public void getRequest() throws IOException, ParseException {
        String url = "https://www.twitter.com/";
        String testUrl = "https://www.youtube.com/watch?v=dBD54EZIrZo";;

        ClassPathResource resource = new ClassPathResource("provider.json");
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        String endPoint = "";
        String format = "format=json";

        out:for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            try {
                // 스키마에서 패턴 찾아서
                JSONArray endpointArray = (JSONArray) object.get("endpoints");
                JSONObject endpointObject = (JSONObject) endpointArray.get(0);
                JSONArray schemesArr = (JSONArray) endpointObject.get("schemes");

                if(schemesArr != null){
                    for (Object o1 : schemesArr) {
                        if(Pattern.compile((String) o1).matcher(testUrl).find()){
                            endPoint = endpointObject.get("url").toString();
                            System.out.println(endPoint);

                            break out;
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Test
    public void schemesMatch() throws IOException, ParseException {
        String url = "https://www.youtube.com/watch?v=dBD54EZIrZo";
        String regex = "https://*.youtube.com/watch*";

        System.out.println(Pattern.compile(regex).matcher(url));
//        String youtubeU = "https://www.youtube.com/watch?v=dBD54EZIrZo";
//        String twitterU = "https://twitter.com/hellopolicy/status/867177144815804416";
//
        String[] youtube = {
                "https://*.youtube.com/watch*",
                "https://*.youtube.com/v/*",
                "https://youtu.be/*",
                "https://*.youtube.com/playlist?list=*",
                "https://youtube.com/playlist?list=*",
                "https://*.youtube.com/shorts*"
        };
//
//        String[] twitter={
//                "https://twitter.com/*",
//                "https://twitter.com/*/status/*",
//                "https://*.twitter.com/*/status/*"
//        };
//
//        for(String pattern : twitter){
//            Pattern pattern1 = Pattern.compile(pattern);
//            Matcher matcher = pattern1.matcher(twitterU);
//
//            if(matcher.find()){
//                System.out.println("matching");
//                System.out.println(pattern);
//                return;
//            }
//        }
//        System.out.println("no matching");
    }


    @Test
    public void httpRequest() throws IOException {
        String request = "https://www.youtube.com/oembed?url=https%3A//youtube.com/watch%3Fv%3DM3r2XDceM6A&format=json";

        URL url = null;
        HttpURLConnection connection = null;
        StringBuilder sb = null;
        BufferedReader br = null;
        JSONObject response = null;
        try{
            url = new URL(request);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            sb = new StringBuilder();
            String line;

            while((line = br.readLine()) != null){
                JSONParser jsonParser = new JSONParser();
                response = (JSONObject) jsonParser.parse(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            br.close();
            connection.disconnect();
        }

        System.out.println(connection.getResponseCode());
        System.out.println(response);

    }

    @Test
    public void pattern(){
        String url = "https://twitter.com/hellopolicy/status/867177144815804416";
        String[] strArr = new String[]{
                "https://twitter.com/*",
                "https://twitter.com/*/status/*",
                "https://*.twitter.com/*/status/*"
        };

        for(String str : strArr){
            if(Pattern.compile(str).matcher(url).find()){
                System.out.println(str);
            }
        }
    }


}
