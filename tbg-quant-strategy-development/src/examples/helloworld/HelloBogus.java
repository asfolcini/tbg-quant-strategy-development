/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.helloworld;

import com.tbg.adapter.bogus.marketdatafeed.BogusMarketDataFeed;
import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.core.model.Security;
import com.tbg.core.model.Order;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.account.IAccount;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.OrderSide;
import com.tbg.core.model.types.OrderTIF;
import com.tbg.core.model.types.OrderType;
import com.tbg.core.model.types.ReportType;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.PositionTracker;

 
/**
 * HelloBogus
 * 
 * using paperBroker and BogusMarketDataFeed
 * 
 * @author sfl  Oct,1,2011
 *
 */
public class HelloBogus extends TradingSystem{
	
	
	private final IReportService reportService = new TextReportService();
	{
		reportService.setReportType(ReportType.OUTPUT_ONLY);
	}
	
	/**
	 * Sets Account
	 */
	private final IAccount account = new PaperAccount(45000, Currency.USD);
	{
		account.setAccountID("Paper Account");
	}			

	
	/**
	 * Sets the broker (marketDataFeed and orders handler)
	 */	
	private final PaperBroker broker = new PaperBroker(account);
	{
		//broker.setSlippage(0.05);
		broker.setBrokerCommissions(SecurityType.STK, 0.05);
		broker.setBrokerCommissions(SecurityType.FUT, 5); // 5 pts
	}
	
	/**
	 * Sets the broker (marketDataFeed and orders handler)
	 */	
	private final BogusMarketDataFeed marketDataFeed = new BogusMarketDataFeed();
	{
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
	}

	
	/**
	 * Sets the security
	 */
	private final Security s = new Security();
	{
		s.setSymbol(new Symbol("AAPL"));
		s.setSecurityType(SecurityType.STK);
		s.setExchange("SMART");
		s.setCurrency(Currency.USD);	
	}; 
		
	
	
	/**
	 * Costructor
	 */
	public HelloBogus() {
		//setRunID("48");
		setTradingSystemName("BOGUS Trade System");
		setTradingSystemDescription("This is a demo strategy using tbg-Quant framework.");	
		/**
		 * BROKER
		 */
		setBroker(broker);		
		/**
		 * MARKET DATA FEED
		 */
		setMarketDataFeed(marketDataFeed);
		/**
		 *  SERVICES
		 */
		setReportService(reportService);
				
		/**
		 * SECURITIES
		 */
		subscribeSecurity(s);

	}
	
	/**
	 * on TradingSystem starts...
	 */
	public void onStart(){
		log.info("onStart(): ");	

		/**
		 * Schedule report generation every 60 seconds
		 */
		scheduleReportGeneration(60);		
	}
	
	
	/**
	 * on TradingSystem stops...
	 */
	public void onStop(){		
		log.info("onStop(): ");
		closeAllOpenPositions();						
		accountReport();
	}

	/**
	 * on Event received...
	 */
	private int count = 0;
	@Override
	public void onEvent(Object event) {
		count++;
		CandleEvent ce = (CandleEvent) event;
		String symbol = ce.getSymbol();
		
        log.info("onEvent(): "+ce.toString());
    
        
		// buy/sell every X events
		if (count>6){
			if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.NON_EXISTENT){
				Order order = new Order();
				order.setSecurity(s);
				order.setOrderSide(OrderSide.BUY);
				order.setOrderType(OrderType.MARKET);
				//order.setOrderType(OrderType.LIMIT);
				//order.setLimitPrice(11.25);
				order.setQuantity(100.0);
				order.setTimeInForce(OrderTIF.DAY);
		
				broker.sendOrder(order);									
				count=0;
			}else {
				closePositionFor(symbol);
				count=0;
			}
		}				

	}
	
	
	
	/**
	 * on TradingSystem error...
	 */
	@Override
	public void onError(Messages msg) {
		
		log.error("onError(): "+msg.getDesc());
		switch (msg){
		case NO_BROKER_CONNECTION:		
			// do somethings.....
			break;
		default:
			// ...		
		}		
	}
	
	
	/**
	 * Start it up !
	 */
	public static void main(String[] args) {
		new HelloBogus().start();
    }
	
	
	
} // end
