/**
 * 
 */
package howtostop;

import com.tbg.adapter.bogus.marketdatafeed.BogusMarketDataFeed;
import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.marketDataFeed.IMarketDataFeed;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.strategy.IStrategy;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;

/**
 * How to stop a strategy <br>
 * <br>
 * Stops the current strategy by calling the invokeStrategyStop() method.<br>
 * 
 * Check out www.thebonnotgang.com online support.
 * <br>
 */
public class HowToStopAStrategy extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount();
	private final IBroker broker = new PaperBroker(account);
	private final IMarketDataFeed marketDataFeed = new BogusMarketDataFeed();
	{
		marketDataFeed.setMarketDataEvent(MarketDataEventType.TICK_EVENT);
	}
	private final IReportService reportService = new TextReportService();
	
	
	public HowToStopAStrategy() {
		setTradingSystemName("TestStopStrategy");
		setTradingSystemDescription("How to stop a strategy");
		setBroker(broker);
		setReportService(reportService);
		setMarketDataFeed(marketDataFeed);
		subscribeSecurities(new LoadSecurities(SecurityType.STK,"SMART",Currency.USD,"IBM,XOM").getSecurities());
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {
		log.info("STOP");
		this.accountReport();
	}

	int count = 0;
	@Override
	public void onEvent(Object event) {
		log.info(count+" "+event);	
		
		count++;
				
		// Quit the strategy...
		if (count>10)
			this.invokeStrategyStop();
		
	}

	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());	
	}


	/**
	 * Start it up!
	 */
	public static void main(String[] args) {
		new HowToStopAStrategy().start();
	}

}
