package org.example.b01.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListAllDTO {  //댓글 갯수,Boar,이미지 3개에 엔티티에 중요정보들만 합쳐서 가져오기 위해 사용

            private Long bno;

            private String title;

            private String writer;

            private LocalDateTime regDate;

            private Long replyCount;

            private List<BoardImageDTO> boardImages;

}
