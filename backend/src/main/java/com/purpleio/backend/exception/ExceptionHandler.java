package com.purpleio.backend.exception;

import com.purpleio.backend.controller.ContentsController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

public class ExceptionHandler extends Throwable {

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<String> noData(Exception exception){
        return new ResponseEntity<String>("데이터가 없습니다.", HttpStatus.NOT_FOUND);
    }

}
