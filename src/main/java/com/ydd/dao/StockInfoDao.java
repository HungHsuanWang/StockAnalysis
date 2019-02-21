package com.ydd.dao;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class StockInfoDao {

	@Resource
	private JdbcTemplate jdbcTemplate;
	
}
