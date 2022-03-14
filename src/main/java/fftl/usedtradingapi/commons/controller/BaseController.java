package fftl.usedtradingapi.commons.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @GetMapping()
    public String ConnectCheck(){
        return "프로젝트가 실행 중 입니다.";
    }
}
