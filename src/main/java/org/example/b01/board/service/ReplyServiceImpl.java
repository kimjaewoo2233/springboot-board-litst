package org.example.b01.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.domain.Reply;
import org.example.b01.board.dto.PageRequestDTO;
import org.example.b01.board.dto.PageResponseDTO;
import org.example.b01.board.dto.ReplyDTO;
import org.example.b01.board.repository.ReplyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository repository;

    private final ModelMapper modelMapper;

    @Override
    public Long register(ReplyDTO replyDTO) {
        return repository.save(modelMapper.map(replyDTO, Reply.class)).getRno();
    }

    @Override
    public ReplyDTO read(Long rno) {
        return modelMapper.map(repository.findById(rno)
                .orElseThrow(RuntimeException::new),ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO dto) {
            Reply reply = repository.findById(dto.getRno())
                    .orElseThrow(RuntimeException::new);
            reply.changeTest(dto.getReplyText());
            repository.save(reply);
    }

    @Override
    public void remove(Long rno) {
            repository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0? 0 : pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("rno").ascending());

        Page<Reply> result = repository.listOfBoard(bno,pageable);

        List<ReplyDTO> replyDTOList = result.getContent().stream().map(
                reply -> modelMapper.map(reply,ReplyDTO.class)
        ).collect(Collectors.toList());


        return PageResponseDTO.<ReplyDTO>withAll()
                .dtoList(replyDTOList)
                .total((int)result.getTotalElements())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }
}
