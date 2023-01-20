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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OembedServiceImpl implements OembedService{

    static final ClassPathResource resource = new ClassPathResource("provider.json");

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
     * @param providerUrl 검색 요청 url의 BASE 주소
     * @return endpoint, 검색url, format 타입을 더한 url
     * @throws GlobalExceptionHandler
     * @throws IOException
     * @throws ParseException
     */
    @Override
    public String getRequest(String requestUrl, String providerUrl) throws IOException, ParseException {

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        String endPoint = "";
        String format = "format=json";

        /*
        스키마 패턴을 찾고
        provider json 데이터에서 endpoints 찾기
         */
        out:for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            try {
                /*
                 schemes 정보 가져오기
                 */
                JSONArray endpointArray = (JSONArray) object.get("endpoints");
                JSONObject endpointObject = (JSONObject) endpointArray.get(0);
                JSONArray schemesArr = (JSONArray) endpointObject.get("schemes");

                /*
                schemes 정보 있으면
                배열에서 매칭
                 */
                if(schemesArr != null){
                    log.info("schemes not null");
                    for (Object scheme : schemesArr) {
                        if(Pattern.compile((String) scheme).matcher(requestUrl).find()){
                            endPoint = endpointObject.get("url").toString();

                            /*
                            {format}으로 지정시 json으로 지정
                             */
                            if(endPoint.contains("{format}")){
                                endPoint.replace("{format}", "json");
                            }
                            log.info("endpoint : {}", endPoint);
                            break out;
                        }
                    }
                }else{
                    /*
                    schemes가 없는 것은
                    provider_url 비교
                     */
                    Object url = endpointObject.get("provider_url");
                    if(providerUrl.equals(url)){
                        endPoint = (String) endpointObject.get(url);
                        break out;
                    }
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
