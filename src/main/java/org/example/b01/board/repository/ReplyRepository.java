package org.example.b01.board.repository;


import org.example.b01.board.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply,Long> {

        //특정한 게시글의 댓글들은 페이징 처리를 할 수 있또록 Pageable기능 넣음

        @Query("select r from Reply  r where r.board.bno = :bno")
        Page<Reply> listOfBoard(Long bno, Pageable pageable);
        //해당 Board 객체와 연관관계에 해당하는 Reply를 Page타입으로 져옴

        void deleteByBoard_Bno(Long bno);
}
