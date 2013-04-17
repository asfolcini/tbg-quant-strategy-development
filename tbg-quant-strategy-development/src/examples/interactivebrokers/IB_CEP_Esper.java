/**
 *  tbg-quant-strategy-development project
 *  
 *  Alberto Sfolcini <a.sfolcini@gmail.com>
 */
package examples.interactivebrokers;

import java.util.Map;

import com.tbg.adapter.interactivebrokers.account.IBAccount;
import com.tbg.adapter.interactivebrokers.broker.IBBroker;
import com.tbg.adapter.interactivebrokers.broker.IBMarketDataFeed;
import com.tbg.adapter.interactivebrokers.broker.InteractiveBrokersAdapter;
import com.tbg.core.model.cep.ICEPProvider;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.TimeStamp;
import com.tbg.service.cep.EsperCEPProvider;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;
 
/**
 * 
 * <strong>Complex Event Processing with InteractiveBrokers</strong><br>
 * Since IB is not providing candles in this example we use the CEP to build 1 minute candles!<br>
 * <br>
 * WARNING: Esper CEP is used, please be sure to include esper library in the classpath !!
 * <br>
 * <b>History:</b><br>
 *  - [13/apr/2012] Created. (Alberto Sfolcini)<br>
 *  <br>
 *  <br>
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class IB_CEP_Esper extends TradingSystem{
			
	
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
	 * Sets CEP Provider, using ESPER (include esper-4.9.0.jar library)
	 */
	private ICEPProvider CEP = new EsperCEPProvider(this);
	{
		// When you use more than 1 instrument be sure to use:   tick.std:groupwin(symbol) 
		final String [] QUERY =
		    {
		    	" INSERT INTO candles"+
		    	" SELECT symbol, max(timestamp) as timestamp, min(price) as low, max(price) as high, prev(count(*)-1, price) as open, price as close, size as volume"+ 
				" FROM tick.std:groupwin(symbol).win:time(1 min)"+
		    	" GROUP BY symbol"
		    	,    	    	
		    	" INSERT INTO quote" +
		    	" SELECT * FROM candles c GROUP BY symbol output last every 1 minute"
		    	,    	    	
		    	" SELECT * FROM quote.std:lastevent()"
		    };
		CEP.setCepQuery(QUERY);
	}
	
			
	
	/**
	 * Costructor
	 */
	public IB_CEP_Esper() {
		setTradingSystemName("IB_CEP_Esper");
		setTradingSystemDescription("InteractiveBrokers tick streaming");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		this.setCEPProvider(CEP);
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
				
		Map candle = getEventMap(event);
		
		String symbol = (String) candle.get("symbol");
		double open  = (Double) candle.get("open");
		double close = (Double) candle.get("close");
		double high  = (Double) candle.get("high");
		double low   = (Double) candle.get("low");
		TimeStamp ts = (TimeStamp) candle.get("timestamp");
		double	volume = (Double) candle.get("volume");
		
		log.info("Symbol: "+symbol+" TS: "+ts+" Open: "+open+" High: "+high+" Low: "+low+" Close: "+close+" Volume: "+volume);
		
		// would you like to record each candle to a CSV or DB ?
		
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
		new IB_CEP_Esper().start();
    }
	
	
	
} // end
