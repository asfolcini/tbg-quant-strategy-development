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
import com.tbg.core.model.Order;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.cep.ICEPProvider;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.OrderSide;
import com.tbg.core.model.types.OrderTIF;
import com.tbg.core.model.types.OrderType;
import com.tbg.core.model.types.SecurityType;
import com.tbg.service.cep.EsperCEPProvider;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.PositionTracker;
 
/**
 * IB_TS_Esper<br>
 * <br>
 * InteractiveBroker Example<br>
 * Using InteractiveBrokers as a MarketDataFeed and Broker<br>
 * <strong>BE AWARE NOT TO USE THIS EXAMPLE ON YOUR IB REAL ACCOUNT !!<br>
 * Orders will be executed!</strong>
 * <br>
 * WARNING: Esper CEP is used, please be sure to include esper library in the classpath !!
 * <br>
 * @author sfl  Oct,1,2011
 *
 */
public class IB_TS_Esper extends TradingSystem{
				
	private int count=0;
	
	/**
	 * Sets Account
	 */
	private final IBAccount account = new IBAccount();
	{
		account.setAccountID("IB Account");
		account.setAccountCurrency(Currency.USD);				
	}	
	
	/**
	 * Sets the InteractiveBrokersAdapter
	 */
	private final InteractiveBrokersAdapter interactiveBrokersAdapter = new InteractiveBrokersAdapter();
	
	/**
	 * Sets the broker (orders handler)
	 */
	private final IBroker broker = new IBBroker(account,interactiveBrokersAdapter);
	
	/**
	 * Sets the marketDataFeeder
	 */
	private final IBMarketDataFeed marketDataFeed = new IBMarketDataFeed(interactiveBrokersAdapter);
	{
		marketDataFeed.setDebug(false);
	}

	private final IReportService reportService = new TextReportService();
	
	/**
	 * Sets the security
	 */
	private final Security s = new Security();
	{
		s.setSymbol(new Symbol("XOM"));
		s.setSecurityType(SecurityType.STK);
		s.setExchange("SMART");
		s.setCurrency(Currency.USD);	

	}; 
	
	/**
	 * Sets CEP Provider, using ESPER (include esper-4.9.0.jar library)
	 */
	private ICEPProvider CEP = new EsperCEPProvider(this);
	{
		final String [] QUERY = {
				"select t.symbol,t.price, t.timestamp from tick.win:time_batch(10 sec) t"+
				" where t.price>0"
				
		};
		CEP.setCepQuery(QUERY);
	}
	
	
	/**
	 * Costructor
	 */
	public IB_TS_Esper() {
		setTradingSystemName("IB_TS_Esper");
		setTradingSystemDescription("This is a IB demo strategy that executes order, using demo or paper accounts.");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		setReportService(reportService);
		subscribeSecurity(s);
		this.setCEPProvider(CEP);
		super.SLEEP_TIME=3000;
	}
	
	/**
	 * on TradingSystem starts...
	 */
	public void onStart(){
		log.info("onStart(): ");	
		// schedule report generation every XX seconds
		//this.scheduleReportGeneration(60);
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

	     String symbol = s.getSymbol().toString();
			
			if (count>4){
				if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.NON_EXISTENT){
					Order order = new Order();
					order.setSecurity(s);
					order.setOrderSide(OrderSide.BUY);
					order.setOrderType(OrderType.MARKET);
					order.setQuantity(100.0);
					order.setTimeInForce(OrderTIF.DAY);
			
					broker.sendOrder(order);
				}
			}
			
			if (count > 10){
				if (positionTracker.getStatusForSymbol(symbol)==PositionTracker.POSITION_EXISTENT){					
					// Close position for symbol
					closePositionFor(symbol);
					count = 0;
				}
			}			
				
			count++;
	// strategy onEvent() ends
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
		new IB_TS_Esper().start();
    }
	
	
	
} // end
