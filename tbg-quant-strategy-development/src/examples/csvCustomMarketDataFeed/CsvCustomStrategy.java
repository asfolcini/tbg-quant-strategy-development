/**
 * 
 */
package examples.csvCustomMarketDataFeed;

import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.marketDataFeed.IMarketDataFeed;
import com.tbg.core.model.strategy.IStrategy;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;

/**
 * CsvCustomMarketDataFeed TradingSystem strategy <br>
 * <br>
 * Use this code to start coding your strategy.
 * <br>
 * Check out www.thebonnotgang.com online support.
 * <br>
 */
public class CsvCustomStrategy extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount();
	private final IBroker broker = new PaperBroker(account);
	private final IMarketDataFeed marketDataFeed = new CsvCustomMarketDataFeed();
	
	public CsvCustomStrategy() {
		setTradingSystemName("CsvCustomStrategy");
		setTradingSystemDescription("CsvCustomMarketDataFeed Strategy");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
		// multisecurity  physical path to your CSV files, 1 files = 1 security.
		subscribeSecurities(new LoadSecurities(SecurityType.STK,"SMART",Currency.USD,"C:\\temp\\XOM_csv_yahoo.csv,C:\\temp\\LUXMI_csv_yahoo.csv").getSecurities());
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
		log.info(event);	
		
	}

	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());	
	}


	/**
	 * Start it up!
	 */
	public static void main(String[] args) {
		new CsvCustomStrategy().start();
	}

}
