package com.citi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.citi.algorithms.CalculationsService;
import com.citi.algorithms.TradeService;
import com.citi.entity.Date;
import com.citi.entity.FixedIncomeSecurity;
import com.citi.entity.RandomValueGenerator;
import com.citi.entity.Trade;
import com.citi.repositories.TradeRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TradeController {

	@Autowired
	private TradeService tradeService;

	@Autowired
	private CalculationsService calculationsService;

	List<FixedIncomeSecurity> masterDB = new ArrayList<>();
			
			

	List<Trade> tradelist;

	Logger logger = LoggerFactory.getLogger(TradeController.class);

	RandomValueGenerator rvg = new RandomValueGenerator();

	//Retrieving master db
	@GetMapping(value = "/masterdb")
	public List<FixedIncomeSecurity> getMasterDB() {
		logger.debug("=========  MASTER DB  =========");
		return masterDB;
	}

	//Get all trades
	@RequestMapping(method = RequestMethod.GET, value = "/trades")
	public ResponseEntity<List<Trade>> getAllTrades() {
		logger.debug("=========  GET ALL TRADES  =========");
		tradelist = tradeService.getAllTrades().getBody();
		return tradeService.getAllTrades();
	}

	//Get trade by id
	@RequestMapping(method = RequestMethod.GET, value = "/trades/{id}")
	public ResponseEntity<Trade> getTradeById(@PathVariable("id") long id) {
		logger.debug("=========  GET TRADE BY ID  =========");
		return tradeService.getTradeById(id);
	}

	//Get entire tradelist
	@RequestMapping(method = RequestMethod.GET, value = "/trades/list")
	public List<Trade> getTradeList() {
		logger.debug("=========  GET TRADE LIST  =========");
		return tradelist;
	}

	//Add new trade by user
	@RequestMapping(method = RequestMethod.POST, value = "/trades/add")
	public ResponseEntity<Trade> addTrade(@RequestBody Trade newTrade) {
		logger.debug("=========  ADD NEW TRADE BY USER  =========");
		return tradeService.addTradeByUser(tradelist, newTrade, masterDB);
	}

	//Get coupon income per security at the end of the year
	@RequestMapping(method = RequestMethod.GET, value = "/couponincome")
	public Map<String, Double> getCouponIncomes() {
		logger.debug("=========  CALCULATING COUPON INCOME  =========");
		return calculationsService.Couponincome(tradelist, masterDB);
	}

	//Get accrued coupon income per security at the end of the year
	@RequestMapping(method = RequestMethod.GET, value = "/acccouponincome")
	public Map<String, Double> getAccruedCouponIncomes() {
		logger.debug("=========  CALCULATING ACCRUED COUPON INCOME  =========");
		return calculationsService.Accruedcouponincome(tradelist, masterDB);
	}

	//Get current fund at a particular date
	@RequestMapping(method = RequestMethod.POST, value = "/closingfund")
	public Double getCurrFunds(@RequestBody GregorianCalendar date) {
		logger.debug("=========  CALCULATING CURRENT FUND  =========");
		return calculationsService.Closingfund(tradelist, masterDB, date);
	}
	
	//Get closing fund at the end of the year
	@RequestMapping(method = RequestMethod.GET, value = "/eoyclosingfund")
	public Double getClosingFund() {
		logger.debug("=========  CALCULATING CLOSING FUND  =========");
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, 2021);
		gc.set(Calendar.MONTH, 0);
		gc.set(Calendar.DATE, 1);
		System.out.println(gc);
		return calculationsService.Closingfund(tradelist, masterDB, gc);
	}

	//Get realised profit and loss per security on a particular date
	@RequestMapping(method = RequestMethod.POST, value = "/pnl")
	public Map<String, Double> getProfitAndLoss(@RequestBody GregorianCalendar date) {
		logger.debug("=========  CALCULATING REALIZED PROFIT AND LOSS PER SECURITY  =========");
		return calculationsService.PLpersecurity(tradelist, masterDB, date);
	}
	
	//Get realised profit and loss per security at the end of the year
	@RequestMapping(method = RequestMethod.GET, value = "/eoypnl")
	public Map<String, Double> getProfitAndLoss() {
		logger.debug("=========  CALCULATING REALIZED PROFIT AND LOSS PER SECURITY  =========");
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, 2021);
		gc.set(Calendar.MONTH, 0);
		gc.set(Calendar.DATE, 1);
		return calculationsService.PLpersecurity(tradelist, masterDB, gc);
	}

	//Get unrealised profit and loss per security at the end of the year
	@RequestMapping(method = RequestMethod.GET, value = "/upnl")
	public Map<String, Double> getUnrealisedProfitAndLoss() {
		logger.debug("=========  CALCULATING UNREALIZED PROFIT AND LOSS PER SECURITY  =========");
		return calculationsService.UPLpersecurity(tradelist, masterDB);
	}

	//Get final market valuation at the end of the year
	@RequestMapping(method = RequestMethod.GET, value = "/mvalue")
	public double getMarketValuation() {
		logger.debug("=========  CALCULATING FINAL MARKET VALUATION  =========");
		return calculationsService.MarketValuation(tradelist, masterDB);
	}

	//Generate initial trades list using Random Generator
	@RequestMapping(method = RequestMethod.POST, value = "/trades/all")
	public List<Trade> createTradeList() {
		logger.debug("=========  GENERATING INITIAL TRADES USING RANDOM GENERATOR  =========");
		masterDB = Arrays.asList(
				new FixedIncomeSecurity("IN1015467857", 100, "GOVERNMENT OF INDIA T-BILL", 0, 2020, 9, 30),
				new FixedIncomeSecurity("INE002A14F01", 1000, "RELIANCE INDUSTRIES CORPORATE BOND", 6, 2020, 02, 15, 2021,
						02, 15, "a/360"),
				new FixedIncomeSecurity("INE002A14F02", 1000, "BAJAJ CERTIFICATE OF DEPOSIT", 4, 2020, 06, 30, 2021, 04, 30,
						"a/365"),
				new FixedIncomeSecurity("INE002A14F03", 1000, "ABG SHIPYARD LTD. DEBENTURE", 5, 2020, 04, 10, 2026, 8, 10,
						"a/366"),
				new FixedIncomeSecurity("INE002A14F04", 100, "GOVERNMENT OF INDIA BOND", 4.5f, 2020, 10, 25, 2021, 06, 27,
						"30/360"));
		masterDB = rvg.UpdateMasterDb(masterDB);
				
		int i = 0;
		while (i < masterDB.size()) {
			
			System.out.println(masterDB.get(i) + "  PRICE: "+masterDB.get(i).getOpeningprice()+ "  qty: "+masterDB.get(i).getOpeningqty() );
			
			if (masterDB.get(i).getBonusdate() != null) {
				System.out.println(masterDB.get(i).getSecurityname() + " " + masterDB.get(i).getBonusdate());
			}
			i++;
		}

		tradelist = tradeService.generateInitialTrades(masterDB);

		return tradelist;
	}

	//Get closing quantity of each security at year end 
	@RequestMapping(method = RequestMethod.GET, value = "/closingqty")
	public Map<String, Integer> getClosingQtyOfSecurities() {
		logger.debug("=========  CLOSING QTY OF SECURITIES  =========");
		return calculationsService.getClosingQty(tradelist, masterDB);
	}

	//User logout clears the trade list
	@RequestMapping(method = RequestMethod.GET, value = "/logout")
	public void userLogout() {
		logger.debug("=========  USER LOGOUT  =========");
		System.out.println(tradelist.size());
		tradeService.deleteDB(tradelist);
		tradelist.clear();
		System.out.println(tradelist.size());
	}

}