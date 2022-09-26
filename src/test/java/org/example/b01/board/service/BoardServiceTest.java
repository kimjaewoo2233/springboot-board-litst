package org.example.b01.board.service;

import lombok.extern.log4j.Log4j2;
import org.example.b01.board.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                .bno(103L)
                .title("Updated...101")
                .content("Updated ocntent 101")
                .build();

        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));
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

    @Test
    public void testRegisterWithImages(){
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample Title")
                .content("Sample Content....")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                    UUID.randomUUID()+"_aaa.jpg",
                    UUID.randomUUID()+"_bbb.jpg",
                    UUID.randomUUID()+"_ccc.jpg"
                ));

        Long rno = boardService.register(boardDTO);

        log.info("bno:"+rno);
    }

    @Test
    public void testReadAll(){
        Long bno = 103L;

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        for(String fileName: boardDTO.getFileNames()){
            log.info(fileName);
        }
    }

    @Test
    public void testRemoveAll(){
        //일단 댓글이 없는 경우만 삭제가 가능하도록함
        Long bno = 10L;

        boardService.remove(bno);
    }
    @Test
    public void testListWithAll(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dto = responseDTO.getDtoList();

        dto.forEach(boardListAllDTO -> {

            log.info(boardListAllDTO.getBno()+":"+boardListAllDTO.getTitle());
            if(boardListAllDTO.getBoardImages() != null){
                for(BoardImageDTO boardImageDTO:boardListAllDTO.getBoardImages()){
                    log.info(boardImageDTO);
                }
            }
            log.info("=============");
        });
    }

}