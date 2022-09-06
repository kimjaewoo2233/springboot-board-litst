package org.example.b01.board.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.dto.BoardDTO;
import org.example.b01.board.dto.PageRequestDTO;
import org.example.b01.board.dto.PageResponseDTO;
import org.example.b01.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO",responseDTO);
    }

    @GetMapping("/register")
    public void registerGet(){

    }
    @GetMapping("/read")
    public void read(Long bno,PageRequestDTO pageRequestDTO,Model model){
        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        model.addAttribute("dto",boardDTO);
    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes){
        log.info("board Post register..........");

        if(bindingResult.hasErrors()){
            log.info("has errors......");
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());

            return "redirect:/board/register";
        }

        log.info(boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result",bno);
        return "redirect:/board/list";
    }



}
