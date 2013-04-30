/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.helloworld;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.tbg.marketdatafeed.TBGMarketDataFeed;
import com.tbg.core.model.Security;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.strategy.TradingSystem;
 
/**
 * HelloTBG<br>
 * <br>
 * This strategy loads data from theBonnotGang website (Online Mode) or by CSV files downloaded from
 * the same website (Offline Mode).<br>
 * TBG Historical 1 minute data are raw data, should be cleaned and checked before backtesting.
 * 
 * @author sfl  Oct,11,2011
 *
 */
public class HelloTBG extends TradingSystem{
		
	/**
	 * Sets Account
	 */
	private final PaperAccount account = new PaperAccount();
	{
		account.setAccountID("Paper Account");
		account.setAccountCurrency(Currency.USD);
	}	
	
	/**
	 * Sets PaperBroker
	 */
	private final PaperBroker broker = new PaperBroker(account);
	{
		broker.setDebug(false);
		broker.setBrokerId(111);
	}
	

	/**
	 * Sets marketDataFeed
	 */
	private final TBGMarketDataFeed marketDataFeed = new TBGMarketDataFeed();	
	{	
		// this is just to set an ID, you can skip it.
		marketDataFeed.setMarketDataFeedId(112);
		// sets the online mode
		marketDataFeed.setTbgMode(TBGMarketDataFeed.TBG_MODE_ONLINE);
		// sets the timeperiod
		marketDataFeed.setTBGParameters("10", "7", "2012", "11", "7", "2012");
		// set the MarketDataEvent type, trades or candles ?
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
	}	
		
	/**
	 * Sets the security
	 */
	private final Security s = new Security();
	{
		//s.setSymbol(new Symbol("C:\\Temp\\XOM_1m.csv"));
		// list of online tbg symbols can be found here: http://thebonnotgang.com/tbg/historical-data/
		s.setSymbol(new Symbol("VXX")); // Online Mode, fetches data from www.thebonnotgang.com
		s.setSecurityType(SecurityType.STK);
		s.setExchange("SMART");
		s.setCurrency(Currency.USD);	
	}; 
	
	
	/**
	 * Costructor
	 */
	public HelloTBG() {
		setTradingSystemName("HelloTBG");
		setTradingSystemName("Backtesting using TheBonnotGang MarketDataFeed offline & online.");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);				
		subscribeSecurity(s);	
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
		log.info("onEvent(): "+event.toString());
		
		// WRITE HERE YOUR SYSTEM LOGIC !!
		
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
		new HelloTBG().start();
    }
	
	
	
} // end
