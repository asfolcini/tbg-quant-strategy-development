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
import com.tbg.core.model.Symbol;
import com.tbg.core.model.account.IAccount;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.cep.ICEPProvider;
import com.tbg.core.model.marketDataFeed.IMarketDataFeed;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.service.cep.SiddhiCEPProvider;
import com.tbg.strategy.TradingSystem;
 
/**
 * HelloSiddhi<br>
 * <br>
 * Siddhi CEP is a lightweight, easy-to-use Open Source Complex Event Processing Engine (CEP) <br>
 * under Apache Software License v2.0. Siddhi CEP processes events which are triggered by various<br> 
 * event sources and notifies appropriate complex events according to the user specified queries.<br>
 * <br>
 * @author Alberto Sfolcini  March,18, 2013
 *
 */
public class HelloSiddhi extends TradingSystem{
			

	/**
	 * Sets Account
	 */
	private final IAccount account = new PaperAccount();
	{
		account.setAccountID("FakeAccount");
		account.setAccountCurrency(Currency.USD);		
	}
	
	/**
	 * Sets the broker (marketDataFeed and orders handler)
	 */
	private final IBroker broker = new PaperBroker(account);
	
	/**
	 * Sets MarketDataFeed
	 */
	private final IMarketDataFeed marketDataFeed = new BogusMarketDataFeed();
	{
		marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
	}
	
	/**
	 * Sets the security
	 */
	private final Security s = new Security();
	{
		s.setSymbol(new Symbol("GOOG"));
		s.setSecurityType(SecurityType.STK);
		s.setExchange("SMART");
		s.setCurrency(Currency.USD);	
	}; 
	
	/**
	 * Sets CEP Provider, using SIDDHI
	 */
	private ICEPProvider CEP = new SiddhiCEPProvider(this);
	{
		final String [] QUERY = {
//				"from tickStream [win.time(100)] ",
//				"insert into tick symbol, avg(price) as avgPrice, volume as vol",
//				"group by symbol"
				"from candleStream [win.time(100)] ",
				"insert into candle symbol, closePrice, avg(closePrice) as avgClose, volume as vol",
				"group by symbol"

		};
		
		CEP.setCepQuery(QUERY);
	}
	
	
	/**
	 * Costructor
	 */
	public HelloSiddhi() {
		setTradingSystemName("HelloSiddhi");
		setTradingSystemDescription("Using SIDDHI as a CEP Provider");
		setBroker(broker);	
		setMarketDataFeed(marketDataFeed);
		subscribeSecurity(s);
		setCEPProvider(CEP);
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
		new HelloSiddhi().start();
    }
	
	
	
} // end
