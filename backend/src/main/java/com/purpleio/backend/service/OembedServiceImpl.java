package com.purpleio.backend.service;

import com.purpleio.backend.exception.ExceptionHandler;
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

@Service
public class OembedServiceImpl implements OembedService{

    static final ClassPathResource resource = new ClassPathResource("provider.json");

    @Override
    public String getRequest(String requestUrl, String providerUrl) throws ExceptionHandler, IOException, ParseException {

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        String endPoint = "";
        String format = "format=json";

        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            try {
                if (
                        providerUrl.equals(object.get("provider_url"))
                ) {
                    JSONArray endpointArray = (JSONArray) object.get("endpoints");
                    JSONObject endpointObject = (JSONObject) endpointArray.get(0);
                    endPoint = endpointObject.get("url").toString();

                    break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return endPoint+"?url="+requestUrl+"&"+format;

    }

    @Override
    public JSONObject getResponse(String request) {
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
            connection.disconnect();
        }

        return response;
    }
}
