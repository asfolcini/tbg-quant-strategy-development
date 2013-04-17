/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.orderRouter;

import java.io.File;

import com.tbg.adapter.event.marketdatafeed.FakeEventMarketDataFeed;
import com.tbg.adapter.interactivebrokers.broker.IBBroker;
import com.tbg.adapter.interactivebrokers.broker.InteractiveBrokersAdapter;
import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.core.model.Order;
import com.tbg.core.model.Security;
import com.tbg.core.model.strategy.IStrategy;
import com.tbg.core.model.types.Messages;
import com.tbg.strategy.TradingSystem;

/**
 * OrderRouter <br>
 * <br>
 * Scenario
 * <br>
 *  - I have already working algorithm wrote in R
 *  - This algo produces signals in CSV format and store them in a folder
 *  - I want to route these orders to IntaractiveBrokers throught tbg-Quant,
 *    so I can monitoring orders & execution with the tbg-Quant-Desk
 * <br>
 * Idea:<br>
 *  - Set the broker like the InteractiveBrokers
 *  - Use a fake MarketDataFeed that produces a fake market event in order to 
 *    trig the onEvent() method 
 *  - Implements onEvent() method with:
 *    - Reading CSV file from a given folder
 *    - Prepare the orders and send them to the broker  
 *    
 * <b>History:</b><br>
 *  - [02/feb/2013] Created. (Alberto Sfolcini)<br>
 *
 *  @author a.sfolcini@gmail.com
 *    
 * <br>
 * Check out www.thebonnotgang.com online support.
 * <br>
 */
public class OrderRouter extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount();
	private final InteractiveBrokersAdapter ibAdapter = new InteractiveBrokersAdapter();
	 {
		 ibAdapter.setDebug(false);
	 }
	private final IBBroker broker = new IBBroker(account, ibAdapter);
	private final FakeEventMarketDataFeed marketDataFeed = new FakeEventMarketDataFeed();
	 {
		 // raise a fake event every X millisecs
		 marketDataFeed.setEventTime(10000); // 10 sec
	 }
	
	// where the CSV orders are stored
	private String ordersPath = "C:\\temp\\signals\\";
	 
	
	public OrderRouter() {
		setTradingSystemName("Orders Router");
		setTradingSystemDescription("Routes orders to InteractiveBrokers");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {
		log.info("STOP");
	}

	@Override
	public void onEvent(Object event) {

		log.info("Event raised!");
		
		// LOGIC
		// 1. Check for CSV file in a given folder.		
		// 2. Read the files
		// 3. Prepare orders
		// 4. Send them to the broker 
		
		if (broker.isConnected()){		
			
			 File[] filesInDirectory = new File(ordersPath).listFiles();  
		        for(File f : filesInDirectory){  
		            String filePath = f.getAbsolutePath();  
		            String fileExtenstion = filePath.substring(filePath.lastIndexOf(".") + 1,filePath.length());  
		            if("csv".equals(fileExtenstion)){  
		                log.info("CSV file found "+filePath);            		                
		                LoadSignalFromCSV signal = new LoadSignalFromCSV(filePath);
		                if (signal.isReady()){
		                	Security _security	= signal.getSecurity();
		                	ibAdapter.subscribeMarketData(_security);
		                	Order 	 _order 	= signal.getOrder();		                			                			                	
		                	broker.sendOrder(_order);
		                	// 	Rename "csv" to "processed", please not that is case sensitive!
		                	File _in = new File(filePath);
		                	File _out = new File(filePath.replaceAll(".csv", ".processed"));			                	
		                	if (_in.exists())
		                		_in.renameTo(_out);		            
		                } else {
		                	log.error("Signal in "+filePath+" cannot be processed !!");
		                	// send out an alarm !
		                }
		            }
		        }
		
		}else{
			log.warn("Cannot connect to the Broker, try again...");
			// send some important alarm
		}			
		
			
	} // end of onEvent()

	@Override
	public void onError(Messages msg) {
		// TODO Auto-generated method stub		
	}


	/**
	 * Start it up!
	 */
	public static void main(String[] args) {
		new OrderRouter().start();
	}

}
