package org.example.b01.board.controller.advice;


import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleBindException(BindException bindException){
        log.error(bindException);

        Map<String,String> errorMap = new HashMap<>();
        if(bindException.hasErrors()){
            BindingResult bindingResult = bindException.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(),fieldError.getCode());
            });
        }
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleFKException(Exception e){
        log.error(e);
        Map<String,String> error = new HashMap<>();

        error.put("time",""+System.currentTimeMillis());
        error.put("msg","constraint fails");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({
            NoSuchElementException.class,   //해당 댓글이 존재하지 않을때
            EmptyResultDataAccessException.class    //존재하지 않은 댓글을 삭제하려 할떄
    })
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleNoSuchException(Exception e){
        log.error(e);

        Map<String,String> error = new HashMap<>();

        error.put("time",""+System.currentTimeMillis());
        error.put("msg","No Such Element Exception");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleIllegal(Exception e){
        Map<String,String> msg = new HashMap<>();

        msg.put("time",""+System.currentTimeMillis());
        msg.put("msg",e.getLocalizedMessage());

        return ResponseEntity.badRequest().body(msg);
    }


}