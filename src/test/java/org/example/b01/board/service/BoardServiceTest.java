package org.example.b01.board.service;

import lombok.extern.log4j.Log4j2;
import org.example.b01.board.dto.BoardDTO;
import org.example.b01.board.dto.PageRequestDTO;
import org.example.b01.board.dto.PageResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister(){
        log.info(boardService.getClass().getName());    //boardService 변수가 가르키는 객체의 클래스명 출력한다.

        BoardDTO boardDTO = BoardDTO.builder()
                .title("Sample Title....")
                .content("Sampe Content...")
                .writer("user00")
                .build();

        Long bno = boardService.register(boardDTO);
        log.info("bno :=="+bno);
    }

    @Test
    public void testModify(){

        //변경에 필요한 데이터만
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("Updated...101")
                .content("Updated ocntent 101")
                .build();

        boardService.modify(boardDTO);
    }

    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("c")
                .keyword("p")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardDTO> boardDTOPageResponseDTO = boardService.list(pageRequestDTO);

        log.info(boardDTOPageResponseDTO);
    }


}