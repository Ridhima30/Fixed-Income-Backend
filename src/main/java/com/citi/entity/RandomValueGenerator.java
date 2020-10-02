package com.citi.entity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class RandomValueGenerator {

	public GregorianCalendar RandomDate() {

		GregorianCalendar gc = new GregorianCalendar();
		int year = randBetween(2020, 2020);
		gc.set(Calendar.YEAR, year);
		int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
		gc.set(Calendar.DAY_OF_YEAR, dayOfYear);
		return gc;
	}

	public static int randBetween(int start, int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}

	public FixedIncomeSecurity RandomElement(List<FixedIncomeSecurity> list) {
		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));
	}

	public String RandomElementofTrade(List<String> list) {

		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));
	}

	public int RandomPriceCalculator(int price) {
		int x = (int) ((Math.random()) * (0.06 * price) + (0.97 * price));
		return x;
	}

	public int RandomQuantityCalculator() {
		int x = (int) ((Math.random()) * (100) + 100);
		return x;
	}

	public int RandomOpeningQuantityCalculator() {
		int x = (int) ((Math.random()) * (1000) + 15000);
		return x;
	}

	public List<FixedIncomeSecurity> UpdateMasterDb(List<FixedIncomeSecurity> Masterdb) {
		RandomValueGenerator gc = new RandomValueGenerator();
		FixedIncomeSecurity bonussecurity = gc.RandomElement(Masterdb);
		while (bonussecurity.getSecurityname().equals("GOVERNMENT OF INDIA T-BILL")) {
			bonussecurity = gc.RandomElement(Masterdb);
		}
		GregorianCalendar bonusdate = gc.RandomDate();
		while (bonusdate.after(bonussecurity.getMaturitydate())
				&& bonusdate.after(bonussecurity.getCouponpaymentdate())) {
			bonusdate = gc.RandomDate();
		}
		bonussecurity.setBonusdate(bonusdate);
		int openingfund = gc.RandomPriceCalculator(200000000);
		System.out.println(openingfund);
		int i = 0;
		while (i < Masterdb.size()) {
			FixedIncomeSecurity updatesecurity = Masterdb.get(i);
			updatesecurity.setOpeningfund(openingfund);
			Masterdb.set(i, updatesecurity);
			System.out.println(updatesecurity.getSecurityname() + " : openingprice: " + updatesecurity.getOpeningprice()
					+ " : qty : " + updatesecurity.getOpeningqty() + "   :finalprice:" + updatesecurity.getFinalprice()
					+ "   :openingfund:" + updatesecurity.getOpeningfund());
			i++;
		}
		System.out.println(bonusdate + "              " + bonussecurity.getSecurityname());
		return Masterdb;
	}
}
