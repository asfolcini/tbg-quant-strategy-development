/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.seasonal;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.Order;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.strategy.IStrategy;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.OrderSide;
import com.tbg.core.model.types.OrderType;
import com.tbg.core.model.types.ReportType;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.SignalType;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.core.util.MiscUtils;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;
import com.tbg.strategy.utils.PositionTracker;

/**
 * 
 * November - April Strategy <br>
 * This strategy tries to exploit the seasonal November-April effect.
 * 
 * DETAILS
 * 
 * A well-known example of seasonal strategy in the stock markets is to buy in November and sell in April.
 * Tests have demonstrated that investors would get better returns by buying at the start of November 
 * and selling at the end of April.
 * In this strategy we want to exploit this effect but we buy at the end of October and sell at the start of May, this
 * way we take advantage of the turn of the month. 
 *  
 * Strategy returns avarge of 6.6% per year (gross) from 1990 to 2014 with a high rate of winner trades (72%)
 * on 3 stocks: XOM, BHP and LMT.
 *  
 * <br>
 * <b>History:</b><br>
 *  - [2/apr/2011] Created. (Alberto Sfolcini)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class NovApr_Strategy extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount(45000, Currency.USD);
	private final IBroker broker = new PaperBroker(account);		
	private final YahooMarketDataFeed marketDataFeed = new YahooMarketDataFeed();	
	{
		marketDataFeed.setYahooParameters("1", "1", "1990", "1", "1", "2014", YahooMarketDataFeed.YAHOO_DAILY);
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
	}
	
	/**
	 * Setting the report service, by default prints report to the output.
	 */
	private final IReportService reportService = new TextReportService("NovApr_Strategy",ReportType.OUTPUT_AND_STORE);
	
	
	/**
	 * Sets the System and services
	 */
	public NovApr_Strategy() {
		setTradingSystemName("November-April Strategy");
		setTradingSystemDescription("Strategy that try to exploit the seasonal November-April effect.");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
		setReportService(reportService);
		subscribeSecurities(new LoadSecurities(SecurityType.STK, "SMART",Currency.USD,"XOM,LMT,BHP").getSecurities());
		setReportService(reportService);
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {		
		this.closeAllOpenPositions();
		this.accountReport();
		log.info("STOP");
	}

	private int startDay   	= 26;
	private int endDay		= 4;
	private int buyMonth	= 11; // means end of October
	private int sellMonth   = 6; // means start of may
	@Override
	public void onEvent(Object event) {		
		CandleEvent ce = (CandleEvent) event;
		String symbol  = ce.getSymbol();
		
		String date = MiscUtils.getFormattedDate("yyyy/MM/dd", ce.getTimeStamp().getDate());
		int tmpDay 	 = Integer.parseInt(date.substring(date.lastIndexOf("/")+1, date.length()));
		int tmpMonth = Integer.parseInt(date.substring(date.indexOf("/")+1, date.lastIndexOf("/")))+1;	

		
		if ((tmpDay>=startDay)&&(tmpMonth>=buyMonth)){
			this.signalTracker.setSignal(symbol, SignalType.LONG);							
		}else{
			if ((tmpDay>=endDay)&&(tmpMonth==sellMonth))					
				this.signalTracker.setSignal(symbol, SignalType.CLOSE);			
		}

		// BUY LONG
		if (signalTracker.getSignal(symbol).equals(SignalType.LONG)){
			if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.NON_EXISTENT){

				// Open position for symbol
        		Order order = new Order();
				order.setSecurity(getSecurityBySymbol(symbol));
				order.setOrderSide(OrderSide.BUY);
				order.setOrderType(OrderType.MARKET);
				order.setQuantity(getSharesFor(10000, ce.getClosePrice()));
								
				broker.sendOrder(order);						
			}
		}

		// CLOSE POSITION
		if (signalTracker.getSignal(symbol).equals(SignalType.CLOSE)){
			if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.POSITION_EXISTENT)
				closePositionFor(symbol);		
		}
			
		
	}

	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());			
	}


	/**
	 * Start it up!
	 */
	public static void main(String[] args) {
		new NovApr_Strategy().start();
	}

}
