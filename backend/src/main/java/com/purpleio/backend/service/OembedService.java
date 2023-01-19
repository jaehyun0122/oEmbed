package com.purpleio.backend.service;


import com.purpleio.backend.exception.ExceptionHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface OembedService {
    String getRequest(String requestUrl, String providerUrl) throws ExceptionHandler, IOException, ParseException;

    JSONObject getResponse(String request) throws IOException;
}
