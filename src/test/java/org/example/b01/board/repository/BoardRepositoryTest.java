package org.example.b01.board.repository;

import lombok.extern.log4j.Log4j2;
import net.bytebuddy.TypeCache;
import org.example.b01.board.domain.Board;
import org.example.b01.board.domain.BoardImage;
import org.example.b01.board.dto.BoardListAllDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;


    @Autowired
    private ImageRepository imageRepository;
    @Test
    void testInsert(){
        IntStream.rangeClosed(1,100).forEach(i -> {
            Board board = Board.builder()
                    .title("titlt..."+i)
                    .content("content..."+i)
                    .writer("user"+(i%10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO: "+result.getBno());
        });
    }

    @Test
    void testSelect(){
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        log.info(board);

    }
    @Test
    void testUpdate(){
        Board board = boardRepository.findById(100L).orElseThrow();

        board.change("update 되었습니다","update 되었습니다");

        boardRepository.save(board);
    }

    @Test
    void testDelete(){
        Long bno = 1L;
        boardRepository.deleteById(bno);
    }

    @Test
    void testPaging(){
        // 1 page orderby bno desc
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count: "+result.getTotalElements());
        log.info("total pages: "+result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());

        List<Board> boardList = result.getContent();
        boardList.forEach(board -> log.info(board));
    }

    @Test
    void testKeyword(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());


        boardRepository.findKeyword("it",pageable).getContent().forEach(System.out::println);
    }
    @Test
    void testSearch(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        boardRepository.search1(pageable).getContent().forEach(System.out::println);
    }
    @Test
    void testSearchAll(){
        String[] types = {"w"};

        String keyword = "1";


       Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
       Page<Board> result =  boardRepository.searchAll(types,keyword,pageable);

       //total pages
        log.info(result.getTotalPages());

        //page size
        log.info(result.getSize());

        //pageNumber
        log.info(result.getNumber());

        //prev next
        log.info(result.hasPrevious()+" : "+result.hasNext());

        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testInsertWithImages(){
        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for(int i=0;i<3;i++){
            board.addImage(UUID.randomUUID().toString(),"file"+i+".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    public void testReadWithImages(){
        Optional<Board> board =
                boardRepository.findByIdWithImages(2L);

        board.orElseThrow(RuntimeException::new)
                .getImageSet().forEach(System.out::println);
    }

    @Test
    @DisplayName("연관관계 테스트")
    @Transactional
    void testRead(){
        Board board = boardRepository.findById(2L)
                .orElseThrow(RuntimeException::new);

        board.getImageSet().forEach(System.out::println);
    }
    @Transactional
    @Commit
    @Test
    public void testModfiyImages(){
                Optional<Board> result = boardRepository.findByIdWithImages(2L);
                Board board = result.orElseThrow();

                //기존의 첨부파일들은 삭제
                board.clearImages();

                for(int i=0;i<2;i++){
                    board.addImage(UUID.randomUUID().toString(),"updatefile"+i+".jpg");
                }

                boardRepository.save(board);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){
        Long bno = 2L;
        replyRepository.deleteByBoard_Bno(bno);
        boardRepository.deleteById(bno);
    }

    @Test
    public void testInsertAll(){

        for(int i= 1;i<=100;i++){
            Board board = Board.builder()
                    .title("Title.."+i)
                    .content("Content.."+i)
                    .writer("writer.."+i)
                    .build();

            for(int j=0;j<3;j++){
                if(i%5 == 0){
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            boardRepository.save(board);    //객체를 생성할때만 가능한짓임
        }
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());
        boardRepository.searchWithAll(null,null,pageable);

    }
    @Test
    @Transactional
    public void testNplusOne() {
        boardRepository.findAll().forEach(board -> {
            System.out.println(board.getImageSet());
        });

    }
        /**
         * findAll()을 가져올때 select * from board를 통해 가져오면서 board 엔티티 객체가 가지고 있는
         * Image r
         *    boardRepository.findAll().forEach(board -> {
         *             System.out.println(board.getImageSet());
         *         });
         *
         */

    @Test
    @Transactional
    void insetImage(){
        Board baord = boardRepository.findById(2L).orElseThrow(RuntimeException::new);
        String uuid = UUID.randomUUID().toString();
       imageRepository.save(BoardImage.builder().uuid(uuid)
               .fileName("te.jpg")
               .board(baord)
               .build());
    }

    @Transactional
    @Test
    public void testSearchimageReplyCount(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<BoardListAllDTO> boardListAllDTOS = boardRepository.searchWithAll(null,null,pageable);

        log.info("------------------------------");
        log.info(boardListAllDTOS.getTotalElements());

        boardListAllDTOS.getContent().forEach(boardListAllDTO -> {log.info(boardListAllDTO);});
    }

}

