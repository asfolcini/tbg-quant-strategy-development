/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.skeleton;

import com.tbg.adapter.bogus.marketdatafeed.BogusMarketDataFeed;
import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.marketDataFeed.IMarketDataFeed;
import com.tbg.core.model.strategy.IStrategy;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;

/**
 * Skeleton Strategy code.
 * <br>
 * Use this code like a starting point to create something more unique :-)
 * <br>
 */
public class SkeletonStrategy extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount();
	private final IBroker broker = new PaperBroker(account);
	private final IMarketDataFeed marketDataFeed = new BogusMarketDataFeed();
	{
		marketDataFeed.setMarketDataEvent(MarketDataEventType.TICK_EVENT);
	}
	
	public SkeletonStrategy() {
		setTradingSystemName("Skeleton Strategy");
		setTradingSystemDescription("Use this code to create something more unique.");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
		subscribeSecurities(new LoadSecurities(SecurityType.STK,"SMART",Currency.USD,"IBM,XOM,AAPL,GOOG").getSecurities());
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {
		log.info("STOP");
	}

	@Override
	public void onEvent(Object event) {
		log.info(event);	
		
	}

	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());	
	}


	/**
	 * Start it up!
	 */
	public static void main(String[] args) {
		new SkeletonStrategy().start();
	}

}
