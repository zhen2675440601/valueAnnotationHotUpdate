package com.zheng.value.valueannoationhotupdate.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengwl
 * @date 2025/9/8
 */
@RestController
@RequestMapping("/con-test")
public class TestController {

    @Value("${test.color:red}")
    private String color;

    @Value("${test.age:10}")
    private Integer age;


    @GetMapping
    private String test() {
        return "testConfig: color:"+ " age:" + "\n" + "value: color:" + color+" age:"+age;
    }

}
