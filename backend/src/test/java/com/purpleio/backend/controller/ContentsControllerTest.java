package com.purpleio.backend.controller;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@SpringBootTest
public class ContentsControllerTest {

    @Test
    public void urlTest(){
        String testUrl = "https://www.youtube.com/watch?v=dBD54EZIrZo";

        Pattern pattern = Pattern.compile("\\www.(.*?)\\.");
        Matcher matcher = pattern.matcher(testUrl);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }else System.out.println("null");

    }
}
