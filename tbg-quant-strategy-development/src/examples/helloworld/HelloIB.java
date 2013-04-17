/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.helloworld;

import com.tbg.adapter.interactivebrokers.account.IBAccount;
import com.tbg.adapter.interactivebrokers.broker.IBBroker;
import com.tbg.adapter.interactivebrokers.broker.IBMarketDataFeed;
import com.tbg.adapter.interactivebrokers.broker.InteractiveBrokersAdapter;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;
 
/**
 * 
 * Hello InteractiveBrokers !! <br>
 * <br>
 * It connects to IB thru TWS or IB Gateway and gets the MarketData for symbols.
 * Be sure to include the IB API library in the classpath !!
 * <br>
 * <b>History:</b><br>
 *  - [01/oct/2011] Created. (sfl)<br>
 *  - [11/mar/2013] Added multisecurity. (sfl)<br>
 *  <br>
 *  @author sfl
 */
public class HelloIB extends TradingSystem{
			
	
	/**
	 * Sets Account
	 */
	private final IBAccount account = new IBAccount();
	{
		account.setAccountID("Paper Account");
		account.setAccountCurrency(Currency.USD);
	}	
	
	/**
	 * Sets the InteractiveBrokersAdapter
	 */
	private final InteractiveBrokersAdapter interactiveBrokersAdapter = new InteractiveBrokersAdapter();
	{
		// settings for IB Gateway/TWS Connection
		// demo account is availabe with this login: edemo/demouser
		interactiveBrokersAdapter.setTWS_PORT(7496); // TWS Client
		//interactiveBrokersAdapter.setTWS_PORT(4001); // IBGateway 
		interactiveBrokersAdapter.setTWS_HOST("127.0.0.1");		
	}
	
	/**
	 * Sets the broker (orders handler)
	 */
	private final IBBroker broker = new IBBroker(account,interactiveBrokersAdapter);
	
	/**
	 * Sets the marketDataFeeder
	 */
	private final IBMarketDataFeed marketDataFeed = new IBMarketDataFeed(interactiveBrokersAdapter);
	{
		marketDataFeed.setDebug(false);
		marketDataFeed.setEventDelay(200);
	}

	
	/**
	 * Costructor
	 */
	public HelloIB() {
		setTradingSystemName("HelloIB");
		setTradingSystemDescription("InteractiveBrokers tick streaming");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		this.subscribeSecurities(new LoadSecurities(SecurityType.STK,"SMART",Currency.USD,"XOM,CVX,MS,IBM,BHP,LMT,GS").getSecurities());
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
			
			log.info(event.toString());
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
		new HelloIB().start();
    }
	
	
	
} // end
