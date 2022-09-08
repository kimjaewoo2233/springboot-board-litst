package org.example.b01.board.service;

import org.example.b01.board.dto.ReplyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ReplyServiceTest {

    @Autowired
    ReplyService replyService;

    @Test
    void testInsert(){
        replyService.register(ReplyDTO.builder()
                        .bno(101L)
                        .replyText("댓글...")
                        .replyer("작성자...")
                        .build());


    }
}