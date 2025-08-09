package ajaajaja.debuging_rounge.domain.question.controller;

import ajaajaja.debuging_rounge.global.auth.oauth.CurrentUserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public void test(@CurrentUserId String userId) {
        System.out.println(userId);
        System.out.println("성공");
    }
}
