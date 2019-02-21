package com.ydd.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ydd.dao.StockInfoDao;
@EnableWebMvc
@RestController
public class HelloController {
	
	@Resource
	private StockInfoDao stockInfoDao;

	
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}