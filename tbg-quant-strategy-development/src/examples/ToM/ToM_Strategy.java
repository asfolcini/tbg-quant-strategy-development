/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.ToM;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.Order;
import com.tbg.core.model.Security;
import com.tbg.core.model.Symbol;
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
import com.tbg.strategy.utils.PositionTracker;

/**
 * 
 * Turn of the Month Effect Strategy <br>
 * This strategy tries to exploit the well known anomaly of the end of the month effect.
 * 
 * DETAIL 
 * The turn of the month is well known effect on stock indexes which states that stocks prices usually 
 * increase during the last four days and the first three days of each month. Therefore it is possible to capture a 
 * substantial part of equity return only during this fraction of market time and stay invested in safe cash 
 * during rest of the year.
 * 
 * Most researchers ascribe this effect to the timing of monthly cash flows received by pension funds and reinvested 
 * in the stock market. End of the month is also a natural point for portfolio/trading models rebalancing between 
 * retail and professional investors and this also could help this effect to become statistically significant. 
 * However, caution is needed in implementing this strategy as calendar effects tends to vanish or rotate to 
 * different days in month. 
 * 
 * FUTURE IMPROVEMENT
 * Try to improve this strategy by filter the entry point if current close is above the 200 days moving average.
 *   
 * <br>
 * <b>History:</b><br>
 *  - [2/apr/2011] Created. (Alberto Sfolcini)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class ToM_Strategy extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount(12000, Currency.USD);
	private final IBroker broker = new PaperBroker(account);		
	private final YahooMarketDataFeed marketDataFeed = new YahooMarketDataFeed();	
	{
		marketDataFeed.setYahooParameters("1", "1", "2000", "1", "1", "2012", "Daily");
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
	}
	
	/**
	 * Setting the report service, by default prints report to the output.
	 */
	private final IReportService reportService = new TextReportService("ToM",ReportType.OUTPUT_AND_STORE);
	
	/**
	 * Sets Security
	 */
	private final Security security = new Security();
	{
		security.setSymbol(new Symbol("SPY"));
		security.setCurrency(Currency.USD);
		security.setSecurityType(SecurityType.STK);
		security.setExchange("SMART");		
	}
	
	
	
	/**
	 * Sets the System and services
	 */
	public ToM_Strategy() {
		setTradingSystemName("Turn of the Month Strategy");
		setTradingSystemDescription("Strategy that try to exploit the well known Turn of the Month effect.");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
		setReportService(reportService);
		this.subscribeSecurity(security);
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

	
	private int startDay	= 26;
	private int endDay		= 3;
	private int buyMonth	= 0;
	@Override
	public void onEvent(Object event) {		
		CandleEvent ce = (CandleEvent) event;
		String symbol  = ce.getSymbol();
		
		String date = MiscUtils.getFormattedDate("yyyy/MM/dd", ce.getTimeStamp().getDate());
		int tmpDay 	 = Integer.parseInt(date.substring(date.lastIndexOf("/")+1, date.length()));
		int tmpMonth = Integer.parseInt(date.substring(date.indexOf("/")+1, date.lastIndexOf("/")))+1;	
		if (tmpMonth==13)
			tmpMonth=1;
		
		if (tmpDay>=startDay){
			buyMonth = tmpMonth+1;
			if (buyMonth==13)
				buyMonth=1;
			this.signalTracker.setSignal(symbol, SignalType.LONG);
							
		}else{
			if ((tmpDay>=endDay)&&(tmpMonth==buyMonth))					
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
		new ToM_Strategy().start();
	}

}
