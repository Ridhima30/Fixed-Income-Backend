package com.citi.algorithms;

import java.util.Calendar;  
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;

import com.citi.entity.FixedIncomeSecurity;
import com.citi.entity.Trade;

@Service
public class CalculationsService {

	Logger logger = LoggerFactory.getLogger(CalculationsService.class);
	
	//COUPON INCOME CALCULATION FUNCTION
	public Map<String, Double> Couponincome(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb) { 
		
		logger.debug("############# INSIDE COUPON INCOME CALCULATION FUNCTION #############");
		
		int i = 0;

		Map<String, Double> allcouponincomes = new HashMap<>();
		int k = 0;
		String bonussecurity = null;
		while (k < masterdb.size()) {
			if (masterdb.get(k).getBonusdate() != null) {
				bonussecurity = masterdb.get(k).getSecurityname();
			}
			k++;
		}

		while (i < masterdb.size()) {
			FixedIncomeSecurity currentsecurity = masterdb.get(i);
			String securityname = currentsecurity.getSecurityname();
			int facevalue = currentsecurity.getFaceValue();
			float couponrate = currentsecurity.getCouponRate();
			int netqty = currentsecurity.getOpeningqty();
			double couponincome = 0;
			int bonusqty = 0;
			GregorianCalendar bonusdate = currentsecurity.getBonusdate();

			int j = 0;
			while (j < tradelist.size() && currentsecurity.getCouponpaymentdate() != null) {
				GregorianCalendar coupondate = currentsecurity.getCouponpaymentdate();
				Trade trade = new Trade();
				trade = tradelist.get(j);
				if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().before(bonusdate)
						&& bonusdate.before(coupondate)) {
					String tradetype = trade.getTradetype();
					bonusqty = currentsecurity.getOpeningqty();
					if (tradetype.equalsIgnoreCase("buy")) {
						bonusqty = bonusqty + trade.getQuantity();
					}
					if (tradetype.equalsIgnoreCase("sell")) {
						bonusqty = bonusqty - trade.getQuantity();
					}
				}
				if (trade.getSecurityname().equalsIgnoreCase(securityname) && trade.getDate().before(coupondate)) {
					String tradetype = trade.getTradetype();
					if (tradetype.equalsIgnoreCase("buy")) {
						netqty = netqty + trade.getQuantity();
					}
					if (tradetype.equalsIgnoreCase("sell")) {
						netqty = netqty - trade.getQuantity();
					}
				}
				j++;
			}

			if (tradelist.size() != 0) {
				if (currentsecurity.getDcc() != null) {
					String dcc = currentsecurity.getDcc();
					System.out.print(bonusqty / 20);
					couponincome = (double) ((netqty + (bonusqty / 20)) * facevalue * 0.01 * couponrate);
				}
			}

			allcouponincomes.put(securityname, couponincome);
			i++;
		}

		return allcouponincomes;
	}

	//ACCRUED COUPON INCOME CALCULATION FUNCTION
	public Map<String, Double> Accruedcouponincome(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb) {
		
		logger.debug("############# INSIDE ACCRUED COUPON INCOME CALCULATION FUNCTION #############");
		
		int i = 0;

		Map<String, Double> allcouponincomes = new HashMap<>();
		int k = 0;
		String bonussecurity = null;
		while (k < masterdb.size()) {
			if (masterdb.get(k).getBonusdate() != null) {
				bonussecurity = masterdb.get(k).getSecurityname();
			}
			k++;
		}

		while (i < masterdb.size()) {
			FixedIncomeSecurity currentsecurity = masterdb.get(i);
			String securityname = currentsecurity.getSecurityname();
			int facevalue = currentsecurity.getFaceValue();
			float couponrate = currentsecurity.getCouponRate();
			double couponincome = 0;
			int sellqty = 0;
			int qty = currentsecurity.getOpeningqty();
			int bonusqty = 0;
			GregorianCalendar bonusdate = currentsecurity.getBonusdate();

			int j = 0;
			while (j < tradelist.size() && currentsecurity.getCouponpaymentdate() != null) {

				Trade trade = new Trade();
				trade = tradelist.get(j);
				if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().before(bonusdate)) {
					bonusqty = currentsecurity.getOpeningqty();
					String tradetype = trade.getTradetype();
					if (tradetype.equalsIgnoreCase("buy")) {
						bonusqty = bonusqty + trade.getQuantity();
					}
					if (tradetype.equalsIgnoreCase("sell")) {
						bonusqty = bonusqty - trade.getQuantity();
					}
				}
				if (trade.getSecurityname().equalsIgnoreCase(securityname)) {
					String tradetype = trade.getTradetype();
					if (tradetype.equalsIgnoreCase("buy")) {
						if (trade.getDate().after(currentsecurity.getCouponpaymentdate())) {
							String dcc = currentsecurity.getDcc();
							GregorianCalendar yearend = new GregorianCalendar(2020, 11, 31);
							long days = (yearend.getTimeInMillis() - trade.getDate().getTimeInMillis()) / (86400000);
							if (dcc.equalsIgnoreCase("a/360")) {

								couponincome = couponincome
										+ (double) ((trade.getQuantity()) * facevalue * 0.01 * couponrate * days) / 360;
							}
							if (dcc.equalsIgnoreCase("a/365")) {

								couponincome = couponincome
										+ (double) ((trade.getQuantity()) * facevalue * 0.01 * couponrate * days) / 365;
							}
							if (dcc.equalsIgnoreCase("a/366")) {
								couponincome = couponincome
										+ (double) ((trade.getQuantity()) * facevalue * 0.01 * couponrate * days) / 366;
							}
							if (dcc.equalsIgnoreCase("30/360")) {
								int month = yearend.get(Calendar.MONTH) - trade.getDate().get(Calendar.MONTH);
								int day = yearend.get(Calendar.DAY_OF_MONTH)
										- trade.getDate().get(Calendar.DAY_OF_MONTH);
								int a = (month * 30) + day;
								couponincome = couponincome
										+ (double) ((trade.getQuantity()) * facevalue * 0.01 * couponrate * a) / 360;
							}
						}
						if (trade.getDate().before(currentsecurity.getCouponpaymentdate())) {
							qty = qty + trade.getQuantity();
						}
					}
					if (tradetype.equalsIgnoreCase("sell")) {
						if (trade.getDate().after(currentsecurity.getCouponpaymentdate())) {
							sellqty = sellqty + trade.getQuantity();
						}
						if (trade.getDate().before(currentsecurity.getCouponpaymentdate())) {
							qty = qty - trade.getQuantity();
						}
					}
				}
				j++;
			}
			if (currentsecurity.getCouponpaymentdate() != null) {
				String dcc = currentsecurity.getDcc();
				GregorianCalendar coupondate = currentsecurity.getCouponpaymentdate();
				GregorianCalendar yearend = new GregorianCalendar(2020, 11, 31);
				long netdays = (yearend.getTimeInMillis() - coupondate.getTimeInMillis()) / (86400000);

				if (dcc.equalsIgnoreCase("a/360")) {

					couponincome = couponincome
							+ (double) ((qty + (bonusqty / 20) - sellqty) * facevalue * 0.01 * couponrate * netdays)
									/ 360;
				}
				if (dcc.equalsIgnoreCase("a/365")) {

					couponincome = couponincome
							+ (double) ((qty + (bonusqty / 20) - sellqty) * facevalue * 0.01 * couponrate * netdays)
									/ 365;
				}
				if (dcc.equalsIgnoreCase("a/366")) {
					couponincome = couponincome
							+ (double) ((qty + (bonusqty / 20) - sellqty) * facevalue * 0.01 * couponrate * netdays)
									/ 366;
				}
				if (dcc.equalsIgnoreCase("30/360")) {
					int month = yearend.get(Calendar.MONTH) - coupondate.get(Calendar.MONTH);
					int day = yearend.get(Calendar.DAY_OF_MONTH) - coupondate.get(Calendar.DAY_OF_MONTH);
					int a = (month * 30) + day;
					couponincome = couponincome
							+ (double) ((qty + (bonusqty / 20) - sellqty) * facevalue * 0.01 * couponrate * a) / 360;
				}
			}
			allcouponincomes.put(securityname, couponincome);
			i++;
		}

		return allcouponincomes;

	}

	//PROFIT AND LOSS CALCULATION FUNCTION
	public Map<String, Double> PLpersecurity(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb,  
			GregorianCalendar entereddate){
		
		logger.debug("############# INSIDE PROFIT AND LOSS CALCULATION FUNCTION #############");
		
		int i = 0;
		int k = 0;
		String bonussecurity = null;
		while (k < masterdb.size()) {
			if (masterdb.get(k).getBonusdate() != null) {
				bonussecurity = masterdb.get(k).getSecurityname();
			}
			k++;
		}

		double finalprice = 0;
		double ufinalprice = 0;
		Map<String, Double> allPLS = new HashMap<>();
		while (i < masterdb.size()) {
			FixedIncomeSecurity currentsecurity = masterdb.get(i);
			double avgbuypricehere = currentsecurity.getOpeningprice();
			double plhere = 0;
			int qtyhere = currentsecurity.getOpeningqty();
			GregorianCalendar bonusdate = currentsecurity.getBonusdate();
			int j = 0;
			String securityname = masterdb.get(i).getSecurityname();
			int check = 0;
			System.out.println(securityname);
			while (j < tradelist.size()) {
				Trade trade = new Trade();
				trade = tradelist.get(j);
				if (trade.getDate().before(entereddate)) {
					if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().after(bonusdate)
							&& check == 0) {
						int oldqtyhere = qtyhere;
						qtyhere = oldqtyhere + (oldqtyhere / 20);
						avgbuypricehere = (avgbuypricehere * oldqtyhere) / (qtyhere);
						check = 1;
						System.out.println("bonussecurity" + securityname);
						System.out.println("qtyhere" + qtyhere);
						System.out.println("avgprice" + avgbuypricehere);
					}
					if (trade.getSecurityname().equalsIgnoreCase(securityname)) {
						String tradetype = trade.getTradetype();
						if (tradetype.equalsIgnoreCase("buy")) {
							qtyhere = qtyhere + trade.getQuantity();
							avgbuypricehere = (avgbuypricehere * (qtyhere - trade.getQuantity())
									+ (trade.getQuantity() * trade.getPrice())) / (qtyhere);
							System.out.println("buy");
//System.out.println("avgprice" + avgbuypricehere);
							System.out.println("qtyhere" + qtyhere);
							System.out.println("avgprice" + avgbuypricehere);
						}
						if (tradetype.equalsIgnoreCase("sell")) {
							plhere = plhere + ((trade.getPrice() - (avgbuypricehere)) * (trade.getQuantity()));
							qtyhere = qtyhere - trade.getQuantity();
							System.out.println("sell");
							System.out.println("qtyhere" + qtyhere);
							System.out.println("avgprice" + avgbuypricehere);
						}
					}
				}
				j++;
			}
			allPLS.put(securityname, plhere);
			i++;
		}

		return allPLS;
	}

	//UNREALIZED PROFIT AND LOSS CALCULATION FUNCTION
	public Map<String, Double> UPLpersecurity(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb){
		
		logger.debug("############# INSIDE UNREALIZED PROFIT AND LOSS CALCULATION FUNCTION #############");
		
		int i = 0;
		GregorianCalendar newyear = new GregorianCalendar();
		newyear.set(Calendar.YEAR, 2021);
		newyear.set(Calendar.MONTH, 0);
		newyear.set(Calendar.DATE, 01);
		int k = 0;
		String bonussecurity = null;
		while (k < masterdb.size()) {
			if (masterdb.get(k).getBonusdate() != null) {
				bonussecurity = masterdb.get(k).getSecurityname();
			}
			k++;
		}
		double finalprice = 0;
		double ufinalprice = 0;
		Map<String, Double> UallPLS = new HashMap<>();
		while (i < masterdb.size()) {
			FixedIncomeSecurity currentsecurity = masterdb.get(i);
			double avgbuypricehere = currentsecurity.getOpeningprice();
			double plhere = 0;
			int qtyhere = currentsecurity.getOpeningqty();
			GregorianCalendar bonusdate = currentsecurity.getBonusdate();
			int j = 0;
			String securityname = masterdb.get(i).getSecurityname();
			int check = 0;
			while (j < tradelist.size()) {
				Trade trade = new Trade();
				trade = tradelist.get(j);
				if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().after(bonusdate)
						&& check == 0) {
					int oldqtyhere = qtyhere;
					qtyhere = qtyhere + (qtyhere / 20);
					avgbuypricehere = (avgbuypricehere * oldqtyhere) / (qtyhere);
					check = 1;
				}
				if (trade.getSecurityname().equalsIgnoreCase(securityname)) {
					String tradetype = trade.getTradetype();
					if (tradetype.equalsIgnoreCase("buy")) {
						qtyhere = qtyhere + trade.getQuantity();
						avgbuypricehere = (avgbuypricehere * (qtyhere - trade.getQuantity())
								+ (trade.getQuantity() * trade.getPrice())) / (qtyhere);
					}
					if (tradetype.equalsIgnoreCase("sell")) {

						qtyhere = qtyhere - trade.getQuantity();
					}
				}
				j++;
			}
			if (currentsecurity.getSecurityname().equalsIgnoreCase(bonussecurity) && check == 0) {
				int oldqtyhere = qtyhere;
				qtyhere = qtyhere + (qtyhere / 20);
				avgbuypricehere = (avgbuypricehere * oldqtyhere) / (qtyhere);
			}
			plhere = qtyhere * (currentsecurity.getFinalprice() - avgbuypricehere);
			UallPLS.put(securityname, plhere);
			i++;
		}

		return UallPLS;
	}

	//CLOSING FUND CALCULATION FUNCTION
	public double Closingfund(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb, GregorianCalendar gc) {
		
		logger.debug("############# INSIDE CLOSING FUND CALCULATION FUNCTION #############");
		
		Map<String, Double> clf = new HashMap<>();
		clf = Couponincome(tradelist, masterdb);

		double a = 0;
		Iterator<Map.Entry<String, Double>> itr = clf.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<String, Double> entry = itr.next();
			a = a + entry.getValue();

		}
		int i = 0;
		double fund = masterdb.get(1).getOpeningfund() + a;
		while (i < tradelist.size()) {
			Trade trade = new Trade();
			trade = tradelist.get(i);
			
			if(trade.getDate().before(gc)) {
				String tradetype = trade.getTradetype();
				if (tradetype.equalsIgnoreCase("buy")) {
					fund = fund - (trade.getQuantity() * trade.getPrice());
				}
				if (tradetype.equalsIgnoreCase("sell")) {
					fund = fund + (trade.getQuantity() * trade.getPrice());
				}
			}
			
			i++;
		}
		
		GregorianCalendar gc2 = new GregorianCalendar();
		gc2.set(Calendar.YEAR, 2021);
		gc2.set(Calendar.MONTH, 0);
		gc2.set(Calendar.DATE, 1);
		
		if(!gc.equals(gc2)) {
			fund = fund-a;
		}
		
		return fund;
	}

	//INSIDE MARKET VALUATION FUNCTION
	public double MarketValuation(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb){
		
		logger.debug("############# INSIDE MARKET VALUATION FUNCTION #############");
		
		int i = 0;
		int k = 0;
		String bonussecurity = null;
		while (k < masterdb.size()) {
			if (masterdb.get(k).getBonusdate() != null) {
				bonussecurity = masterdb.get(k).getSecurityname();
			}
			k++;
		}

		double mvalue = 0;

		Map<String, Double> allPLS = new HashMap<>();

		while (i < masterdb.size()) {
			FixedIncomeSecurity currentsecurity = masterdb.get(i);
			int netqty = currentsecurity.getOpeningqty();
			double finalmp = currentsecurity.getFinalprice();
			
			int bonusqty = 0;
			GregorianCalendar bonusdate = currentsecurity.getBonusdate();

			int netsellprice = 0;
			int j = 0;
			String securityname = masterdb.get(i).getSecurityname();
			while (j < tradelist.size()) {
				Trade trade = new Trade();
				trade = tradelist.get(j);
				if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().before(bonusdate)) {
					bonusqty = currentsecurity.getOpeningqty();
					String tradetype = trade.getTradetype();
					if (tradetype.equalsIgnoreCase("buy")) {
						bonusqty = bonusqty + trade.getQuantity();
					}
					if (tradetype.equalsIgnoreCase("sell")) {
						bonusqty = bonusqty - trade.getQuantity();
					}
				}
				if (trade.getSecurityname().equalsIgnoreCase(securityname)) {
					String tradetype = trade.getTradetype();
					if (tradetype.equalsIgnoreCase("buy")) {
						netqty = netqty + trade.getQuantity();
					}
					if (tradetype.equalsIgnoreCase("sell")) {
						netqty = netqty - trade.getQuantity();
					}

				}
				mvalue = mvalue + finalmp * (netqty + bonusqty);
				j++;
			}
			i++;
		}
		return mvalue;
	}

	//CLOSING QUANTITY OF SECURITIES CALCULATION FUNCTION
	public Map<String, Integer> getClosingQty(List<Trade> tradelist, List<FixedIncomeSecurity> masterdb) {  
			
			logger.debug("############# INSIDE CLOSING QUANTITY OF SECURITIES CALCULATION FUNCTION #############");
			
			int i = 0;

			Map<String, Integer> closingQty = new HashMap<>();
			int k = 0;
			String bonussecurity = null;
			while (k < masterdb.size()) {
				if (masterdb.get(k).getBonusdate() != null) {
					bonussecurity = masterdb.get(k).getSecurityname();
				}
				k++;
			}

			while (i < masterdb.size()) {
				FixedIncomeSecurity currentsecurity = masterdb.get(i);
				String securityname = currentsecurity.getSecurityname();
				int netqty = currentsecurity.getOpeningqty();
				int qty = 0;
				int bonusqty = 0;
				GregorianCalendar bonusdate = currentsecurity.getBonusdate();

				int j = 0;
				while (j < tradelist.size() && currentsecurity.getCouponpaymentdate() != null) {
					GregorianCalendar coupondate = currentsecurity.getCouponpaymentdate();
					Trade trade = new Trade();
					trade = tradelist.get(j);
					if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().before(bonusdate)
							&& bonusdate.before(coupondate)) {
						String tradetype = trade.getTradetype();
						bonusqty = currentsecurity.getOpeningqty();
						if (tradetype.equalsIgnoreCase("buy")) {
							bonusqty = bonusqty + trade.getQuantity();
						}
						if (tradetype.equalsIgnoreCase("sell")) {
							bonusqty = bonusqty - trade.getQuantity();
						}
					}
					if (trade.getSecurityname().equalsIgnoreCase(securityname) && trade.getDate().before(coupondate)) {
						String tradetype = trade.getTradetype();
						if (tradetype.equalsIgnoreCase("buy")) {
							netqty = netqty + trade.getQuantity();
						}
						if (tradetype.equalsIgnoreCase("sell")) {
							netqty = netqty - trade.getQuantity();
						}
					}
					j++;
				}

				if (tradelist.size() != 0) {
					if (currentsecurity.getDcc() != null) {
						qty = (netqty + (bonusqty / 20));
					}
				}

				closingQty.put(securityname, qty);
				i++;
			}

			return closingQty;
		}
		
}