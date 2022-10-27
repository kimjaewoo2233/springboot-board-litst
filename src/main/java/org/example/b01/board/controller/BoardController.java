package org.example.b01.board.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.b01.board.dto.*;
import org.example.b01.board.service.BoardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {

    @Value("${org.example.b01.path}")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        //PageResponseDTO<BoardListReplyCountDTO> pageResponseDTO = boardService.listWithReplyCount(pageRequestDTO);

        PageResponseDTO<BoardListAllDTO> responseDTO =
                    boardService.listWithAll(pageRequestDTO);
        log.info(responseDTO);

        model.addAttribute("responseDTO",responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/register")
    public void registerGet(){

    }
    @GetMapping({"/read","/modify"})
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

    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid BoardDTO boardDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes){
            log.info("board modify post...."+boardDTO);

            if(bindingResult.hasErrors()){
                log.info("has error >>>>>");
                String link = pageRequestDTO.getLink();

                redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());

                redirectAttributes.addAttribute("bno",boardDTO.getBno());

                return "redirect:/board/modify?"+link;

            }

            boardService.modify(boardDTO);

            redirectAttributes.addFlashAttribute("result","modified");

            redirectAttributes.addAttribute("bno",boardDTO.getBno());

            return "redirect:/board/read";
    }

    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO,RedirectAttributes redirectAttributes){

        Long bno = boardDTO.getBno();
        boardService.remove(bno);
        redirectAttributes.addFlashAttribute("result","removed");

        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result","removed");
        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files){
            for(String fileName:files){
                Resource resource = new FileSystemResource(uploadPath + File.separator+fileName);

                String resourceName = resource.getFilename();
                try{
                    String contentType = Files.probeContentType(resource.getFile().toPath());

                    resource.getFile().delete();

                    //섬네일이 존재한다면
                    if(contentType.startsWith("image")){
                        File thumbnailFile = new File(uploadPath+File.separator+"s_"+fileName);
                        thumbnailFile.delete();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }



}
