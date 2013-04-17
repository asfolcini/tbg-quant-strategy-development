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
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.service.cep.EsperCEPProvider;
import com.tbg.strategy.TradingSystem;
 
/**
 * HelloEsper<br>
 * <br>
 * ESPER is an opensource CEP distributed under GPL2.0 license.<br>
 * Since tbg-Quant is proprietary software the Esper library is not bundled.<br>
 * Be sure to add the Esper library in the classpath.<br>
 * You can download esper library from here: http://esper.codehaus.org/
 * <br>
 * @author Alberto Sfolcini  March,18, 2013
 *
 */
public class HelloEsper extends TradingSystem{
			

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
		//marketDataFeed.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);
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
	 * Sets CEP Provider, using ESPER
	 */
	private ICEPProvider CEP = new EsperCEPProvider(this);
	{
		final String [] QUERY = {
				//"select symbol, closePrice, avg(closePrice) from candle.win:time_batch(5 sec)"
				"select symbol, price, avg(price) from tick.win:time_batch(5 sec)"
		};		
		CEP.setCepQuery(QUERY);
	}
	
	
	/**
	 * Costructor
	 */
	public HelloEsper() {
		setTradingSystemName("HelloEsper");
		setTradingSystemDescription("Using ESPER as a CEP Provider");
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
		new HelloEsper().start();
    }
	
	
	
} // end
