package com.ydd.mytest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cz.framework.DateUtil;
import com.cz.framework.JsonUtil;
import com.cz.framework.LogUtil;
import com.ydd.AppConfig;
import com.ydd.dao.StockInfoDao;
import com.ydd.model.HttpResInfo;
import com.ydd.model.entity.StockHistory;
import com.ydd.model.entity.StockInfo;
import com.ydd.util.Constants;
import com.ydd.util.HttpsUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class TestMain {

	@Autowired
	private StockInfoDao stockInfoDao;

	public void testStockInfo() throws IOException {
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

	private static final int QUERY_AMOUNT = 50;
	
	@Test
	public void testStockHistory() throws UnsupportedEncodingException {
		//System.out.println(SecurityUtil.crtyMd5("timke", "DC483E80A7A0BD9EF71D8CF973673924".toLowerCase()));
		String url = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=";//tse_0050.tw_20190120";
		List<StockInfo> list = stockInfoDao.queryAll();
		String startDateStr = "20190301";
		String queryStr = "";
		Date startDate = null;
		for(StockInfo si: list) {
			queryStr = "";
			startDate = DateUtil.strToDate(startDateStr, DateUtil.YYYYMMDD);
			while(startDate.before(new Date())) {
				int limit = 0;
				while(startDate.before(new Date()) && limit < QUERY_AMOUNT) {
					String queryDate = DateUtil.dateToStr(startDate, DateUtil.YYYYMMDD);
					queryStr += si.getType() + "_" + si.getCode() + ".tw_" + queryDate + "|";
					startDate = DateUtils.addDays(startDate, 1);
					limit++;
				}
			}
			String reqUrl = url + URLEncoder.encode(queryStr, "UTF-8");
			HttpResInfo hri = HttpsUtil.get(reqUrl, null);
			if(!hri.isNotOk()) {
				String respJson = hri.getRepMsg();
				Map<String, String> returnMap = JsonUtil.toMapObject(respJson);
				String stockArr = JsonUtil.toJson(returnMap.get("msgArray"));
				List<StockHistory> sdList = JsonUtil.toList(stockArr, StockHistory.class);
				if(!sdList.isEmpty()) {
					for(StockHistory sd: sdList) {
						System.out.println(sd.getName() + ";" + sd.getOpenPrice() + " ; " + sd.getTodayDate());
					}
				} else {
					LogUtil.info("非交易日");
				}
			}
		}
	}

	
	public static void main(String[] args) throws Exception {
		System.out.println(Solution.reverse(100));
	}
	
	static class Solution {
	    public static int reverse(int x) {
	    	String str = x + "";
	    	if(str.length() == 1 || (x < 0 && str.length() == 2)) {
	    		return x;
	    	}
	    	
	    	return x;	        
	    }
	}
}
