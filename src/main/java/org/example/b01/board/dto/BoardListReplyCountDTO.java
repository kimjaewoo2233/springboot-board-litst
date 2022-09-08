package org.example.b01.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO {   //list에서만 쓸것이기에 내용은 필요없음

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;

    private Long replyCount;
}
