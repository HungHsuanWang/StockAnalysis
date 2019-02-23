package com.ydd.dao;

import org.springframework.stereotype.Repository;

import com.cz.framework.dao.MysqlBaseDaoImpl;
import com.ydd.model.entity.StockInfo;


@Repository
public class StockInfoDao  extends MysqlBaseDaoImpl<StockInfo, Integer> {
	
}
