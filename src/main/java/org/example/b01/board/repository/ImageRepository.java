package org.example.b01.board.repository;

import org.example.b01.board.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<BoardImage,String> {
}
