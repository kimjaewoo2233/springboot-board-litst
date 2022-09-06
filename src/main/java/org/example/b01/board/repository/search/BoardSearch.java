package org.example.b01.board.repository.search;

import org.example.b01.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String[] types,String keyword,Pageable pageable);

}
