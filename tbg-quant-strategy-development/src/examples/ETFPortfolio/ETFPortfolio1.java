/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.ETFPortfolio;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.Order;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.OrderSide;
import com.tbg.core.model.types.OrderType;
import com.tbg.core.model.types.ReportType;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.SignalType;
import com.tbg.core.model.types.TrendSide;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.indicator.SMA;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;
import com.tbg.strategy.utils.PositionTracker;

/**
 * ETF Portfolio  <br>
 * <pre>
 * Portfolio management with Moving averages and ETF
 * 
 * 
 * </pre>
 * <b>History:</b><br>
 *  - [27/mar/2013] Created. (Alberto Sfolcini)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class ETFPortfolio1 extends TradingSystem {

	/**
	 * ACCOUNT
	 */
	private final PaperAccount account = new PaperAccount(45000, Currency.USD);
	
	/**
	 * BROKER 
	 */
	private final IBroker broker = new PaperBroker(account);
	{
		//broker.setBrokerCommissions(SecurityType.FUTURE, 3.0);
	}
	
	/**
	 * MARKET DATA FEED
	 */
	private final YahooMarketDataFeed marketDataFeed = new YahooMarketDataFeed();
	{
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
		marketDataFeed.setYahooParameters("1", "1", "2000", "1", "1", "2013", "Weekly");
	}
	
	/**
	 * REPORT SERVICE
	 */
	private final IReportService reportService = new TextReportService();
	{
		reportService.setReportType(ReportType.OUTPUT_ONLY);
	}
	
	
	/**
	 * Costructor
	 */
	public ETFPortfolio1() {
		setTradingSystemName("Triple MovingAverage Strategy");
		setTradingSystemDescription("TrendFollower");
		setBroker(broker);
		setReportService(reportService);
		setMarketDataFeed(marketDataFeed);
		subscribeSecurities(new LoadSecurities(SecurityType.STK, "SMART",Currency.USD,"GLD,SPY,SHY").getSecurities());		
		setMinimunSystemPeriods(200);
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {
		log.info("STOP");
		closeAllOpenPositions();
		accountReport();
	}
	
	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());		
	}
	
	
	/**
	 * ON EVENT 
	 */
	SMA slowSMA = new SMA(200);
	@Override
	public void onEvent(Object event) {
		
		CandleEvent ce = (CandleEvent) event;
		String symbol  = ce.getSymbol();
		
		slowSMA.add(symbol, ce.getClosePrice());
		trendTracker.setTrendSide(symbol, TrendSide.FLAT);
		
		// if system is activated, call the core engine!
        if (getSystemActivation(symbol)){           
        	core(ce);
        }

	}
	
	
	/**
	 * Core engine
	 * @param ce
	 */
	private int	    delayEntry 	 = 1;
	private void core(CandleEvent ce){
		String symbol  = ce.getSymbol();
		
	
    	if (ce.getClosePrice()>slowSMA.getValue(symbol))
    		signalTracker.setSignal(symbol, SignalType.LONG);
    	else
    		signalTracker.setSignal(symbol, SignalType.FLAT);
    	
    	
    	// LONG SIDE
     	if ((signalTracker.getSignal(symbol)==SignalType.LONG)&&(signalTracker.getPeriods(symbol)>=delayEntry)){
	        	if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.NON_EXISTENT){
	        		// Open position for symbol
	        		Order order = new Order();
					order.setSecurity(getSecurityBySymbol(symbol));
					order.setOrderSide(OrderSide.BUY);
					order.setOrderType(OrderType.MARKET);
					order.setQuantity(getSharesFor(10000, ce.getClosePrice()));
									
					broker.sendOrder(order);						
				}    	
        }else{
	        	if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.POSITION_EXISTENT){				
					closePositionFor(symbol);
				}
        }
    	    	    	
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ETFPortfolio1().start();

	}

}
