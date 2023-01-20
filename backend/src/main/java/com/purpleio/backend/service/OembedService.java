package com.purpleio.backend.service;


import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.regex.Matcher;

public interface OembedService {
    Matcher getMatch(String url);

    String getRequest(String requestUrl, String providerUrl) throws IOException, ParseException;

    JSONObject getResponse(String request) throws IOException;
}
