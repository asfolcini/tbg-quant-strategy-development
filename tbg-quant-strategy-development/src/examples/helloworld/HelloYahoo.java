/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.helloworld;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.Security;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.strategy.TradingSystem;

/**
 * HelloYahoo! <br>
 * <br>
 * This strategy shows the YahooMarketDataFeed and the CANDLE_EVENT.
 * It processes DAILY candles.
 * 
 * <br>
 * <b>History:</b><br>
 *  - [18/lug/2012] Created. (sfl)<br>
 *
 *  @author sfl
 */
public class HelloYahoo extends TradingSystem{
	
	
	
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
		marketDataFeed.setYahooParameters("1", "7", "1900", "", "", "", YahooMarketDataFeed.YAHOO_DAILY);
		
		/**
		 * Sets the MarketDataEvent like CANDLE_EVENT, this is because YAHOO provides daily candles
		 */
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
	}
	
	
	
	/**
	 * Sets the security
	 */
	private final Security s = new Security();
	{
		// this is the YAHOO symbol
		s.setSymbol(new Symbol("XOM"));
		s.setSecurityType(SecurityType.INDEX);
		s.setExchange("SMART");
		s.setCurrency(Currency.USD);	
	}; 
	
	
	/**
	 * Costructor
	 */
	public HelloYahoo() {
		setTradingSystemName("HelloYahoo");
		setTradingSystemDescription("Yahoo Market Data Feed adapter");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		subscribeSecurity(s);
		super.SLEEP_TIME=0;
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
		new HelloYahoo().start();
    }
	
	
	
} // end
