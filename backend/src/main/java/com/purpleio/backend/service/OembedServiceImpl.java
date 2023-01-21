package com.purpleio.backend.service;

import com.purpleio.backend.exception.GlobalExceptionHandler;
import com.purpleio.backend.exception.InValidUrl;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OembedServiceImpl implements OembedService{

    @Override
    public Boolean isValidUrl(String url) {
        Matcher matcher = Pattern.compile("^((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?").matcher(url);
        if(matcher.find()){
            return true;
        }
        return false;
    }

    @Override
    public String getProvider(String url) throws Exception {
        Matcher matcher = Pattern.compile("(//)(.*?)(/)").matcher(url);

        if(matcher.find()){
            String group = matcher.group(2);
            if(group.startsWith("www.")){
                return group.substring(4, group.indexOf(".com"));
            }else{
                return group.substring(0, group.indexOf("."));
            }
        } else throw new InValidUrl("유효하지 않은 URL 입니다.");

    }

    @Override
    public Boolean isValidProvider(ArrayList<String> list, String provider) {
        for(String providers : list){
            if(provider.equalsIgnoreCase(providers)) return true;
        }
        return false;
    }

    @Override
    public JSONArray getJsonArr() {
        URL url = null;
        try {
            url = new URL("https://oembed.com/providers.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        JSONArray response = null;

        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;

            while((line = br.readLine()) != null){
                sb.append(line);
            }

            JSONParser parser = new JSONParser();
            response = (JSONArray) parser.parse(sb.toString());

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public ArrayList<String> getProviderList() {
        JSONArray jsonArray = getJsonArr();
        ArrayList<String> result = new ArrayList<>();

        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            String provider_name = jsonObject.get("provider_name").toString();
            result.add(provider_name);
        }

        return result;
    }

    /**
     *
     * @param requestUrl 사용자의 검색 url
     * @param provider 검색 요청 url의 BASE 주소
     * @return endpoint, 검색url, format 타입을 더한 url
     * @throws GlobalExceptionHandler
     * @throws IOException
     * @throws ParseException
     */
    @Override
    public String getRequest(String requestUrl, String provider) {

        JSONArray jsonArray = getJsonArr();

        String endPoint = "";
        String format = "format=json";

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
                        return endPoint+"?url="+requestUrl;
                    }

                    break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return endPoint+"?url="+requestUrl+"&"+format;

    }

    /**
     *
     * @param request 플랫폼의 endpoint, 사용자 요청 url, 응답 데이터 포맷 조합
    * @return 검색 결과
     */
    @Override
    public JSONObject getResponse(String request) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedReader br = null;
        JSONObject response = null;

        /*
        Get 요청 후
        응답 데이터를 JSON 객체로 변환
         */
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
            connection.disconnect();
        }

        return response;
    }
}
