package com.ydd.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ydd.util.UnixTimestampDeserializer;

@Table(name = "stock_history")
public class StockHistory {

	Long id;

	@JsonProperty("c")
	String code;
	
	@JsonProperty("n")
	String name;

	@JsonProperty("z")
	Double closePrice;

	@JsonProperty("o")
	Double openPrice;	

	@JsonProperty("tv")
	Integer currentVol;
	
	@JsonProperty("v")
	Integer volume;
	
	@JsonProperty("a")
	String bestSellPrice;
	
	@JsonProperty("f")
	String bestSellVol;
	
	@JsonProperty("b")
	String bestBuyPrice;	

	@JsonProperty("g")
	String bestBuyVol;
	
	@JsonProperty("h")
	Double dayHigh;

	@JsonProperty("l")
	Double dayLow;
	
	@JsonProperty("u")
	Double limitUp;
	
	@JsonProperty("w")
	Double limitBottom;		
	
	@JsonDeserialize(using = UnixTimestampDeserializer.class)
	@JsonProperty("tlong")
	@Temporal(TemporalType.TIMESTAMP)
	Date todayDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}
	
	@Column(name = "code")
	public String getCode() {
		return code;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}
	
	@Column(name = "close_price")
	public Double getClosePrice() {
		return closePrice;
	}

	@Column(name = "open_price")
	public Double getOpenPrice() {
		return openPrice;
	}

	@Column(name = "current_vol")
	public Integer getCurrentVol() {
		return currentVol;
	}

	@Column(name = "volume")
	public Integer getVolume() {
		return volume;
	}

	@Column(name = "best_sell_price")
	public String getBestSellPrice() {
		return bestSellPrice;
	}

	@Column(name = "best_sell_vol")
	public String getBestSellVol() {
		return bestSellVol;
	}

	@Column(name = "best_buy_price")
	public String getBestBuyPrice() {
		return bestBuyPrice;
	}

	@Column(name = "best_buy_vol")
	public String getBestBuyVol() {
		return bestBuyVol;
	}

	@Column(name = "day_high")
	public Double getDayHigh() {
		return dayHigh;
	}

	@Column(name = "day_low")
	public Double getDayLow() {
		return dayLow;
	}

	@Column(name = "limit_up")
	public Double getLimitUp() {
		return limitUp;
	}

	@Column(name = "limit_bottom")
	public Double getLimitBottom() {
		return limitBottom;
	}

	@Column(name = "today_date")
	public Date getTodayDate() {
		return todayDate;
	}

	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public void setCurrentVol(String currentVolStr) {
        try {
        	this.currentVol = Integer.parseInt(currentVolStr);
        } catch (NumberFormatException e) {
        	this.currentVol = 0;
        }
	}

	public void setVolume(String volumeStr) {
        try {
        	this.volume = Integer.parseInt(volumeStr);
        } catch (NumberFormatException e) {
        	this.volume = 0;
        }
	}

	public void setBestSellPrice(String bestSellPrice) {
		this.bestSellPrice = bestSellPrice;
	}

	public void setBestSellVol(String bestSellVol) {
		this.bestSellVol = bestSellVol;
	}

	public void setBestBuyPrice(String bestBuyPrice) {
		this.bestBuyPrice = bestBuyPrice;
	}

	public void setBestBuyVol(String bestBuyVol) {
		this.bestBuyVol = bestBuyVol;
	}

	public void setDayHigh(Double dayHigh) {
		this.dayHigh = dayHigh;
	}

	public void setDayLow(Double dayLow) {
		this.dayLow = dayLow;
	}

	public void setLimitUp(Double limitUp) {
		this.limitUp = limitUp;
	}

	public void setLimitBottom(Double limitBottom) {
		this.limitBottom = limitBottom;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
