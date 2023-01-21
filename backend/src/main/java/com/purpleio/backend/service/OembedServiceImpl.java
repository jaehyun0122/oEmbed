package com.purpleio.backend.service;

import com.purpleio.backend.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OembedServiceImpl implements OembedService{

    static final ClassPathResource resource = new ClassPathResource("provider.json");

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
        } else throw new Exception();

    }

    @Override
    public Boolean isValidProvider(ArrayList<String> list, String provider) {
        for(String providers : list){
            if(provider.equalsIgnoreCase(providers)) return true;
        }
        return false;
    }

    @Override
    public ArrayList<String> getProviderList() throws IOException, ParseException {
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));
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
     * @param url
     * @return https ~ / 까지
     */
    @Override
    public Matcher getMatch(String url) {
        Pattern pattern = Pattern.compile("\\bhttps(.*?)\\b/");
        Matcher matcher = pattern.matcher(url);

        return matcher;
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
    public String getRequest(String requestUrl, String provider) throws IOException, ParseException {

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));

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
