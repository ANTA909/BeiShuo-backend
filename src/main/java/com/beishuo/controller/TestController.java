package com.beishuo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "碑说后端服务启动成功！时间：" + new java.util.Date();
    }

    @GetMapping("/")
    public String home() {
        return "欢迎访问碑说后端API服务";
    }
}