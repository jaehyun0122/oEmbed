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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class ContentsControllerTest {

    @Test
    public void getProvider() throws IOException, ParseException {
        String url1 = "https://twitter.com/watch?v=dBD54EZIrZo";
        String url2 = "https://www.twitter.com/watch?v=dBD54EZIrZo";

        Matcher matcher = Pattern.compile("(//)(.*?)(/)").matcher(url1);
        if(matcher.find()){
            String group = matcher.group(2);
            if(group.startsWith("www.")){
                Assertions.assertThat(group.substring(4, group.indexOf(".com"))).isEqualTo("twitter");
            }else{
                Assertions.assertThat(group.substring(0, group.indexOf("."))).isEqualTo("twitter");
            }
        }else System.out.println("miss");
    }

    @Test
    public void searchProvider() {
        ClassPathResource resource = new ClassPathResource("provider.json");
        Assertions.assertThat(resource.getFilename()).isEqualTo("provider.json");
    }

    @Test
    public void getRequest() throws IOException, ParseException {
        String provider = "vimeo";
        String requestUrl = "https://vimeo.com/20097015";
        String endPoint = "";
        String format = "format=json";

        ClassPathResource resource = new ClassPathResource("provider.json");
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));
        out:for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            try {
                // provider가 provider_url에 포함되면
                // endpoint 가져오기
                if(object.get("provider_url").toString().contains(provider)){
                    JSONArray endpointsArr = (JSONArray) object.get("endpoints");
                    JSONObject endpointsObj = (JSONObject) endpointsArr.get(0);
                    endPoint = endpointsObj.get("url").toString();

                    if(endPoint.contains("{format}")){
                        endPoint = endPoint.replace("{format}", "json");
                        System.out.println(endPoint);
                    }

                    break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println(endPoint+"?url="+requestUrl+"&"+format);

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

    @Test
    public void getProviderList() throws IOException, ParseException {
        ClassPathResource resource = new ClassPathResource("provider.json");
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        ArrayList<String> result = new ArrayList<>();

        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            String provider_name = jsonObject.get("provider_name").toString();
            result.add(provider_name);
        }

        Assertions.assertThat(result.size()).isEqualTo(288);
    }

}
