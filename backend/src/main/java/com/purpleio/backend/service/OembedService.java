package com.purpleio.backend.service;


import com.purpleio.backend.exception.InValidUrl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public interface OembedService {
    String getProvider(String url) throws Exception;

    String getRequest(String requestUrl, String provider) throws IOException, ParseException;

    JSONObject getResponse(String request) throws IOException;

    ArrayList<String> getProviderList() throws IOException, ParseException;

    Boolean isValidProvider(ArrayList<String> list, String provider);

    JSONArray getJsonArr() throws InValidUrl;

}
