package org.example.b01.board.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.example.b01.board.domain.Board;
import org.example.b01.board.domain.BoardImage;
import org.example.b01.board.domain.QBoard;
import org.example.b01.board.domain.QReply;
import org.example.b01.board.dto.BoardImageDTO;
import org.example.b01.board.dto.BoardListAllDTO;
import org.example.b01.board.dto.BoardListReplyCountDTO;
import org.hibernate.criterion.Projection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     *
     */
    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {
        QBoard board = QBoard.board;   //Q 도메인 객체 받음

        JPQLQuery<Board> query = from(board);                    //select ... from board;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.or(board.title.contains("c"));           //title like
        booleanBuilder.or(board.content.contains("a"));              //content like

        query.where(booleanBuilder);
        query.where(board.bno.gt(0L));
       // query.where(board.title.contains("1"));             //where 절임 where title like (1)

        this.getQuerydsl().applyPagination(pageable,query);                 //QueryDslRepositorySupport가 지원해주는거임

        List<Board> list = query.fetch();                   //List로 반환시켜준다 fetuch메소드가

        long count = query.fetchCount();                         //count() 쿼리를 실행해준다.

        return new PageImpl<>(list,pageable,count);
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        if((types != null && types.length > 0) && keyword != null){ //검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for(String type: types){    //여기가 향상 for문이고 type을 배열로 받는다 그래서 twc이따구로 보내도 or조건추가됨
                switch(type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }
        query.where(board.bno.gt(0L));

        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();


        return new PageImpl<>(list,pageable,count); //Page<T> 제너릭타입떄문에 이걸 넘기게해줌
        //실제 목록 데이터, 페이지관련정보객체, 전체개수

    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));
        //select * from board left join reply on board.id = reply.board.id
        query.groupBy(board);

        if((types != null && types.length >0) && keyword != null){

            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type : types){
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }
        query.where(board.bno.gt(0L));

        JPQLQuery<BoardListReplyCountDTO> dtojpqlQuery = query.select(Projections
                .bean(BoardListReplyCountDTO.class,
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,
                        reply.count().as("replyCount")
                        ));

        this.getQuerydsl().applyPagination(pageable,query);

        List<BoardListReplyCountDTO> dtoList = dtojpqlQuery.fetch();

        long count = query.fetchCount();


        return new PageImpl<>(dtoList,pageable,count);



    }

    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QBoard board =QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));
        //left join

        if((types != null && types.length > 0) && keyword != null){
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type:types){
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.eq(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.eq(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.eq(keyword));
                        break;
                }
            }
            boardJPQLQuery.where(booleanBuilder);
        }

        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable,boardJPQLQuery);

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board,reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {
            Board board1 = (Board)tuple.get(board);
            long replyCount = tuple.get(1,Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            List<BoardImageDTO> boardImageDTO = board1.getImageSet().stream().sorted().map(
                    boardImage ->
                         BoardImageDTO.builder()
                                 .uuid(boardImage.getUuid())
                                 .fileName(boardImage.getFileName())
                                 .ord(boardImage.getOrd())
                                .build()
            ).collect(Collectors.toList());

            dto.setBoardImages(boardImageDTO);
            return dto;
        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList,pageable,totalCount);

    }
    public void test(){
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board).leftJoin(reply)
                .on(reply.board.eq(board)).where(board.bno.eq(2L));

        List<Board> boardList = boardJPQLQuery.fetch();

        boardList.forEach(board1 -> {
            System.out.println(board1.getBno());
            System.out.println(board1.getImageSet());
            System.out.println("-------------------------------------");
        });
    }
}
