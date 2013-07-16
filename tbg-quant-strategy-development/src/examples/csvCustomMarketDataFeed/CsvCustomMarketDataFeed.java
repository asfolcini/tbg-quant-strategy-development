/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.csvCustomMarketDataFeed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.tbg.core.model.Security;
import com.tbg.core.model.marketDataFeed.BacktestMarketDataFeedBase;
import com.tbg.core.model.marketDataFeed.IMarketDataFeed;
import com.tbg.core.model.types.MarketDataEventType;
import com.tbg.core.model.types.TimeStamp;
import com.tbg.core.model.types.event.CandleEvent;

/**
 * Csv Custom MarketDataFeed <br>
 * Show how to write your custom CSV MarketDataFeed, this example show how to use local CSV (Yahoo Format).<br>
 * You can easily adapt to your CSV format by changing downloadQuotes() & loadData() methods.<br>
 * <br>
 * <br>
 * <b>History:</b><br>
 *  - [15/jul/2012] Created. (sfl)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class CsvCustomMarketDataFeed extends BacktestMarketDataFeedBase implements IMarketDataFeed{

	protected final static Logger log = Logger.getLogger(CsvCustomMarketDataFeed.class);
		
	private BufferedReader input;
	private boolean skipFirstLine = true;				
	

	public CsvCustomMarketDataFeed(){
		this.setMarketDataEvent(MarketDataEventType.CANDLE_EVENT);		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.tbg.core.model.marketDataFeed.BacktestMarketDataFeedBase#onSubscribeMarketData(com.tbg.core.model.Security)
	 */
	@Override
	public void onSubscribeMarketData(Security security) {						
		downloadQuotes(security);													
	}


	
	
	/**
	 * Downloads Quotes
	 * @param security
	 */
	private void downloadQuotes(Security security){
		List<String> content = new ArrayList<String>();
		
		String csvFile = security.getSymbol().toString();
		log.info("Fetching "+csvFile+" content...");
		
		try {
			input = new BufferedReader(new FileReader(csvFile));
		} catch (IOException e1) {
			log.error(e1);
		}
		if (skipFirstLine){ 
			try {input.readLine();}catch(Exception e){}
		}

		String str;
		try{
			while ((str = input.readLine()) != null) {
				content.add(str);
			}
		}catch(Exception e){}
		
		// reverse the order (you need to do that with yahoo data)
		Collections.reverse(content);
			
		loadData(content,security);			
		
	}
	
	
	/**
	 * loads data, one candle at time and generate tickEvents...
	 */
	private void loadData(List<String> content,Security security) {
		double open = 0.0; 
		double high = 0.0;
		double low = 0.0;
		double close = 0.0;
		double adjClose = 0.0;
		int volume = 0;
		TimeStamp timestamp = null;
		ArrayList<CandleEvent> candles = new ArrayList<CandleEvent>();
		
		for(int i=0;i<content.size();i++){			
			StringTokenizer st = new StringTokenizer(content.get(i).toString(), ","); 
			while(st.hasMoreTokens()){		
				String data_tmp = st.nextToken();
				Date dt = null;
				try {
					dt = new SimpleDateFormat("yyyy-MM-dd").parse(data_tmp);
				} catch (ParseException e) {
					log.error(e);
				}
				timestamp 	= new TimeStamp(new Timestamp(dt.getTime())); 				
				open 		= Double.parseDouble(st.nextToken());
				high 		= Double.parseDouble(st.nextToken());
				low 		= Double.parseDouble(st.nextToken());
				close 		= Double.parseDouble(st.nextToken());
				volume 		= Integer.parseInt(st.nextToken());
				adjClose 	= Double.parseDouble(st.nextToken());
				
				CandleEvent candleEvent = new CandleEvent();
				candleEvent.setSymbol(security.getSymbol().toString());
				candleEvent.setOpenPrice(open);
				candleEvent.setHighPrice(high);
				candleEvent.setLowPrice(low);
				//candleEvent.setClosePrice(adjClose);
				candleEvent.setClosePrice(close);
				candleEvent.setTimeStamp(timestamp);
				candleEvent.setVolume(volume);
				
				if (debug) log.info(" ====> Csv Candle: "+candleEvent.toString());											
				
				candles.add(candleEvent);
				candleEvents.put(security, candles);															
			}
		} // end for	
	}
	

	@Override
	public void connectToMarketFeed() {
		log.info("Connected to YahooMarketDataFeed.");	
	}

	@Override
	public void disconnectFromMarketDataFeed() {
		log.info("Connection to YahooMarketDataFeed closed.");	
	}



}
