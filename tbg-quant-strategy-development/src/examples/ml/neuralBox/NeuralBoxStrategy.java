/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.ml.neuralBox;
 
import org.encog.Encog;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.Security;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.report.IReportService;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.ReportType;
import com.tbg.core.model.types.RollingWindow;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.ml.neuralbox.DataSet;
import com.tbg.service.report.TextReportService;
import com.tbg.strategy.TradingSystem;

/**
 * Machine Learning - Neural Networks Strategy <br>
 * <br>
 * FeedForward NN
 * INPUTS	:  	SMA1 , SMA2 , Close  
 * IDEAL	:	Close+1
 * 
 * 
 * <br>
 * 
 * <b>History:</b><br>
 *  - [18/lug/2012] Created. (sfl)<br>
 *
 *  @author sfl
 */
public class NeuralBoxStrategy extends TradingSystem{
	
	private int ROLLING_WINDOW = 250;
		
	/**
	 * Sets Account
	 */
	private final PaperAccount account = new PaperAccount(20000, Currency.USD);
	{
		account.setAccountID("Paper Account");
		account.setAccountCurrency(Currency.USD);
	}
	
	
	/**
	 * Sets the broker (orders handler)
	 */
	private final IBroker broker = new PaperBroker(account);
	
	/**
	 * Sets report service
	 */
	private final IReportService reportService = new TextReportService("NNStrategy",ReportType.OUTPUT_ONLY);

	
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
		marketDataFeed.setYahooParameters("1", "1", "2012", "2", "1", "2013", YahooMarketDataFeed.YAHOO_DAILY);
		
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
		s.setSymbol(new Symbol("SPY"));
		s.setSecurityType(SecurityType.INDEX);
		s.setExchange("SMART");
		s.setCurrency(Currency.USD);	
	}; 
	
	
	/**
	 * Costructor
	 */
	public NeuralBoxStrategy() {
		setTradingSystemName("NeuralNetworks Strategy");
		setTradingSystemDescription("");
		setBroker(broker);		
		setMarketDataFeed(marketDataFeed);
		setReportService(reportService);
		subscribeSecurity(s);
		setMinimunSystemPeriods(ROLLING_WINDOW);
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
		System.err.println(rw.size());
		Encog.getInstance().shutdown();
		//this.accountReport();
	}

	/**
	 * on Event received...
	 */	
	RollingWindow rw = new RollingWindow(ROLLING_WINDOW);
	DataSet	ds = new DataSet();
	@Override
	public void onEvent(Object event) {
		CandleEvent ce = (CandleEvent) event;		
		String symbol = ce.getSymbol().toString();
			
		rw.add(ce);			
			
		if (getSystemActivation(s.getSymbol().toString())){   												
			/**
			 * BUILDING DATASET  ( RollingWindow )
			 */			
			// inputs
			Double[] yield = new Double[rw.size()]; // historical Data
			Double[] volume = new Double[rw.size()]; // trading volume
			// output
			Double[] yield_y = new Double[rw.size()]; // tomorrow yield
			
			for (int i=0;i<rw.size();i++){
				CandleEvent c = (CandleEvent) rw.get(i);
				yield[i] 	= Math.log(c.getClosePrice()/c.getOpenPrice())*100;
				volume[i]	= (double) c.getVolume();
				//yield_y[i] 	= 0.0;
				if (i>0)
					yield_y[i-1]  = yield[i];
			}
			
			try {
				// INPUTS X
				ds.addInputDataSet("Yield t", yield);
				ds.addInputDataSet("Volume", volume);				
				// OUTPUT Y
				ds.addIdealDataSet("Yield t+1", yield_y);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println(ds.displayDataSet());
			try {
				ds.normalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ds.displayDataSet());
			System.out.println(ds.displayDataSetInfo());
			
		} // end of systemActivation
		
	}

		
	/**
	 * OnPrediction event
	 * @param event
	 * @param prediction
	 */
	protected void onPrediction(Object event, double[] prediction){
		
	}
	
	
	/**
	 * on TradingSystem error...
	 */
	@Override
	public void onError(Messages msg) {	
		log.error("onError(): "+msg.getDesc());
	}
	
	
	/**
	 * Start it up !
	 */
	public static void main(String[] args) {
		new NeuralBoxStrategy().start();
    }
	
	
	
} // end
