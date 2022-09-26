package org.example.b01.board.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.example.b01.board.domain.UploadFileDTO;
import org.example.b01.board.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.*;

@RestController
@Log4j2
public class UpdownController {

    @Value("${org.example.b01.path}")
    private String uploadPath;

    @ApiOperation(value = "Upload POST",notes = "Post 방식으로 파일등록")
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO){
        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null){
            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach( multipartFile -> {

                String originalFileName = multipartFile.getOriginalFilename();

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath,uuid+"_"+originalFileName);

                boolean image = false;

                try{
                    multipartFile.transferTo(savePath);
                    //이미지 파일의 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")){
                        image = true;

                        File thumbFile = new File(uploadPath,"s_"+uuid+"_"+originalFileName);

                        Thumbnailator.createThumbnail(savePath.toFile(),thumbFile,200,200);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

                list.add(UploadResultDTO.builder()
                                .uuid(uuid)
                                .fileName(originalFileName)
                                .img(image)
                        .build());

            });
            return list;

        }
        return null;
    }

    @ApiOperation(value = "view 파일",notes = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){
        Resource resource = new FileSystemResource(uploadPath+File.separator+fileName); //+.png 빼버림

        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type",Files.probeContentType(resource.getFile().toPath()));
        }catch (IOException e){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @ApiOperation(value = "remove 파일",notes = "DELETE 방식으로 파일삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName){
            Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
            String resourceName = resource.getFilename();

            Map<String,Boolean> resultMap = new HashMap<>();
            boolean removed = false;

            try{
                String contentType = Files.probeContentType(resource.getFile().toPath());
                removed = resource.getFile().delete();

                if(contentType.startsWith("image")){
                    File thnumbnailFile = new File(uploadPath+File.separator+"s_"+fileName);
                    thnumbnailFile.delete();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            resultMap.put("result",removed);

            return resultMap;
    }
}
