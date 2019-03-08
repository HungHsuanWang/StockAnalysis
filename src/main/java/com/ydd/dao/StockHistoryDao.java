package com.ydd.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cz.framework.dao.MysqlBaseDaoImpl;
import com.ydd.model.entity.StockHistory;


@Repository
public class StockHistoryDao  extends MysqlBaseDaoImpl<StockHistory, Integer> {
	public void createTable(String tableName) {
		String sql = "CREATE TABLE `" + tableName + "` ( " + 
				"  `date_id` bigint(20) NOT NULL COMMENT '時間ID',  " + 
				"  `code` varchar(32) NOT NULL COMMENT '代號',  " + 
				"  `name` varchar(32) NOT NULL COMMENT '名稱',  " + 
				"  `close_price` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '最近成交價',  " + 
				"  `open_price` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '開盤價',  " + 
				"  `current_vol` int(11) DEFAULT '0' COMMENT '當盤成交量',  " + 
				"  `volume` int(11) DEFAULT '0' COMMENT '當日累計成交量',  " + 
				"  `best_sell_price` varchar(32) NOT NULL COMMENT '最佳五檔賣出價格',  " + 
				"  `best_sell_vol` varchar(32) NOT NULL COMMENT '最價五檔賣出數量',  " + 
				"  `best_buy_price` varchar(32) NOT NULL COMMENT '最佳五檔買入價格',  " + 
				"  `best_buy_vol` varchar(32) NOT NULL COMMENT '最佳五檔買入數量',  " + 
				"  `day_high` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '今日最高',  " + 
				"  `day_low` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '今日最低',  " + 
				"  `limit_up` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '今日最高',  " + 
				"  `limit_bottom` double(20,2) NOT NULL DEFAULT '0.00' COMMENT '今日最高',  " + 
				"  `today_date` timestamp NOT NULL COMMENT '資料日期',  " + 
				"  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '資料建立日期',  " + 
				"  PRIMARY KEY (`date_id`),  " + 
				"  UNIQUE KEY `stock_history_idx` (`today_date`) USING BTREE  " + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='個股歷史';";
		buildSQL(sql).execSQL();
	}

	public void batchInsertToTable(String tableName, List<StockHistory> shList) {
		for(StockHistory sh: shList) {
			String sql = "INSERT INTO `" + tableName + "` "
					+ "(`id`, `code`, `name`, `close_price`, `open_price`, `current_vol`, `volume`, `best_sell_price`, `best_sell_vol`, `best_buy_price`, `best_buy_vol`, `day_high`, `day_low`, `limit_up`, `limit_bottom`, `today_date`, `create_date`) "
					+ "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
			buildSQL(sql)
					.addArgs(sh.getCode(), sh.getName(), sh.getClosePrice(), 
							sh.getOpenPrice(), sh.getCurrentVol(), sh.getVolume(), 
							sh.getBestSellPrice(), sh.getBestSellVol(), sh.getBestBuyPrice(), 
							sh.getBestBuyVol(), sh.getDayHigh(), sh.getDayLow(), sh.getLimitUp(), 
							sh.getLimitBottom(), sh.getTodayDate(), sh.getCreateDate()).execSQL();
		}
	}
}
