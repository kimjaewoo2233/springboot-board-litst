package org.example.b01.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.domain.Board;
import org.example.b01.board.dto.*;
import org.example.b01.board.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final ModelMapper modelMapper;

    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {

        Long bno = boardRepository.save(dtoToEntity(boardDTO)).getBno();

        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {

            //board_image까지 조인처리되는 findByWithImages()를 이용하낟.

        Optional<Board> result = boardRepository.findByIdWithImages(bno);

        Board board = result.orElseThrow();

        return entityToDTO(board);
    }

    @Override
    public void modify(BoardDTO boardDTO) {
       Board board = boardRepository.findById(boardDTO.getBno()).orElseThrow(RuntimeException::new);
       board.change(boardDTO.getTitle(),boardDTO.getContent());

       board.clearImages();

       if(boardDTO.getFileNames() != null){
           for(String fileName : boardDTO.getFileNames()){
               String[] arr = fileName.split("_");
               board.addImage(arr[0],arr[1]);
           }
       }

       boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
            boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");  //몇페이지 몇개 정렬 방법을 여기서 가지고 있음

        Page<Board> result = boardRepository.searchAll(types,keyword,pageable);
        //위 pageable 파라미터는 기능적으로 쓰기 보다 넘겨주는 pageable 객체임 PageImpl이 필요로 함
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board,BoardDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .total((int)result.getTotalElements())
                .build();

    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListReplyCountDTO> boardListReplyCountDTO = boardRepository.searchWithReplyCount(types,keyword,pageable);

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .dtoList(boardListReplyCountDTO.getContent())
                .total((int)boardListReplyCountDTO.getTotalElements())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result =boardRepository.searchWithAll(types,keyword,pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


}
