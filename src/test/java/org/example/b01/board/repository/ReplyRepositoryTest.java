package org.example.b01.board.repository;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.domain.Board;
import org.example.b01.board.domain.Reply;
import org.example.b01.board.dto.BoardListReplyCountDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository repository;
    @Autowired
    private BoardRepository boardRepository;


    @Test
    public void testInsert() {
        Long bno = 100L;

        Board board = Board.builder()
                .bno(bno)
                .build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("댓글입니다....")
                .replyer("replayer")
                .build();

        repository.save(reply);
    }

    @Test
    public void testBoardReplies(){
        Long bno = 100L;

        Pageable pageable = PageRequest.of(0,5, Sort.by("rno").descending());

        Page<Reply> page = repository.listOfBoard(bno,pageable);

        page.getContent().forEach(reply -> {
            log.info(reply);
        });
    }

    @Test
    public void testSearchReplyCount(){
        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,5,Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types,keyword,pageable);

        System.out.println(result.getTotalPages());
        System.out.println(result.getSize());
        System.out.println(result.getNumber());
        System.out.println(result.hasPrevious());
        result.getContent().forEach(System.out::println);

    }
}
