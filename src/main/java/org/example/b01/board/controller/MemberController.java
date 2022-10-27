package org.example.b01.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    @GetMapping("/login")
    public void loginGET(String error,String logout){   //로그인 과정에 문제가 생기거나 로그아웃 처리할떄 사용하기 위해서 사용
            log.info("log in......");
            log.info("logout: "+logout);

        if(logout != null){
            log.info("user logout....");
        }
    }


}
