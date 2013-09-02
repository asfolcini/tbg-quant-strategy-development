/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.MultiTimeframes;


import com.tbg.adapter.bogus.marketdatafeed.BogusMarketDataFeed;
import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.marketDataFeed.IMarketDataFeed;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.TimeFrame;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.core.model.types.event.TickEvent;
import com.tbg.service.utils.TickToCandleEventService;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;

/**
 * MultiTimeFrame
 * Shows how to look at different timeframes.
 * 
 * @author sfl
 */
public class MultiTimeFrame extends TradingSystem {

	private final PaperAccount account = new PaperAccount();
	private final IBroker broker = new PaperBroker(account);
	private final IMarketDataFeed marketDataFeed = new BogusMarketDataFeed();
	{
		marketDataFeed.setMarketDataEvent(MarketDataEventType.TICK_EVENT);
	}

	
	
	public MultiTimeFrame() {
		setTradingSystemName("MultiTimeFrame");
		setTradingSystemDescription("This Strategy will look at different timeframes before taking action");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
		subscribeSecurities(new LoadSecurities(SecurityType.STK,"SMART",Currency.USD,"IBM,GOOG").getSecurities());
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {
		log.info("STOP");
	}

	
	// Services 
	TickToCandleEventService candle1min = new TickToCandleEventService(TimeFrame.TIMEFRAME_1min,this);
	TickToCandleEventService candle5min = new TickToCandleEventService(TimeFrame.TIMEFRAME_5min,this);

	@Override
	public void onEvent(Object event) {
		log.info(event);	
		
		String symbol = ((TickEvent) event).getSymbol();
		// will produce candleEvents and fire them onCustomCandleEvent()...
		candle1min.subscribe(symbol,event);
		candle5min.subscribe(symbol,event);
		
		// do other stuff on tickEvents....
		
	}

	@Override
	public void onCustomCandleEvent(CandleEvent event){
		
		switch (event.getTimeFrame()){
			case TIMEFRAME_1min:
				System.err.println("1 MINUTE !!");
				break;
			case TIMEFRAME_5min:
				System.err.println("5 MINUTE !!");
				break;
		default:
			break;
		}
		
		log.info("onCustomCandleEvent(): "+event.toString());
	}
	
	
	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());	
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MultiTimeFrame().start();

	}

}
