package com.ydd.controller;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ydd.service.StockInfoService;

@RequestMapping("/stockInfo")
@RestController
public class StockInfoController {
	
	@Resource
	private StockInfoService stockInfoService;
	
    @RequestMapping("/init")
    public String init() throws IOException {
        return "Greetings from Spring Boot!";
    }

}