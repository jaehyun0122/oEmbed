package com.purpleio.backend.service;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedResponse;
import com.purpleio.backend.exception.InValidUrl;
import net.minidev.json.JSONUtil;
import org.apache.http.impl.client.DefaultHttpClient;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class OembedServiceImplTest {

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
        BufferedReader br = null;
        JSONObject response = null;
        try{
            url = new URL(request);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
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


    // MalformedURLException : new url
    @Test
    public void getJson() throws MalformedURLException, InValidUrl {
        URL url = new URL("https://oembed.com/providers.json");
        HttpURLConnection connection = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        JSONArray response = null;
        JSONArray jsonArray = null;

        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONParser parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(sb.toString());

        } catch (IOException| ParseException e) {
            e.printStackTrace();
            throw new InValidUrl("요청에 실패했습니다.");
        }
    }
}
