package com.ydd.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "stock_info")
public class StockInfo {
	Integer id;
	
	// 代號
	String code;
	
	// 名稱
	String name;
	
	// 市場別
	String type;
	
	// 產業別
	String industryType;

	// 上市日
	Date startDate;
	
	// 最後資料日
	Date lastDataDate;
	
	// 備註
	String remark;

	@Id
	@Column(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	

	@Column(name = "code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}	

	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "industry_type")
	public String getIndustryType() {
		return industryType;
	}
	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_data_date")
	public Date getLastDataDate() {
		return lastDataDate;
	}
	public void setLastDataDate(Date lastDataDate) {
		this.lastDataDate = lastDataDate;
	}
	
	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
