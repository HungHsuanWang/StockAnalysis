package com.ydd.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cz.framework.DateUtil;
import com.cz.framework.JsonUtil;
import com.cz.framework.LogUtil;
import com.ydd.dao.StockHistoryDao;
import com.ydd.dao.StockInfoDao;
import com.ydd.model.HttpResInfo;
import com.ydd.model.entity.StockHistory;
import com.ydd.model.entity.StockInfo;
import com.ydd.util.HttpsUtil;

@Service
public class StockHistoryService {

	@Resource
	private StockInfoDao stockInfoDao;
	
	@Resource
	private StockHistoryDao stockHistoryDao;

	private static final int QUERY_AMOUNT = 50;
	
	@Transactional
	public void initStockTable() {
		LogUtil.info("initStockTable - start");
		List<StockInfo> list = stockInfoDao.queryAll();
		for(StockInfo si: list) {
			String tableName = si.getType() + "_" + si.getCode();
			stockHistoryDao.createTable(tableName);
		}
		LogUtil.info("initStockTable - end");
	}
	
	@Transactional
	public void parseStockHistory() throws UnsupportedEncodingException {
		//tse_0050.tw_20190120"
		String url = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=";
		List<StockInfo> list = stockInfoDao.queryAll();
		String startDateStr = "20190101";
		String queryStr = "";
		Date startDate = null;
		for(StockInfo si: list) {
			String tableName = si.getType() + "_" + si.getCode();
			System.out.println(tableName);
			startDate = DateUtil.strToDate(startDateStr, DateUtil.YYYYMMDD);
			queryStr = createQueryStr(si, startDate);
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
					stockHistoryDao.batchInsertToTable(tableName, sdList);
				} else {
					LogUtil.info("非交易日");
				}
			}
			break;
		}
	}
	
	private String createQueryStr(StockInfo si, Date startDate) {
		String queryStr = "";
		while(startDate.before(new Date())) {
			int limit = 0;
			while(startDate.before(new Date()) && limit < QUERY_AMOUNT) {
				String queryDate = DateUtil.dateToStr(startDate, DateUtil.YYYYMMDD);
				queryStr += si.getType() + "_" + si.getCode() + ".tw_" + queryDate + "|";
				startDate = DateUtils.addDays(startDate, 1);
				limit++;
			}
		}
		return queryStr;
	}
}
