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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
            System.out.println(matcher.group(0)); // https://www.youtube.com/
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
            try {
                if (
                        url.equals(object.get("provider_url"))
                ) {
                JSONArray endpointArray = (JSONArray) object.get("endpoints");
                System.out.println("arra "+endpointArray);
                JSONObject endpointObject = (JSONObject) endpointArray.get(0);
                System.out.println("object "+endpointObject);
                endPoint = endpointObject.get("url").toString();

                    break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        String request = endPoint+"?url="+testUrl+"&"+format; // https://www.youtube.com/oembed?url=https%3A//youtube.com/watch%3Fv%3DM3r2XDceM6A&format=json
        System.out.println(request);
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


}
