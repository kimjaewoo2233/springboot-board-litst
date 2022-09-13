package org.example.b01.board.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.dto.PageRequestDTO;
import org.example.b01.board.dto.PageResponseDTO;
import org.example.b01.board.dto.ReplyDTO;
import org.example.b01.board.service.ReplyService;
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
@RequiredArgsConstructor
@Log4j2
public class ReplyController {

    private final ReplyService replyService;

    @ApiOperation(value = "Replies POST",notes = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Long>> register(@Valid @RequestBody ReplyDTO replyDTO,
                                                     BindingResult bindingResult) throws BindException {
        log.info(replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",replyService.register(replyDTO));
            //
            //메소드 리턴 값에 문제가 있다면 @RestControllerAdvice가 처리할 것이므로 정상적인 결과만을 리턴할 것이다.
        return ResponseEntity.ok(resultMap);
    }

    @ApiOperation(value = "Replies of Board", notes = "GET 방식으로 특정 게시물의 댓글 목록")
    @GetMapping("/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno,
                                             PageRequestDTO pageRequestDTO){
            PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno,pageRequestDTO);    //해당 bno에 달린 댓글을 Page로 가져옴
            return responseDTO;
    }

    @ApiOperation(value = "Read Reply",notes = "GET방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno){
        return replyService.read(rno);
    }

    @ApiOperation(value = "Delete Relpy",notes = "DELETE 방식으로 특정댓글 삭제")
    @DeleteMapping("/{rno}")
    public ResponseEntity remove(@PathVariable("rno") Long rno){
                replyService.remove(rno);
                return ResponseEntity.ok("delete success");
    }

    @ApiOperation(value = "Modify Reply",notes = "PUT방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> modify(@PathVariable("rno") Long rno,@Valid @RequestBody ReplyDTO replyDTO){

                replyDTO.setBno(rno);
                replyService.modify(replyDTO);
                Map<String,Long> resultMap = new HashMap<>();
                resultMap.put("rno",rno);

                return resultMap;
    }

}
