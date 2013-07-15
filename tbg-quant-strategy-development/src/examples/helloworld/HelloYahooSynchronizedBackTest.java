/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.helloworld;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;

/**
 * SynchronizedBacktester  <br>
 * <br>
 * This strategy shows how to stream synchronized yahoo symbols.
 * Ex.  DAX (europe) and SPY (usa)
 *  * 
 * <br>
 * <b>History:</b><br>
 *  - [18/lug/2012] Created. (sfl)<br>
 *
 *  @author sfl
 */
public class HelloYahooSynchronizedBackTest extends TradingSystem{
	
	/**
	 * Sets Account
	 */
	private final PaperAccount account = new PaperAccount();
	{
		account.setAccountID("Paper Account");
		account.setAccountCurrency(Currency.USD);
	}
	
	
	/**
	 * Sets the broker (orders handler)
	 */
	private final IBroker broker = new PaperBroker(account);
	
	/**
	 * Sets the marketDataFeeder
	 */
	private final YahooMarketDataFeed marketDataFeed = new YahooMarketDataFeed();
	{
		/**
		 * Sets the historical daily data from Yahoo
		 * From : DD , MM , YYYY
		 * To   : DD , MM , YYYY
		 * Yahoo provides DAILY, WEEKLY and MONTHLY
		 */
		marketDataFeed.setYahooParameters("1", "7", "2013", "", "", "", YahooMarketDataFeed.YAHOO_DAILY);
		
		/**
		 * Sets the MarketDataEvent like CANDLE_EVENT, this is because YAHOO provides daily candles
		 */
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
		marketDataFeed.SLEEP_TIME = 1000;
		
		/**
		 * Use this setting only if you want to force synchronization between securities:
		 * ex:  
		 * 		XOM starts from 1970,  BHP from 1987 
		 * If you set FORCE_SYNCHRONIZATION = true you wont get any events until the 1980,
		 * if the parameter is false, you get only XOM event until 1980, then you will get
		 * both XOM and BHP...
		 */
		marketDataFeed.FORCE_SYNCHRONIZATION = false;
	}
	
	

	
	/**
	 * Costructor
	 */
	public HelloYahooSynchronizedBackTest() {
		setTradingSystemName("HelloYahooSynchronized");
		setTradingSystemDescription("Yahoo Market Data Feed adapter w/Synchronization");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		subscribeSecurities(new LoadSecurities(SecurityType.STK,"Yahoo",Currency.USD,"SPY,^GDAXI").getSecurities());		
	}
	
	/**
	 * on TradingSystem starts...
	 */
	public void onStart(){
		log.info("onStart(): ");		
	}
	
	
	/**
	 * on TradingSystem stops...
	 */
	public void onStop(){
		log.info("onStop(): ");
	}

	/**
	 * on Event received...
	 */
	@Override
	public void onEvent(Object event) {
		if (event instanceof CandleEvent) {
             CandleEvent candle = (CandleEvent) event;
             log.info("onEvent(): "+candle.toString());
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
		new HelloYahooSynchronizedBackTest().start();
    }
	
	
	
} // end
