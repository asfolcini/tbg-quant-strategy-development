/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.tripleMAStartegy;

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
 * Triple MovingAverage Strategy <br>
 * <pre>
 * TrenFollower system working on Weekly data.
 * 
 * Fast SMA  10 
 * Mid  SMA  60
 * Slow SMA  200
 * 
 * RULES:
 * 
 * 1) Catching the trend
 * 		If Close>SlowSMA => TrendFilter UP
 *		If Close<SlowSMA => TrendFilter DOWN
 * 
 * 2) Catching the Signal
 * 		If TrendFilter is UP   &  FastSMA > MidSMA  => SignalLong  true
 * 		If TrendFilter is DOWN &  FastSMA < MidSMA  => SignalShort true
 * 
 * 3) Filtering the Signal
 * 		delay = 3 period;		
 * 
 * 		If SignalLong  = true & SignalLongPeriod > delay   => BUY
 * 			Else ClosePosition
 * 
 * 		If SignalShort = true & SignalLongPeriod > delay   => SHORT
 * 			Else ClosePosition    	 
 * 
 * </pre>
 * <b>History:</b><br>
 *  - [27/mar/2013] Created. (Alberto Sfolcini)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class TripleMAStrategy extends TradingSystem {

	/**
	 * ACCOUNT
	 */
	private final PaperAccount account = new PaperAccount(50000, Currency.EUR);
	
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
		marketDataFeed.setYahooParameters("1", "1", "2006", "1", "1", "2013", "Weekly");
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
	public TripleMAStrategy() {
		setTradingSystemName("Triple MovingAverage Strategy");
		setTradingSystemDescription("TrendFollower");
		setBroker(broker);
		setReportService(reportService);
		setMarketDataFeed(marketDataFeed);
		subscribeSecurities(new LoadSecurities(SecurityType.STK, "SMART",Currency.USD,"BAC,MA,AAPL").getSecurities());		
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
	SMA fastSMA = new SMA(21);
	SMA midSMA  = new SMA(40);
	SMA slowSMA = new SMA(200);
	@Override
	public void onEvent(Object event) {
		
		CandleEvent ce = (CandleEvent) event;
		String symbol  = ce.getSymbol();
		
		fastSMA.add(symbol, ce.getClosePrice());
		midSMA.add(symbol, ce.getClosePrice());
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
	private int	    delayEntry 	 = 3;
	private void core(CandleEvent ce){
		String symbol  = ce.getSymbol();
		
		/**
		 * 	1) Catching the trend
		 * 		If Close>SlowSMA => TrendFilter UP
		 *		If Close<SlowSMA => TrendFilter DOWN
		 */
    	if (ce.getClosePrice()>slowSMA.getValue(symbol))
    		trendTracker.setTrendSide(symbol, TrendSide.UP);
    	else
    		if (ce.getClosePrice()==slowSMA.getValue(symbol))
    			trendTracker.setTrendSide(symbol, TrendSide.FLAT);
    		else
    			trendTracker.setTrendSide(symbol, TrendSide.DOWN);
    	
    	/**
    	 * 2) Catching the Signal
    	 * 		If TrendFilter is UP   &  FastSMA > MidSMA  => SignalLong  true
    	 * 		If TrendFilter is DOWN &  FastSMA < MidSMA  => SignalShort true
    	 */
    	if ((trendTracker.getTrendSide(symbol)==TrendSide.UP)&&(fastSMA.getValue(symbol)>midSMA.getValue(symbol)))
    		signalTracker.setSignal(symbol, SignalType.LONG);
    	else    	
	    	if ((trendTracker.getTrendSide(symbol)==TrendSide.DOWN)&&(fastSMA.getValue(symbol)<midSMA.getValue(symbol)))
	        	signalTracker.setSignal(symbol, SignalType.SHORT);
			else
	        	signalTracker.setSignal(symbol, SignalType.FLAT);
		
    	/**
    	 * 		If SignalLong  = true & SignalLongPeriod > delay   => BUY
    	 * 			Else ClosePosition
    	 * 
    	 * 		If SignalShort = true & SignalLongPeriod > delay   => SHORT
    	 * 			Else ClosePosition    
    	 */
    	
    	// LONG SIDE
    	if (trendTracker.getTrendSide(symbol)==TrendSide.UP){
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
    	} // end TrendUP
    	
    	// SHORT SIDE
    	if (trendTracker.getTrendSide(symbol)==TrendSide.DOWN){
	    	if ((signalTracker.getSignal(symbol)==SignalType.SHORT)&&(signalTracker.getPeriods(symbol)>=delayEntry)){
	        	if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.NON_EXISTENT){
	        		// Open position for symbol
	        		Order order = new Order();
					order.setSecurity(getSecurityBySymbol(symbol));
					order.setOrderSide(OrderSide.SHORT_SELL);
					order.setOrderType(OrderType.MARKET);
					order.setQuantity(getSharesFor(10000, ce.getClosePrice()));
									
					broker.sendOrder(order);						
				}    	
	        }else{
	        	if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.POSITION_EXISTENT){				
					closePositionFor(symbol);
				}
	        }
    	} // end TrendDOWN
    	
    	
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TripleMAStrategy().start();

	}

}
