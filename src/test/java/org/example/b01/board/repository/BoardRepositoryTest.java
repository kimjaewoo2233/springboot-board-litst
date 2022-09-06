package org.example.b01.board.repository;

import lombok.extern.log4j.Log4j2;
import net.bytebuddy.TypeCache;
import org.example.b01.board.domain.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

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


}

