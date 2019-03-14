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

	@Resource 
	private StockHistoryService stockHistoryService;

	@Resource
	private StockInfoService stockInfoService;

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

	public void parseStockHistory(String startDateStr) throws UnsupportedEncodingException {
		String url = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=";
		List<StockInfo> list = stockInfoDao.queryAll();
		String queryStr = "";
		Date startDate;
		String reqUrl = "";
		for(StockInfo si: list) {
			startDate = DateUtil.strToDate(startDateStr, DateUtil.YYYYMMDD);	
			queryStr = createQueryStr(si, startDate);				
			reqUrl = url + URLEncoder.encode(queryStr, "UTF-8");
			parseTask(si, startDate, reqUrl);		
		}
	}

	private void parseTask(final StockInfo si, final Date startDate, final String reqUrl) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(null == si.getLastDataDate() || startDate.after(si.getLastDataDate())) {				
						HttpResInfo hri = HttpsUtil.get(reqUrl, null);
						if(!hri.isNotOk()) {
							String respJson = hri.getRepMsg();
							Map<String, String> returnMap = JsonUtil.toMapObject(respJson);
							String stockArr = JsonUtil.toJson(returnMap.get("msgArray"));
							List<StockHistory> sdList = JsonUtil.toList(stockArr, StockHistory.class);
							if(!sdList.isEmpty()) {
								for(StockHistory sd: sdList) {	
									try {
										stockHistoryService.save(sd);
									} catch(Exception e) {
										LogUtil.warn(String.format("%s日期%s重覆 - 繼續執行", si.getName(), sd.getTodayDate()));
									}
								}
								si.setLastDataDate(new Date());
								stockInfoService.update(si);
								LogUtil.info(String.format("%s執行更新成功", si.getName()));
							} else {
								LogUtil.info("非交易日");
							}
						}
					} else {
						LogUtil.info(String.format("%s不執行更新，最後資料日為：%s", si.getName(), si.getLastDataDate()));
					}
				} catch (Exception e) {
					LogUtil.warn(e.getMessage());
				}
			}
		}).start();	
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

	@Transactional
	public void save(StockHistory sh) {
		stockHistoryDao.save(sh);
	}
}
