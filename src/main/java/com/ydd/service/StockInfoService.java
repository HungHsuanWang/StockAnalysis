package com.ydd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cz.framework.DateUtil;
import com.cz.framework.LogUtil;
import com.ydd.dao.StockInfoDao;
import com.ydd.model.entity.StockInfo;
import com.ydd.util.Constants;

@Service
public class StockInfoService {

	@Resource
	private StockInfoDao stockInfoDao;
	
	@Transactional
	public void initStockCode() throws IOException {
		//String url = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
		String url = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
		Document doc = Jsoup.connect(url).maxBodySize(0).timeout(5000).get(); 
		Elements links = doc.getElementsByTag("tr");
		int count = 0;
		for (Element link : links) {
			if(link.childNodeSize() > 4) {
				Element stockType = link.child(3);
				boolean isNormal = link.child(0).text().split("　")[0].length() == 4;
				if(!StringUtils.isBlank(stockType.text()) && isNormal
						&& (stockType.text().equals("上市") || stockType.text().equals("上櫃"))) {
					StockInfo si = new StockInfo();
					String code = link.child(0).text().split("　")[0];
					String name = link.child(0).text().split("　")[1];
					System.out.println(code + " : " + name);
					String type = "上市".equals(stockType.text())? Constants.STOCK_TYPE_EX : Constants.STOCK_TYPE_OC;
					Date startDate = DateUtil.getParseTime(link.child(2).text(), "yyyy/MM/dd");
					System.out.println(startDate);
					String industryType = link.child(4).text();
					si.setCode(code);
					si.setName(name);
					si.setType(type);
					si.setStartDate(startDate);
					si.setIndustryType(industryType);
					stockInfoDao.save(si);
					count++;
				}
			}
		}
		LogUtil.info("total:" + count);
	}
	
	@Transactional
	public void update(StockInfo si) {
		stockInfoDao.updateObject(si);
	}
}
