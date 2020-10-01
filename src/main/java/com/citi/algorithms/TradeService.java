package com.citi.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.citi.controller.TradeController;
import com.citi.entity.FixedIncomeSecurity;
import com.citi.entity.RandomValueGenerator;
import com.citi.entity.Trade;
import com.citi.repositories.TradeRepository;

@Service
public class TradeService {

	@Autowired
	private TradeRepository tradeRepository;

	List<String> mytrade = Arrays.asList("Buy", "Sell");

	RandomValueGenerator randomTrade = new RandomValueGenerator();

	Logger logger = LoggerFactory.getLogger(TradeService.class);

	// GET ALL TRADES FUNCTION
	public ResponseEntity<List<Trade>> getAllTrades() {

		logger.debug("############# INSIDE GET ALL TRADES FUNCTION #############");

		List<Trade> traders = new ArrayList<>();
		try {
			tradeRepository.findAll().forEach(traders::add);

			if (traders.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(traders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// GET TRADE BY ID FUNCTION
	public ResponseEntity<Trade> getTradeById(long id) {

		logger.debug("############# INSIDE GET TRADE BY ID FUNCTION #############");

		Optional<Trade> tradeData = tradeRepository.findById(id);

		if (tradeData.isPresent()) {
			return new ResponseEntity<>(tradeData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// GENERATE INITIAL TRADES FUNCTION
	public List<Trade> generateInitialTrades(List<FixedIncomeSecurity> masterdb) { 

		logger.debug("############# INSIDE GENERATE INITIAL TRADES FUNCTION #############");

		List<Trade> tradelist = new ArrayList<>();
		GregorianCalendar newyear = new GregorianCalendar();
		newyear.set(Calendar.YEAR, 2021);
		newyear.set(Calendar.MONTH, 0);
		newyear.set(Calendar.DATE, 01);
		System.out.println("generating trade");

		for (int i = 0; i < 25; i++) {
			System.out.println("i" + i);

			FixedIncomeSecurity randomSecurity = randomTrade.RandomElement(masterdb);
			String tradetype = randomTrade.RandomElementofTrade(mytrade);
			int newQuantity = randomTrade.RandomQuantityCalculator();
			GregorianCalendar date = randomTrade.RandomDate();

			String isin = randomSecurity.getIsin();
			String securityName = randomSecurity.getSecurityname();
			int faceValue = randomSecurity.getFaceValue();
			GregorianCalendar maturitydate = randomSecurity.getMaturitydate();
			String dcc = randomSecurity.getDcc();
			float couponRate = randomSecurity.getCouponRate();
			GregorianCalendar couponpaymentdate = randomSecurity.getCouponpaymentdate();
			GregorianCalendar bonusdate = randomSecurity.getBonusdate();

			int newprice = randomTrade.RandomPriceCalculator(faceValue);

			Trade trade = new Trade();
			trade.setSecurityname(securityName);
			trade.setIsin(isin);
			trade.setPrice(newprice);
			trade.setQuantity(newQuantity);
			trade.setDate(date);
			trade.setTradetype(tradetype);
			trade.setCouponRate(couponRate);
			trade.setDcc(dcc);
			trade.setFaceValue(faceValue);
			trade.setTradetype(tradetype);
			trade.setCouponpaymentdate(couponpaymentdate);
			trade.setMaturity(maturitydate);

			trade.setBonusdate(bonusdate);

			if (maturitydate.after(date)) {
				if (trade.getSecurityname().equalsIgnoreCase("GOVERNMENT OF INDIA T-BILL")
						&& trade.getPrice() <= trade.getFaceValue()) {
					tradelist.add(trade);
				}

				if (trade.getSecurityname() != ("GOVERNMENT OF INDIA T-BILL")) {
					tradelist.add(trade);
				}
			}

		}

		int i = 0;
		int a = 0;
		int b = 0;
		int k = 0;
		String bonussecurity = null;
		while (k < masterdb.size()) {
			if (masterdb.get(k).getBonusdate() != null) {
				bonussecurity = masterdb.get(k).getSecurityname();
			}
			k++;
		}

		while (i < masterdb.size()) {

			FixedIncomeSecurity security = masterdb.get(i);
			if (security.getMaturitydate().before(newyear)) {
				a = security.getOpeningqty();
				b = 0;
				GregorianCalendar bonusdate = security.getBonusdate();
				int j = 0;
				while (j < tradelist.size()) {
					System.out.println("j" + j);
					Trade trade = new Trade();
					trade = tradelist.get(j);
					if (trade.getSecurityname().equalsIgnoreCase(bonussecurity) && trade.getDate().before(bonusdate)) {
						b = security.getOpeningqty();
						String tradetype = trade.getTradetype();
						if (tradetype.equalsIgnoreCase("buy")) {
							b = b + trade.getQuantity();
						}
						if (tradetype.equalsIgnoreCase("sell")) {
							b = b - trade.getQuantity();
						}
					}
					if (trade.getSecurityname().equalsIgnoreCase(security.getSecurityname())) {
						if (trade.getTradetype().equalsIgnoreCase("buy")) {
							a = a + trade.getQuantity();
						}
						if (trade.getTradetype().equalsIgnoreCase("sell")) {
							a = a - trade.getQuantity();
						}
					}
					j++;
				}

				Trade trade = new Trade();
				trade.setSecurityname(security.getSecurityname());
				trade.setIsin(security.getIsin());
				trade.setPrice(security.getFaceValue());
				trade.setQuantity(a + (b / 20));
				trade.setDate(security.getMaturitydate());
				trade.setTradetype("Sell");
				trade.setCouponRate(security.getCouponRate());
				trade.setDcc(security.getDcc());
				trade.setFaceValue(security.getFaceValue());
				trade.setCouponpaymentdate(security.getCouponpaymentdate());
				trade.setMaturity(security.getMaturitydate());
				trade.setBonusdate(security.getBonusdate());

				tradelist.add(trade);

			}
			i++;
		}
		Collections.sort(tradelist, new Comparator<Trade>() {
			@Override
			public int compare(Trade object1, Trade object2) {
				return (int) (object1.getDate().compareTo(object2.getDate()));
			}
		});

		for (int m = 0; m < tradelist.size(); m++) {

			Trade trade = tradelist.get(m);
			tradeRepository
					.save(new Trade(trade.getSecurityname(), trade.getIsin(), trade.getPrice(), trade.getQuantity(),
							trade.getDate(), trade.getTradetype(), trade.getFaceValue(), trade.getCouponRate(),
							trade.getCouponpaymentdate(), trade.getMaturity(), trade.getDcc(), trade.getBonusdate()));

		}

		return tradelist;
	}
	
	

	// ADD TRADE BY USER FUNCTION
	public ResponseEntity<Trade> addTradeByUser(List<Trade> tradelist, Trade newTrade,
			List<FixedIncomeSecurity> masterDB) {

		logger.debug("############# INSIDE ADD TRADE BY USER FUNCTION #############");

		int openfund = masterDB.get(0).getOpeningfund();
		int opensecurity = 10000;
		int i = 0;
		while (i < masterDB.size()) {
			if (masterDB.get(i).getSecurityname().equalsIgnoreCase(newTrade.getSecurityname()))
				opensecurity = masterDB.get(i).getOpeningqty();
			i++;
		}

		int currentfund = openfund;
		String TradeSecurity = newTrade.getSecurityname();
		int Count = opensecurity;
		Iterable<Trade> it = tradeRepository.findAll();
		for (Trade element : it) {

			if (element.getDate().after(newTrade.getDate())) {
				break;
			}
			if (element.getDate().before(newTrade.getDate())) {

				if (newTrade.getTradetype().equalsIgnoreCase("sell")) {
					currentfund += element.getPrice() * element.getQuantity();
				}

				if (newTrade.getTradetype().equalsIgnoreCase("buy")) {
					currentfund -= element.getPrice() * element.getQuantity();
				}

				if (newTrade.getSecurityname().equalsIgnoreCase(TradeSecurity)) {
					if (newTrade.getTradetype().equalsIgnoreCase("sell")) {
						Count -= newTrade.getQuantity();
					}

					if (newTrade.getTradetype().equalsIgnoreCase("buy")) {
						Count += newTrade.getQuantity();
					}
				}
			}
		}

		int Allow = 0;
		if (newTrade.getTradetype().equalsIgnoreCase("buy")
				&& currentfund > (newTrade.getPrice() * newTrade.getQuantity())) {
			Allow = 1;
		}
		if (newTrade.getTradetype().equalsIgnoreCase("sell") && newTrade.getQuantity() < Count) {
			Allow = 1;
		}

		System.out.println(Allow);

		System.out.println(tradelist);

		if (Allow == 1) {
			try {
				System.out.println(tradelist);
				Trade trader = tradeRepository.save(new Trade(newTrade.getSecurityname(), newTrade.getIsin(),
						newTrade.getPrice(), newTrade.getQuantity(), newTrade.getDate(), newTrade.getTradetype(),
						newTrade.getFaceValue(), newTrade.getCouponRate(), newTrade.getCouponpaymentdate(),
						newTrade.getMaturity(), newTrade.getDcc(), newTrade.getBonusdate()));
				tradelist.add(newTrade);

				Collections.sort(tradelist, new Comparator<Trade>() {
					@Override
					public int compare(Trade object1, Trade object2) {
						return (int) (object1.getDate().compareTo(object2.getDate()));
					}
				});

				return new ResponseEntity<>(trader, HttpStatus.CREATED);
			} catch (Exception e) {
				System.out.println(e);
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>(null);
		}

	}

	public void deleteDB(List<Trade> tradelist) {

		tradeRepository.deleteAll();
	}

}
