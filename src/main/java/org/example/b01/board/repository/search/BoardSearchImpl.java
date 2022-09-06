package org.example.b01.board.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.example.b01.board.domain.Board;
import org.example.b01.board.domain.QBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

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
}
