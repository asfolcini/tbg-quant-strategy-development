/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.interactivebrokers;

import com.tbg.adapter.interactivebrokers.account.IBAccount;
import com.tbg.adapter.interactivebrokers.broker.IBBroker;
import com.tbg.adapter.interactivebrokers.broker.IBMarketDataFeed;
import com.tbg.adapter.interactivebrokers.broker.InteractiveBrokersAdapter;
import com.tbg.core.model.Security;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.strategy.TradingSystem;

 
/**
 * 
 * <strong>Futures with InteractiveBrokers</strong><br>
 * Streaming futures quotes from IBMarketDataFeed.<br>
 * <br>
 * WARNING: Be sure to include IB API library !!
 * <br>
 * <b>History:</b><br>
 *  - [18/amay/2012] Created. (Alberto Sfolcini)<br>
 *  <br>
 *  <br>
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class IB_Futures extends TradingSystem{
			
	
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
			
	private final Security DAX = new Security();
	{
		DAX.setSymbol(new Symbol("DAX")); 
		DAX.setSecurityType(SecurityType.FUT);
		DAX.setExchange("DTB");
		DAX.setCurrency(Currency.EUR);
		DAX.setExpiry("201306");
	}

	private final Security ESmini = new Security();
	{
		ESmini.setSymbol(new Symbol("ES")); 
		ESmini.setSecurityType(SecurityType.FUT);
		ESmini.setExchange("GLOBEX");
		ESmini.setCurrency(Currency.USD);
		ESmini.setExpiry("201306");
	}

	private final Security FTMIB = new Security();
	{
		FTMIB.setSymbol(new Symbol("FTMIB")); 
		FTMIB.setSecurityType(SecurityType.FUT);
		FTMIB.setExchange("IDEM");
		FTMIB.setCurrency(Currency.EUR);
		FTMIB.setExpiry("201306");
	}
		
	/**
	 * Costructor
	 */
	public IB_Futures() {
		setTradingSystemName("IB_Futures");
		setTradingSystemDescription("InteractiveBrokers Futures example");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		subscribeSecurity(DAX);
		subscribeSecurity(ESmini);
		subscribeSecurity(FTMIB);
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
		new IB_Futures().start();
    }
	
	
	
} // end
