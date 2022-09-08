package org.example.b01.board.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.dto.ReplyDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
public class ReplyController {

    @ApiOperation(value = "Replies POST",notes = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Long>> register(@Valid @RequestBody ReplyDTO replyDTO,
                                                     BindingResult bindingResult) throws BindException {
        log.info(replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",111L);

            //메소드 리턴 값에 문제가 있다면 @RestControllerAdvice가 처리할 것이므로 정상적인 결과만을 리턴할 것이다.
        return ResponseEntity.ok(resultMap);
    }

}
