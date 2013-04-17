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
import com.tbg.core.model.cep.ICEPProvider;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.OrderSide;
import com.tbg.core.model.types.OrderTIF;
import com.tbg.core.model.types.OrderType;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.event.TickEvent;
import com.tbg.service.cep.EsperCEPProvider;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;
 
/**
 * IB_CancelingOrders_Esper<br>
 * <br>
 * TickEvent are processed by ESPER Engine and printed out every 5 seconds with average price.
 * <br>
 * It sends a limit order and then cancel it.
 * <br>
 * WARNING: Esper CEP is used, please be sure to include esper library in the classpath !!
 * <br>
 * @author sfl  Oct,1,2011
 *
 */
public class IB_CancelingOrders_Esper extends TradingSystem{
				
	private int count=0;
	
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

	private final IReportService reportService = new TextReportService();
	
	/**
	 * Sets the security
	 */
	private final Security s1 = new Security();
	{
		s1.setSymbol(new Symbol("XOM"));
		s1.setSecurityType(SecurityType.STK);
		s1.setExchange("SMART");
		s1.setCurrency(Currency.USD);	
	}; 
	private final Security s2 = new Security();
	{
		s2.setSymbol(new Symbol("CVX"));
		s2.setSecurityType(SecurityType.STK);
		s2.setExchange("SMART");
		s2.setCurrency(Currency.USD);	
	}; 
	
	
	/**
	 * Sets CEP Provider, using ESPER (include esper-4.9.0.jar library)
	 */
	private ICEPProvider CEP = new EsperCEPProvider(this);
	{
		final String [] QUERY = {
				"select * from tick.std:groupwin(symbol).win:time_batch(5 sec) group by symbol"
		};		
		CEP.setCepQuery(QUERY);
	}
	
	
	/**
	 * Costructor
	 */
	public IB_CancelingOrders_Esper() {
		setTradingSystemName("IB_cancelingOrders");
		setTradingSystemDescription("CEP query, placing and canceling an order with InteractiveBrokers.");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		setReportService(reportService);
		subscribeSecurity(s1);
		subscribeSecurity(s2);
		this.setCEPProvider(CEP);
		super.SLEEP_TIME=1000;
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
			count++;
			
			TickEvent tick = (TickEvent) event;
			
			if (count==5){					
					Order order = new Order();
					order.setSecurity(getSecurityBySymbol(tick.getSymbol()));
					order.setOrderSide(OrderSide.BUY);					
					order.setOrderType(OrderType.LIMIT);
					order.setLimitPrice(0.99);
					order.setQuantity(100.0);
					order.setTimeInForce(OrderTIF.DAY);
			
					broker.sendOrder(order);
			}
						
			if (count>8) {						
				cancelOrderForSymbol(getSecurityBySymbol(tick.getSymbol()).getSymbol());
				count = 0;
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
		new IB_CancelingOrders_Esper().start();
    }
	
	
	
} // end
