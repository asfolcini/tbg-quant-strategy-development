/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package utils;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import com.tbg.adapter.paper.account.PaperAccount;
import com.tbg.adapter.paper.broker.PaperBroker;
import com.tbg.adapter.yahoo.marketdatafeed.YahooMarketDataFeed;
import com.tbg.core.model.broker.IBroker;
import com.tbg.core.model.strategy.IStrategy;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.Messages;
import com.tbg.core.model.types.SecurityType;
import com.tbg.core.model.types.TimeStamp;
import com.tbg.core.model.types.event.CandleEvent;
import com.tbg.core.util.MiscUtils;
import com.tbg.core.util.Writer;
import com.tbg.strategy.TradingSystem;
import com.tbg.strategy.utils.LoadSecurities;

/**
 * 
 * Yahoo Historical Data Synchronizer<br>
 * <br>
 * Synchronizes symbols data, save result in a CSV file. 
 *   
 * <br>
 * <b>History:</b><br>
 *  - [06/may/2013] Created. (sfl)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 */
public class YahooDataSynchronizer extends TradingSystem implements IStrategy{
	
	private final PaperAccount account = new PaperAccount();
	private final IBroker broker = new PaperBroker(account);
	
	
	/**
	 * USER'S PARAMETERS 
	 */
	// SYM1,SYM2,SYMn  without space
	private String SYMBOLS 		= "";
	private String OUTPUT  		= "output.csv";
	private String SEPARATOR 	= ";";
	private String DECIMAL 		= ",";
	// Needs top be DD/MM/YYYY
	private String FROM	 		= "01/01/1900";
	private String TO			= "31/12/2020";
	// DAILY, WEEKLY, MONTHLY
	private String TIMEFRAME 	= "DAILY";
	
	/**
	 * Sets the marketDataFeeder
	 */
	private final YahooMarketDataFeed marketDataFeed = new YahooMarketDataFeed();
	
	public YahooDataSynchronizer(String symbols,String from,String to,String timeframe,String fileOutput,String separatorChar,String decimalChar) {
		this.SYMBOLS 	= symbols;
		this.FROM		= from;
		this.TO			= to;
		this.TIMEFRAME	= timeframe;
		this.OUTPUT		= fileOutput;
		this.SEPARATOR	= separatorChar;
		this.DECIMAL	= decimalChar;

		log.info("PARAMETERS");
		log.info(" SYMBOLS   : "+symbols);
		log.info(" FROM      : "+from);
		log.info(" TO        : "+to);
		log.info(" TIMEFRAME : "+timeframe);
		log.info(" OUTPUT    : "+fileOutput);
		log.info(" SEPARATOR : "+separatorChar);
		log.info(" DECIMAL   : "+decimalChar);
				
		String _from[] 	= FROM.split("/");
		String _to[] 	= TO.split("/");		
		marketDataFeed.setYahooParameters(_from[0], _from[1], _from[2], _to[0], _to[1], _to[2], TIMEFRAME);
		
		setTradingSystemName("Yahoo Historical Data Synchronizer");
		setTradingSystemDescription("Synchronizes Yahoo Historical Data");
		setBroker(broker);
		setMarketDataFeed(marketDataFeed);
		// securityType, exchange and currency really dont matter... :-)
		subscribeSecurities(new LoadSecurities(SecurityType.STK,"SMART",Currency.USD,SYMBOLS).getSecurities());
	}
	
	
	@Override
	public void onStart() {
		log.info("START");
	}

	@Override
	public void onStop() {
		Writer wrt = null;
		try {
			wrt = new Writer(OUTPUT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
				
		String header = "Nr;DateTime";
		String s[] = SYMBOLS.split(",");	
		for(int i=0;i<s.length;i++)
			header += SEPARATOR+s[i]+" Open"+SEPARATOR+s[i]+" High"+SEPARATOR+s[i]+" Low"+SEPARATOR+s[i]+" Close"+SEPARATOR+s[i]+" Volume"+SEPARATOR+s[i]+" Yield C/O";
		// System.err.println(header);
		wrt.writeln(header);
		int counter = 0;
		for (Entry<TimeStamp, ArrayList<CandleEvent>> e : hm.entrySet()) {
			counter++;
			String row = counter+SEPARATOR+MiscUtils.getFormattedDate("yyyy/MM/dd hh:mm:sss",e.getKey().getDate());
		    ArrayList<CandleEvent> ace = e.getValue();
		    // Align
		    if (ace.size()==s.length){
			    for(int j=0;j<s.length;j++){
				    for(int i=0;i<ace.size();i++){
				    	CandleEvent ce = ace.get(i);
				    	if (ce.getSymbol().equalsIgnoreCase(s[j])){
				    		String yield = (""+Math.log(ce.getClosePrice()/ce.getOpenPrice())*100).replace(".", DECIMAL); 
				    		row  +=  (SEPARATOR+ce.getOpenPrice()).replace(".", DECIMAL)
				    				+(SEPARATOR+ce.getHighPrice()).replace(".", DECIMAL)
				    				+(SEPARATOR+ce.getLowPrice()).replace(".", DECIMAL)
				    				+(SEPARATOR+ce.getClosePrice()).replace(".", DECIMAL)
				    				+(SEPARATOR+ce.getVolume()).replace(".", DECIMAL)
				    				+(SEPARATOR+yield).replace(".", DECIMAL);
				    		break;
				    	}
				    }				 
			    }
		    	//System.err.println(row);
			    wrt.writeln(row);
			}		    		    		   
		} //TreeMap
		wrt.close();
		log.info("========>>>>>> "+counter+" rows synchronized for symbols ["+SYMBOLS+"], output file saved in "+OUTPUT);
	}
	
	// this is an ordered map
	TreeMap<TimeStamp, ArrayList<CandleEvent>> hm = new TreeMap<TimeStamp, ArrayList<CandleEvent>>(); 
	@Override
	public void onEvent(Object event) {					
		
		CandleEvent ce = (CandleEvent) event;
		
		if (hm.containsKey(ce.getTimeStamp())){
			ArrayList<CandleEvent> ace = hm.get(ce.getTimeStamp());
			ace.add(ce);
			hm.put(ce.getTimeStamp(), ace);
		}else{
			ArrayList<CandleEvent> ace = new ArrayList<CandleEvent>();
			ace.add(ce);
			hm.put(ce.getTimeStamp(), ace);
		}
								
	}

	
	@Override
	public void onError(Messages msg) {
		log.error(msg.toString());	
	}


	
	private static boolean isLegalDate(String s) {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    sdf.setLenient(false);
	    return sdf.parse(s, new ParsePosition(0)) != null;
	}
	
	public static void showHelp(){
		System.out.println();
		System.out.println(YahooDataSynchronizer.class.getSimpleName()+": Synchronizes Yahoo Historical Data.");		
		System.out.println(":: Alberto Sfolcini <a.sfolcini@gmail.com>");
		System.out.println(":: "+Messages.VERSION.getDesc()+"   "+Messages.WWWTBG.getDesc());
		System.out.println();
		System.out.println("USAGE:\t"+YahooDataSynchronizer.class.getSimpleName()+" [symbols] [from] [to] [timeframe] [outputFile] [csvSeparator] [decimalSeparator]");
		System.out.println();
		System.out.println("PARAMETERS:");
		System.out.println("\t [symbols]          list of symbols to synchronize, comma separated ex. XOM,BHP,LMT");
		System.out.println("\t [from]             From Date format DD/MM/YYYY ex. 01/05/1999");
		System.out.println("\t [to]               To Date   format DD/MM/YYYY ex. 31/12/2012");
		System.out.println("\t [timeframe]        Daily, Weekly, Monhtly");
		System.out.println("\t [outputFile]       Path and Name of the output file ex. SynchronizedData.csv");
		System.out.println("\t [csvSeparator]     CSV Separator comma or semicolon ex. ; ");
		System.out.println("\t [decimalSeparator] Decimal Separator comma or dot ex. ,");
		System.out.println();
		System.out.println("EXAMPLE:\t"+YahooDataSynchronizer.class.getSimpleName()+" SPY,^GDAXI 01/01/2006 31/12/2013 daily synchronizedDax.csv ';' ','");
		System.out.println();
		
	}
	
	/**
	 * Start it up!
	 */
	public static void main(String[] args) {
		boolean error = false;
		String result = "PARAMETERS ERRORS: ";
		
		if (args.length!=7)
			showHelp();
		else{
			
			/**
			 * Here we should check each parameter...
			 * We cannot check if the symbol code exists...
			 */
			
			// Symbols checking
			if (args[0].split(",").length<2){
				error = true;
				result = result + "\n [symbols] insert more than one symbol separated by comma, no spaces.";
			}
			// timeframe checking
			if (!((args[3].equalsIgnoreCase("daily"))||(args[3].equalsIgnoreCase("weekly"))||(args[3].equalsIgnoreCase("monthly")))){
				error = true;
				result = result + "\n [timeframe] values : daily, weekly, monthly";
			}
			// Date Checking
			if (!isLegalDate(args[1])){
				error = true;
				result = result + "\n [from] From Date, use the format dd/MM/yyyy ex. 10/05/2012";				
			}
			if (!isLegalDate(args[2])){
				error = true;
				result = result + "\n [to] To Date, use the format dd/MM/yyyy ex. 10/05/2012";				
			}
			
			// Output, we do not check...						
			// csvSeparator, we do not check...			
			// decimal Separator
			if (!((args[6].equals("."))||(args[6].equals(",")))){
				error = true;
				result = result + "\n [decimalSeparator] values : ,(comma) or .(dot) ";				
			}
			
			if (error){
				System.out.println(result);
			}else{
				// SYM1,SYM2,SYMn  without space
				String SYMBOLS 		= args[0];
				// Needs top be DD/MM/YYYY
				String FROM	 		= args[1];
				String TO			= args[2];
				// DAILY, WEEKLY, MONTHLY					
				String TIMEFRAME 	= args[3];
				if (args[3].equalsIgnoreCase("daily"))
					TIMEFRAME = YahooMarketDataFeed.YAHOO_DAILY;
				if (args[3].equalsIgnoreCase("weekly"))
					TIMEFRAME = YahooMarketDataFeed.YAHOO_WEEKLY;
				if (args[3].equalsIgnoreCase("monthly"))
					TIMEFRAME = YahooMarketDataFeed.YAHOO_MONTHLY;								
				// full path to outputfile (CSV)
				String OUTPUT  		= args[4];
				String SEPARATOR 	= args[5];
				String DECIMAL 		= args[6];
						
				YahooDataSynchronizer yds = new YahooDataSynchronizer(SYMBOLS,FROM,TO,TIMEFRAME,OUTPUT,SEPARATOR,DECIMAL);
				yds.start();
			}
		}
	}

}
