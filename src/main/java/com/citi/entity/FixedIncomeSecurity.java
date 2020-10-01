package com.citi.entity;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FixedIncomeSecurity {

	private String isin;
	private String Securityname;
	private int faceValue;
	private GregorianCalendar maturitydate;
	private String dcc;
	private float couponRate;
	private GregorianCalendar couponpaymentdate;
	private int openingprice;
	private int openingqty;
	private int finalprice;
	private int openingfund;
	private GregorianCalendar bonusdate;

	public String getDcc() {
		return dcc;
	}

	public void setDcc(String dcc) {
		this.dcc = dcc;
	}

	public int getOpeningfund() {
		return openingfund;
	}

	public void setOpeningfund(int openingfund) {
		this.openingfund = openingfund;
	}

	public GregorianCalendar getBonusdate() {
		return bonusdate;
	}

	public void setBonusdate(GregorianCalendar bonusdate) {
		this.bonusdate = bonusdate;
	}

	public String getSecurityname() {
		return Securityname;
	}

	public void setSecurityname(String securityname) {
		this.Securityname = securityname;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public int getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(int faceValue) {
		this.faceValue = faceValue;
	}

	public GregorianCalendar getMaturitydate() {
		return maturitydate;
	}

	public void setMaturitydate(GregorianCalendar date) {
		this.maturitydate = date;
	}

	public float getCouponRate() {
		return couponRate;
	}

	public void setCouponRate(float couponRate) {
		this.couponRate = couponRate;
	}

	public GregorianCalendar getCouponpaymentdate() {
		return couponpaymentdate;
	}

	public void setCouponpaymentdate(GregorianCalendar couponpaymentdate) {
		this.couponpaymentdate = couponpaymentdate;
	}

	public int getOpeningprice() {
		return openingprice;
	}

	public void setOpeningprice(int openingprice) {
		this.openingprice = openingprice;
	}

	public int getOpeningqty() {
		return openingqty;
	}

	public void setOpeningqty(int openingqty) {
		this.openingqty = openingqty;
	}

	public int getFinalprice() {
		return finalprice;
	}

	public void setFinalprice(int finalprice) {
		this.finalprice = finalprice;
	}

	public FixedIncomeSecurity(String isin, int faceValue, String securityname, float couponRate, int couponyear,
			int couponmonth, int coupondate, int maturityyear, int maturitymonth, int maturitydate, String dcc) {

		this.Securityname = securityname;
		this.isin = isin;
		this.faceValue = faceValue;
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, maturityyear);
		gc.set(Calendar.MONTH, maturitymonth);
		gc.set(Calendar.DATE, maturitydate);
		this.maturitydate = gc;
		GregorianCalendar gc1 = new GregorianCalendar();
		gc1.set(Calendar.YEAR, couponyear);
		gc1.set(Calendar.MONTH, couponmonth);
		gc1.set(Calendar.DATE, coupondate);
		this.couponpaymentdate = gc1;
		this.dcc = dcc;
		this.couponRate = couponRate;
		RandomValueGenerator rvg = new RandomValueGenerator();
		this.openingprice = rvg.RandomPriceCalculator(faceValue);
		this.finalprice = rvg.RandomPriceCalculator(faceValue);
		this.openingqty = rvg.RandomOpeningQuantityCalculator();
		this.bonusdate = null;
		this.openingfund=0;
	}

	public FixedIncomeSecurity(String isin, int faceValue, String securityname, float couponRate, int maturityyear,
			int maturitymonth, int maturitydate) {

		this.Securityname = securityname;
		this.isin = isin;
		this.faceValue = faceValue;
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, maturityyear);
		gc.set(Calendar.MONTH, maturitymonth);
		gc.set(Calendar.DATE, maturitydate);
		this.maturitydate = gc;
		this.couponRate = couponRate;
		RandomValueGenerator rvg = new RandomValueGenerator();
		int finalopeningprice =rvg.RandomPriceCalculator(faceValue) ;
		while( finalopeningprice > faceValue)
		{
			finalopeningprice = rvg.RandomPriceCalculator(faceValue);
		}
		this.openingprice =finalopeningprice;
		this.finalprice = 0;
		this.openingqty = rvg.RandomOpeningQuantityCalculator();
		this.bonusdate = null;
		this.openingfund=0;
	}
}
