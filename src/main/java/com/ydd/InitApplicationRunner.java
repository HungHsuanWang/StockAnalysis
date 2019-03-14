package com.ydd;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.cz.framework.LogUtil;
import com.ydd.service.StockHistoryService;
import com.ydd.service.StockInfoService;

@Component
public class InitApplicationRunner implements ApplicationRunner{
	@Resource
	private StockInfoService stockInfoService;
	@Resource
	private StockHistoryService stockHistoryService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		LogUtil.info("springBoot init BEGINING=====");
		// 初始化載入目前stock_info
		//stockInfoService.initStockCode();
		// 初始化建立個股table
		//stockHistoryService.initStockTable();
		stockHistoryService.parseStockHistory("20190310");
		LogUtil.info("springBoot ENDING=====");
	}
	
}
