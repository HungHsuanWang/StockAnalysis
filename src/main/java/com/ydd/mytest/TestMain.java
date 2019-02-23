package com.ydd.mytest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cz.framework.DateUtil;
import com.ydd.AppConfig;
import com.ydd.dao.StockInfoDao;
import com.ydd.model.entity.StockInfo;
import com.ydd.util.Constants;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class TestMain {

	@Autowired
	private StockInfoDao stockInfoDao;
	
	@Test
	@Transactional
	public void test() throws IOException {
		//String url = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_0050.tw_20190130";
		//HttpRespBean hri = HttpsUtil.get(url, null);
		//System.out.println(hri.getRespBody());
		String url2 = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
		Document doc = Jsoup.connect(url2).timeout(5000).get(); 
		Elements links = doc.getElementsByTag("tr");
		System.out.println("size: " + links.size());
		List<StockInfo> stockList = new ArrayList<>();
		int count = 0;
		for (Element link : links) {
			if(link.childNodeSize() > 4) {
				Element stockType = link.child(3);
				boolean isNormal = link.child(0).text().split("　")[0].length() == 4;
				if(!StringUtils.isBlank(stockType.text()) && stockType.text().equals("上市") && isNormal) {
					StockInfo si = new StockInfo();
					System.out.println(link.text());
					String code = link.child(0).text().split("　")[0];
					String name = link.child(0).text().split("　")[1];
					String type = "上市".equals(stockType.text())? Constants.STOCK_TYPE_EX : Constants.STOCK_TYPE_OC;
					Date startDate = DateUtil.getParseTime(link.child(2).text(), "yyyy/MM/dd");
					String industryType = link.child(4).text();
					si.setCode(code);
					si.setName(name);
					si.setType(type);
					si.setStartDate(startDate);
					si.setIndustryType(industryType);
					System.out.println(stockInfoDao);
					stockInfoDao.save(si);
					count++;
				}
			}			
		}
		System.out.println("total:" + count);
	}
	
}
